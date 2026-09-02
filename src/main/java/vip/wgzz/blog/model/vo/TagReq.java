package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.group.Insert;
import vip.wgzz.blog.common.group.Update;

/**
 * @author wgzz
 * @date 2026/8/9 17:35
 * @description 标签请求参数
 */
@Data
@Accessors(chain = true)
public class TagReq {

    /**
     * id
     */
    @NotNull(message = "标签id不能为空", groups = {Update.class})
    private Integer id;

    /**
     * 标签名称
     */
    @NotNull(message = "便签名不能为空", groups = {Update.class, Insert.class})
    @Size(message = "便签名长度不能超过20", min = 1, max = 20, groups = {Update.class, Insert.class})
    private String tagName;

}
