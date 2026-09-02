package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.BaseConstants;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/15 14:04
 * @description 评论树
 */
@Data
@Accessors(chain = true)
public class CommentTree {

    /**
     * 评论id
     */
    private Integer id;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 归属文章id
     */
    private Integer articleId;

    /**
     * 归属文章id
     */
    private String articleTitle;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户ip归属地
     */
    private String userIpAddr;

    /**
     * 顶级评论ID
     */
    private Integer rootCommentId;

    /**
     * 回复评论id
     */
    private Integer toCommentId;

    /**
     * 回复用户名称
     */
    private String toUserName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 评论状态 0未审核 1审核通过 2审核拒绝
     */
    private Integer commentStatus;

    /**
     * 评论状态
     */
    public String getCommentStatusStr() {
        return BaseConstants.CommentStatus.getCommentStatusStr(commentStatus);
    }

    /**
     * 子评论
     */
    private List<CommentTree> childList;

}

