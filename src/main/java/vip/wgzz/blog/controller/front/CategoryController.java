package vip.wgzz.blog.controller.front;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.service.CategoryService;

/**
 * @author wgzz
 * @date 2026/8/8 20:47
 * @description 前台分类Controller
 */
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 标签下拉框
     */
    @ResponseBody
    @PostMapping("/select")
    public RespResult select() {
        return RespResult.success(categoryService.getSelectList());
    }
}
