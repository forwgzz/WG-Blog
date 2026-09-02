package vip.wgzz.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import vip.wgzz.blog.model.bo.LoginUserInfo;

/**
 * @author wgzz
 * @date 2026/8/21 23:15
 * @description 登录Service
 */
public interface LoginService {

    /**
     * 设置管理员权限
     *
     * @param oldAuth  旧权限
     * @param request  请求
     * @param response 响应
     */
    void setAdminAuth(Authentication oldAuth, HttpServletRequest request, HttpServletResponse response);

    /**
     * 设置访客权限
     *
     * @param request  请求
     * @param response 响应
     * @return 用户信息
     */
    LoginUserInfo setVisitorAuth(HttpServletRequest request, HttpServletResponse response);


    /**
     * 更新访客权限
     *
     * @param request  请求
     * @param response 响应
     * @return 用户信息
     */
    LoginUserInfo updateVisitorAuth(HttpServletRequest request, HttpServletResponse response);

}
