package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/8 9:22
 * @description 统计信息
 */
@Data
@Accessors(chain = true)
public class AdminStatsInfo {

    /**
     * 文章总数
     */
    private Long articleTotal;

    /**
     * 今日文章数
     */
    private Long articleToday;

    /**
     * 昨日文章数
     */
    private Long articleYesterday;

    /**
     * 评论总数
     */
    private Long commentTotal;

    /**
     * 今日评论数
     */
    private Long commentToday;

    /**
     * 昨日评论数
     */
    private Long commentYesterday;

    /**
     * 友链总数
     */
    private Long linkTotal;

    /**
     * 今日友链数
     */
    private Long linkToday;

    /**
     * 昨日友链数
     */
    private Long linkYesterday;

    /**
     * 标签总数
     */
    private Long tagTotal;

    /**
     * 今日标签数
     */
    private Long tagToday;

    /**
     * 昨日标签数
     */
    private Long tagYesterday;

    /**
     * 访问量总数
     */
    private Long viewTotal;

    /**
     * 今日访问数
     */
    private Long viewToday;

    /**
     * 昨日访问数
     */
    private Long viewYesterday;

    /**
     * 分类总数
     */
    private Long categoryTotal;

    /**
     * 今日分类数
     */
    private Long categoryToday;

    /**
     * 昨日分类数
     */
    private Long categoryYesterday;
}
