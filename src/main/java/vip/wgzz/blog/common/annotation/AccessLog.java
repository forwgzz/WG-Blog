package vip.wgzz.blog.common.annotation;

import java.lang.annotation.*;

/**
 * @author wgzz
 * @date 2026/8/21 21:08
 * @description 访问日志注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLog {
}
