package vip.wgzz.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.bo.ArticleInfo;
import vip.wgzz.blog.model.vo.ArticlePageQuery;
import vip.wgzz.blog.model.vo.ArticleReq;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.ArticleService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/14 17:19
 * @description 后台文章Controller
 */
@RestController
@RequestMapping("/{adminPath}/article")
public class AdminArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * @param pageQuery 分页参数
     * @return 文章分页
     */
    @PostMapping("/page")
    public RespResult page(@RequestBody ArticlePageQuery pageQuery) {
        IPage<ArticleInfo> page = articleService.page(pageQuery);
        return RespResult.success(page);
    }

    /**
     * @param topReq 置顶参数
     * @return 置顶
     */
    @PostMapping("/top")
    public RespResult topTag(@RequestBody TopReq topReq) {
        articleService.topArticle(topReq);
        return RespResult.success();
    }

    /**
     * @param articleReq 文章请求参数
     * @return 新增文章
     */
    @PostMapping("/add")
    public RespResult addArticle(@RequestBody ArticleReq articleReq) {
        ArticleInfo articleBO = articleService.addArticle(articleReq);
        return RespResult.success(articleBO);
    }

    /**
     * @param articleReq 文章请求参数
     * @return 修改文章
     */
    @PostMapping("/update")
    public RespResult updateArticle(@RequestBody ArticleReq articleReq) {
        ArticleInfo articleBO = articleService.updateArticle(articleReq);
        return RespResult.success(articleBO);
    }

    /**
     * @param articleReq 文章请求参数
     * @return 修改状态
     */
    @PostMapping("/status")
    public RespResult updateStatus(@RequestBody ArticleReq articleReq) {
        ArticleInfo articleBO = articleService.updateStatus(articleReq);
        return RespResult.success(articleBO);
    }

    /**
     *
     * @param ids 文章id列表
     * @return 删除文章
     */
    @PostMapping("/delete")
    public RespResult deleteArticle(@RequestBody List<Integer> ids) {
        articleService.deleteArticleByIds(ids);
        return RespResult.success();
    }

    /**
     *
     * @param articleReq 文章请求参数
     * @return 文章详情
     */
    @PostMapping("/info")
    public RespResult getArticle(@RequestBody ArticleReq articleReq) {
        ArticleInfo articleBO = articleService.getArticleWithTagsById(articleReq.getId());
        return RespResult.success(articleBO);
    }

}
