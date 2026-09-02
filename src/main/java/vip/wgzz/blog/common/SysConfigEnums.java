package vip.wgzz.blog.common;

import lombok.Getter;
import vip.wgzz.blog.common.util.SysConfigCacheUtils;

/**
 * @author wgzz
 * @date 2026/8/2 17:04
 * @description 系统设置枚举
 */
@Getter
public enum SysConfigEnums {

    WEB_HOST("webHost","网站域名","http://localhost"),
    ADMIN_PATH("adminPath","后台管理路径","/admin"),
    AVATAR_URL("avatarUrl","头像地址","/public/pic/tx.png"),
    WEB_TITLE("webTitle","网站标题","WG日记"),
    WEB_DESCRIPTION("webDescription","网站描述","喜欢计算机的咸鱼一条，经常抽出空余时间学习各种语言编程以及知识，虽然懂得不多，但是这是我最喜欢的爱好吧。"),
    SHOW_TEXT_TITLE("showTextTitle","逐显标题","WG's Blog"),
    SHOW_TEXT_DESC("showTextDesc","逐显描述","Accept yourself as ordinary and do your best to excel."),
    LOGIN_CAPTCHA_ON("loginCaptchaOn","登录验证码","1"),
    ABOUT_REL_ARTICLE_ID("aboutRelArticleId","关于页面ID","请选择对应文章ID"),

    WEB_START_TIME("webStartTime","网站起始时间","2022-01-01 00:00:00"),
    COPYRIGHT_YEAR("copyrightYear","版权年份","2022-2023"),
    FOOTER_ICP("footerICP","网站ICP备案","湘ICP备 XXXXXXXXXX号"),
    FOOTER_POLICE("footerPolice","网站公安备案","湘公安备 XXXXXXXXXX号"),


    IP_LOCATION_API("ipLocationApi","IP地址查询API","https://ip9.com.cn/get?ip={ip}"),
    IP_LOCATION_API_IPV6("ipLocationApiIpv6","支持IPV6","0"),
    IP_LOCATION_API_TEMPLATE("ipLocationApiTemplate","Json解析模板","{\"nation\":\"$.data.country\",\"province\":\"$.data.prov\",\"city\":\"$.data.city\",\"district\":\"$.data.area\",\"isp\":\"$.data.isp\",\"ip\":\"$.data.ip\"}"),

    WEB_STATS("webStats","站长统计脚本","<script>百度(https://tongji.baidu.com/)或其他统计脚本</script>"),

    DONATE_URL("donateUrl","捐赠图片地址",""),
    ;


    /**
     * 配置code
     */
    private final String code;

    /**
     * 配置名称
     */
    private final String name;

    /**
     * 默认值
     */
    private final String defValue;


    SysConfigEnums(String key, String name, String defValue) {
        this.code = key;
        this.name = name;
        this.defValue = defValue;
    }

    public static SysConfigEnums getSysConfigEnum(String code){
        for (SysConfigEnums item : SysConfigEnums.values()) {
            if(item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @return 获取配置值String
     */
    public String getValue() {
        return SysConfigCacheUtils.getValue(code);
    }

    /**
     * @return 获取配置值Bool
     */
    public boolean getBool() {
        return SysConfigCacheUtils.getBool(code);
    }
}
