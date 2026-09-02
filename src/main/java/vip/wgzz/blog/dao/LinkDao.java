package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.LinkPO;

/**
 * @author wgzz
 * @date 2026/8/2 16:57
 * @description 友链Dao
 */
@Mapper
public interface LinkDao extends BaseMapper<LinkPO> {
}