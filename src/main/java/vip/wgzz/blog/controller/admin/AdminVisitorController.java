package vip.wgzz.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.bo.AccessInfo;
import vip.wgzz.blog.model.vo.AccessPageQuery;
import vip.wgzz.blog.service.AccessLogService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 16:33
 * @description 后台访客Controller
 */
@RestController
@RequestMapping("/{adminPath}/visitor")
public class AdminVisitorController {

    @Resource
    private AccessLogService accessLogService;

    /**
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @PostMapping("/page")
    public RespResult page(@RequestBody AccessPageQuery pageQuery) {
        IPage<AccessInfo> page = accessLogService.getAccessLogPage(pageQuery);
        return RespResult.success(page);
    }

    /**
     * @param ids 访客ids
     * @return 删除访客
     */
    @PostMapping("/delete")
    public RespResult deleteVisitor(@RequestBody List<Integer> ids) {
        accessLogService.deleteVisitorByIds(ids);
        return RespResult.success();
    }
}
