package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/19 21:49
 * @description 归档年份
 */
@Data
@AllArgsConstructor
public class ArchiveYear {

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份列表
     */
    private List<ArchiveMonth> months;

    /**
     * 文章总数
     */
    private Integer total;
}
