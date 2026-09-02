package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/9 12:31
 * @description 分类
 */
@Data
@Accessors(chain = true)
public class CategoryInfo {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类说明
     */
    private String categoryDesc;

    /**
     * 排序值
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