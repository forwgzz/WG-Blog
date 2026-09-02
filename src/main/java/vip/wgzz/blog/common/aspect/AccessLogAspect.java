package vip.wgzz.blog.common.aspect;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.AccessLogPO;
import vip.wgzz.blog.service.AccessLogService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author wgzz
 * @date 2026/8/21 21:12
 * @description 访问日志切面
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class AccessLogAspect {


    @Resource
    private AccessLogService accessLogService;

    /**
     * 切入点：@AccessLogRecord注解
     */
    @Pointcut("@annotation(vip.wgzz.blog.common.annotation.AccessLog)")
    public void accessLogPointcut() {}

    @Around("accessLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取请求对象
        HttpServletRequest request = getRequest();
        // 获取用户信息
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        // 处理日志数据
        saveLogAsync(request, loginUser);
        return joinPoint.proceed();

    }

    @Async("taskExecutor")
    public void saveLogAsync(HttpServletRequest request, LoginUserInfo loginUser) {
        try {
            // 如果上级设置了访客权限不记录
            if(request.getAttribute(BaseConstants.AttributeName.SET_VISITOR_AUTH) != null){
                return;
            }
            // 日志信息
            AccessLogPO accessLogPO = new AccessLogPO()
                    .setUserId(loginUser.getUserId())
                    .setUserName(loginUser.getUserName())
                    .setUserEmail(loginUser.getUserEmail())
                    .setUserType(loginUser.getUserType())
                    .setUserIp(loginUser.getLoginIp())
                    .setUserAddress(loginUser.getLoginAddress())
                    .setBrowserKey(loginUser.getBrowserKey())
                    .setBrowserAgent(loginUser.getBrowserInfo().getAgent())
                    .setTraceId(loginUser.getTraceId())
                    .setAccessTime(LocalDateTime.now())
                    .setAccessMethod(request.getMethod())
                    .setAccessPath(LoginUserUtils.getFullRequestUrl(request))
                    .setLastAccessPath(LoginUserUtils.getLastRequestUrl(request));
            // 保存
            accessLogService.save(accessLogPO);
        } catch (Exception e) {
            log.error("保存访问日志失败:", e);
        }
    }

    /**
     * 获取请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attrs).getRequest();
    }
}
