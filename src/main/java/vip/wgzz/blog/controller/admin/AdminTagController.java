package vip.wgzz.blog.controller.admin;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TagReq;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.TagService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/9 18:11
 * @description 后台标签Controller
 */
@RestController
@RequestMapping("/{adminPath}/tag")
public class AdminTagController {

    @Resource
    TagService tagService;

    /**
     * @param pageQuery 查询条件
     * @return 标签分页查询
     */
    @PostMapping("/page")
    public RespResult getTagPage(@RequestBody PageQuery pageQuery) {
        return RespResult.success(tagService.getTagPage(pageQuery));
    }

    /**
     * @param tag 标签参数
     * @return 添加标签
     */
    @PostMapping("/add")
    public RespResult getTagPage(@RequestBody TagReq tag) {
        tagService.addTag(tag);
        return RespResult.success();
    }


    /**
     * @param tag 标签参数
     * @return 修改标签
     */
    @PostMapping("/update")
    public RespResult updateTag(@RequestBody TagReq tag) {
        tagService.updateTag(tag);
        return RespResult.success();
    }


    /**
     * @param ids 标签id
     * @return 删除标签
     */
    @PostMapping("/delete")
    public RespResult deleteTags(@RequestBody List<Integer> ids) {
        tagService.deleteTagByIds(ids);
        return RespResult.success();
    }

    /**
     * @param topReq 置顶参数
     * @return 置顶标签
     */
    @PostMapping("/top")
    public RespResult topTag(@RequestBody TopReq topReq) {
        tagService.topTag(topReq);
        return RespResult.success();
    }
}
