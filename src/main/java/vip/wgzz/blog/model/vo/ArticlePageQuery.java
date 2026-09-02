package vip.wgzz.blog.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/5 10:50
 * @description 文章分页查询
 */
@Data
@Accessors(chain = true)
public class ArticlePageQuery extends PageQuery {

    /**
     * 文章状态 0草稿 1发布
     * 前台查询 由Controller层赋值为1
     */
    private Integer articleStatus;

    /**
     * 标签id
     */
    private Integer tagId;

    /**
     * 分类id
     */
    private Integer categoryId;

}

