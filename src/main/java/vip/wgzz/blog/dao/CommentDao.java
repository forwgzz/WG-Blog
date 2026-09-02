package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.CommentPO;

/**
 * @author wgzz
 * @date 2026/8/2 16:58
 * @description 评论Dao
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentPO> {
}
