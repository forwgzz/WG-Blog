package vip.wgzz.blog.service;

import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.vo.PasswordReq;
import vip.wgzz.blog.model.vo.UserReq;

/**
 * @author wgzz
 * @date 2026/8/4 10:26
 * @description 用户Service
 */
public interface UserService {

    /**
     * @return 管理员用户
     */
    LoginUserInfo getAdminUser();

    /**
     * 更新密码
     *
     * @param passwordReq 密码
     */
    void updatePassword(PasswordReq passwordReq);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    void updateUserInfo(UserReq user);
}
