package vip.wgzz.blog.service;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.dao.ArticleDao;
import vip.wgzz.blog.model.po.ArticlePO;

import java.io.File;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/22 22:31
 * @description Sitemap工具类
 */
@Slf4j
@Service
public class SitemapService {

    @Autowired
    private ArticleDao articleDao;

    /**
     * sitemap路径
     */
    @Value("${sitemap-path:./file/}")
    private String sitemapPath;

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    /**
     * 更新sitemap
     */
    @Async("taskExecutor")
    public void updateSitemap() {
        try {
            // 查询所有发布文章
            List<ArticlePO> articlePOList = articleDao.getPublishArticleWithTagsList();
            WebSitemapGenerator generator = WebSitemapGenerator.builder(SysConfigEnums.WEB_HOST.getValue(), new File(sitemapPath)).build();
            // 首页
            generator.addUrl(new WebSitemapUrl.Options(SysConfigEnums.WEB_HOST.getValue())
                    .lastMod(new Date())
                    .changeFreq(ChangeFreq.DAILY)
                    .priority(1.0)
                    .build());
            // 归档
            generator.addUrl(new WebSitemapUrl.Options(SysConfigEnums.WEB_HOST.getValue() + "/archive")
                    .lastMod(new Date())
                    .changeFreq(ChangeFreq.DAILY)
                    .priority(0.7)
                    .build());
            // 关于
            generator.addUrl(new WebSitemapUrl.Options(SysConfigEnums.WEB_HOST.getValue() + "/about")
                    .priority(0.6)
                    .build());
            // 友链
            generator.addUrl(new WebSitemapUrl.Options(SysConfigEnums.WEB_HOST.getValue() + "/link")
                    .priority(0.7)
                    .build());
            for (ArticlePO articlePO : articlePOList) {
                generator.addUrl(new WebSitemapUrl.Options(SysConfigEnums.WEB_HOST.getValue() + BaseConstants.ARTICLE_URL_PREFIX + articlePO.getId())
                        .lastMod(Date.from(articlePO.getUpdateTime().atZone(ZONE).toInstant()))
                        .changeFreq(ChangeFreq.MONTHLY)
                        .priority(articlePO.getSort() != 0 ? 0.6 : 0.4)
                        .build());
            }
            generator.write();
        } catch (Exception e) {
            log.error("更新sitemap出错", e);
        }
    }

    /**
     * 获取sitemap文件
     */
    public Resource getSitemapFile() {
        File file = new File(sitemapPath + "sitemap.xml");
        if (!file.exists()){
            return null;
        }
        return new FileSystemResource(file);
    }

    /**
     * 获取sitemap地址
     */
    public String getSitemapUrl(){
        File file = new File(sitemapPath + "sitemap.xml");
        if (!file.exists()){
            return null;
        }
        return SysConfigEnums.WEB_HOST.getValue() + "/sitemap.xml";
    }
}
