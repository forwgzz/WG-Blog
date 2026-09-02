package vip.wgzz.blog.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.service.LoginService;

import java.io.IOException;

/**
 * @author wgzz
 * @date 2026/8/3 13:58
 * @description Security配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityContextRepository securityContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setAllowSessionCreation(true);
        return repo;
    }

    /**
     * 密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security前处理
     */
    @Bean
    public PreBasicAuthFilter preBasicAuthFilter(LoginService loginService) {
        return new PreBasicAuthFilter(loginService);
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * 配置过滤器链
     *
     * @param http 请求
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository securityContextRepository, PreBasicAuthFilter preBasicAuthFilter) throws Exception {
        // Security校验前处理
        http.securityContext(ctx -> ctx.securityContextRepository(securityContextRepository))
                .addFilterBefore(preBasicAuthFilter, BasicAuthenticationFilter.class).authorizeHttpRequests(auth -> auth
                        // 动态登录页 访客权限
                        .requestMatchers(passAdminMatcher())
                        .hasAnyAuthority(BaseConstants.AuthorityType.VISITOR)
                        // 动态后台资源 管理权限
                        .requestMatchers(checkAdminMatcher()).hasAnyAuthority(BaseConstants.AuthorityType.ADMIN)
                        // 默认访客权限
                        .anyRequest().hasAnyAuthority(BaseConstants.AuthorityType.VISITOR));

        // 登出处理
        http.logout(logout -> logout
                // 动态登出接口
                .logoutRequestMatcher(adminLogoutMatcher())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler((request, response, authentication) ->
                        response.sendRedirect(SysConfigEnums.ADMIN_PATH.getValue() + "/login")
                )
        );

        // 异常拦截
        http.exceptionHandling(ex -> ex
                // 没有管理权限 跳转登录页
                .authenticationEntryPoint((req, resp, e) -> sendRedirectToLogin(req, resp))
                // 权限不足 跳转登录页
                .accessDeniedHandler((req, resp, e) -> sendRedirectToLogin(req, resp)));

        // 禁用CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 动态放行 /ADMIN_PATH/login /ADMIN_PATH/getCaptcha
     */
    private RequestMatcher passAdminMatcher() {
        return request -> request.getRequestURI().equals(SysConfigEnums.ADMIN_PATH.getValue() + "/login") ||
                request.getRequestURI().equals(SysConfigEnums.ADMIN_PATH.getValue() + "/getCaptcha");
    }

    /**
     * 动态拦截 /ADMIN_PATH/**
     */
    private RequestMatcher checkAdminMatcher() {
        return request -> request.getRequestURI().equals(SysConfigEnums.ADMIN_PATH.getValue()) || request.getRequestURI().startsWith(SysConfigEnums.ADMIN_PATH.getValue() + "/");
    }

    /**
     * 动放 /ADMIN_PATH/logout 登出
     */
    private RequestMatcher adminLogoutMatcher() {
        return request -> request.getRequestURI().equals(SysConfigEnums.ADMIN_PATH.getValue() + "/logout");
    }

    /**
     * 转发登录页 前处理
     *
     * @param request  请求
     * @param response 响应
     * @throws IOException 异常
     */
    private void sendRedirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 清除登录信息
        LoginUserUtils.clearLoginUser(request, response);
        // 转发登录页
        response.sendRedirect(SysConfigEnums.ADMIN_PATH.getValue() + "/login");
    }

}