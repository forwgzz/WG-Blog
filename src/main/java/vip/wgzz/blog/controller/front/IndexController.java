package vip.wgzz.blog.controller.front;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.annotation.AccessLog;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.model.bo.ArticleInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.bo.ThMetaInfo;
import vip.wgzz.blog.model.bo.ThBaseInfo;
import vip.wgzz.blog.model.po.CategoryPO;
import vip.wgzz.blog.model.po.TagPO;
import vip.wgzz.blog.model.vo.ArticlePageQuery;
import vip.wgzz.blog.service.*;

import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/5 10:48
 * @description 前台首页Controller
 */
@Slf4j
@Controller
public class IndexController {

    @Resource
    private ArticleService articleService;

    @Resource
    private StatsService statsService;

    @Resource
    private UserService userService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    @Resource
    private LinkService linkService;

    /**
     * 前台基础信息
     */
    private void setBaseInfo(Model model) {
        // 登陆用户
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        // 基础信息
        ThBaseInfo thBaseInfo = new ThBaseInfo().setUserInfo(loginUser)
                .setWebTitle(SysConfigEnums.WEB_TITLE.getValue())
                .setShowTextTitle(SysConfigEnums.SHOW_TEXT_TITLE.getValue())
                .setShowTextDesc(SysConfigEnums.SHOW_TEXT_DESC.getValue())
                .setWebDescription(SysConfigEnums.WEB_DESCRIPTION.getValue())
                .setAvatarUrl(SysConfigEnums.AVATAR_URL.getValue())
                .setFooterICP(SysConfigEnums.FOOTER_ICP.getValue())
                .setFooterPolice(SysConfigEnums.FOOTER_POLICE.getValue())
                .setWebStartTime(SysConfigEnums.WEB_START_TIME.getValue())
                .setCopyrightYear(SysConfigEnums.COPYRIGHT_YEAR.getValue())
                .setWebStats(SysConfigEnums.WEB_STATS.getValue())
                .setDonateUrl(SysConfigEnums.DONATE_URL.getValue());
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, thBaseInfo);
        // 统计信息
        model.addAttribute(BaseConstants.AttributeName.STATS_INFO, statsService.getFrontStats());
        // 作者信息
        model.addAttribute(BaseConstants.AttributeName.AUTHOR_INFO, userService.getAdminUser());
    }


    /**
     * 分页数据
     *
     * @param model     模型
     * @param pageQuery 分页参数
     * @return 是否成功
     */
    private Boolean buildPage(Model model, ArticlePageQuery pageQuery, Integer page) {
        ThMetaInfo thMetaInfo = new ThMetaInfo();
        if (pageQuery.getCategoryId() != null) {
            CategoryPO category = categoryService.getById(pageQuery.getCategoryId());
            if (category == null) {
                return false;
            }
            thMetaInfo.setId(category.getId())
                    .setName(category.getCategoryName())
                    .setKeyword(category.getCategoryName())
                    .setTitle("分类");
        }
        if (pageQuery.getTagId() != null) {
            TagPO tag = tagService.getById(pageQuery.getTagId());
            if (tag == null) {
                return false;
            }
            thMetaInfo.setId(tag.getId())
                    .setName(tag.getTagName())
                    .setKeyword(tag.getTagName())
                    .setTitle("标签");
        }
        // 搜索
        if (pageQuery.getSearchStr() != null) {
            if (pageQuery.getSearchStr().length() > 15) {
                // 截取15位
                pageQuery.setSearchStr(pageQuery.getSearchStr().substring(0, 15));
            }
            thMetaInfo.setName(pageQuery.getSearchStr())
                    .setKeyword(pageQuery.getSearchStr())
                    .setTitle("搜索");
        }
        if (page != null) {
            if (page < 1) return false;
            pageQuery.setPage(page);
            model.addAttribute(BaseConstants.AttributeName.CURRENT_PAGE, page);
        }
        // 默认每页10条
        pageQuery.setSize(10);
        // 默认发布
        pageQuery.setArticleStatus(BaseConstants.ArticleStatus.PUBLISH);

        //分页查询
        IPage<ArticleInfo> articlePage = articleService.page(pageQuery);
        // 页码超出限制
        if (pageQuery.getPage() != 0 && articlePage.getCurrent() != pageQuery.getPage()) {
            return false;
        }
        model.addAttribute(BaseConstants.AttributeName.ARTICLE_PAGE, articlePage);
        if (thMetaInfo.getId() != null) {
            // 文章名
            String articleTitles = articlePage.getRecords().stream().map(ArticleInfo::getArticleTitle).collect(Collectors.joining(","));
            thMetaInfo.setDescription(String.format("当前 %s[%s] 关联 %s 篇文章：%s", thMetaInfo.getTitle(), thMetaInfo.getName(), articlePage.getRecords().size(), articleTitles));
        }
        if (StrUtil.isNotBlank(thMetaInfo.getTitle()) || StrUtil.isNotBlank(thMetaInfo.getName())) {
            thMetaInfo.setTitle(String.format("%s: %s", thMetaInfo.getTitle(), thMetaInfo.getName()));
        }
        model.addAttribute(BaseConstants.AttributeName.META_INFO, thMetaInfo);
        return true;
    }

    /**
     * @param model 模型
     * @param page 页码
     * @return 首页分页
     */
    @AccessLog
    @GetMapping("/")
    public String index(Model model, Integer page) {
        setBaseInfo(model);
        if (buildPage(model, new ArticlePageQuery(), page)) {
            return "front/index";
        }
        throw BaseException.notFind();
    }

    /**
     * @param model 模型
     * @param searchStr 搜索内容
     * @param page 页码
     * @return 搜索文章分页
     */
    @AccessLog
    @GetMapping("/search/{searchStr}")
    public String searchPage(Model model, @PathVariable String searchStr, Integer page) {
        setBaseInfo(model);
        ArticlePageQuery query = new ArticlePageQuery();
        query.setSearchStr(searchStr);
        if (buildPage(model, query, page)) {
            return "front/index";
        }
        throw BaseException.notFind();
    }

    /**
     * @param model 模型
     * @param categoryId 分类ID
     * @param page 页码
     * @return 分类文章分页
     */
    @AccessLog
    @GetMapping("/category/{categoryId}")
    public String categoryPage(Model model, @PathVariable Integer categoryId, Integer page) {
        setBaseInfo(model);
        if (buildPage(model, new ArticlePageQuery().setCategoryId(categoryId), page)) {
            return "front/index";
        }
        throw BaseException.notFind();
    }

    /**
     * @param model 模型
     * @param tagId 标签ID
     * @param page 页码
     * @return 标签文章分页
     */
    @AccessLog
    @GetMapping("/tag/{tagId}")
    public String tagPage(Model model, @PathVariable Integer tagId, Integer page) {
        setBaseInfo(model);
        if (buildPage(model, new ArticlePageQuery().setTagId(tagId), page)) {
            return "front/index";
        }
        throw BaseException.notFind();
    }

    /**
     * @param model 模型
     * @param articleId 文章ID
     * @return 文章详情
     */
    @AccessLog
    @GetMapping("/article/{articleId}")
    public String article(Model model, @PathVariable Integer articleId) {
        setBaseInfo(model);
        try {
            // 更新访问次数
            articleService.updateArticleViewCount(articleId);
            ArticleInfo articleInfo = articleService.getArticleWithTagsById(articleId);
            if (articleInfo == null || BaseConstants.ArticleStatus.PUBLISH != articleInfo.getArticleStatus()) {
                throw BaseException.notFind();
            }
            model.addAttribute(BaseConstants.AttributeName.ARTICLE_INFO, articleInfo);
        } catch (Exception e) {
            log.error("获取文章详情失败:{}", e.getMessage(), e);
            throw BaseException.notFind();
        }
        return "front/article";
    }

    /**
     * @param model 模型
     * @return 关于我
     */
    @AccessLog
    @GetMapping("/about")
    public String about(Model model) {
        setBaseInfo(model);
        // 关联文章id
        String articleId = SysConfigEnums.ABOUT_REL_ARTICLE_ID.getValue();
        if(StrUtil.isBlank(articleId)){
            throw BaseException.notFind();
        }
        try {
            Integer id = Integer.valueOf(articleId);
            // 更新访问次数
            articleService.updateArticleViewCount(id);
            ArticleInfo articleInfo = articleService.getArticleWithTagsById(id);
            if (articleInfo == null || BaseConstants.ArticleStatus.PUBLISH != articleInfo.getArticleStatus()) {
                throw BaseException.notFind();
            }
            model.addAttribute(BaseConstants.AttributeName.ARTICLE_INFO, articleInfo);
        } catch (Exception e) {
            log.error("获取文章详情失败:{}", e.getMessage(), e);
            throw BaseException.notFind();
        }
        return "front/article";
    }

    /**
     * @param model 模型
     * @return 友链
     */
    @AccessLog
    @GetMapping("/link")
    public String link(Model model) {
        setBaseInfo(model);
        model.addAttribute(BaseConstants.AttributeName.LINK, linkService.getLinkShowList());
        return "front/link";
    }

    /**
     * @param model 模型
     * @return 归档
     */
    @AccessLog
    @GetMapping("/archive")
    public String archive(Model model) {
        setBaseInfo(model);
        // 归档数据
        model.addAttribute(BaseConstants.AttributeName.ARCHIVE_YEAR, statsService.getArchiveArticles());
        return "front/archive";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        LoginUserUtils.clearLoginUser();
        return "success";
    }

}
