package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.group.Insert;
import vip.wgzz.blog.common.group.Update;
import vip.wgzz.blog.common.util.CacheUtils;
import vip.wgzz.blog.common.util.ValidatorUtils;
import vip.wgzz.blog.dao.ArticleDao;
import vip.wgzz.blog.dao.CategoryDao;
import vip.wgzz.blog.model.bo.CategoryInfo;
import vip.wgzz.blog.model.bo.SelectInfo;
import vip.wgzz.blog.model.po.ArticlePO;
import vip.wgzz.blog.model.po.CategoryPO;
import vip.wgzz.blog.model.vo.CategoryReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.CategoryService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/8 20:51
 * @description 分类Service实现
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryPO> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private ArticleDao articleDao;

    /**
     * @return 分类下拉框
     */
    @Override
    public List<SelectInfo> getSelectList() {

        List<SelectInfo> categorySelect = CacheUtils.getList(BaseConstants.CacheKeys.CATEGORY_SELECT, SelectInfo.class);
        if (categorySelect != null) return categorySelect;

        // 查询数据
        List<CategoryPO> list = categoryDao.getCategoryWithArticleNumList();
        if (CollectionUtil.isEmpty(list)) {
            categorySelect = Collections.emptyList();
        } else {
            // 转换
            categorySelect = list.stream()
                    .map(category -> new SelectInfo(category.getId(), category.getCategoryName(), category.getNumber()))
                    .collect(Collectors.toList());
        }
        CacheUtils.put(BaseConstants.CacheKeys.CATEGORY_SELECT, categorySelect);
        return categorySelect;
    }

    /**
     * @param pageQuery 查询条件
     * @return 分类分页
     */
    @Override
    public IPage<CategoryInfo> getCategoryPage(PageQuery pageQuery) {
        // 查询条件
        LambdaQueryWrapper<CategoryPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(pageQuery.getSearchStr()), CategoryPO::getCategoryName, pageQuery.getSearchStr());
        // 排序
        lqw.orderByDesc(CategoryPO::getSort).orderByDesc(CategoryPO::getId);
        // 分页查询
        Page<CategoryPO> categoryPOPage = categoryDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        return categoryPOPage.convert(categoryPO -> BeanUtil.copyProperties(categoryPO, CategoryInfo.class));
    }

    /**
     * 新增分类
     *
     * @param category 分类信息
     */
    @Override
    public void addCategory(CategoryReq category) {
        // 新增校验
        ValidatorUtils.validate(category, Insert.class);
        // 查询是否存在
        LambdaQueryWrapper<CategoryPO> queryWrapper = new LambdaQueryWrapper<>(new CategoryPO()
                .setDataStatus(BaseConstants.YesOrNo.YES)
                .setCategoryName(category.getCategoryName()));
        long count = categoryDao.selectCount(queryWrapper);
        if (count > 0) {
            throw BaseException.format("分类 [{}] 已存在", category.getCategoryName());
        }
        // 新增
        CategoryPO categoryPO = new CategoryPO()
                .setCategoryName(category.getCategoryName())
                .setCategoryDesc(category.getCategoryDesc())
                .setDataStatus(BaseConstants.YesOrNo.YES);
        log.info("新增分类:{}", JSONUtil.toJsonStr(categoryPO));
        categoryDao.insert(categoryPO);
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }


    /**
     * 更新分类
     *
     * @param category 分类信息
     */
    @Override
    public void updateCategory(CategoryReq category) {
        // 更新校验
        ValidatorUtils.validate(category, Update.class);
        // 查询原分类信息
        CategoryPO categoryPO = categoryDao.selectById(category.getId());
        if (categoryPO == null) {
            throw new BaseException("分类id不存在");
        }
        // 判断是否修改
        if (Objects.equals(category.getCategoryName(), categoryPO.getCategoryName()) && Objects.equals(category.getCategoryDesc(), categoryPO.getCategoryDesc())) {
            throw new BaseException("请修改分类信息再提交");
        }
        // 更新
        categoryDao.updateById(new CategoryPO()
                .setId(category.getId())
                .setCategoryName(category.getCategoryName())
                .setCategoryDesc(category.getCategoryDesc()));

        // 更新文章 冗余分类名
        if (!Objects.equals(category.getCategoryName(), categoryPO.getCategoryName())) {
            articleDao.update(new ArticlePO().setCategoryName(category.getCategoryName()), new LambdaUpdateWrapper<>(new ArticlePO().setCategoryId(category.getId())));
        }
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

    /**
     * 置顶分类
     *
     * @param topReq 置顶参数
     */
    @Override
    public void topCategory(TopReq topReq) {
        if (topReq.getId() == null) {
            throw new BaseException("分类id不能为空");
        }
        CategoryPO categoryPO = new CategoryPO()
                .setId(topReq.getId())
                .setSort(Optional.ofNullable(topReq.getSort()).orElse(System.currentTimeMillis()));
        int i = categoryDao.updateById(categoryPO);
        if (i != 1) {
            throw new BaseException("分类id不存在");
        }
    }

    /**
     * 删除分类
     *
     * @param ids 分类id列表
     */
    @Override
    public void deleteCategoryByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("分类id不能为空");
        }
        // 查询是否有引用
        Long count = articleDao.selectCount(new LambdaQueryWrapper<ArticlePO>().in(ArticlePO::getCategoryId, ids));
        if (count > 0) {
            throw new BaseException("当前分类正在使用中，不能删除");
        }
        // 删除
        categoryDao.deleteByIds(ids);
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

}
