package vip.wgzz.blog.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/15 14:01
 * @description 评论分页请求
 */
@Data
@Accessors(chain = true)
public class CommentPageQuery extends PageQuery {

    /**
     * 评论状态 0未审核 1审核通过 2审核拒绝
     */
    private Integer commentStatus;

}