package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import vip.wgzz.blog.model.bo.ArchiveCard;
import vip.wgzz.blog.model.po.ArticlePO;

import java.util.Collection;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/2 15:13
 * @description 文章Dao
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticlePO> {

    /**
     * 更新文章浏览次数
     *
     * @param articleId 文章ID
     * @return 影响行数
     */
    @Update("UPDATE tb_article SET view_count = view_count + 1 WHERE id = #{articleId}")
    int updateViewCount(@Param("articleId") Integer articleId);

    /**
     * 获取文章关联标签列表(分页)
     *
     * @param page 分页参数
     * @param article 查询条件
     * @return 分页数据
     */
    Page<ArticlePO> getArticleWithTags(@Param("page") Page<ArticlePO> page, @Param("article") ArticlePO article);

    /**
     * @return 获取发布文章关联标签列表
     */
    List<ArticlePO> getPublishArticleWithTagsList();

    /**
     * 获取文章关联标签列表
     * @param article 查询条件
     * @return 数据
     */
    ArticlePO getArticleWithTagsById(@Param("article") ArticlePO article);

    /**
     * @return 获取归档信息 最多6个月
     */
    List<ArchiveCard> getArchiveInfo();


    /**
     * 更新文章评论数量
     *
     * @param ids 文章id列表
     */
    void updateCommentCountByIds(@Param("ids") Collection<Integer> ids);

}
