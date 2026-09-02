package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import vip.wgzz.blog.model.bo.ArticleInfo;
import vip.wgzz.blog.model.vo.ArticlePageQuery;
import vip.wgzz.blog.model.vo.ArticleReq;
import vip.wgzz.blog.model.vo.TopReq;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/14 10:20
 * @description 文章Service
 */
public interface ArticleService {

    /**
     * @param pageQuery 文章分页查询参数
     * @return 文章分页信息
     */
    IPage<ArticleInfo> page(ArticlePageQuery pageQuery);

    /**
     * @param article 文章
     * @return 新增文章
     */
    ArticleInfo addArticle(ArticleReq article);

    /**
     * @param article 文章
     * @return 修改文章
     */
    ArticleInfo updateArticle(ArticleReq article);

    /**
     * @param article 文章
     * @return 修改文章状态
     */
    ArticleInfo updateStatus(ArticleReq article);

    /**
     * @param articleId 文章id
     * @return 通过id获取文章信息(带标签)
     */
    ArticleInfo getArticleWithTagsById(Integer articleId);

    /**
     * 置顶
     *
     * @param topReq 置顶参数
     */
    void topArticle(TopReq topReq);

    /**
     * 删除
     *
     * @param ids 文章id列表
     */
    void deleteArticleByIds(List<Integer> ids);

    /**
     * 更新访问量
     *
      * @param articleId 文章id
     */
    void updateArticleViewCount(Integer articleId);
}
