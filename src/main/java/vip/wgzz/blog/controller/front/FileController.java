package vip.wgzz.blog.controller.front;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.common.util.FileTypeUtils;
import vip.wgzz.blog.service.FileService;

import java.io.IOException;

/**
 * @author wgzz
 * @date 2026/8/8 21:07
 * @description 前台文件Controller
 */
@Controller
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 文件类型下拉框
     */
    @ResponseBody
    @PostMapping("/type/select")
    public RespResult fileTypes() {
        return RespResult.success(FileTypeUtils.selectList());
    }


    /**
     * 文件预览
     *
     * @param fileId   文件ID
     * @param request  请求
     * @param response 响应
     */
    @GetMapping("/p/{fileId}")
    public void previewFile(@PathVariable("fileId") String fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileService.previewFile(fileId, request, response);
    }


}
