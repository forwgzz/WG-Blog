package vip.wgzz.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import vip.wgzz.blog.model.bo.CommentInfo;
import vip.wgzz.blog.model.bo.CommentTree;
import vip.wgzz.blog.model.bo.CommentTreePageQuery;
import vip.wgzz.blog.model.vo.*;

import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/15 14:00
 * @description 评论Service
 */
public interface CommentService {

    /**
     * @param pageQuery 评论分页查询
     * @return 评论分页
     */
    IPage<CommentInfo> page(CommentPageQuery pageQuery);

    /**
     * 更新状态
     *
     * @param commentReq 评论请求
     */
    void updateStatus(CommentReq commentReq);

    /**
     *
     *
     * @param commentReq 评论请求
     * @return 评论详情
     */
    CommentTree commentDetail(CommentTreeReq commentReq);

    /**
     * @param pageQuery 评论树分页查询
     * @return 评论树分页
     */
    IPage<CommentTree> treePage(CommentTreePageQuery pageQuery);



    /**
     * 新增评论
     *
     * @param commentAdd 评论
     * @param request    请求
     */
    void addComment(CommentAdd commentAdd, HttpServletRequest request);


    /**
     * 管理员回复
     *
     * @param commentAdd 评论
     */
    void adminReply(CommentAdd commentAdd);

    /**
     * 置顶评论
     *
     * @param topReq 置顶参数
     */
    void topComment(TopReq topReq);

    /**
     * 删除评论
     *
     * @param ids 评论id列表
     */
    void deleteCommentByIds(List<Integer> ids);
}
