package vip.wgzz.blog.config.storage;

import java.io.InputStream;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/10 20:59
 * @description 文件存储接口
 */
public interface FileStorageService {

    /**
     * @return 存储类型
     */
    String getStorageType();

    /**
     * 上传文件
     *
     * @param key      文件路径
     * @param inputStream   文件流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @return 文件访问地址
     */
    void upload(String key, InputStream inputStream, long contentLength, String contentType);

    /**
     * 获取预签名URL
     *
     * @param key   文件路径
     * @param expireInSeconds 过期时间
     * @return 文件访问地址
     */
    String getPresignedUrl(String key, long expireInSeconds);

    /**
     * 删除文件
     *
     * @param keyList 文件路径
     */
    void delete(List<String> keyList);

}
