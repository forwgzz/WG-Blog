package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.CacheUtils;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.dao.*;
import vip.wgzz.blog.model.bo.ArticleInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.bo.NodeInfo;
import vip.wgzz.blog.model.po.*;
import vip.wgzz.blog.model.vo.ArticlePageQuery;
import vip.wgzz.blog.model.vo.ArticleReq;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.ArticleService;
import vip.wgzz.blog.service.SitemapService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wgzz
 * @date 2026/8/14 10:35
 * @description 文章Service实现
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleDao articleDao;

    @Resource
    private ArticleTagRelDao articleTagRelDao;

    @Resource
    private ArticleFileRelDao articleFileRelDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private FileStoreDao fileStoreDao;

    @Resource
    private SitemapService sitemapService;


    /**
     * @param pageQuery 文章分页查询参数
     * @return 文章分页信息
     */
    @Override
    public IPage<ArticleInfo> page(ArticlePageQuery pageQuery) {
        // 查询条件
        ArticlePO query = new ArticlePO()
                .setDataStatus(BaseConstants.YesOrNo.YES)
                // 文章状态 前台查询 由Controller层赋值为1
                .setArticleStatus(pageQuery.getArticleStatus())
                .setTagId(pageQuery.getTagId())
                .setCategoryId(pageQuery.getCategoryId())
                .setArticleTitle(pageQuery.getSearchStr());
        // 分页查询
        Page<ArticlePO> page = articleDao.getArticleWithTags(new Page<>(pageQuery.getPage(), pageQuery.getSize()), query);
        // 转换
        return page.convert(record -> BeanUtil.toBean(record, ArticleInfo.class));
    }

    /**
     * 新增文章
     *
     * @param article 文章
     * @return 文章id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ArticleInfo addArticle(ArticleReq article) {
        // 检验文章
        checkArticleInfo(article);
        // 解析目录
        parserArticleCatalog(article);

        // 新增文章
        ArticlePO articleSave = BeanUtil.copyProperties(article, ArticlePO.class);
        // 字数
        articleSave.setWordCount(countWordsByRegex(articleSave.getArticleMarkdown()));
        articleDao.insert(articleSave);
        Integer articleId = articleSave.getId();

        //文章标签关联表
        for (Integer tagId : article.getTagIdList()) {
            articleTagRelDao.insert(new ArticleTagRelPO().setArticleId(articleId).setTagId(tagId));
        }
        //文章文件关联表
        for (String fileId : article.getFileIdList()) {
            articleFileRelDao.insert(new ArticleFileRelPO().setFileId(fileId).setArticleId(articleId));
        }
        // 清除缓存
        CacheUtils.deleteCommonCache();
        // 生成 siteMap.xml
        if(BaseConstants.ArticleStatus.PUBLISH == articleSave.getArticleStatus()){
            sitemapService.updateSitemap();
        }

        // 返回前端文章id
        return new ArticleInfo().setId(articleId);
    }

    /**
     * 修改文章
     *
     * @param article 文章
     * @return 文章id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ArticleInfo updateArticle(ArticleReq article) {
        // 文章id
        if (article.getId() == null) {
            log.error("[updateArticle]:文章id不能为空-{}", JSONUtil.toJsonStr(article));
            throw new BaseException("文章id不能为空");
        }
        // 查询原文章
        ArticlePO articleOld = articleDao.selectById(article.getId());
        if (articleOld == null) {
            throw new BaseException("无效的文章id");
        }
        // 检验
        checkArticleInfo(article);
        // 字数
        Integer wordCount = null;
        // Markdown内容未变动无需修改目录
        if (articleOld.getArticleMarkdown().equals(article.getArticleMarkdown())) {
            article.setArticleMarkdown(null);
            article.setArticleContent(null);
            article.setArticleCatalog(null);
        } else {
            // 解析目录
            parserArticleCatalog(article);
            wordCount = countWordsByRegex(article.getArticleMarkdown());
        }

        // 更新文章
        ArticlePO articleUpdate = BeanUtil.copyProperties(article, ArticlePO.class);
        // 字数不为null ,表示内容更新
        if(wordCount != null){
            articleUpdate.setWordCount(wordCount);
            articleUpdate.setUpdateTime(LocalDateTime.now());
        }
        articleDao.updateById(articleUpdate);

        // 删除旧标签
        articleTagRelDao.delete(new LambdaQueryWrapper<ArticleTagRelPO>().eq(ArticleTagRelPO::getArticleId, article.getId()));
        // 插入新标签
        for (Integer tagId : article.getTagIdList()) {
            articleTagRelDao.insert(new ArticleTagRelPO().setArticleId(article.getId()).setTagId(tagId));
        }

        // 删除旧文件
        articleFileRelDao.delete(new LambdaQueryWrapper<ArticleFileRelPO>().eq(ArticleFileRelPO::getArticleId, article.getId()));
        // 插入新文件
        for (String fileId : article.getFileIdList()) {
            articleFileRelDao.insert(new ArticleFileRelPO().setFileId(fileId).setArticleId(article.getId()));
        }

        // 清除缓存
        CacheUtils.deleteCommonCache();

        // 生成 siteMap.xml
        if(BaseConstants.ArticleStatus.PUBLISH == articleUpdate.getArticleStatus()){
            sitemapService.updateSitemap();
        }
        return new ArticleInfo().setId(article.getId());
    }

    /**
     * 更新文章状态
     *
     * @param article 文章
     * @return 文章id
     */
    @Override
    public ArticleInfo updateStatus(ArticleReq article) {
        // 文章id
        if (article.getId() == null) {
            throw new BaseException("文章id不能为空");
        }
        //文章状态
        if (BaseConstants.UnknowStatus.equals(BaseConstants.ArticleStatus.getArticleStatusStr(article.getArticleStatus()))) {
            throw new BaseException("无效的文章状态");
        }
        ArticlePO old = articleDao.selectById(article.getId());
        if (old == null) {
            throw new BaseException("文章id不存在");
        }

        // 更新
        articleDao.updateById(new ArticlePO().setId(article.getId())
                .setArticleStatus(article.getArticleStatus()));
        // 清除缓存
        CacheUtils.deleteCommonCache();

        // 生成 siteMap.xml
        if(BaseConstants.ArticleStatus.PUBLISH == article.getArticleStatus()){
            sitemapService.updateSitemap();
        }
        return new ArticleInfo().setId(article.getId());
    }

    /**
     * @param articleId 文章id
     * @return 通过id获取文章信息(带标签)
     */
    @Override
    public ArticleInfo getArticleWithTagsById(Integer articleId) {
        if (articleId == null) {
            throw new BaseException("文章id不能为空");
        }
        ArticlePO articlePO = articleDao.getArticleWithTagsById(new ArticlePO().setId(articleId));
        if (articlePO == null) {
            throw new BaseException("无效的文章id");
        }
        return BeanUtil.copyProperties(articlePO, ArticleInfo.class);
    }

    /**
     * 置顶
     *
     * @param topReq 置顶
     */
    @Override
    public void topArticle(TopReq topReq) {
        if (topReq.getId() == null) {
            throw new BaseException("文章id不能为空");
        }
        ArticlePO old = articleDao.selectById(topReq.getId());
        if (old == null) {
            throw new BaseException("文章id不存在");
        }
        ArticlePO articlePO = new ArticlePO()
                .setId(topReq.getId())
                .setSort(Optional.ofNullable(topReq.getSort()).orElse(System.currentTimeMillis()));
        articleDao.updateById(articlePO);

        // 生成 siteMap.xml
        if(BaseConstants.ArticleStatus.PUBLISH == old.getArticleStatus()){
            sitemapService.updateSitemap();
        }
    }

    /**
     * 删除
     *
     * @param ids 文章id列表
     */
    @Override
    public void deleteArticleByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("文章id不能为空");
        }
        // 删除
        articleDao.deleteByIds(ids);
        // 删除关联标签
        LambdaQueryWrapper<ArticleTagRelPO> lqw = new LambdaQueryWrapper<>();
        lqw.in(ArticleTagRelPO::getArticleId, ids);
        articleTagRelDao.delete(lqw);
        // 删除关联文件
        LambdaQueryWrapper<ArticleFileRelPO> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(ArticleFileRelPO::getArticleId, ids);
        articleFileRelDao.delete(lqw2);
        // 清除缓存
        CacheUtils.deleteCommonCache();
        // 生成 siteMap.xml
        sitemapService.updateSitemap();
    }

    /**
     * 更新访问量
     *
     * @param articleId 文章id
     */
    @Override
    public void updateArticleViewCount(Integer articleId) {
        // 用户信息
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        // 没有指纹的数据不统计
        if (loginUser == null || StrUtil.isBlank(loginUser.getBrowserKey())) {
            return;
        }

        ArticlePO old = articleDao.selectById(articleId);
        if (old == null) {
            throw new BaseException("文章id不存在");
        }

        // 更新文章访问量
        articleDao.updateViewCount(articleId);
    }


    /**
     * 文章基本信息校验
     *
     * @param article 文章
     */
    private void checkArticleInfo(ArticleReq article) {
        log.info("article:{}", JSONUtil.toJsonStr(article));

        // 查询分类
        CategoryPO categoryPO = categoryDao.selectById(article.getCategoryId());
        if (categoryPO == null) {
            throw new BaseException("无效的文章分类");
        }
        // 更新分类名称
        article.setCategoryName(categoryPO.getCategoryName());

        // 关联文件id
        Set<String> articleFileIdSet = new HashSet<>();
        // 封面
        String articleCoverId = article.getArticleCover();
        if (StrUtil.isNotBlank(articleCoverId)) {
            articleFileIdSet.add(articleCoverId.replace(BaseConstants.FILE_URL_PREFIX, ""));
        }

        // 提取内容文件地址
        String regex = BaseConstants.FILE_URL_PREFIX + "([a-zA-Z0-9]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(article.getArticleContent());
        while (matcher.find()) {
            articleFileIdSet.add(matcher.group(1));
        }
        // 验证fileId
        if (CollectionUtil.isNotEmpty(articleFileIdSet)) {
            List<FileStorePO> fileStorePOList = fileStoreDao.selectByIds(new ArrayList<>(articleFileIdSet));
            if (CollectionUtil.isEmpty(fileStorePOList) || fileStorePOList.size() != articleFileIdSet.size()) {
                throw new BaseException("无效的文件地址");
            }
        }
        // 更新文件id
        article.setFileIdList(articleFileIdSet);

        // 标签id集合
        List<Integer> tagIdList = article.getTagIdList();
        // 查询标签
        if (CollectionUtil.isNotEmpty(tagIdList)) {
            List<TagPO> tagPOList = tagDao.selectByIds(tagIdList);
            if (CollectionUtil.isEmpty(tagPOList) || tagPOList.size() != tagIdList.size()) {
                throw new BaseException("无效的文章标签");
            }
        } else {
            tagIdList = new ArrayList<>();
        }
        // 新增标签
        List<String> newTagNameList = article.getNewTagNameList();
        if (CollectionUtil.isNotEmpty(newTagNameList)) {
            // 标签名查询
            LambdaQueryWrapper<TagPO> lqw = new LambdaQueryWrapper<>();
            lqw.in(TagPO::getTagName, newTagNameList);
            List<TagPO> tagPOList = tagDao.selectList(lqw);
            if (CollectionUtil.isNotEmpty(tagPOList)) {
                throw new BaseException("重复的文章标签");
            }
            for (String tagName : newTagNameList) {
                // 新增标签
                TagPO tagPO = new TagPO().setTagName(tagName);
                tagDao.insert(tagPO);
                tagIdList.add(tagPO.getId());
            }
        }
        // 更新标签id
        article.setTagIdList(tagIdList);
    }

    /**
     * 提取目录
     *
     * @param article 文章
     */
    private void parserArticleCatalog(ArticleReq article) {
        // 解析文章内容
        Document document = Jsoup.parse(article.getArticleContent());
        // 节点列表
        List<NodeInfo> nodeInfoList = new ArrayList<>();

        // 获取所有标题元素
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        // 最大级别(初始6)
        int maxLevel = 6;
        for (Element heading : headings) {
            // 获取标题级别
            int level = Integer.parseInt(heading.tagName().substring(1));
            // 标题文本
            String text = heading.text();
            if (StrUtil.isBlank(text)) continue;
            // 添加跳转锚点id
            String id = text + "-" + level;
            heading.attr("id", id);
            // 更新最大级别
            if (level < maxLevel) {
                maxLevel = level;
            }
            // 添加节点信息
            nodeInfoList.add(new NodeInfo(level, id, text));
        }
        StringBuilder sb = new StringBuilder();
        // 节点信息转ul列表
        if(CollectionUtil.isNotEmpty(nodeInfoList)){
            sb.append("<ul>");
            int lastLevel = maxLevel;
            for (NodeInfo nodeInfo : nodeInfoList) {
                Integer level = nodeInfo.getLevel();
                // 获取标题文本
                if (level > lastLevel) {
                    sb.append("<ul>".repeat(Math.max(0, (level - lastLevel))));
                } else if (level < lastLevel) {
                    sb.append("</ul>".repeat(Math.max(0, (lastLevel - level))));
                }
                // 拼接跳转标签
                String li = String.format("<li><a data-target=\"%s\">%s</a></li>", nodeInfo.getId(), nodeInfo.getText());
                sb.append(li);
                lastLevel = level;
            }
            sb.append("</ul>");
        }
        // 更新文章内容和目录
        article.setArticleContent(document.body().html());
        article.setArticleCatalog(sb.toString());
    }

    /**
     * 计算文章字数
     *
     * @param markdown markdown
     * @return 字数
     */
    public static Integer countWordsByRegex(String markdown) {
        if (markdown == null || markdown.isBlank()) return 0;
        String cleaned = markdown
                // 自定义表情 :e-xxx: 算一个字符
                .replaceAll(":e-[a-zA-Z0-9_-]+:", "\u0001")
                // Unicode表情 算一个字符
                .replaceAll("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+", "\u0001")
                // 移除换行
                .replaceAll("[\\r\\n]+", "")
                // 移除图片
                .replaceAll("!\\[[^]]*]\\([^)]*\\)", "")
                // 链接保留文本
                .replaceAll("\\[([^]]+)]\\([^)]*\\)", "$1")
                // 移除代码块
                .replaceAll("(?m)^[ \\\\t]*```[^\\\\n]* $ ", "")
                // 移除HTML
                .replaceAll("<[^>]+>", "")
                // 移除格式符号
                .replaceAll("[#*_~>`\\-|]", "")
                // 移除格式符号
                .replaceAll("\\s+", " ");
        return cleaned.length();
    }

}
