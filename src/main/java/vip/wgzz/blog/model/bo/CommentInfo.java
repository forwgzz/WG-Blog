package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.BaseConstants;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/15 14:02
 * @description 评论信息
 */
@Data
@Accessors(chain = true)
public class CommentInfo {

    /**
     * id
     */
    private Integer id;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 浏览器指纹
     */
    private String browserKey;

    /**
     * 用户ip
     */
    private String userIp;

    /**
     * 用户ip归属地
     */
    private String userIpAddr;

    /**
     * 顶级评论id 0表示为自身为顶级评论
     */
    private Integer rootCommentId;

    /**
     * 回复评论id
     */
    private Integer toCommentId;

    /**
     * 邮件通知 1是 0否
     */
    private Integer emailNotify;

    /**
     * 评论状态 0未审核 1审核通过 2审核拒绝
     */
    private Integer commentStatus;

    /**
     * 排序值 时间戳
     */
    private Long sort;

    /**
     * 数据状态 1有效 0无效
     */
    private Integer dataStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 评论状态
     */
    public String getCommentStatusStr() {
        return BaseConstants.CommentStatus.getCommentStatusStr(commentStatus);
    }

}
