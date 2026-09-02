package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/17 20:37
 * @description 前台数据统计
 */
@Data
@Accessors(chain = true)
public class FrontStatsInfo {

    /**
     * 文章总数
     */
    private Long articleTotal;

    /**
     * 分类总数
     */
    private Long categoryTotal;

    /**
     * 分类列表
     */
    private List<SelectInfo> categoryList;

    /**
     * 标签总数
     */
    private Long tagTotal;

    /**
     * 标签列表
     */
    private List<SelectInfo> tagList;

    /**
     * 总访客数
     */
    private Long visitorTotal;

    /**
     * 总访问量
     */
    private Long viewTotal;

    /**
     * 总文字数
     */
    private Long wordTotal;

    /**
     * 归档 年月-> 文字数
     */
    private List<ArchiveCard> archiveList;


}
