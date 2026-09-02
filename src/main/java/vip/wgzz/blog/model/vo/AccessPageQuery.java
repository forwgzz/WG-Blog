package vip.wgzz.blog.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 16:34
 * @description 访客分页参数
 */
@Data
public class AccessPageQuery extends PageQuery {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 登录IP
     */
    private String userIp;

    /**
     * 登录地址
     */
    private String userAddress;

    /**
     * 浏览器指纹
     */
    private String browserKey;

    /**
     * 浏览器指纹为空 1是 0否
     */
    private Integer browserKeyBlank;

    /**
     * 浏览器头
     */
    private String browserAgent;

    /**
     * 访问路径
     */
    private String accessPath;

    /**
     * 跟踪ID
     */
    private String traceId;

    /**
     * 访问时间范围
     */
    private List<LocalDateTime> dateRange;

}
