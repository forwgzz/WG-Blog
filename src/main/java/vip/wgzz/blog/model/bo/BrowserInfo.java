package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/3 14:23
 * @description 浏览器信息
 */
@Data
@Accessors(chain = true)
public class BrowserInfo {

    /**
     * 浏览器指纹
     */
    private String key;

    /**
     * 浏览器头
     */
    private String agent;

    /**
     * ip
     */
    private String ip;

    /**
     * ip地址
     */
    private String address;

    /**
     * ip地址完整信息
     */
    private LocationInfo location;
}
