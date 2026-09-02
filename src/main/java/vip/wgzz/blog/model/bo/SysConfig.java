package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/16 21:47
 * @description 系统配置信息
 */
@Data
@AllArgsConstructor
public class SysConfig {

    /**
     * 配置键
     */
    private String code;

    /**
     * 配置键名
     */
    private String name;

    /**
     * 配置值
     */
    private String value;

    /**
     * 默认配置值
     */
    private String defValue;
}
