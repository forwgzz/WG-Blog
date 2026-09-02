package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.ArticleFileRelPO;

/**
 * @author wgzz
 * @date 2026/8/2 16:59
 * @description 文章文件关联Dao
 */
@Mapper
public interface ArticleFileRelDao extends BaseMapper<ArticleFileRelPO> {
}
