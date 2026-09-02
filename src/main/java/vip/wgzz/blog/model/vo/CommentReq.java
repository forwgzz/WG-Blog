package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/15 14:07
 * @description 评论请求
 */
@Data
public class CommentReq {

    /**
     * 评论id集合
     */
    @NotNull(message = "评论id不能为空")
    @Size(min = 1, message = "评论id不能为空")
    private List<Integer> idList;

    /**
     * 评论状态
     */
    @NotNull
    @Max(value = 2, message = "评论状态异常")
    @Min(value = 0, message = "评论状态异常")
    private Integer commentStatus;


}
