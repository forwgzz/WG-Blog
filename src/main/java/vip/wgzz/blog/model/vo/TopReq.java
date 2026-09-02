package vip.wgzz.blog.model.vo;

import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/5 10:51
 * @description 通用置顶请求
 */
@Data
public class TopReq {

    /**
     * id
     */
    private Integer id;

    /**
     * 排序值
     */
    private Long sort;
}
