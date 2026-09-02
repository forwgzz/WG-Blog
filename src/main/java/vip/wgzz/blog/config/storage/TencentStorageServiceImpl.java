package vip.wgzz.blog.config.storage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/11 10:57
 * @description 腾讯云存储
 */
@Slf4j
public class TencentStorageServiceImpl implements FileStorageService {

    /**
     * 存储空间名称
     */
    private final String bucket;

    /**
     * cos客户端
     */
    private final COSClient cosClient;

    public TencentStorageServiceImpl(StorageProperties.TencentConfig config) {

        if (StrUtil.isBlank(config.getSecretId()) || StrUtil.isBlank(config.getSecretKey())) {
            throw new BaseException("腾讯云密钥未配置");
        }
        if (StrUtil.isBlank(config.getBucket())) {
            throw new BaseException("腾讯云存储空间未配置");
        }
        bucket = config.getBucket();
        if (StrUtil.isBlank(config.getRegion())) {
            throw new BaseException("腾讯云区域未配置");
        }
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        // 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        // 生成 cos 客户端
        cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * @return 存储类型
     */
    @Override
    public String getStorageType() {
        return BaseConstants.StorageType.TENCENT;
    }

    /**
     * 上传文件
     *
     * @param key           文件名
     * @param inputStream   文件流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @return 文件访问地址
     */
    @Override
    public void upload(String key, InputStream inputStream, long contentLength, String contentType) {

        // 设置文件元信息
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(contentLength);
        if (contentType != null && !contentType.isBlank()) {
            meta.setContentType(contentType);
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream, meta);
        try {
            // 上传
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            log.info("腾讯云上传文件成功: {}", JSONUtil.toJsonStr(putObjectResult));
        } catch (Exception e) {
            log.error("腾讯云上传文件失败", e);
            throw new BaseException("腾讯云上传文件失败：" + e.getMessage());
        }
    }

    /**
     * 获取预签名URL
     *
     * @param key             文件名
     * @param expireInSeconds 过期时间
     * @return 文件访问地址
     */
    @Override
    public String getPresignedUrl(String key, long expireInSeconds) {
        // 过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expireInSeconds * 1000);
        // 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.GET;
        URL url = cosClient.generatePresignedUrl(bucket, key, expirationDate, method);
        return url.toString();
    }

    /**
     * 删除文件
     *
     * @param keyList 文件路径
     */
    @Override
    public void delete(List<String> keyList) {

        if (CollectionUtil.isEmpty(keyList)) return;
        if (keyList.size() > 1000) {
            throw new BaseException("腾讯云删除文件数量超过限制");
        }

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket);
        // 转List<KeyVersion>
        List<DeleteObjectsRequest.KeyVersion> keyVersionList = keyList.stream().map(DeleteObjectsRequest.KeyVersion::new).collect(Collectors.toList());
        deleteObjectsRequest.setKeys(keyVersionList);

        try {
            DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
            // List<DeleteObjectsResult.DeletedObject> deleteObjectResultArray = deleteObjectsResult.getDeletedObjects();
            log.info("腾讯云删除文件成功: {}", JSONUtil.toJsonStr(deleteObjectsResult));
        } catch (MultiObjectDeleteException mde) {
            // 如果部分删除成功部分失败, 返回 MultiObjectDeleteException
            List<DeleteObjectsResult.DeletedObject> deleteObjects = mde.getDeletedObjects();
            List<MultiObjectDeleteException.DeleteError> deleteErrors = mde.getErrors();
            log.info("腾讯云删除文件成功: {} ----- 删除文件失败: {}", JSONUtil.toJsonStr(deleteObjects), JSONUtil.toJsonStr(deleteErrors));
        } catch (Exception e) {
            log.error("腾讯云删除文件失败", e);
            throw new BaseException("腾讯云删除文件失败：" + e.getMessage());
        }
    }


}
