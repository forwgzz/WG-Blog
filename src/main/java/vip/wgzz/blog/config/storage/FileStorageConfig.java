package vip.wgzz.blog.config.storage;

import lombok.extern.slf4j.Slf4j;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;


/**
 * @author wgzz
 * @date 2026/8/11 11:18
 * @description 存储工厂
 */
@Slf4j
@Configuration
public class FileStorageConfig {

    @Bean
    public FileStorageService getFileStoreService(StorageProperties storageProperties) {
        return switch (storageProperties.getStorageType()) {
            case BaseConstants.StorageType.LOCAL -> new LocalStorageServiceImpl(storageProperties.getLocal());
            case BaseConstants.StorageType.QINIU -> new QiniuStorageServiceImpl(storageProperties.getQiniu());
            case BaseConstants.StorageType.TENCENT -> new TencentStorageServiceImpl(storageProperties.getTencent());
            default -> throw new BaseException("未知参数");
        };
    }

}
