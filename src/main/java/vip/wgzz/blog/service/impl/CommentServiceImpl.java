package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.SysConfigEnums;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.*;
import vip.wgzz.blog.dao.ArticleDao;
import vip.wgzz.blog.dao.CommentDao;
import vip.wgzz.blog.model.bo.CommentInfo;
import vip.wgzz.blog.model.bo.CommentTree;
import vip.wgzz.blog.model.bo.CommentTreePageQuery;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.ArticlePO;
import vip.wgzz.blog.model.po.CommentPO;
import vip.wgzz.blog.model.vo.*;
import vip.wgzz.blog.service.AccessLogService;
import vip.wgzz.blog.service.CommentService;
import vip.wgzz.blog.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/15 14:12
 * @description 评论Service实现
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentDao commentDao;

    @Resource
    private ArticleDao articleDao;

    @Resource
    private UserService userService;

    @Resource
    private AccessLogService accessLogService;


    /**
     * @param pageQuery 评论分页查询
     * @return 评论分页
     */
    @Override
    public IPage<CommentInfo> page(CommentPageQuery pageQuery) {
        LambdaQueryWrapper<CommentPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(pageQuery.getSearchStr()), CommentPO::getCommentContent, pageQuery.getSearchStr());
        // 评论状态
        lqw.eq(pageQuery.getCommentStatus() != null, CommentPO::getCommentStatus, pageQuery.getCommentStatus());
        // 排序
        lqw.orderByDesc(CommentPO::getSort).orderByDesc(CommentPO::getId);
        //
        IPage<CommentPO> page = commentDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        // 查询文章标题
        Map<Integer, String> articleMap = new HashMap<>();
        articleMap.put(BaseConstants.LinkArticle.ID, BaseConstants.LinkArticle.TITLE);
        if (CollectionUtil.isNotEmpty(page.getRecords())) {
            Set<Integer> articleIds = page.getRecords().stream().map(CommentPO::getArticleId).collect(Collectors.toSet());
            List<ArticlePO> articleList = articleDao.selectByIds(articleIds);
            articleList.forEach(articlePO -> articleMap.put(articlePO.getId(), articlePO.getArticleTitle()));
        }
        return page.convert(commentPO ->
                BeanUtil.copyProperties(commentPO, CommentInfo.class).setArticleTitle(articleMap.get(commentPO.getArticleId()))
        );
    }

    /**
     * 更新状态
     *
     * @param commentReq 评论请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(CommentReq commentReq) {
        // 校验
        ValidatorUtils.validate(commentReq);
        // 查询原始数据
        List<CommentPO> oldCommentList = commentDao.selectList(new LambdaQueryWrapper<CommentPO>().in(CommentPO::getId, commentReq.getIdList()));
        if (!Objects.equals(commentReq.getIdList().size(), oldCommentList.size())) {
            log.error("含有无效评论id:{}", JSONUtil.toJsonStr(commentReq));
            throw new BaseException("含有无效评论id");
        }
        // 更新
        LambdaUpdateWrapper<CommentPO> luw = new LambdaUpdateWrapper<>();
        luw.in(CommentPO::getId, commentReq.getIdList());
        luw.set(CommentPO::getCommentStatus, commentReq.getCommentStatus());
        commentDao.update(luw);

        // 获取文章id
        Set<Integer> articleIdSet = oldCommentList.stream().map(CommentPO::getArticleId).collect(Collectors.toSet());
        // 更新评论数
        articleDao.updateCommentCountByIds(articleIdSet);

        // 非审批通过直接返回
        if (!Objects.equals(commentReq.getCommentStatus(), BaseConstants.CommentStatus.PASS)) {
            return;
        }
        // 通知成功的评论
        List<Integer> notifySuccessList = new ArrayList<>();
        // 获取需要通知的被回复评论
        Set<Integer> toCommentIdSet = new HashSet<>();
        for (CommentPO commentPO : oldCommentList) {
            // 排除 已经通知过 或者顶级评论
            if (Objects.equals(BaseConstants.YesOrNo.YES, commentPO.getNotifySuccess()) || Objects.equals(commentPO.getRootCommentId(), BaseConstants.TOP_COMMENT_ID)) {
                continue;
            }
            notifySuccessList.add(commentPO.getId());
            toCommentIdSet.add(commentPO.getToCommentId());
        }
        if (CollectionUtil.isEmpty(toCommentIdSet)) {
            return;
        }

        // 被回复评论
        List<CommentPO> toCommentList = commentDao.selectByIds(toCommentIdSet);
        // 登录用户
        LoginUserInfo loginUserInfo = userService.getAdminUser();
        for (CommentPO commentPO : toCommentList) {
            // 排除自己 或者 不需要通知
            if (Objects.equals(commentPO.getUserId(), loginUserInfo.getUserId()) || !Objects.equals(commentPO.getEmailNotify(), BaseConstants.YesOrNo.YES)) {
                continue;
            }
            emailNotify(commentPO);
        }

        // 更新通知状态
        LambdaUpdateWrapper<CommentPO> luwSuccess = new LambdaUpdateWrapper<>();
        luwSuccess.in(CommentPO::getId, notifySuccessList);
        luwSuccess.set(CommentPO::getNotifySuccess, BaseConstants.YesOrNo.YES);
        commentDao.update(luwSuccess);
    }

    /**
     *
     *
     * @param commentReq 评论请求
     * @return 评论详情
     */
    @Override
    public CommentTree commentDetail(CommentTreeReq commentReq) {
        // 校验
        ValidatorUtils.validate(commentReq);
        // 查询文章
        ArticlePO articlePO = getArticle(commentReq.getArticleId());

        // 查询当前评论
        CommentPO nowComment = commentDao.selectById(commentReq.getNowCommentId());
        if (nowComment == null || !Objects.equals(nowComment.getArticleId(), articlePO.getId())) {
            log.error("无效评论id:{}", JSONUtil.toJsonStr(commentReq));
            throw new BaseException("无效评论id");
        }

        // 查询该顶级评论下所有评论
        LambdaQueryWrapper<CommentPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CommentPO::getArticleId, commentReq.getArticleId());
        if (nowComment.getRootCommentId() == BaseConstants.TOP_COMMENT_ID) {
            lqw.eq(CommentPO::getRootCommentId, nowComment.getId())
                    // 包含自身
                    .or().eq(CommentPO::getId, nowComment.getId());
        } else {
            lqw.eq(CommentPO::getRootCommentId, nowComment.getRootCommentId())
                    // 包含顶级
                    .or().eq(CommentPO::getId, nowComment.getRootCommentId());
        }
        // 排序
        lqw.orderByDesc(CommentPO::getSort).orderByDesc(CommentPO::getId);
        List<CommentPO> commentPOS = commentDao.selectList(lqw);
        if (commentPOS == null || commentPOS.isEmpty()) {
            log.error("查看详情异常:{}", JSONUtil.toJsonStr(commentReq));
            throw new BaseException("查看详情异常");
        }
        // 转换为Map,组装评论树
        Map<Integer, CommentPO> childMap = commentPOS.stream().collect(Collectors.toMap(CommentPO::getId, Function.identity(), (key1, key2) -> key2));
        // 顶级评论
        CommentPO rootComment = null;
        // 子评论列表
        List<CommentTree> childList = new ArrayList<>();
        for (CommentPO commentPO : commentPOS) {
            // 顶级评论
            if (commentPO.getRootCommentId() == BaseConstants.TOP_COMMENT_ID) {
                rootComment = commentPO;
                continue;
            }
            // 子评论
            CommentTree childComment = BeanUtil.copyProperties(commentPO, CommentTree.class);
            childComment.setArticleTitle(articlePO.getArticleTitle());
            // 回复用户名称
            CommentPO toComment = childMap.get(childComment.getToCommentId());
            if (toComment != null) {
                childComment.setToUserName(toComment.getUserName());
            }
            childList.add(childComment);
        }
        if (rootComment == null) {
            log.error("顶级评论不存在：{}", JSONUtil.toJsonStr(commentReq));
            throw new BaseException("顶级评论不存在");
        }
        CommentTree commentTree = BeanUtil.copyProperties(rootComment, CommentTree.class);
        commentTree.setArticleTitle(articlePO.getArticleTitle());
        commentTree.setChildList(childList);
        return commentTree;
    }


    /**
     * @param pageQuery 评论树分页查询
     * @return 评论树分页
     */
    @Override
    public IPage<CommentTree> treePage(CommentTreePageQuery pageQuery) {
        if (pageQuery.getArticleId() == null) {
            log.error("评论归属文章id不能为空:{}", JSONUtil.toJsonStr(pageQuery));
            throw new BaseException("评论归属文章id不能为空");
        }
        // 查询文章
        ArticlePO articlePO = getArticle(pageQuery.getArticleId());
        if (!BaseConstants.YesOrNo.YES.equals(articlePO.getCommentOpen())) {
            return new Page<>();
        }
        // 查询顶级评论
        LambdaQueryWrapper<CommentPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CommentPO::getArticleId, pageQuery.getArticleId());
        // 查询顶级评论 0表示为自身为顶级评论
        lqw.eq(CommentPO::getRootCommentId, BaseConstants.TOP_COMMENT_ID);
        lqw.eq(CommentPO::getCommentStatus, BaseConstants.CommentStatus.PASS);
        lqw.eq(CommentPO::getDataStatus, BaseConstants.YesOrNo.YES);
        // 排序
        lqw.orderByDesc(CommentPO::getSort).orderByDesc(CommentPO::getId);
        // 顶级评论分页
        Page<CommentPO> rootPage = commentDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        // 转换
        return rootPage.convert(commentPO -> {
            CommentTree rootComment = BeanUtil.copyProperties(commentPO, CommentTree.class);
            // 查询所有子评论
            LambdaQueryWrapper<CommentPO> childQuery = new LambdaQueryWrapper<>(new CommentPO()
                    .setArticleId(commentPO.getArticleId())
                    .setRootCommentId(commentPO.getId()));

            childQuery.orderByDesc(CommentPO::getSort).orderByDesc(CommentPO::getId);
            List<CommentPO> childCommentPOList = commentDao.selectList(childQuery);
            if (childCommentPOList.isEmpty()) {
                rootComment.setChildList(new ArrayList<>());
                return rootComment;
            }

            // 处理子评论
            List<CommentTree> childTreeList = new ArrayList<>();
            // 转map Id -> CommentPO
            Map<Integer, CommentPO> childMap = childCommentPOList.stream().collect(Collectors.toMap(CommentPO::getId, Function.identity(), (key1, key2) -> key2));
            for (CommentPO child : childCommentPOList) {
                CommentTree childComment = BeanUtil.copyProperties(child, CommentTree.class);
                // 回复用户
                CommentPO toComment = childMap.get(childComment.getToCommentId());
                if (toComment != null) {
                    childComment.setToUserName(toComment.getUserName());
                } else if (Objects.equals(commentPO.getId(), childComment.getToCommentId())) {
                    childComment.setToUserName(commentPO.getUserName());
                }
                childTreeList.add(childComment);
            }
            return rootComment.setChildList(childTreeList);
        });
    }

    /**
     * 新增评论
     *
     * @param commentAdd 评论
     * @param request    请求
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addComment(CommentAdd commentAdd, HttpServletRequest request) {

        // 校验
        ValidatorUtils.validate(commentAdd);
        // 验证码
        Object captchaSave = request.getSession().getAttribute(BaseConstants.AttributeName.CAPTCHA_CODE);
        if (captchaSave == null || !commentAdd.getCaptcha().equalsIgnoreCase(captchaSave.toString())) {
            log.error("验证码错误:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("验证码错误");
        }

        // 登录用户
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || StrUtil.isBlank(loginUser.getBrowserKey())) {
            log.error("环境异常，请刷新页面后再次尝试:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("环境异常，请刷新页面后再次尝试");
        }

        // 查询文章
        ArticlePO articlePO = getArticle(commentAdd.getArticleId());
        if (!Objects.equals(BaseConstants.ArticleStatus.PUBLISH, articlePO.getArticleStatus())) {
            log.error("无效的文章id:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("无效的文章id");
        }

        if (!BaseConstants.YesOrNo.YES.equals(articlePO.getCommentOpen())) {
            throw new BaseException("当前文章评论已关闭");
        }

        // 顶级评论id校验
        if (commentAdd.getToCommentId() == null) {
            commentAdd.setRootCommentId(BaseConstants.TOP_COMMENT_ID);
            commentAdd.setToCommentId(BaseConstants.TOP_COMMENT_ID);
        } else {
            // 查询回复评论
            CommentPO toComment = commentDao.selectById(commentAdd.getToCommentId());
            if (toComment == null || !Objects.equals(toComment.getArticleId(), commentAdd.getArticleId())) {
                log.error("无效回复评论id:{}", JSONUtil.toJsonStr(commentAdd));
                throw new BaseException("无效回复评论id");
            }
            // 顶级评论id
            Integer rootCommentId = Objects.equals(toComment.getRootCommentId(), BaseConstants.TOP_COMMENT_ID)
                    ? toComment.getId() : toComment.getRootCommentId();
            if (!Objects.equals(commentAdd.getRootCommentId(), rootCommentId)) {
                log.error("无效顶级评论id:{}", JSONUtil.toJsonStr(commentAdd));
                throw new BaseException("无效顶级评论id");
            }
        }

        // 评论内容
        CommentPO commentPO = BeanUtil.copyProperties(commentAdd, CommentPO.class);
        // ip 浏览器信息
        commentPO.setUserIp(loginUser.getLoginIp())
                .setBrowserKey(loginUser.getBrowserKey())
                .setUserIpAddr(loginUser.getBrowserInfo().getLocation().getAddress());
        // 管理员直接审批通过
        if (Objects.equals(BaseConstants.UserType.ADMIN, loginUser.getUserType())) {
            commentPO.setCommentStatus(BaseConstants.CommentStatus.PASS);
        }
        // 新增评论
        commentDao.insert(commentPO);
        // 删除缓存
        CacheUtils.deleteCommonCache();
        if (Objects.equals(BaseConstants.UserType.ADMIN, loginUser.getUserType())) {
            // 更新文章评论数
            articleDao.updateCommentCountByIds(Collections.singletonList(commentAdd.getArticleId()));
            return;
        }

        // 通知管理员
        adminNotify();
        // 用户名或邮箱 变更
        if (!Objects.equals(loginUser.getUserName(), commentAdd.getUserName()) || !Objects.equals(loginUser.getUserEmail(), commentAdd.getUserEmail())) {
            accessLogService.insertCommentUserLog(loginUser
                    .setUserName(commentAdd.getUserName())
                    .setUserEmail(commentAdd.getUserEmail()));
        }
    }


    /**
     * 管理员回复
     *
     * @param commentAdd 评论
     */
    @Override
    public void adminReply(CommentAdd commentAdd) {
        // 登录用户
        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(loginUser.getUserType(), BaseConstants.UserType.ADMIN)) {
            log.error("登录状态异常:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("登录状态异常");
        }
        // 校验
        if (StrUtil.isBlank(commentAdd.getCommentContent()) || commentAdd.getCommentContent().length() > 500) {
            log.error("评论字数范围1-500:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("评论字数范围1-500");
        }
        if (commentAdd.getArticleId() == null) {
            log.error("文章id不能为空:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("文章id不能为空");
        }
        if (commentAdd.getToCommentId() == null) {
            log.error("回复评论id不能为空:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("回复评论id不能为空");
        }
        if (commentAdd.getRootCommentId() == null) {
            log.error("顶级评论id不能为空:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("顶级评论id不能为空");
        }

        // 文章
        ArticlePO articlePO = getArticle(commentAdd.getArticleId());

        if (!BaseConstants.YesOrNo.YES.equals(articlePO.getCommentOpen())) {
            throw new BaseException("当前文章评论已关闭");
        }

        // 查询回复评论
        CommentPO toComment = commentDao.selectById(commentAdd.getToCommentId());
        if (toComment == null || !Objects.equals(toComment.getArticleId(), commentAdd.getArticleId())) {
            log.error("无效回复评论id:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("无效回复评论id");
        }
        // 查询顶级评论
        CommentPO rootComment = commentDao.selectById(commentAdd.getRootCommentId());
        if (rootComment == null || !Objects.equals(rootComment.getArticleId(), commentAdd.getArticleId())) {
            log.error("无效顶级评论id:{}", JSONUtil.toJsonStr(commentAdd));
            throw new BaseException("无效顶级评论id");
        }

        // 评论内容
        CommentPO commentPO = new CommentPO()
                .setToCommentId(commentAdd.getToCommentId())
                .setArticleId(commentAdd.getArticleId())
                .setRootCommentId(commentAdd.getRootCommentId())
                .setCommentContent(commentAdd.getCommentContent())
                .setNotifySuccess(BaseConstants.YesOrNo.YES)
                .setUserId(loginUser.getUserId())
                .setUserName(loginUser.getUserName())
                .setUserEmail(loginUser.getUserEmail())
                .setUserIp(loginUser.getLoginIp())
                .setUserIpAddr(loginUser.getBrowserInfo().getLocation().getAddress())
                .setBrowserKey(loginUser.getBrowserKey())
                .setCommentStatus(BaseConstants.CommentStatus.PASS);
        // 新增评论
        commentDao.insert(commentPO);
        // 邮件通知
        if (Objects.equals(toComment.getEmailNotify(), BaseConstants.YesOrNo.YES)) {
            emailNotify(toComment);
        }
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

    /**
     * 置顶评论
     *
     * @param topReq 置顶参数
     */
    @Override
    public void topComment(TopReq topReq) {
        if (topReq.getId() == null) {
            throw new BaseException("评论id不能为空");
        }
        CommentPO commentPO = new CommentPO()
                .setId(topReq.getId())
                .setSort(Optional.ofNullable(topReq.getSort()).orElse(System.currentTimeMillis()));
        int i = commentDao.updateById(commentPO);
        if (i != 1) {
            throw new BaseException("评论id不存在");
        }
    }

    /**
     * 删除评论
     *
     * @param ids 评论id列表
     */
    @Override
    public void deleteCommentByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("评论id不能为空");
        }
        // 查询是否存在子评论
        LambdaQueryWrapper<CommentPO> lqw = new LambdaQueryWrapper<CommentPO>().in(CommentPO::getToCommentId, ids);
        if (commentDao.selectCount(lqw) > 0) {
            throw new BaseException("存在子评论，不能删除");
        }
        // 删除评论
        commentDao.deleteByIds(ids);
        // 删除缓存
        CacheUtils.deleteCommonCache();
    }

    /**
     * 获取文章
     *
     * @param articleId 文章id
     * @return 文章
     */
    private ArticlePO getArticle(Integer articleId) {
        ArticlePO articlePO = null;
        if (Objects.equals(articleId, BaseConstants.LinkArticle.ID)) {
            articlePO = new ArticlePO().setId(BaseConstants.LinkArticle.ID)
                    .setArticleTitle(BaseConstants.LinkArticle.TITLE)
                    .setArticleStatus(BaseConstants.ArticleStatus.PUBLISH)
                    .setCommentOpen(BaseConstants.YesOrNo.YES);
        } else {
            articlePO = articleDao.selectById(articleId);
        }
        if (articlePO == null) {
            log.error("无效文章id:{}", JSONUtil.toJsonStr(articleId));
            throw new BaseException("无效文章id");
        }
        return articlePO;
    }

    /**
     * 管理员通知
     */
    private void adminNotify() {
        LoginUserInfo adminUser = userService.getAdminUser();
        String model = "您有一条新评论，请前往审批。";
        MailUtils.send(adminUser.getUserEmail(), "新评论", model, false);
    }

    /**
     * 回复提醒
     *
     * @param toComment 被回复的评论
     */
    private void emailNotify(CommentPO toComment) {

        String url = BaseConstants.LinkArticle.ID == toComment.getArticleId() ? "/link" : BaseConstants.ARTICLE_URL_PREFIX + toComment.getArticleId();
        String model = String.format("您的评论内容被回复，请前往查看。文章地址：%s%s",
                SysConfigEnums.WEB_HOST.getValue(), url);
        MailUtils.send(toComment.getUserEmail(), "评论回复", model, false);
        log.info("邮件通知：" + JSONUtil.toJsonStr(toComment));
    }
}
