package vip.wgzz.blog.controller.front;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.service.SitemapService;

/**
 * @author wgzz
 * @date 2026/8/22 23:21
 * @description 站点地图
 */
@RestController
public class SitemapController {

    @Autowired
    private SitemapService sitemapService;

    /**
     * 获取站点地图
     */
    @GetMapping("/sitemap.xml")
    public ResponseEntity<Resource> sitemap() {
        // TODO 如果文章数超过5万条 要修改多文件返回
        Resource sitemapFile = sitemapService.getSitemapFile();
        if (sitemapFile == null){
            throw BaseException.notFind();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(sitemapFile);
    }

    /**
     * 获取 robots.txt
     */
    @GetMapping(value = "/robots.txt", produces = "text/plain;charset=UTF-8")
    public String robots() {
        String sitemapUrl = sitemapService.getSitemapUrl();
        return "User-agent: *\n" +
                "Disallow: /public\n" +
                "Disallow: /file\n" +
                "Disallow: /search\n" +
                "Disallow: /*?page=\n" +
                "Allow: /\n" +
                (StrUtil.isNotBlank(sitemapUrl) ? "Sitemap: " + sitemapUrl : "");
    }
}
