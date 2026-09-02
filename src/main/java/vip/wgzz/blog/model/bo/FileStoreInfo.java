package vip.wgzz.blog.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/12 17:34
 * @description 文件信息
 */
@Data
public class FileStoreInfo {

    /**
     * 文件id
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String fileOldName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件内容类型
     */
    private String fileContentType;

    /**
     * 排序值 时间戳
     */
    private Long sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
