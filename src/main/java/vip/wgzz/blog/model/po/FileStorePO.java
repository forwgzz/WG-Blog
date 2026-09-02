package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:43
 * @description 文件存储表
 */
@Data
@Accessors(chain = true)
@TableName("tb_file_store")
public class FileStorePO {

    /**
     * 文件id
     */
    @TableId
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件原名称
     */
    private String fileOldName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型： image图片 video视频 audio音频 code代码  zip压缩包 document文档 code代码 executable程序  other其他
     */
    private String fileType;

    /**
     * 文件内容类型 mine
     */
    private String fileContentType;

    /**
     * 文件md5值
     */
    private String fileMd5;

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

}
