package vip.wgzz.blog.common.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.model.bo.IpTestInfo;
import vip.wgzz.blog.model.bo.LocationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author wgzz
 * @date 2026/8/3 14:23
 * @description ip地址查询工具类
 */
@Slf4j
public class IpLocationUtils {

    /**
     * @param ip ip
     * @return 获取ip地址信息
     */
    public static LocationInfo getLocation(String ip) {
        log.info("开始查询ip地址:{}", ip);
        if (StrUtil.isBlank(ip)) {
            log.error("ip为空");
            return LocationInfo.error(ip, "ip为空");
        }
        // 缓存
        LocationInfo locationInfo = CacheUtils.get(ip, LocationInfo.class);
        if (locationInfo != null) {
            log.info("API缓存ip:{} ==== info{}", ip, JSONUtil.toJsonStr(locationInfo));
            return locationInfo;
        }
        // api查询
        String apiUrl = SysConfigEnums.IP_LOCATION_API.getValue();
        if (StrUtil.isEmpty(apiUrl)) {
            log.error("Ip地址查询API缺失");
            return LocationInfo.error(ip, "api缺失");
        }
        String template = SysConfigEnums.IP_LOCATION_API_TEMPLATE.getValue();
        if (StrUtil.isEmpty(template)) {
            log.error("Ip地址解析template缺失");
            return LocationInfo.error(ip, "template缺失");
        }

        // ipv6检测
        if (!SysConfigEnums.IP_LOCATION_API_IPV6.getBool() && Validator.isIpv6(ip)) {
            log.error("当前api不支持ipv6:{}", apiUrl);
            return LocationInfo.error(ip, "当前api不支持ipv6");
        }
        String res = HttpUtil.get(apiUrl.replace("{ip}", ip));
        if (StrUtil.isBlank(res)) {
            log.error("API查询异常");
            return LocationInfo.error(ip, "API查询异常");
        }
        // 解析数据
        locationInfo = parseJsonData(ip, res, template);

        if(StrUtil.isBlank(locationInfo.getErrorMsg())){
            // 缓存6小时
            CacheUtils.put(ip, locationInfo, 6, TimeUnit.HOURS);
        }
        log.info("API查询ip:{} ==== info{}", ip, JSONUtil.toJsonStr(locationInfo));
        return locationInfo;
    }

    /**
     * @param request 请求
     * @return 获取ip
     */
    public static String getIp(HttpServletRequest request) {
        log.info("requestHeader:{}", JakartaServletUtil.getHeaderMap(request));
        // 腾讯EdgeOne 真实ip
        String ip = JakartaServletUtil.getHeaderIgnoreCase(request, "EO-CONNECTING-IP");
        if (StrUtil.isNotBlank(ip) && !NetUtil.isUnknown(ip)) {
            return ip;
        }
        ip = JakartaServletUtil.getClientIP(request);
        return NetUtil.isUnknown(ip) ? "" : ip;
    }

    /**
     * 根据模板解析json数据
     *
     * @param ip       ip
     * @param jsonData 数据
     * @param template 模板
     * @return LocationInfo
     */
    public static LocationInfo parseJsonData(String ip, String jsonData, String template) {
        try {
            // 转json对象
            JSONObject dataJson = JSONUtil.parseObj(jsonData);
            JSONObject templateJson = JSONUtil.parseObj(template);

            //转换数据收集
            JSONObject locationInfoJson = new JSONObject();
            // 遍历模板
            templateJson.forEach((key, templateKey) -> {
                if (templateKey != null && templateKey.toString().startsWith("$.")) {
                    //获取实际数据
                    Object relValue = dataJson.getByPath(templateKey.toString().trim());
                    if (relValue != null) {
                        //赋值
                        locationInfoJson.set(key, relValue.toString());
                    }
                }
            });
            // 判断数据是否为空
            if (locationInfoJson.isEmpty()) {
                log.error("数据与模板不匹配-jsonData:{}---template:{}", jsonData, template);
                return LocationInfo.error(ip, "数据与模板不匹配:" + jsonData);
            }
            // 转换实体对象
            return JSONUtil.toBean(locationInfoJson, LocationInfo.class);
        } catch (Exception e) {
            log.error("解析json数据异常-jsonData:{}---template:{}", jsonData, template, e);
            return LocationInfo.error(ip, "解析json数据异常:" + jsonData);
        }

    }

    private static final String TEST_IPV4 = "114.114.114.114";
    private static final String TEST_IPV6 = "2400:3200::1";

    /**
     * api测试
     *
     * @return List<IpTestInfo>
     */
    public static List<IpTestInfo> testIp() {
        List<IpTestInfo> resultList = new ArrayList<>();
        // api查询
        String apiUrl = SysConfigEnums.IP_LOCATION_API.getValue();
        if (StrUtil.isEmpty(apiUrl)) {
            log.error("Ip地址查询API缺失");
            throw new BaseException("Ip地址查询API缺失");
        }
        String template = SysConfigEnums.IP_LOCATION_API_TEMPLATE.getValue();
        if (StrUtil.isEmpty(template)) {
            log.error("Ip地址解析template缺失");
            throw new BaseException("template缺失");
        }
        String res = HttpUtil.get(apiUrl.replace("{ip}", TEST_IPV4));
        LocationInfo ipv4TestInfo = parseJsonData(TEST_IPV4, res, template);
        resultList.add(new IpTestInfo(TEST_IPV4, res, ipv4TestInfo));
        if (SysConfigEnums.IP_LOCATION_API_IPV6.getBool()) {
            String resIpv6 = HttpUtil.get(apiUrl.replace("{ip}", TEST_IPV6));
            LocationInfo ipv6TestInfo = parseJsonData(TEST_IPV6, resIpv6, template);
            resultList.add(new IpTestInfo(TEST_IPV6, resIpv6, ipv6TestInfo));
        }
        return resultList;
    }
}
