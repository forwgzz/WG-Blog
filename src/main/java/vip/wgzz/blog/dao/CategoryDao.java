package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.wgzz.blog.model.po.CategoryPO;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/2 16:55
 * @description 分类Dao
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryPO> {

    /**
     * @return 获取分类列表(关联文章数量)
     */
    List<CategoryPO> getCategoryWithArticleNumList();
}
