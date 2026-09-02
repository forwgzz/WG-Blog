package vip.wgzz.blog.config.security;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import vip.wgzz.blog.common.util.IpLocationUtils;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.service.LoginService;

import java.io.IOException;
import java.util.Objects;

/**
 * @author wgzz
 * @date 2026/8/3 14:39
 * @description Security校验前处理
 */
@Slf4j
public class PreBasicAuthFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    public PreBasicAuthFilter(LoginService loginService) {
        this.loginService = loginService;
    }

    private static final String BROWSER_KEY_URL = "/public/js/vfp.min";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 请求url
        String requestURI = request.getRequestURI();
        // ip
        String ip = IpLocationUtils.getIp(request);
        // 指纹
        String browserKey = LoginUserUtils.getBrowserKey(request);
        log.info("request:[{}] --- [{}] --- [{}]--{}", request.getMethod(), ip, requestURI, browserKey);

        // 登录用户信息
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(ip, loginUser.getLoginIp())) {
            // 登录用户不存在 或 ip变化 设置新的访客权限
            loginService.setVisitorAuth(request, response);
        } else if (!Objects.equals(browserKey, loginUser.getBrowserKey())) {
            // 指纹变化 更新访客权限
            loginService.updateVisitorAuth(request, response);
        }

        // 静态资源,文件 需要携带指纹才能访问 排除指纹获取vfp.min.js
        if (StrUtil.isBlank(browserKey) && !requestURI.startsWith(BROWSER_KEY_URL) &&
                (requestURI.startsWith("/public/") || requestURI.startsWith("/file/"))) {
            return;
        }

        filterChain.doFilter(request, response);
    }
}
