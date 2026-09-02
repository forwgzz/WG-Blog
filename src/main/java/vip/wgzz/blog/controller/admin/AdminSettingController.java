package vip.wgzz.blog.controller.admin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.IpLocationUtils;
import vip.wgzz.blog.common.util.SysConfigCacheUtils;
import vip.wgzz.blog.config.LocalDateFormatConfig;
import vip.wgzz.blog.dao.ArticleDao;
import vip.wgzz.blog.model.bo.SysConfig;
import vip.wgzz.blog.model.po.ArticlePO;
import vip.wgzz.blog.model.vo.SysConfigReq;
import vip.wgzz.blog.service.ArticleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author wgzz
 * @date 2026/8/16 19:35
 * @description 后台系统设置Controller
 */
@RestController
@RequestMapping("/{adminPath}/setting")
public class AdminSettingController {

    @Resource
    private ArticleDao articleDao;

    /**
     * @return 系统配置
     */
    @PostMapping("/config")
    public RespResult getConfig() {
        List<SysConfig> sysConfigList = Arrays.stream(SysConfigEnums.values())
                .map(config -> new SysConfig(config.getCode(), config.getName(), config.getValue(), config.getDefValue()))
                .toList();
        return RespResult.success(sysConfigList);
    }

    /**
     * @param sysConfigReq 配置项
     * @return 更新配置
     */
    @PostMapping("/update")
    public RespResult updateConfig(@RequestBody @Valid SysConfigReq sysConfigReq) {
        SysConfigEnums now = SysConfigEnums.getSysConfigEnum(sysConfigReq.getConfigCode());
        if (now == null) {
            throw new BaseException("配置键不存在");
        }
        String value = sysConfigReq.getConfigValue();
        switch (now) {
            // 后端管理路径
            case ADMIN_PATH -> {
                Pattern pattern = Pattern.compile("^/\\w{2,10}$");
                if (!pattern.matcher(value).matches()) {
                    throw new BaseException(String.format("%s格式必须是'/'+'2-10位字母，数字或_'", SysConfigEnums.ADMIN_PATH.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.ADMIN_PATH.getCode(), value);
            }
            // 后端管理路径
            case ABOUT_REL_ARTICLE_ID -> {
                try {
                    int id = Integer.parseInt(value);
                    ArticlePO articlePO = articleDao.selectById(id);
                    if (articlePO == null) {
                        throw new BaseException("无效文章ID");
                    }
                    if (BaseConstants.ArticleStatus.PUBLISH != articlePO.getArticleStatus()) {
                        throw new BaseException("当前文章未发布");
                    }
                } catch (Exception e) {
                    throw new BaseException(String.format("%s格式必须是数字", SysConfigEnums.ABOUT_REL_ARTICLE_ID.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.ABOUT_REL_ARTICLE_ID.getCode(), value);
            }
            // 开启登录验证码
            case LOGIN_CAPTCHA_ON -> {
                if (!BaseConstants.YesOrNoStr.YES.equals(value) && !BaseConstants.YesOrNoStr.NO.equals(value))
                    throw new BaseException(String.format("%s值必须是%s或%s", SysConfigEnums.LOGIN_CAPTCHA_ON.getName(), BaseConstants.YesOrNoStr.YES, BaseConstants.YesOrNoStr.NO));
                SysConfigCacheUtils.updateValue(SysConfigEnums.LOGIN_CAPTCHA_ON.getCode(), value);
                break;
            }
            // 网站标题
            case WEB_TITLE -> {
                if (value.length() > 50) {
                    throw new BaseException(String.format("%s长度必须在50以内", SysConfigEnums.WEB_TITLE.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.WEB_TITLE.getCode(), value);
            }
            // 网站描述
            case WEB_DESCRIPTION -> {
                if (value.length() > 200) {
                    throw new BaseException(String.format("%s长度必须在200以内", SysConfigEnums.WEB_DESCRIPTION.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.WEB_DESCRIPTION.getCode(), value);
            }
            // 逐显标题
            case SHOW_TEXT_TITLE -> {
                if (value.length() > 100) {
                    throw new BaseException(String.format("%s长度必须在100以内", SysConfigEnums.SHOW_TEXT_TITLE.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.SHOW_TEXT_TITLE.getCode(), value);
            }
            // 逐显描述
            case SHOW_TEXT_DESC -> {
                if (value.length() > 200) {
                    throw new BaseException(String.format("%s长度必须在200以内", SysConfigEnums.SHOW_TEXT_DESC.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.SHOW_TEXT_DESC.getCode(), value);
            }
            // 网站起始时间
            case WEB_START_TIME -> {
                try {
                    if (StrUtil.isNotBlank(value))
                        LocalDateTime.parse(value, DateTimeFormatter.ofPattern(LocalDateFormatConfig.DATE_TIME_PATTERN));
                } catch (Exception e) {
                    throw new BaseException(String.format("%s格式必须是%s", SysConfigEnums.WEB_START_TIME.getName(), LocalDateFormatConfig.DATE_TIME_PATTERN));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.WEB_START_TIME.getCode(), value);
            }
            // 版权年份 2022 或者 2022-2023
            case COPYRIGHT_YEAR -> {
                if (StrUtil.isNotBlank(value)) {
                    Pattern pattern = Pattern.compile("^(\\d{4})(?:-(\\d{4}))?$");
                    if (!pattern.matcher(value).matches()) {
                        throw new BaseException(String.format("%s格式必须是%s", SysConfigEnums.COPYRIGHT_YEAR.getName(), "2022或2022-2023"));
                    }
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.COPYRIGHT_YEAR.getCode(), value);
            }
            // 网站备案号
            case FOOTER_ICP -> {
                if (value.length() > 30) {
                    throw new BaseException(String.format("%s长度必须在30以内", SysConfigEnums.FOOTER_ICP.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.FOOTER_ICP.getCode(), value);
            }
            // 网站备案号
            case FOOTER_POLICE -> {
                if (value.length() > 30) {
                    throw new BaseException(String.format("%s长度必须在30以内", SysConfigEnums.FOOTER_POLICE.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.FOOTER_POLICE.getCode(), value);
            }
            // 头像地址
            case AVATAR_URL -> {
                if (value.length() > 100) {
                    throw new BaseException(String.format("%s长度必须在100以内", SysConfigEnums.AVATAR_URL.getName()));
                }
                if (!value.startsWith(BaseConstants.FILE_URL_PREFIX) && !value.startsWith("/public/")) {
                    throw new BaseException(String.format("%s必须以%s、/public/开头", SysConfigEnums.AVATAR_URL.getName(), BaseConstants.FILE_URL_PREFIX));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.AVATAR_URL.getCode(), value);
            }
            // IP地址查询API
            case IP_LOCATION_API -> {
                if (value.length() > 150) {
                    throw new BaseException(String.format("%s长度必须在150以内", SysConfigEnums.IP_LOCATION_API.getName()));
                }
                if (!value.contains("{ip}") || (!value.startsWith("http://") && !value.startsWith("https://"))) {
                    throw new BaseException(String.format("%s必须以http://或https://开头,且包含{ip}", SysConfigEnums.IP_LOCATION_API.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.IP_LOCATION_API.getCode(), value);
            }
            // 是否支持IPV6
            case IP_LOCATION_API_IPV6 -> {
                if (!BaseConstants.YesOrNoStr.YES.equals(value) && !BaseConstants.YesOrNoStr.NO.equals(value))
                    throw new BaseException(String.format("%s值必须是%s或%s", SysConfigEnums.IP_LOCATION_API_IPV6.getName(), BaseConstants.YesOrNoStr.YES, BaseConstants.YesOrNoStr.NO));
                SysConfigCacheUtils.updateValue(SysConfigEnums.IP_LOCATION_API_IPV6.getCode(), value);
            }
            // IP地址查询API模板
            case IP_LOCATION_API_TEMPLATE -> {
                if (value.length() > 1000) {
                    throw new BaseException(String.format("%s长度必须在1000以内", SysConfigEnums.IP_LOCATION_API_TEMPLATE.getName()));
                }
                try {
                    JSONObject jsonObject = JSONUtil.parseObj(value);
                } catch (Exception e) {
                    throw new BaseException(String.format("%s格式必须是json", SysConfigEnums.IP_LOCATION_API_TEMPLATE.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.IP_LOCATION_API_TEMPLATE.getCode(), value);
            }
            case WEB_HOST -> {
                if (value.length() > 50) {
                    throw new BaseException(String.format("%s长度必须在50以内", SysConfigEnums.WEB_HOST.getName()));
                }
                if (!value.startsWith("http://") && !value.startsWith("https://")) {
                    throw new BaseException(String.format("%s必须以http://或https://开头", SysConfigEnums.WEB_HOST.getName()));
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.WEB_HOST.getCode(), value);
            }
            // 站长统计
            case WEB_STATS -> {
                if (StrUtil.isNotBlank(value)) {
                    if (value.length() > 1000) {
                        throw new BaseException(String.format("%s长度必须在1000以内", SysConfigEnums.WEB_STATS.getName()));
                    }
                    if (!value.startsWith("<script>") && !value.endsWith("</script>")) {
                        throw new BaseException(String.format("%s必须以<script>开头，</script>结尾", SysConfigEnums.WEB_STATS.getName()));
                    }
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.WEB_STATS.getCode(), value);
            }
            // 捐赠图片地址
            case DONATE_URL -> {
                if(StrUtil.isNotBlank(value)){
                    if (value.length() > 100) {
                        throw new BaseException(String.format("%s长度必须在100以内", SysConfigEnums.DONATE_URL.getName()));
                    }
                    if (!value.startsWith(BaseConstants.FILE_URL_PREFIX)) {
                        throw new BaseException(String.format("%s必须以%s开头", SysConfigEnums.DONATE_URL.getName(), BaseConstants.FILE_URL_PREFIX));
                    }
                }
                SysConfigCacheUtils.updateValue(SysConfigEnums.DONATE_URL.getCode(), value);
            }
            default -> {
                return RespResult.error("配置键不存在");
            }
        }
        return RespResult.success();
    }

    /**
     * @return 测试IP地址查询
     */
    @PostMapping("/iptest")
    public RespResult ipTest() {
        return RespResult.success(IpLocationUtils.testIp());
    }
}
