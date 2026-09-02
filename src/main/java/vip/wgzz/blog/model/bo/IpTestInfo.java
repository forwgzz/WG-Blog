package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/17 11:40
 * @description ip接口测试信息
 */
@Data
@AllArgsConstructor
public class IpTestInfo {

    /**
     * ip地址
     */
    private String ip;

    /**
     * 放回结果
     */
    private String result;

    /**
     * 转换结果
     */
    private LocationInfo convertResult;
}
