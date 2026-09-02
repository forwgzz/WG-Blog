package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/12 17:52
 * @description 文件请求参数
 */
@Data
public class FileReq {

    /**
     * 文件id
     */
    @NotBlank(message = "文件id不能为空")
    private String id;

    /**
     * 文件名
     */
    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100")
    private String fileName;

    /**
     * 排序
     */
    private Long sort;

}
