package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.ArticleTagRelPO;

/**
 * @author wgzz
 * @date 2026/8/2 15:58
 * @description 文章标签关联Dao
 */
@Mapper
public interface ArticleTagRelDao extends BaseMapper<ArticleTagRelPO> {
}