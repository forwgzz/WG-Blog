package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.CacheUtils;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.dao.*;
import vip.wgzz.blog.model.bo.*;
import vip.wgzz.blog.model.po.*;
import vip.wgzz.blog.service.CategoryService;
import vip.wgzz.blog.service.StatsService;
import vip.wgzz.blog.service.TagService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/8 10:09
 * @description 数据统计Service实现
 */
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Resource
    private ArticleDao articleDao;

    @Resource
    private CommentDao commentDao;

    @Resource
    private TagService tagService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private AccessLogDao accessLogDao;

    @Resource
    private LinkDao linkDao;


    /**
     * @return 获取后台数据统计信息
     */
    @Override
    public AdminStatsInfo getAdminStats() {
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || BaseConstants.UserType.ADMIN != loginUser.getUserType()) {
            log.error("没有权限,请登录后再试");
            throw new BaseException("没有权限,请登录后再试");
        }
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // 昨天 00:00:00
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        // 昨天 23:59:59
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);
        // 当天 00:00:00
        LocalDateTime today = LocalDate.now().atStartOfDay();
        // 查缓存
        AdminStatsInfo adminStatsInfo = CacheUtils.get(BaseConstants.CacheKeys.ADMIN_STATS_INFO, AdminStatsInfo.class);
        if (adminStatsInfo == null) {
            adminStatsInfo = new AdminStatsInfo();

            // 文章统计查询
            Long articleTotal = articleDao.selectCount(new LambdaQueryWrapper<ArticlePO>().eq(ArticlePO::getDataStatus, BaseConstants.YesOrNo.YES));
            Long articleToday = articleDao.selectCount(new LambdaQueryWrapper<ArticlePO>().eq(ArticlePO::getDataStatus, BaseConstants.YesOrNo.YES).ge(ArticlePO::getCreateTime, today));
            Long articleYesterday = articleDao.selectCount(new LambdaQueryWrapper<ArticlePO>().eq(ArticlePO::getDataStatus, BaseConstants.YesOrNo.YES).ge(ArticlePO::getCreateTime, startOfYesterday).le(ArticlePO::getCreateTime, endOfYesterday));
            adminStatsInfo.setArticleTotal(articleTotal)
                    .setArticleToday(articleToday)
                    .setArticleYesterday(articleYesterday);

            // 评论统计查询
            Long commentTotal = commentDao.selectCount(new LambdaQueryWrapper<CommentPO>().eq(CommentPO::getDataStatus, BaseConstants.YesOrNo.YES));
            Long commentToday = commentDao.selectCount(new LambdaQueryWrapper<CommentPO>().eq(CommentPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(CommentPO::getCreateTime, today));
            Long commentYesterday = commentDao.selectCount(new LambdaQueryWrapper<CommentPO>().eq(CommentPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(CommentPO::getCreateTime, startOfYesterday).le(CommentPO::getCreateTime, endOfYesterday));
            adminStatsInfo.setCommentTotal(commentTotal)
                    .setCommentToday(commentToday)
                    .setCommentYesterday(commentYesterday);

            // 标签统计查询
            Long tagTotal = tagService.count(new LambdaQueryWrapper<TagPO>().eq(TagPO::getDataStatus, BaseConstants.YesOrNo.YES));
            Long tagToday = tagService.count(new LambdaQueryWrapper<TagPO>().eq(TagPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(TagPO::getCreateTime, today));
            Long tagYesterday = tagService.count(new LambdaQueryWrapper<TagPO>().eq(TagPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(TagPO::getCreateTime, startOfYesterday).le(TagPO::getCreateTime, endOfYesterday));
            adminStatsInfo.setTagTotal(tagTotal)
                    .setTagToday(tagToday)
                    .setTagYesterday(tagYesterday);

            // 分类统计查询
            Long categoryTotal = categoryService.count(new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getDataStatus, BaseConstants.YesOrNo.YES));
            Long categoryToday = categoryService.count(new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(CategoryPO::getCreateTime, today));
            Long categoryYesterday = categoryService.count(new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(CategoryPO::getCreateTime, startOfYesterday).le(CategoryPO::getCreateTime, endOfYesterday));
            adminStatsInfo.setCategoryTotal(categoryTotal)
                    .setCategoryToday(categoryToday)
                    .setCategoryYesterday(categoryYesterday);

            // 友链统计查询
            Long linkTotal = linkDao.selectCount(new LambdaQueryWrapper<LinkPO>().eq(LinkPO::getDataStatus, BaseConstants.YesOrNo.YES));
            Long linkToday = linkDao.selectCount(new LambdaQueryWrapper<LinkPO>().eq(LinkPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(LinkPO::getCreateTime, today));
            Long linkYesterday = linkDao.selectCount(new LambdaQueryWrapper<LinkPO>().eq(LinkPO::getDataStatus, BaseConstants.YesOrNo.YES).ge(LinkPO::getCreateTime, startOfYesterday).le(LinkPO::getCreateTime, endOfYesterday));
            adminStatsInfo.setLinkTotal(linkTotal)
                    .setLinkToday(linkToday)
                    .setLinkYesterday(linkYesterday);

            // 当前缓存
            CacheUtils.putTodayTime(BaseConstants.CacheKeys.ADMIN_STATS_INFO, adminStatsInfo);
        }

        // 访客统计查询 实时查询
        Long viewTotal = accessLogDao.selectCount(null);
        Long viewToday = accessLogDao.selectCount(new LambdaQueryWrapper<AccessLogPO>().ge(AccessLogPO::getAccessTime, today));
        Long viewYesterday = accessLogDao.selectCount(new LambdaQueryWrapper<AccessLogPO>().ge(AccessLogPO::getAccessTime, startOfYesterday).le(AccessLogPO::getAccessTime, endOfYesterday));
        adminStatsInfo.setViewTotal(viewTotal)
                .setViewToday(viewToday)
                .setViewYesterday(viewYesterday);

        return adminStatsInfo;
    }

    /**
     * @return 获取前台数据统计信息
     */
    @Override
    public FrontStatsInfo getFrontStats() {
        // 查缓存
        FrontStatsInfo frontStatsInfo = CacheUtils.get(BaseConstants.CacheKeys.FRONT_STATS_INFO, FrontStatsInfo.class);
        if (frontStatsInfo == null) {
            frontStatsInfo = new FrontStatsInfo();
            // 文章统计查询
            LambdaQueryWrapper<ArticlePO> articleLqw = new LambdaQueryWrapper<>(new ArticlePO()
                    .setDataStatus(BaseConstants.YesOrNo.YES)
                    .setArticleStatus(BaseConstants.ArticleStatus.PUBLISH));
            List<ArticlePO> articleList = articleDao.selectList(articleLqw);
            frontStatsInfo.setArticleTotal((long) articleList.size());
            // 总字数
            long wordCount = articleList.stream().mapToLong(ArticlePO::getWordCount).sum();
            frontStatsInfo.setWordTotal(wordCount);

            // 标签统计查询
            List<SelectInfo> tagSelectList = tagService.getSelectList();
            frontStatsInfo.setTagTotal((long) tagSelectList.size());
            frontStatsInfo.setTagList(tagSelectList);

            // 分类统计查询
            List<SelectInfo> categorySelectList = categoryService.getSelectList();
            frontStatsInfo.setCategoryTotal((long) categorySelectList.size());
            frontStatsInfo.setCategoryList(categorySelectList);

            // 归档 统计
            frontStatsInfo.setArchiveList(articleDao.getArchiveInfo());
            // 当前缓存
            CacheUtils.putTodayTime(BaseConstants.CacheKeys.FRONT_STATS_INFO, frontStatsInfo);
        }
        // 总访客数 实时查询
        frontStatsInfo.setVisitorTotal(accessLogDao.getVisitorCount());
        // 总访问量
        frontStatsInfo.setViewTotal(accessLogDao.selectCount(null));
        return frontStatsInfo;
    }

    /**
     * @return 获取归档文章
     */
    @Override
    public ArchiveInfo getArchiveArticles() {
        // 所有发布的文章
        List<ArticlePO> articlePOS = articleDao.getPublishArticleWithTagsList();
        if (CollectionUtil.isEmpty(articlePOS)) {
            return null;
        }
        // 置顶文章
        List<ArticleInfo> topArticles = new ArrayList<>();
        // 年-{月-ArticleList} 倒序
        TreeMap<Integer, TreeMap<Integer, List<ArticleInfo>>> yearMap = new TreeMap<>(Collections.reverseOrder());

        for (ArticlePO po : articlePOS) {
            ArticleInfo info = BeanUtil.copyProperties(po, ArticleInfo.class);
            // sort不等于0置顶
            if (info.getSort() != null && info.getSort() != 0) {
                topArticles.add(info);
            }
            // 拆分年月
            LocalDateTime createTime = info.getCreateTime();
            int year = createTime.getYear();
            int month = createTime.getMonthValue();
            // 筛选年月
            yearMap.computeIfAbsent(year, k -> new TreeMap<>(Collections.reverseOrder()))
                    .computeIfAbsent(month, k -> new ArrayList<>())
                    .add(info);

        }

        // 转换 ArchiveYear
        List<ArchiveYear> archiveYears = yearMap.entrySet().stream()
                .map(yearEntry ->
                        new ArchiveYear(
                                // 年
                                yearEntry.getKey(),
                                // ArchiveMonth
                                yearEntry.getValue().entrySet().stream()
                                        .map(monthEntry -> new ArchiveMonth(
                                                yearEntry.getKey(),
                                                monthEntry.getKey(),
                                                monthEntry.getValue()))
                                        .collect(Collectors.toList()),
                                // 文章总数
                                yearEntry.getValue().values().stream()
                                        .mapToInt(List::size)
                                        .sum()
                        ))
                .toList();
        // 倒序
        if (topArticles.size() > 2) {
            topArticles.sort((a, b) -> b.getSort().compareTo(a.getSort()));
        }
        return new ArchiveInfo(topArticles, archiveYears);
    }

}
