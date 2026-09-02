package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/17 14:01
 * @description 修改密码
 */
@Data
public class PasswordReq {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=;'\":|,.<>/?~]{6,20}$",
            message = "密码只能包含字母、数字和!@#$%^&*()_+-=;'\":|,.<>/?~，长度6-20位")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=;'\":|,.<>/?~]{6,20}$",
            message = "密码只能包含字母、数字和!@#$%^&*()_+-=;'\":|,.<>/?~，长度6-20位")
    private String newPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=;'\":|,.<>/?~]{6,20}$",
            message = "密码只能包含字母、数字和!@#$%^&*()_+-=;'\":|,.<>/?~，长度6-20位")
    private String confirmPassword;
}
