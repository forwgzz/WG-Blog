package vip.wgzz.blog.controller.admin;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.vo.FilePageQuery;
import vip.wgzz.blog.model.vo.FileReq;
import vip.wgzz.blog.service.FileService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/12 22:25
 * @description 后台文件Controller
 */
@RestController
@RequestMapping("/{adminPath}/file")
public class AdminFileController {

    @Resource
    private FileService fileService;

    /**
     * @param file 文件
     * @return 文件上传
     */
    @PostMapping("/upload")
    public RespResult uploadFile(@RequestParam("file") MultipartFile file) {
        return RespResult.success(fileService.uploadFile(file));
    }


    /**
     * @param pageQuery 分页查询参数
     * @return 分页查询
     */
    @PostMapping("/page")
    public RespResult page(@RequestBody FilePageQuery pageQuery) {
        return RespResult.success(fileService.page(pageQuery));
    }


    /**
     * @param fileIds 文件ID列表
     * @return 删除
     */
    @PostMapping("/delete")
    public RespResult deleteFiles(@RequestBody List<String> fileIds) {
        fileService.deleteFileByIds(fileIds);
        return RespResult.success();
    }

    /**
     * @param fileReq 文件请求参数
     * @return 置顶
     */
    @PostMapping("/top")
    public RespResult topFile(@RequestBody FileReq fileReq) {
        fileService.topFile(fileReq);
        return RespResult.success();
    }

    /**
     * @param fileReq 文件请求参数
     * @return 更新
     */
    @PostMapping("/update")
    public RespResult updateFile(@RequestBody FileReq fileReq) {
        fileService.updateFile(fileReq);
        return RespResult.success();
    }

}