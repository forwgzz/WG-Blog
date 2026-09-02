package vip.wgzz.blog.controller.front;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.common.annotation.AccessLog;
import vip.wgzz.blog.model.bo.CommentTree;
import vip.wgzz.blog.model.bo.CommentTreePageQuery;
import vip.wgzz.blog.model.vo.CommentAdd;
import vip.wgzz.blog.service.CommentService;

import java.io.IOException;

/**
 * @author wgzz
 * @date 2026/8/19 16:48
 * @description 前台评论Controller
 */
@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * @param pageQuery 评论分页查询参数
     * @return 评论分页
     */
    @AccessLog
    @PostMapping("/page")
    public RespResult page(@RequestBody CommentTreePageQuery pageQuery) {
        // 每次只允许8个
        pageQuery.setSize(8);
        IPage<CommentTree> commentTreeIPage = commentService.treePage(pageQuery);
        return RespResult.success(commentTreeIPage);
    }

    /**
     * @param comment 评论
     * @return 新增评论
     */
    @AccessLog
    @PostMapping("/add")
    public RespResult add(@RequestBody CommentAdd comment, HttpServletRequest request) {
        commentService.addComment(comment,request);
        return RespResult.success();
    }

    /**
     * 获取验证码
     *
     * @param request  请求
     * @param response 响应
     */
    @AccessLog
    @GetMapping("/getCaptcha")
    public void getVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置响应头
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        // 自定义纯数字的验证码（随机4位数字，可重复）
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        lineCaptcha.setGenerator(randomGenerator);
        // 重新生成code
        lineCaptcha.createCode();

        //存入Session
        request.getSession().setAttribute(BaseConstants.AttributeName.CAPTCHA_CODE, lineCaptcha.getCode());
        log.info("生成评论验证码：" + lineCaptcha.getCode());
        //输出图片流
        lineCaptcha.write(response.getOutputStream());
        response.getOutputStream().close();
    }
}
