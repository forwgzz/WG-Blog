package vip.wgzz.blog.controller.admin;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.annotation.AccessLog;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.common.util.SysConfigCacheUtils;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.bo.ThBaseInfo;
import vip.wgzz.blog.model.vo.LoginReq;
import vip.wgzz.blog.model.vo.PasswordReq;
import vip.wgzz.blog.model.vo.UserReq;
import vip.wgzz.blog.service.LoginService;
import vip.wgzz.blog.service.StatsService;
import vip.wgzz.blog.service.UserService;

import java.io.IOException;
import java.util.Map;

/**
 * @author wgzz
 * @date 2026/8/4 21:16
 * @description 后台管理页面Controller
 */
@Slf4j
@Controller
@RequestMapping("/{adminPath}")
public class AdminPageController {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private StatsService statsService;
    @Autowired
    private UserService userService;

    @Resource
    private LoginService loginService;

    /**
     * 后台基础信息
     */
    private ThBaseInfo getBaseInfo() {
        // 登陆用户
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        // 基础信息
        return new ThBaseInfo().setUserInfo(loginUser)
                .setAvatarUrl(SysConfigEnums.AVATAR_URL.getValue())
                .setAdminPath(SysConfigCacheUtils.getAdminPath())
                .setCopyrightYear(SysConfigEnums.COPYRIGHT_YEAR.getValue())
                .setWebTitle(SysConfigEnums.WEB_TITLE.getValue());
    }


    /**
     * 后台首页(仪表盘)
     *
     * @param model 模型
     * @return
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // 基础信息
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        // 统计信息
        model.addAttribute(BaseConstants.AttributeName.STATS_INFO, statsService.getAdminStats());
        return "admin/index";
    }

    /**
     * 文章发布
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/article/edit")
    public String articleEdit(Model model) {
        // 基础信息
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/edit";
    }

    /**
     * 文章编辑
     *
     * @param articleId 文章id
     * @param model     模型
     * @return
     */
    @GetMapping("/article/edit/{articleId}")
    public String articleEdit(@PathVariable Integer articleId, Model model) {
        // 基础信息
        ThBaseInfo baseInfo = getBaseInfo();
        baseInfo.setArticleId(articleId);
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, baseInfo);
        Map<String, Object> map = model.asMap();
        return "admin/edit";
    }

    /**
     * 文章管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/article")
    public String article(Model model) {
        // 基础信息
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/article";
    }

    /**
     * 评论管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/comment")
    public String comment(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/comment";
    }

    /**
     * 分类管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/category";
    }

    /**
     * 标签管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/tag")
    public String tag(HttpServletRequest request, Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/tag";
    }

    /**
     * 文件管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/file")
    public String files(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/file";
    }

    /**
     * 友链管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/link")
    public String links(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/link";
    }

    /**
     * 访客管理
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/visitor")
    public String visitor(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/visitor";
    }

    /**
     * 系统设置
     *
     * @param model 模型
     * @return
     */
    @GetMapping("/setting")
    public String setting(Model model) {
        model.addAttribute(BaseConstants.AttributeName.TH_BASE_INFO, getBaseInfo());
        return "admin/setting";
    }

    /**
     * 登录页
     *
     * @param model 模型
     * @return
     */
    @AccessLog
    @GetMapping("/login")
    public String login(Model model) {
        //后台地址
        model.addAttribute(SysConfigEnums.ADMIN_PATH.getCode(), SysConfigCacheUtils.getAdminPath());
        //是否开启验证码
        model.addAttribute(SysConfigEnums.LOGIN_CAPTCHA_ON.getCode(), SysConfigEnums.LOGIN_CAPTCHA_ON.getBool());
        return "admin/login";
    }


    /**
     * 登录校验
     *
     * @param loginReq 登陆请求参数
     * @param request  请求
     * @param response 响应
     * @return
     */
    @AccessLog
    @PostMapping("/login")
    @ResponseBody
    public RespResult login(@Valid @RequestBody LoginReq loginReq, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 是否开启验证码
        boolean captchaOn = SysConfigEnums.LOGIN_CAPTCHA_ON.getBool();

        // 校验验证码
        if (captchaOn) {
            if (StrUtil.isBlank(loginReq.getCaptcha())) {
                throw new BaseException("验证码不能为空");
            }
            Object captchaSave = request.getSession().getAttribute(BaseConstants.AttributeName.CAPTCHA_CODE);
            if (captchaSave == null || !loginReq.getCaptcha().equalsIgnoreCase(captchaSave.toString())) {
                throw new BaseException("验证码错误");
            }
        }
        // 用户名密码校验
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getUserCode(), loginReq.getPassword()));
        } catch (Exception e) {
            log.error("用户[{}]登录失败：" + e.getMessage(), loginReq.getUserCode());
        }
        if (authenticate == null) {
            throw new BaseException("用户名或密码不正确");
        }
        // 设置管理权限
        loginService.setAdminAuth(authenticate, request, response);

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

        // 设置响应头
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        // 生成验证码
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 30, 4, 2);
        // 存入Session
        request.getSession().setAttribute(BaseConstants.AttributeName.CAPTCHA_CODE, shearCaptcha.getCode());
        log.info("生成验证码：" + shearCaptcha.getCode());
        // 输出图片流
        shearCaptcha.write(response.getOutputStream());
        response.getOutputStream().close();
    }


    /**
     * @param passwordReq 密码
     * @return 修改密码
     */
    @PostMapping("/password")
    @ResponseBody
    public RespResult password(@RequestBody PasswordReq passwordReq) {
        userService.updatePassword(passwordReq);
        return RespResult.success();
    }

    /**
     * @return 获取登录用户信息
     */
    @GetMapping("/user")
    @ResponseBody
    public RespResult getLoginUser() {
        return RespResult.success(LoginUserUtils.getLoginUser());
    }

    /**
     * @param userReq 用户信息
     * @return 修改用户信息
     */
    @PostMapping("/user")
    @ResponseBody
    public RespResult updateLoginUser(@RequestBody UserReq userReq) {
        userService.updateUserInfo(userReq);
        return RespResult.success();
    }
}
