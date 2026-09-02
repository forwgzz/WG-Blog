package vip.wgzz.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.bo.CommentInfo;
import vip.wgzz.blog.model.bo.CommentTree;
import vip.wgzz.blog.model.vo.*;
import vip.wgzz.blog.service.CommentService;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 10:34
 * @description 后台评论Controller
 */
@RestController
@RequestMapping("/{adminPath}/comment")
public class AdminCommentController {


    @Resource
    private CommentService commentService;

    /**
     * @param commentPageQuery 分页参数
     * @return 评论分页
     */
    @PostMapping("/page")
    public RespResult page(@RequestBody CommentPageQuery commentPageQuery) {
        IPage<CommentInfo> page = commentService.page(commentPageQuery);
        return RespResult.success(page);
    }

    /**
     * @param commentTreeReq 详情参数
     * @return 评论详情
     */
    @PostMapping("/detail")
    public RespResult detail(@RequestBody CommentTreeReq commentTreeReq) {
        CommentTree commentTree = commentService.commentDetail(commentTreeReq);
        return RespResult.success(commentTree);
    }

    /**
     * @param commentReq 更新参数
     * @return 更新状态
     */
    @PostMapping("/status")
    public RespResult status(@RequestBody CommentReq commentReq) {
        commentService.updateStatus(commentReq);
        return RespResult.success();
    }

    /**
     * @param topReq 置顶参数
     * @return 置顶
     */
    @PostMapping("/top")
    public RespResult topComment(@RequestBody TopReq topReq) {
        commentService.topComment(topReq);
        return RespResult.success();
    }

    /**
     *
     * @param ids 评论id列表
     * @return 删除评论
     */
    @PostMapping("/delete")
    public RespResult deleteComment(@RequestBody List<Integer> ids) {
        commentService.deleteCommentByIds(ids);
        return RespResult.success();
    }


    /**
     * @param commentAdd 回复内容
     * @return 管理员回复
     */
    @PostMapping("/reply")
    public RespResult reply(@RequestBody CommentAdd commentAdd) {
        commentService.adminReply(commentAdd);
        return RespResult.success();
    }

}
