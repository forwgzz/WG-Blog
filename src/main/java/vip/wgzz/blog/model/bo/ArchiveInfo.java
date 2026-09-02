package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/19 21:52
 * @description 全部归档信息
 */
@Data
@AllArgsConstructor
public class ArchiveInfo {

    /**
     * 置顶文章列表
     */
    private List<ArticleInfo> tops;

    /**
     * 归档文章列表
     */
    private List<ArchiveYear> years;
}
