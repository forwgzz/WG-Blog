package vip.wgzz.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.IdUtils;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.config.security.LoginUserDetail;
import vip.wgzz.blog.dao.UserDao;
import vip.wgzz.blog.model.bo.BrowserInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.AccessLogPO;
import vip.wgzz.blog.model.po.UserPO;
import vip.wgzz.blog.service.AccessLogService;
import vip.wgzz.blog.service.LoginService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static vip.wgzz.blog.common.util.LoginUserUtils.getBrowserInfo;

/**
 * @author wgzz
 * @date 2026/8/21 23:18
 * @description 登录Service实现
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserDao userDao;

    @Resource
    private AccessLogService accessLogService;

    @Resource
    private SecurityContextRepository securityContextRepository;

    /**
     * 设置管理员权限
     *
     * @param oldAuth  旧权限
     * @param request  请求
     * @param response 响应
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setAdminAuth(Authentication oldAuth, HttpServletRequest request, HttpServletResponse response) {
        // security 登录成功 自动生成的权限
        Object principal = oldAuth.getPrincipal();
        if (!(principal instanceof LoginUserDetail)) {
            log.error("错误权限:{}", JSONUtil.toJsonStr(principal));
            throw new BaseException("错误权限");
        }
        // 用户登录信息
        LoginUserInfo userInfo = ((LoginUserDetail) principal).getUser();

        // 浏览器信息
        BrowserInfo browserInfo = getBrowserInfo(request);
        userInfo.setBrowserKey(browserInfo.getKey())
                .setBrowserInfo(browserInfo)
                // 上次登陆信息
                .setLastLoginIp(userInfo.getLoginIp())
                .setLastLoginAddress(userInfo.getLastLoginAddress())
                .setLastLoginTime(userInfo.getLastLoginTime())
                // 本次登陆信息
                .setLoginIp(browserInfo.getIp())
                .setLoginAddress(browserInfo.getAddress())
                .setLoginTime(LocalDateTime.now())
                .setTraceId(IdUtils.timeId());

        // 新权限
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                new LoginUserDetail(userInfo),
                oldAuth.getCredentials(),
                oldAuth.getAuthorities()
        );
        updateAuth(newAuth, request, response);
        log.info("设置管理权限：{}", JSONUtil.toJsonStr(userInfo));
        // 更新用户表日志
        userDao.updateLoginUser(new UserPO()
                .setId(userInfo.getUserId())
                .setLastLoginIp(userInfo.getLastLoginIp())
                .setLastLoginAddress(userInfo.getLastLoginAddress())
                .setLastLoginTime(userInfo.getLastLoginTime())
                .setLoginIp(userInfo.getLoginIp())
                .setLoginAddress(userInfo.getLoginAddress())
                .setLoginTime(userInfo.getLoginTime()));
        // 更新访问日志
        accessLogService.save(new AccessLogPO()
                .setUserId(userInfo.getUserId())
                .setUserName(userInfo.getUserName())
                .setUserType(userInfo.getUserType())
                .setUserEmail(userInfo.getUserEmail())
                .setUserIp(userInfo.getLoginIp())
                .setUserAddress(userInfo.getLoginAddress())
                .setAccessTime(userInfo.getLoginTime())
                .setBrowserKey(userInfo.getBrowserKey())
                .setBrowserAgent(userInfo.getBrowserInfo().getAgent())
                .setTraceId(userInfo.getTraceId())
                .setAccessMethod(request.getMethod())
                .setAccessPath(LoginUserUtils.getFullRequestUrl(request))
                .setLastAccessPath(LoginUserUtils.getLastRequestUrl(request)));
    }


    /**
     * 设置访客权限
     *
     * @param request  请求
     * @param response 响应
     * @return 用户信息
     */
    @Override
    public LoginUserInfo setVisitorAuth(HttpServletRequest request, HttpServletResponse response) {
        // 浏览器信息
        BrowserInfo browserInfo = getBrowserInfo(request);

        // 根据浏览器指纹查询访客
        AccessLogPO accesslog = accessLogService.getVisitorByKey(browserInfo.getKey());
        if (accesslog == null) {
            accesslog = new AccessLogPO();
        }
        // 登录用户信息
        LoginUserInfo userInfo = new LoginUserInfo()
                .setUserId(accesslog.getUserId())
                .setUserName(accesslog.getUserName())
                .setUserType(BaseConstants.UserType.VISITOR)
                .setUserEmail(accesslog.getUserEmail())
                .setLoginIp(browserInfo.getIp())
                .setLoginAddress(browserInfo.getAddress())
                .setLoginTime(LocalDateTime.now())
                .setLastLoginIp(accesslog.getUserIp())
                .setLastLoginAddress(accesslog.getUserAddress())
                .setBrowserKey(browserInfo.getKey())
                .setBrowserInfo(browserInfo)
                .setTraceId(IdUtils.timeId());

        // 更新访客信息
        accesslog.setId(null)
                .setUserType(BaseConstants.UserType.VISITOR)
                .setBrowserKey(browserInfo.getKey())
                .setBrowserAgent(browserInfo.getAgent())
                .setUserIp(browserInfo.getIp())
                .setUserAddress(browserInfo.getAddress())
                .setAccessTime(LocalDateTime.now())
                .setAccessMethod(request.getMethod())
                .setAccessPath(LoginUserUtils.getFullRequestUrl(request))
                .setLastAccessPath(LoginUserUtils.getLastRequestUrl(request))
                .setTraceId(userInfo.getTraceId());

        // 新增成功 自动赋值自增id
        accessLogService.save(accesslog);
        userInfo.setAccessLogId(accesslog.getId());
        // 更新
        updateAuth(buildVisitorAuth(userInfo), request, response);
        log.info("设置访客权限：{}", JSONUtil.toJsonStr(userInfo));
        // 设置访客权限标识
        request.setAttribute(BaseConstants.AttributeName.SET_VISITOR_AUTH, true);
        return userInfo;
    }

    /**
     * 更新访客权限
     *
     * @param request  请求
     * @param response 响应
     * @return 用户信息
     */
    @Override
    public LoginUserInfo updateVisitorAuth(HttpServletRequest request, HttpServletResponse response) {
        LoginUserInfo oldUser = LoginUserUtils.getLoginUser();
        if (oldUser == null) {
            return setVisitorAuth(request, response);
        }
        // 浏览器信息
        BrowserInfo browserInfo = LoginUserUtils.getBrowserInfo(request);
        if (StrUtil.isBlank(browserInfo.getKey())) {
            return oldUser;
        }
        // 访问时间在10s内视为正常获取指纹更新上条无指纹记录
        if (oldUser.getLoginTime().plusSeconds(10).isAfter(LocalDateTime.now())) {
            // 访问日志
            AccessLogPO accessLogPO = new AccessLogPO()
                    .setId(oldUser.getAccessLogId())
                    .setBrowserKey(browserInfo.getKey());
            accessLogService.updateById(accessLogPO);
            // 更新权限
            oldUser.setBrowserKey(browserInfo.getKey())
                    .setBrowserInfo(browserInfo);
            updateAuth(buildVisitorAuth(oldUser), request, response);
            request.setAttribute(BaseConstants.AttributeName.SET_VISITOR_AUTH, true);
            log.info("更新访客权限：{}", JSONUtil.toJsonStr(oldUser));
            return oldUser;
        }
        return setVisitorAuth(request, response);
    }


    /**
     * 构建访客权限
     *
     * @param userInfo 用户信息
     * @return 访客权限
     */
    private Authentication buildVisitorAuth(LoginUserInfo userInfo) {
        String[] visitorAuthorityList = BaseConstants.UserType.getAuthorityList(BaseConstants.UserType.VISITOR);
        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(visitorAuthorityList).map(SimpleGrantedAuthority::new).toList();
        // 新Authentication
        return new UsernamePasswordAuthenticationToken(new LoginUserDetail(userInfo), null, grantedAuthorities);
    }


    /**
     * 更新权限
     *
     * @param auth     权限
     * @param request  请求
     * @param response 响应
     */
    private void updateAuth(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
        // SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // 更新SecurityContext
        securityContextRepository.saveContext(context, request, response);
    }
}
