package vip.wgzz.blog.config.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import vip.wgzz.blog.common.BaseConstants;

/**
 * @author wgzz
 * @date 2026/8/10 20:51
 * @description 文件存储配置
 */
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * 存储类型
     */
    private String storageType = BaseConstants.StorageType.LOCAL;

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 七牛云存储配置
     */
    private QiniuConfig qiniu = new QiniuConfig();

    /**
     * 腾讯云存储配置
     */
    private TencentConfig tencent = new TencentConfig();

    @Data
    public static class LocalConfig {
        private String path;
    }

    @Data
    public static class QiniuConfig {
        private String region;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
    }

    @Data
    public static class TencentConfig {
        private String region;
        private String secretId;
        private String secretKey;
        private String bucket;
    }


}
