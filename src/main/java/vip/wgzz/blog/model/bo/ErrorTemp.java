package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/22 14:45
 * @description 错误信息模板
 */
@Data
@Accessors(chain = true)
public class ErrorTemp {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * url
     */
    private String url;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 时间
     */
    private LocalDateTime time;
}
