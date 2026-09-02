package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.wgzz.blog.model.bo.AccessInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.AccessLogPO;
import vip.wgzz.blog.model.vo.AccessPageQuery;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 16:48
 * @description 访问日志Service
 */
public interface AccessLogService extends IService<AccessLogPO> {


    /**
     * @param browserKey 指纹
     * @return 根据指纹查询访客
     */
    AccessLogPO getVisitorByKey(String browserKey);

    /**
     * 插入评论用户日志
     *
     * @param user 用户信息
     */
    void insertCommentUserLog(LoginUserInfo user);


    /**
     * @param pageQuery 分页参数
     * @return 分页
     */
    IPage<AccessInfo> getAccessLogPage(AccessPageQuery pageQuery);

    /**
     * 删除
     *
     * @param ids 访客日志id列表
     */
    void deleteVisitorByIds(List<Integer> ids);


}
