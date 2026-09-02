package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/15 16:17
 * @description 评论详情请求
 */
@Data
public class CommentTreeReq {

    /**
     * 当前评论id
     */
    @NotNull(message = "评论id不能为空")
    private Integer nowCommentId;

    /**
     * 归属文章id
     */
    @NotNull(message = "文章id不能为空")
    private Integer articleId;
}
