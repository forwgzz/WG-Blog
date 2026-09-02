package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/16 19:43
 * @description 系统设置请求
 */
@Data
public class SysConfigReq {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    private String configCode;

    /**
     * 配置值
     */
    @NotNull(message = "配置值不能为空")
    private String configValue;
}
