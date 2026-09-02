package vip.wgzz.blog.config.cache;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author wgzz
 * @date 2026/8/2 9:57
 * @description 本地缓存
 */

class LocalCacheService implements CacheService {

    // 最大256个,LFU清理最少使用
    private final Cache<String, Object> cache = CacheUtil.newLFUCache(256);

    /**
     * 获取缓存
     *
     * @param key   键
     * @param clazz 值类型
     * @return 值
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = cache.get(key);
        if (value == null) {
            return null;
        }
        try {
            return clazz.cast(value);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 设置缓存并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        long timeoutMillis = unit.toMillis(timeout);
        cache.put(key, value, timeoutMillis);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 值
     */
    @Override
    public boolean hasKey(String key) {
        return cache.containsKey(key);
    }
}
