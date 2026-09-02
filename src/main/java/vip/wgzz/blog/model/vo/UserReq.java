package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/17 14:12
 * @description 用户信息
 */
@Data
public class UserReq {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 用户编码（登录名）
     */
    @NotBlank(message = "登录名不能为空")
    private String userCode;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "用户邮箱不能为空")
    private String userEmail;

    /**
     * 用户qq
     */
    private String userQq;

    /**
     * 用户头像
     */
    private String userAvatar;

}
