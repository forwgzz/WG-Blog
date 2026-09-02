package vip.wgzz.blog.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/5 10:51
 * @description 通用分页查询
 */
@Data
@Accessors(chain = true)
public class PageQuery {

    /**
     * 当前页
     */
    private int page;

    /**
     * 页大小
     */
    private int size;

    /**
     * 搜索词
     */
    private String searchStr;
}
