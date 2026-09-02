package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/4 21:19
 * @description 登录参数
 */
@Data
public class LoginReq {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 10, message = "用户名长度异常")
    private String userCode;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 5, max = 18, message = "密码长度异常")
    private String password;

    /**
     * 验证码
     */
    private String captcha;
}
