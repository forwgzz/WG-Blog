package vip.wgzz.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.bo.LinkInfo;
import vip.wgzz.blog.model.vo.LinkReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.LinkService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 14:30
 * @description 后台友链Controller
 */
@RestController
@RequestMapping("/{adminPath}/link")
public class AdminLinkController {

    @Resource
    private LinkService linkService;

    /**
     * @param pageQuery 分页参数
     * @return 友链分页
     */
    @PostMapping("/page")
    public RespResult page(@RequestBody PageQuery pageQuery) {
        IPage<LinkInfo> page = linkService.getLinkPage(pageQuery);
        return RespResult.success(page);
    }

    /**
     * @param topReq 置顶参数
     * @return 友链置顶
     */
    @PostMapping("/top")
    public RespResult topLink(@RequestBody TopReq topReq) {
        linkService.topLink(topReq);
        return RespResult.success();
    }

    /**
     * @param link 友链信息
     * @return 新增友链
     */
    @PostMapping("/add")
    public RespResult addLink(@RequestBody LinkReq link) {
        linkService.addLink(link);
        return RespResult.success();
    }

    /**
     * @param link 友链信息
     * @return 修改友链
     */
    @PostMapping("/update")
    public RespResult updateLink(@RequestBody LinkReq link) {
        linkService.updateLink(link);
        return RespResult.success();
    }

    /**
     * @param ids 友链ids
     * @return 删除友链
     */
    @PostMapping("/delete")
    public RespResult deleteLink(@RequestBody List<Integer> ids) {
        linkService.deleteLinkByIds(ids);
        return RespResult.success();
    }
}
