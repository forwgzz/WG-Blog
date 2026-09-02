package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import vip.wgzz.blog.dao.ArticleTagRelDao;
import vip.wgzz.blog.dao.TagDao;
import vip.wgzz.blog.model.bo.SelectInfo;
import vip.wgzz.blog.model.bo.TagInfo;
import vip.wgzz.blog.model.po.ArticleTagRelPO;
import vip.wgzz.blog.model.po.TagPO;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TagReq;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.TagService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/8 20:58
 * @description 标签Service实现
 */
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, TagPO> implements TagService {

    @Resource
    private TagDao tagDao;

    @Resource
    private ArticleTagRelDao articleTagRelDao;

    /**
     * @return 标签下拉框
     */
    @Override
    public List<SelectInfo> getSelectList() {
        List<SelectInfo> tagSelect = CacheUtils.getList(BaseConstants.CacheKeys.TAG_SELECT, SelectInfo.class);
        if (tagSelect != null) return tagSelect;
        // 查询数据
        List<TagPO> tagPOList = tagDao.getTagWithArticleNumList();
        if (CollectionUtil.isEmpty(tagPOList)) {
            tagSelect = Collections.emptyList();
        } else {
            // 转换
            tagSelect = tagPOList.stream()
                    .map(tagPO -> new SelectInfo(tagPO.getId(), tagPO.getTagName(), tagPO.getNumber()))
                    .collect(Collectors.toList());
        }
        // 缓存
        CacheUtils.put(BaseConstants.CacheKeys.TAG_SELECT, tagSelect);
        return tagSelect;
    }

    /**
     * @param pageQuery 查询条件
     * @return 标签分页查询
     */
    @Override
    public IPage<TagInfo> getTagPage(PageQuery pageQuery) {
        // 查询条件
        LambdaQueryWrapper<TagPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(pageQuery.getSearchStr()), TagPO::getTagName, pageQuery.getSearchStr());
        // 排序
        lqw.orderByDesc(TagPO::getSort).orderByDesc(TagPO::getId);
        // 分页查询
        Page<TagPO> tagPOPage = tagDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        return tagPOPage.convert(tagPO -> BeanUtil.copyProperties(tagPO, TagInfo.class));
    }

    /**
     * 新增标签
     *
     * @param tag 标签信息
     */
    @Override
    public void addTag(TagReq tag) {
        // 校验
        ValidatorUtils.validate(tag, Insert.class);
        // 查询是否存在
        LambdaQueryWrapper<TagPO> queryWrapper = new LambdaQueryWrapper<>(new TagPO().setDataStatus(BaseConstants.YesOrNo.YES).setTagName(tag.getTagName()));
        List<TagPO> tagPOS = tagDao.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(tagPOS)) {
            throw BaseException.format("标签 [{}] 已存在", tag.getTagName());
        }
        // 新增
        TagPO tagPO = new TagPO()
                .setTagName(tag.getTagName())
                .setDataStatus(BaseConstants.YesOrNo.YES);
        log.info("insert tag:{}", JSONUtil.toJsonStr(tagPO));
        tagDao.insert(tagPO);
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

    /**
     * 更新便签
     *
     * @param tag 标签信息
     */
    @Override
    public void updateTag(TagReq tag) {
        // 校验
        ValidatorUtils.validate(tag, Update.class);
        // 更新
        int re = tagDao.updateById(new TagPO()
                .setId(tag.getId())
                .setTagName(tag.getTagName()));
        if (re != 1) {
            throw new BaseException("标签id不存在");
        }
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

    /**
     * 置顶
     *
     * @param topReq 置顶参数
     */
    @Override
    public void topTag(TopReq topReq) {
        if (topReq.getId() == null) {
            throw new BaseException("标签id不能为空");
        }
        TagPO tagPO = new TagPO()
                .setId(topReq.getId())
                .setSort(Optional.ofNullable(topReq.getSort()).orElse(System.currentTimeMillis()));
        int i = tagDao.updateById(tagPO);
        if (i != 1) {
            throw new BaseException("标签id不存在");
        }
    }

    /**
     * 删除标签
     *
     * @param ids 标签id列表
     */
    @Override
    public void deleteTagByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("标签id不能为空");
        }
        //查询是否有引用
        Long count = articleTagRelDao.selectCount(new LambdaQueryWrapper<ArticleTagRelPO>().in(ArticleTagRelPO::getTagId, ids));
        if (count > 0) {
            throw new BaseException("当前标签正在使用中，不能删除");
        }
        // 删除
        tagDao.deleteByIds(ids);
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

}
