package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/9 18:17
 * @description 标签信息
 */
@Data
@Accessors(chain = true)
public class TagInfo {

    /**
     * id
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
