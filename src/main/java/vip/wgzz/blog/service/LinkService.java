package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import vip.wgzz.blog.model.bo.LinkInfo;
import vip.wgzz.blog.model.vo.LinkReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 14:14
 * @description 友链Service
 */
public interface LinkService {

    /**
     * @param pageQuery 分页参数
     * @return 分页
     */
    IPage<LinkInfo> getLinkPage(PageQuery pageQuery);


    /**
     * 新增
     *
     * @param link 友链信息
     */
    void addLink(LinkReq link);

    /**
     * 更新
     *
     * @param link 友链信息
     */
    void updateLink(LinkReq link);

    /**
     * 置顶
     *
     * @param topReq 置顶参数
     */
    void topLink(TopReq topReq);

    /**
     * 删除
     *
     * @param ids 友链id列表
     */
    void deleteLinkByIds(List<Integer> ids);

    /**
     * @return 友链列表（仅查询可显示友链）
     */
    List<LinkInfo> getLinkShowList();
}
