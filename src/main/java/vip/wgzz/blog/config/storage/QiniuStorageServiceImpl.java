package vip.wgzz.blog.config.storage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/11 10:55
 * @description 七牛云存储
 */
@Slf4j
public class QiniuStorageServiceImpl implements FileStorageService {

    /**
     * 七牛云授权
     */
    private final Auth auth;

    /**
     * 七牛云区域
     */
    private final Region region;

    /**
     * 七牛云仓库
     */
    private final String bucket;

    /**
     * 七牛云访问域名
     */
    private final String domain;


    public QiniuStorageServiceImpl(StorageProperties.QiniuConfig config) {
        if (StrUtil.isBlank(config.getAccessKey()) || StrUtil.isBlank(config.getSecretKey())) {
            throw new BaseException("七牛云密钥未配置");
        }
        if (StrUtil.isBlank(config.getBucket())) {
            throw new BaseException("七牛云仓库未配置");
        }
        this.bucket = config.getBucket();
        if (StrUtil.isBlank(config.getDomain()) || !config.getDomain().startsWith("http") ){
            throw new BaseException("七牛云访问域名配置有误");
        }
        this.domain = config.getDomain();

        if (StrUtil.isBlank(config.getRegion())) {
            this.region = Region.autoRegion();
        } else {
            this.region = Region.createWithRegionId(config.getRegion());
        }
        this.auth = Auth.create(config.getAccessKey(), config.getSecretKey());
    }

    /**
     * @return 存储类型
     */
    @Override
    public String getStorageType() {
        return BaseConstants.StorageType.QINIU;
    }

    /**
     * 上传文件
     *
     * @param key      文件名
     * @param inputStream   文件流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @return 文件访问地址
     */
    @Override
    public void upload(String key, InputStream inputStream, long contentLength, String contentType) {
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = Configuration.create(region);
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        UploadManager uploadManager = new UploadManager(cfg);
        // 获取上传token
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(inputStream, key, upToken, null, contentType);
            //解析上传成功的结果
            log.info("上传成功：{}", response.bodyString());
        } catch (QiniuException ex) {
            log.error("七牛云上次失败：{}", ex.response, ex);
            throw new BaseException("七牛云上传失败：" + ex.response);
        }
    }

    /**
     * 获取预签名URL
     *
     * @param key        文件名
     * @param expireInSeconds 过期时间
     * @return 文件访问地址
     */
    @Override
    public String getPresignedUrl(String key, long expireInSeconds) {
        // 转码
        String enKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        String publicUrl = String.format("%s/%s", domain, enKey);
        // 拼接授权
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }


    /**
     * 删除文件
     *
     * @param keyList 文件路径
     */
    @Override
    public void delete(List<String> keyList) {
        try {
            if(CollectionUtil.isEmpty(keyList)){
                return;
            }
            if(keyList.size()>1000){
                throw new BaseException("七牛云删除文件数量超过限制");
            }
            // 基础配置
            Configuration cfg = Configuration.create(region);
            BucketManager bucketManager = new BucketManager(auth, cfg);

            // 批量删除
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(bucket, keyList.toArray(new String[0]));
            Response response = bucketManager.batch(batchOperations);
            // BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            log.info("七牛云删除成功：{}", response.bodyString());
        }catch (QiniuException ex){
            log.info("七牛云删除失败：{}",ex.response.toString());
            throw new BaseException("七牛云删除失败：" + ex.response);
        }
    }
}
