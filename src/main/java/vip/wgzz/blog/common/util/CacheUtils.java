package vip.wgzz.blog.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.config.cache.CacheService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wgzz
 * @date 2026/8/2 10:15
 * @description 通用缓存工具类
 */
@Component
public class CacheUtils {

    /**
     * 默认缓存时间 180 分钟
     */
    private static final long DEFAULT_TIMEOUT = 180;

    private static CacheService cacheService;

    /**
     * CacheService注入
     */
    @Autowired
    public CacheUtils(CacheService cacheService) {
        CacheUtils.cacheService = cacheService;
    }

    /**
     * 读取字符串
     *
     * @param key 键
     * @return 值
     */
    public static String getStr(String key) {
        return get(key, String.class);
    }

    /**
     * 读取指定集合类
     *
     * @param key   键
     * @param clazz 值类型
     * @return 值
     */
    public static <T> List<T> getList(String key, Class<T> clazz) {
        Object object = cacheService.get(key, Object.class);
        if (object instanceof List) {
            return (List<T>) object;
        }
        return null;
    }

    /**
     * 获取缓存
     *
     * @param key   键
     * @param clazz 值类型
     * @return 值
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object value = cacheService.get(key, clazz);
        return value == null ? null : clazz.cast(value);
    }

    /**
     * 默认缓存时间
     *
     * @param key   键
     * @param value 值
     */
    public static void putDefaultTime(String key, Object value) {
        cacheService.put(key, value, DEFAULT_TIMEOUT, TimeUnit.MINUTES);
    }

    /**
     * 当天有效
     *
     * @param key   键
     * @param value 值
     */
    public static void putTodayTime(String key, Object value) {
        cacheService.put(key, value, getSecondsEndOfDay(), TimeUnit.SECONDS);
    }

    /**
     * 设置缓存永不过期（本地缓存数据重启丢失）
     *
     * @param key   键
     * @param value 值
     */
    public static void put(String key, Object value) {
        cacheService.put(key, value);
    }


    /**
     * 设置缓存并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public static void put(String key, Object value, long timeout, TimeUnit unit) {
        cacheService.put(key, value, timeout, unit);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    public static void delete(String key) {
        cacheService.delete(key);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 值
     */
    public static boolean hasKey(String key) {
        return cacheService.hasKey(key);
    }


    /**
     * 获取距离当天结束的剩余秒数
     */
    public static long getSecondsEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX); // 23:59:59.999999999
        long seconds = Duration.between(now, endOfDay).getSeconds();
        // 最少1s
        if (seconds <= 0) seconds = 1;
        return seconds;
    }

    /**
     * 删除常用缓存
     */
    public static void deleteCommonCache() {
        // 统计
        delete(BaseConstants.CacheKeys.ADMIN_STATS_INFO);
        delete(BaseConstants.CacheKeys.FRONT_STATS_INFO);
        // 分类
        delete(BaseConstants.CacheKeys.CATEGORY_SELECT);
        // 标签
        delete(BaseConstants.CacheKeys.TAG_SELECT);
    }
}
