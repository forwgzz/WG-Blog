package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.TagPO;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/2 15:57
 * @description 标签Dao
 */
@Mapper
public interface TagDao extends BaseMapper<TagPO> {

    /**
     * @return 获取标签列表 关联发布文章数量
     */
    List<TagPO> getTagWithArticleNumList();
}
