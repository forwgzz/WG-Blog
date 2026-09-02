package vip.wgzz.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import vip.wgzz.blog.config.storage.StorageProperties;

/**
 * @author wgzz
 * @date 2026/8/2 9:50
 * @description 启动类
 */
@SpringBootApplication
@MapperScan("vip.wgzz.blog.dao")
@EnableConfigurationProperties(StorageProperties.class)
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
