package vip.wgzz.blog.model.bo;

import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/18 15:15
 * @description 归档卡片信息
 */
@Data
public class ArchiveCard {

    /**
     * 年月
     */
    private String yearMonth;

    /**
     * 数量
     */
    private Long count;
}
