package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 11:10
 * @description 访问日志表
 */
@Data
@Accessors(chain = true)
@TableName("tb_access_log")
public class AccessLogPO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户类型 0访客 1管理员
     */
    private Integer userType;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户ip
     */
    private String userIp;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

    /**
     * 访问方法
     */
    private String accessMethod;

    /**
     * 访问路径
     */
    private String accessPath;

    /**
     * 上一个访问路径
     */
    private String lastAccessPath;

    /**
     * 浏览器指纹
     */
    private String browserKey;

    /**
     * 浏览器头
     */
    private String browserAgent;

    /**
     * 跟踪id
     */
    private String traceId;

}
