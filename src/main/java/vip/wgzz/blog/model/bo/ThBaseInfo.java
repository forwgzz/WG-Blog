package vip.wgzz.blog.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wgzz
 * @date 2026/8/5 15:05
 * @description Thymeleaf基础信息
 */
@Data
@Accessors(chain = true)
public class ThBaseInfo {

    /**
     * 网站标题
     */
    private String webTitle;

    /**
     * 网站描述
     */
    private String webDescription;

    /**
     * 逐显标题
     */
    private String showTextTitle;

    /**
     * 逐显描述
     */
    private String showTextDesc;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 后台路径
     */
    private String adminPath;

    /**
     * 登陆用户信息
     */
    private LoginUserInfo userInfo;

    /**
     * 版权年份
     */
    private String copyrightYear;

    /**
     * 网站起始时间
     */
    private String webStartTime;

    /**
     * ICP备案号
     */
    private String footerICP;

    /**
     * 公安备案
     */
    private String footerPolice;

    /**
     * 网站统计
     */
    private String webStats;

    /**
     * 捐赠地址
     */
    private String donateUrl;

}
