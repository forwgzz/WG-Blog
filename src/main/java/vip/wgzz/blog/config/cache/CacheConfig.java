package vip.wgzz.blog.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author wgzz
 * @date 2026/8/2 10:10
 * @description 缓存自动选择配置
 */
@Configuration
public class CacheConfig {

    /**
     * 自定义redis序列化
     *
     * @param redisConnectionFactory redis连接工厂
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.open", havingValue = "true")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 创建一个json的序列化对象
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 设置value的序列化方式json
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置key序列化方式String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置hash key序列化方式String
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash value序列化json
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置支持事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 检查 spring.data.redis.open 是否开启redis缓存
     *
     * @param redisTemplate
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.open", havingValue = "true")
    public CacheService redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheService(redisTemplate);
    }

    /**
     * redis未启用，默认走本地缓存
     */
    @Bean
    @ConditionalOnMissingBean(CacheService.class)
    public CacheService localCacheService() {
        return new LocalCacheService();
    }


}
