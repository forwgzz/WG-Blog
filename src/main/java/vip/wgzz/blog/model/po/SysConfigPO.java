package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:55
 * @description 配置表
 */
@Data
@Accessors(chain = true)
@TableName("tb_sys_config")
public class SysConfigPO {

    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置备注
     */
    private String configDesc;

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
