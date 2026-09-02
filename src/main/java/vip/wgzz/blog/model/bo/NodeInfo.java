package vip.wgzz.blog.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wgzz
 * @date 2026/8/14 15:48
 * @description 目录节点信息
 */
@Data
@AllArgsConstructor
public class NodeInfo {

    /**
     * 目录级别
     */
    private final Integer level;
    /**
     * 目录节点id
     */
    private final String id;
    /**
     * 目录节点文本
     */
    private final String text;
}
