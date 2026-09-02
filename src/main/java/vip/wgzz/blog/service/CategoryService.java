package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.wgzz.blog.model.bo.CategoryInfo;
import vip.wgzz.blog.model.bo.SelectInfo;
import vip.wgzz.blog.model.po.CategoryPO;
import vip.wgzz.blog.model.vo.CategoryReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/8 20:48
 * @description 分类Service
 */
public interface CategoryService extends IService<CategoryPO> {

    /**
     * @return 分类下拉框
     */
    List<SelectInfo> getSelectList();


    /**
     * @param pageQuery 查询条件
     * @return 分类分页
     */
    IPage<CategoryInfo> getCategoryPage(PageQuery pageQuery);


    /**
     * 新增分类
     *
     * @param category 分类信息
     */
    void addCategory(CategoryReq category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     */
    void updateCategory(CategoryReq category);

    /**
     * 置顶分类
     *
     * @param topReq 置顶参数
     */
    void topCategory(TopReq topReq);

    /**
     * 删除分类
     *
     * @param ids 分类id列表
     */
    void deleteCategoryByIds(List<Integer> ids);
}
