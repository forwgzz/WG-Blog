package vip.wgzz.blog.controller.admin;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.vo.CategoryReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.CategoryService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/9 10:26
 * @description 后台分类Controller
 */
@RestController
@RequestMapping("/{adminPath}/category")
public class AdminCategoryController {


    @Resource
    CategoryService categoryService;


    /**
     * @param pageQuery 分页参数
     * @return 分类分页查询
     */
    @PostMapping("/page")
    public RespResult getCategoryPage(@RequestBody PageQuery pageQuery) {
        return RespResult.success(categoryService.getCategoryPage(pageQuery));
    }

    /**
     * @param categoryReq 分类参数
     * @return 添加分类
     */
    @PostMapping("/add")
    public RespResult getCategoryPage(@RequestBody CategoryReq categoryReq) {
        categoryService.addCategory(categoryReq);
        return RespResult.success();
    }


    /**
     * @param categoryReq 分类参数
     * @return 修改分类
     */
    @PostMapping("/update")
    public RespResult updateCategoryPage(@RequestBody CategoryReq categoryReq) {
        categoryService.updateCategory(categoryReq);
        return RespResult.success();
    }


    /**
     * @param ids 分类id列表
     * @return 删除分类
     */
    @PostMapping("/delete")
    public RespResult deleteCategoryPage(@RequestBody List<Integer> ids) {
        categoryService.deleteCategoryByIds(ids);
        return RespResult.success();
    }

    /**
     * @param topReq 置顶参数
     * @return 置顶分类
     */
    @PostMapping("/top")
    public RespResult topCategoryPage(@RequestBody TopReq topReq) {
        categoryService.topCategory(topReq);
        return RespResult.success();
    }
}
