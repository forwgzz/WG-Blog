package vip.wgzz.blog.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vip.wgzz.blog.config.security.LoginUserDetail;
import vip.wgzz.blog.model.bo.BrowserInfo;
import vip.wgzz.blog.model.bo.LocationInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;

import java.util.Objects;


/**
 * @author wgzz
 * @date 2026/8/3 14:31
 * @description 登录用户工具类
 */
@Slf4j
@Component
public class LoginUserUtils {

    /**
     * @return 获取登录用户信息
     */
    public static LoginUserInfo getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetail) {
            return ((LoginUserDetail) principal).getUser();
        }
        return null;
    }

    /**
     * @param request 请求
     * @return 获取浏览器信息
     */
    public static BrowserInfo getBrowserInfo(HttpServletRequest request) {
        BrowserInfo browserInfo = new BrowserInfo();
        // 获取ip地址信息
        String ip = IpLocationUtils.getIp(request);
        LocationInfo location = IpLocationUtils.getLocation(ip);
        browserInfo.setIp(ip);
        browserInfo.setLocation(location);
        browserInfo.setAddress(location.getAllAddress());
        // 浏览器指纹
        browserInfo.setKey(getBrowserKey(request));
        browserInfo.setAgent(request.getHeader("user-agent"));
        return browserInfo;
    }

    /**
     * @param request 请求
     * @return 获取浏览器指纹
     */
    public static String getBrowserKey(HttpServletRequest request) {
        // 查询cookie 获取指纹
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            // 前端指纹标识 JFPSSIONID
            if ("JFPSSIONID".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * @param request 请求
     * @return 获取请求的完整url
     */
    public static String getFullRequestUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append('?').append(queryString);
        }
        return url.toString();
    }

    /**
     * @param request 请求
     * @return 获取请求的完整url
     */
    public static String getLastRequestUrl(HttpServletRequest request) {
        return request.getHeader("Referer");
    }


    /**
     * 获取请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attrs).getRequest();
    }

    /**
     * 清除登录信息
     *
     * @param request  请求
     * @param response 响应
     */
    public static void clearLoginUser(HttpServletRequest request, HttpServletResponse response) {
        // 清除SecurityContext
        SecurityContextHolder.clearContext();
        // 失效Session
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 删除Cookie
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(cookie);
    }

    /**
     * 清除登录信息
     */
    public static void clearLoginUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;
        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();
        clearLoginUser(request, response);
    }
}
