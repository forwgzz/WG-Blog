package vip.wgzz.blog.config.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author wgzz
 * @date 2026/8/2 9:52
 * @description 缓存服务接口
 */
public interface CacheService {

    /**
     * 获取缓存
     * @param key 键
     * @param clazz 值类型
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     */
    void put(String key, Object value);

    /**
     * 设置缓存并指定过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void put(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 删除缓存
     * @param key 键
     */
    void delete(String key);

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean hasKey(String key);
}
