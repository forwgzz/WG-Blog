package vip.wgzz.blog.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.dao.SysConfigDao;
import vip.wgzz.blog.model.po.SysConfigPO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/2 17:44
 * @description 系统设置缓存工具类
 */
@Slf4j
@Component
public class SysConfigCacheUtils {

    /**
     * 默认后台路径
     */
    public static String DEFAULT_ADMIN_PATH = "/admin";

    /**
     * 配置信息缓存
     */
    private volatile static Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>();

    private static SysConfigDao sysConfigDao;
    /**
     * 初始化ConfigDao
     */
    @Resource
    public void setSysConfigDao(SysConfigDao sysConfigDao) {
        SysConfigCacheUtils.sysConfigDao = sysConfigDao;
    }

    /**
     * 更新所有配置信息
     */
    public static void updateAllSysConfig() {
        CONFIG_MAP.clear();
        // 查询有效配置信息
        List<SysConfigPO> ydConfigPOS = sysConfigDao.selectList(new LambdaQueryWrapper<>(new SysConfigPO().setDataStatus(BaseConstants.YesOrNo.YES)));
        if (CollectionUtil.isEmpty(ydConfigPOS)) {
            return;
        }
        // 存入缓存
        CONFIG_MAP = ydConfigPOS.stream().collect(Collectors.toMap(SysConfigPO::getConfigCode, SysConfigPO::getConfigValue, (k1, k2) -> k1));
    }

    /**
     * @param code 编码
     * @return 获取配置值String
     */
    public static String getValue(String code) {
        if (CONFIG_MAP.isEmpty()) {
            updateAllSysConfig();
        }
        return CONFIG_MAP.get(code);
    }

    /**
     * @param code 编码
     * @return 获取配置值Bool
     */
    public static boolean getBool(String code) {
        String config = getValue(code);
        if (config == null) return false;
        return BaseConstants.YesOrNoStr.YES.equals(config);

    }

    /**
     * 更新配置值
     *
     * @param code  编码
     * @param value 值
     */
    public static void updateValue(String code, String value) {
        //清除缓存
        CONFIG_MAP.clear();

        //更新条件
        LambdaUpdateWrapper<SysConfigPO> luw = new LambdaUpdateWrapper<>();
        luw.eq(SysConfigPO::getConfigCode, code).set(SysConfigPO::getConfigValue, value);
        //更新数据库
        int update = sysConfigDao.update(null, luw);
        if (update != 1) {
            throw new BaseException("更新配置编码:" + code + " 失败");
        }
    }

    /**
     * @return 后台管理路径
     */
    public static String getAdminPath() {
        return Optional.ofNullable(SysConfigEnums.ADMIN_PATH.getValue()).orElse(DEFAULT_ADMIN_PATH);
    }

}
