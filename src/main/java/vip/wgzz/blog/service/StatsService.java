package vip.wgzz.blog.service;

import vip.wgzz.blog.model.bo.AdminStatsInfo;
import vip.wgzz.blog.model.bo.ArchiveInfo;
import vip.wgzz.blog.model.bo.FrontStatsInfo;

/**
 * @author wgzz
 * @date 2026/8/8 10:07
 * @description 数据统计Service
 */
public interface StatsService {

    /**
     * @return 获取后台数据统计信息
     */
    AdminStatsInfo getAdminStats();

    /**
     * @return 获取前台数据统计信息
     */
    FrontStatsInfo getFrontStats();

    /**
     * @return 获取归档文章
     */
    ArchiveInfo getArchiveArticles();
}
