package vip.wgzz.blog.config.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/10 21:25
 * @description 本地存储实现
 */
@Slf4j
public class LocalStorageServiceImpl implements FileStorageService {

    private final String path;

    public LocalStorageServiceImpl(StorageProperties.LocalConfig config) {

        this.path = config.getPath();
        if (StrUtil.isBlank(this.path)) {
            throw new BaseException("本地存储path不能为空");
        }
    }

    /**
     * @return 存储类型
     */
    @Override
    public String getStorageType() {
        return BaseConstants.StorageType.LOCAL;
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
        try {
            // 完整文件地址
            File saveFile = new File(this.path + key);
            // 创建目录
            FileUtil.mkParentDirs(saveFile);
            // 保存
            FileUtil.writeFromStream(inputStream, saveFile);
        } catch (Exception e) {
            log.error("本地保存文件失败", e);
            throw new BaseException("本地保存文件失败", e);
        }
    }

    /**
     * 获取预签名URL
     *
     * @param key   文件路径
     * @param expireInSeconds 过期时间
     * @return 文件访问地址
     */
    @Override
    public String getPresignedUrl(String key, long expireInSeconds) {
        String filePath = this.path + key;
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在：" + key);
            return null;
        }
        return filePath;
    }

    /**
     * 删除文件
     *
     * @param keyList 文件路径
     */
    @Override
    public void delete(List<String> keyList) {
        try {
            for (String key : keyList) {
                File file = new File(this.path + key);
                if (!file.exists()) {
                   continue;
                }
                file.delete();
            }
        } catch (Exception e) {
            log.error("本地删除文件失败：" + e.getMessage(), e);
            throw new BaseException("本地删除文件失败",e);
        }
    }




}
