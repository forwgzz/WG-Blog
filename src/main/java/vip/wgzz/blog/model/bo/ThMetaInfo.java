package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/18 23:42
 * @description meta信息
 */
@Data
@Accessors(chain = true)
public class ThMetaInfo {
    /**
     * 分类或标签id
     */
    private Integer id;
    /**
     * 分类或标签名
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 描述
     */
    private String description;
}
