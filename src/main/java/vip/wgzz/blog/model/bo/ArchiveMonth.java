package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/19 21:46
 * @description 归档月份
 */
@Data
@AllArgsConstructor
public class ArchiveMonth {

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 文章列表
     */
    private List<ArticleInfo> articles;
}
