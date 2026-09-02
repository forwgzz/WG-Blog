package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.wgzz.blog.model.bo.SelectInfo;
import vip.wgzz.blog.model.bo.TagInfo;
import vip.wgzz.blog.model.po.TagPO;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TagReq;
import vip.wgzz.blog.model.vo.TopReq;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/8 20:58
 * @description 标签Service
 */
public interface TagService extends IService<TagPO> {

    /**
     * @return 标签下拉框
     */
    List<SelectInfo> getSelectList();


    /**
     * @param pageQuery 查询条件
     * @return 标签分页查询
     */
    IPage<TagInfo> getTagPage(PageQuery pageQuery);


    /**
     * 新增标签
     *
     * @param tag 标签信息
     */
    void addTag(TagReq tag);

    /**
     * 更新便签
     *
     * @param tag 标签信息
     */
    void updateTag(TagReq tag);

    /**
     * 置顶
     *
     * @param topReq 置顶参数
     */
    void topTag(TopReq topReq);

    /**
     * 删除标签
     *
     * @param ids 标签id列表
     */
    void deleteTagByIds(List<Integer> ids);

}
