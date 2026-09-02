package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/4 10:31
 * @description 登陆用户信息
 */
@Data
@Accessors(chain = true)
public class LoginUserInfo {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户qq
     */
    private String userQq;

    /**
     * 用户头像地址
     */
    private String userAvatar;

    /**
     * 浏览器指纹
     */
    private String browserKey;

    /**
     * 浏览器信息
     */
    private BrowserInfo browserInfo;

    /**
     * 用户类型 0访客 1管理员
     */
    private Integer userType;

    /**
     * 用户状态 0正常 1锁定 2注销
     */
    private Integer userStatus;

    /**
     * 锁定时间
     */
    private LocalDateTime lockedTime;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录地址
     */
    private String loginAddress;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 上次登录ip
     */
    private String lastLoginIp;

    /**
     * 上次登录地址
     */
    private String lastLoginAddress;

    /**
     * 数据状态 1有效 0无效
     */
    private Integer dataStatus;

    /**
     * 登录次数
     */
    private Integer loginNums;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户日志id 用于指纹用户合并记录
     */
    private Integer userLogId;

    /**
     * 跟踪id
     */
    private String traceId;

    /**
     * 访问日志id
     */
    private Integer accessLogId;
}
