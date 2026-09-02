package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/15 14:08
 * @description 评论新增
 */
@Data
public class CommentAdd {

    /**
     * 评论内容
     */
    @NotBlank(message = "评论字数范围1-500")
    @Size(min = 1, max = 500, message = "评论字数范围1-500")
    private String commentContent;

    /**
     * 用户名称
     */
    @NotBlank(message = "昵称字数范围1-10")
    @Size(min = 1, max = 10, message = "昵称字数范围1-10")
    private String userName;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "邮箱字数范围1-50")
    @Size(min = 1, max = 50, message = "邮箱字数范围1-50")
    private String userEmail;

    /**
     * 邮件通知
     */
    @Max(value = 1, message = "邮件通知状态异常")
    @Min(value = 0, message = "邮件通知状态异常")
    private int emailNotify;

    /**
     * 文章id
     */
    @NotNull(message = "文章id不能为空")
    private Integer articleId;

    /**
     * 验证码
     */
    @NotBlank
    @Size(max = 4, message = "验证码异常")
    @Size(min = 4, message = "验证码异常")
    private String captcha;

    /**
     * 顶级评论id
     */
    private Integer rootCommentId;

    /**
     * 回复评论id
     */
    private Integer toCommentId;

}