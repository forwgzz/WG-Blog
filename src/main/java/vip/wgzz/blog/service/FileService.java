package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import vip.wgzz.blog.model.bo.FileStoreInfo;
import vip.wgzz.blog.model.vo.FilePageQuery;
import vip.wgzz.blog.model.vo.FileReq;

import java.io.IOException;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/12 17:32
 * @description 文件Service
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件存储信息
     */
    FileStoreInfo uploadFile(MultipartFile file);

    /**
     * 分页查询文件
     *
     * @param pageQuery 查询条件
     * @return 文件分页信息
     */
    IPage<FileStoreInfo> page(FilePageQuery pageQuery);

    /**
     * 删除
     *
     * @param ids 文件ID集合
     */
    void deleteFileByIds(List<String> ids);

    /**
     * 置顶
     *
     * @param fileReq 请求参数
     */
    void topFile(FileReq fileReq);

    /**
     * 更新
     *
     * @param fileReq 请求参数
     */
    void updateFile(FileReq fileReq);

    /**
     * 文件预览
     *
     * @param fileId   文件ID
     * @param request  请求
     * @param response 响应
     */
    void previewFile(String fileId, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
