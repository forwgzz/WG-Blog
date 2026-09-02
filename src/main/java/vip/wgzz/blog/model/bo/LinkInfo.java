package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import vip.wgzz.blog.common.BaseConstants;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/16 14:15
 * @description 友链信息
 */
@Data
@Accessors(chain = true)
public class LinkInfo {
    /**
     * id
     */
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


    public String getLinkStatusStr() {
        return BaseConstants.LinkStatus.getLinkStatusStr(linkStatus);
    }
}
