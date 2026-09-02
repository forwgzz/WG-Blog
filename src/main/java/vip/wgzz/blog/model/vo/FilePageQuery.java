package vip.wgzz.blog.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/12 17:48
 * @description 文件分页查询
 */
@Data
public class FilePageQuery extends PageQuery {

    /**
     * 文件类型集合
     */
    private List<String> fileTypes;
}
