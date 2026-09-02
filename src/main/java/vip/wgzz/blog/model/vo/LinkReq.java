package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/16 14:18
 * @description 友链请求参数
 */
@Data
public class LinkReq {
    /**
     * id
     */
    private Integer id;

    /**
     * 友链名称
     */
    @NotBlank(message = "友链名称不能为空")
    @Size(max = 50, message = "友链名称长度不能超过50")
    private String linkName;

    /**
     * 友链地址
     */
    @NotBlank(message = "友链地址不能为空")
    @Size(max = 100, message = "友链地址长度不能超过100")
    private String linkUrl;

    /**
     * 友链头像
     */
    @NotBlank(message = "友链头像不能为空")
    @Size(max = 100, message = "友链头像地址长度不能超过100")
    private String linkAvatar;

    /**
     * 友链描述
     */
    @Size(max = 100, message = "友链描述长度不能超过100")
    private String linkDesc;

    /**
     * 友链状态 1显示 0隐藏
     */
    @Min(value = 0, message = "友链状态异常")
    @Max(value = 1, message = "友链状态异常")
    private Integer linkStatus;
}
