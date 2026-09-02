package vip.wgzz.blog.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.exception.BaseException;

import java.util.Objects;

/**
 * @author wgzz
 * @date 2026/8/20 21:18
 * @description 检验动态后台地址
 */
@ControllerAdvice("vip.wgzz.blog.controller.admin")
public class AdminPathAdvice {

    @ModelAttribute("adminPath")
    public String validateAdminPath(@PathVariable String adminPath, HttpServletRequest request) {
        // 不等后台地址 直接404
        if (!Objects.equals("/" + adminPath, SysConfigEnums.ADMIN_PATH.getValue())) {
            throw BaseException.notFind();
        }
        return adminPath;
    }
}
