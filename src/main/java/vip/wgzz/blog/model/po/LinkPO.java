package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:31
 * @description 友链表
 */
@Data
@Accessors(chain = true)
@TableName("tb_link")
public class LinkPO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 友链名称
     */
    private String linkName;

    /**
     * 友链地址
     */
    private String linkUrl;

    /**
     * 友链头像
     */
    private String linkAvatar;

    /**
     * 友链描述
     */
    private String linkDesc;

    /**
     * 友链状态 1显示 0隐藏
     */
    private Integer linkStatus;

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
