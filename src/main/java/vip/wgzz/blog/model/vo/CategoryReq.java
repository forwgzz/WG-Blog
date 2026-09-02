package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.group.Insert;
import vip.wgzz.blog.common.group.Update;

/**
 * @author wgzz
 * @date 2026/8/9 12:51
 * @description 分类请求参数
 */
@Data
@Accessors(chain = true)
public class CategoryReq {

    /**
     * id
     */
    @NotNull(message = "分类id不能为空", groups = {Update.class})
    private Integer id;

    /**
     * 分类名称
     */
    @NotNull(message = "分类名不能为空", groups = {Update.class, Insert.class})
    @Size(message = "分类名长度不能超过20", min = 1, max = 20, groups = {Update.class, Insert.class})
    private String categoryName;

    /**
     * 分类说明
     */
    @Size(message = "分类说明长度不能超过100", max = 100, groups = {Update.class, Insert.class})
    private String categoryDesc;
}
