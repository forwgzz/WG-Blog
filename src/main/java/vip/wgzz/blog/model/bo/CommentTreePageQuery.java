package vip.wgzz.blog.model.bo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import vip.wgzz.blog.model.vo.PageQuery;

/**
 * @author wgzz
 * @date 2026/8/16 10:57
 * @description 评论树分页请求
 */
@Data
public class CommentTreePageQuery extends PageQuery {
    /**
     * 归属文章id
     */
    @NotNull(message = "文章id不能为空")
    private Integer articleId;
}
