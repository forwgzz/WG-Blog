package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.CacheUtils;
import vip.wgzz.blog.common.util.FileTypeUtils;
import vip.wgzz.blog.common.util.IdUtils;
import vip.wgzz.blog.common.util.ValidatorUtils;
import vip.wgzz.blog.config.storage.FileStorageService;
import vip.wgzz.blog.dao.ArticleFileRelDao;
import vip.wgzz.blog.dao.FileStoreDao;
import vip.wgzz.blog.model.bo.FileStoreInfo;
import vip.wgzz.blog.model.po.ArticleFileRelPO;
import vip.wgzz.blog.model.po.FileStorePO;
import vip.wgzz.blog.model.vo.FilePageQuery;
import vip.wgzz.blog.model.vo.FileReq;
import vip.wgzz.blog.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author wgzz
 * @date 2026/8/12 17:55
 * @description 文件Service实现
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileStoreDao fileStoreDao;

    @Resource
    private ArticleFileRelDao articleFileRelDao;

    @Resource
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件存储信息
     */
    @Override
    public FileStoreInfo uploadFile(MultipartFile file) {
        // 文件校验
        FileStorePO fileStorePO = fileCheck(file);
        try {
            // 上传文件
            fileStorageService.upload(fileStorePO.getFilePath(), file.getInputStream(), file.getSize(), fileStorePO.getFileContentType());
            // 保存数据库
            fileStoreDao.insert(fileStorePO);
            return BeanUtil.copyProperties(fileStorePO, FileStoreInfo.class);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException("上传失败", e);
        }
    }

    /**
     * 分页查询文件
     *
     * @param pageQuery 查询条件
     * @return 文件分页信息
     */
    @Override
    public IPage<FileStoreInfo> page(FilePageQuery pageQuery) {
        // 查询条件
        LambdaQueryWrapper<FileStorePO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(FileStorePO::getDataStatus, BaseConstants.YesOrNo.YES);
        lqw.like(StrUtil.isNotBlank(pageQuery.getSearchStr()), FileStorePO::getFileName, pageQuery.getSearchStr());
        // 类型
        lqw.in(CollectionUtil.isNotEmpty(pageQuery.getFileTypes()), FileStorePO::getFileType, pageQuery.getFileTypes());
        // 排序
        lqw.orderByDesc(FileStorePO::getSort).orderByDesc(FileStorePO::getCreateTime);
        // 分页查询
        Page<FileStorePO> page = fileStoreDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        return page.convert(fileStorePO -> BeanUtil.copyProperties(fileStorePO, FileStoreInfo.class));
    }

    /**
     * 删除
     *
     * @param ids 文件ID集合
     */
    @Override
    public void deleteFileByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("文件id不能为空");
        }
        try {
            // 查询引用
            Long count = articleFileRelDao.selectCount(new LambdaQueryWrapper<ArticleFileRelPO>().in(ArticleFileRelPO::getFileId, ids));
            if (count > 0) {
                throw new BaseException("当前文件正在使用中,不能删除");
            }
            // 详情
            List<FileStorePO> fileStorePOList = fileStoreDao.selectByIds(ids);
            if (CollectionUtil.isEmpty(fileStorePOList)) {
                return;
            }
            // 获取路径
            List<String> filePathList = fileStorePOList.stream().map(FileStorePO::getFilePath).toList();

            // 删除存储的文件
            fileStorageService.delete(filePathList);

            // 删除数据库
            fileStoreDao.deleteByIds(ids);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除文件失败:{}", ids, e);
            throw new BaseException("删除文件失败", e);
        }
    }

    /**
     * 置顶
     *
     * @param fileReq 请求参数
     */
    @Override
    public void topFile(FileReq fileReq) {
        if (StrUtil.isBlank(fileReq.getId())) {
            throw new BaseException("文件id不能为空");
        }
        FileStorePO fileStorePO = new FileStorePO()
                .setId(fileReq.getId())
                .setSort(Optional.ofNullable(fileReq.getSort()).orElse(System.currentTimeMillis()));
        int i = fileStoreDao.updateById(fileStorePO);
        if (i != 1) {
            throw new BaseException("文件id不存在");
        }
    }

    /**
     * 更新 文件名
     *
     * @param fileReq 请求参数
     */
    @Override
    public void updateFile(FileReq fileReq) {
        // 校验
        ValidatorUtils.validate(fileReq);

        //更新
        FileStorePO updateFile = new FileStorePO()
                .setId(fileReq.getId())
                .setFileName(fileReq.getFileName());
        fileStoreDao.updateById(updateFile);
    }

    /**
     * 文件预览
     *
     * @param fileId   文件ID
     * @param request  请求
     * @param response 响应
     */
    @Override
    public void previewFile(String fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StrUtil.isBlank(fileId)) {
            log.error("文件ID为空");
            // 返回404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 查询数据库
        FileStorePO fileStorePO = fileStoreDao.selectById(fileId);
        if (fileStorePO == null) {
            log.error("数据库文件【{}】不存在", fileId);
            // 返回404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 本地存储 返回Stream流
        if (BaseConstants.StorageType.LOCAL.equals(fileStorageService.getStorageType())) {
            String path = fileStorageService.getPresignedUrl(fileStorePO.getFilePath(), 0);
            if (StrUtil.isBlank(path)) {
                log.error("本地文件【{}】不存在", fileStorePO.getFilePath());
                // 返回404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            File file = new File(path);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                response.setHeader("Accept-Ranges", "bytes");
                response.setContentType(fileStorePO.getFileContentType());
                //返回文件名
                if (StrUtil.isBlank(request.getHeader("Range"))) {
                    /*response.setHeader("Content-Disposition", "attachment; filename=\"" + URLUtil.encode(fileStorePO.getFileName()) + "\"");*/
                }
                // 图片缓存
                if (FileTypeUtils.IMAGE.equals(fileStorePO.getFileType())) {
                    response.setHeader("Cache-Control", "public, max-age=31536000");
                }
                response.setContentLengthLong(file.length());
                // 输出流
                StreamUtils.copy(inputStream, response.getOutputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BaseException(e.getMessage());
            }
            return;
        }
        // 云存储url（一小时过期）
        String presignedUrl = CacheUtils.getStr(fileId);
        if (StrUtil.isBlank(presignedUrl)) {
            presignedUrl = fileStorageService.getPresignedUrl(fileStorePO.getFilePath(), 60 * 60);
            if (StrUtil.isBlank(presignedUrl)) {
                log.error("云存储文件【{}】不存在", fileStorePO.getFilePath());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            CacheUtils.put(fileId, presignedUrl, 60 * 60, TimeUnit.SECONDS);
        }
        // 重定向
        response.sendRedirect(presignedUrl);
    }

    /**
     * 文件信息校验
     *
     * @param file
     * @return
     */
    @SneakyThrows
    private FileStorePO fileCheck(MultipartFile file) {
        // 文件名检验
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            throw new BaseException("文件名不能为空");
        }
        if (fileName.length() > 100) {
            throw new BaseException("文件名长度不能超过100");
        }
        String suffix = FileNameUtil.extName(fileName);
        if (StrUtil.isBlank(suffix)) {
            throw new BaseException("文件名异常");
        }
        // 文件大小限制50m以下
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new BaseException("文件大小不能超过50M");
        }

        // 内容类型
        String contentType = FileTypeUtils.getContentType(file);
        if (StrUtil.isBlank(contentType)) {
            throw new BaseException("文件内容异常");
        }

        // MD5校验
        String md5 = DigestUtils.md5DigestAsHex(file.getBytes());
        FileStorePO sameMD5 = fileStoreDao.selectOne(new LambdaQueryWrapper<>(new FileStorePO().setFileMd5(md5)));
        if (sameMD5 != null) {
            throw new BaseException("文件与[" + sameMD5.getFileName() + "]重复");
        }

        // 文件路径
        String filePath = String.format("%s/%s", LocalDate.now(), fileName);
        // 查询文件路径是否重复
        FileStorePO samePath = fileStoreDao.selectOne(new LambdaQueryWrapper<>(new FileStorePO().setFilePath(filePath)));
        if (samePath != null) {
            throw new BaseException("文件路径重复，请修改文件名后重试");
        }

        //以id为前缀
        return new FileStorePO()
                .setId(IdUtils.shortNanoId())
                .setFileName(fileName)
                .setFileOldName(fileName)
                .setFilePath(filePath)
                .setFileType(FileTypeUtils.getFileType(suffix))
                .setFileContentType(contentType)
                .setFileMd5(md5);

    }
}
