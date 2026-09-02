package vip.wgzz.blog.common;

/**
 * @author wgzz
 * @date 2026/8/2 17:04
 * @description 基础常量
 */
public class BaseConstants {


    /**
     * 文件地址前缀
     */
    public static String FILE_URL_PREFIX = "/file/p/";

    /**
     * 文章预览前缀
     */
    public static String ARTICLE_URL_PREFIX = "/article/";

    /**
     * 缓存key
     */
    public static class CacheKeys {
        /**
         * 后台数据统计缓存
         */
        public static final String ADMIN_STATS_INFO = "adminStatsInfo";

        /**
         * 前台数据统计缓存
         */
        public static final String FRONT_STATS_INFO = "frontStatsInfo";

        /**
         * 分类下拉框
         */
        public static final String CATEGORY_SELECT = "categorySelect";

        /**
         * 标签下拉框
         */
        public static final String TAG_SELECT = "tagSelect";

    }

    /**
     * 友链文章
     */
    public static class LinkArticle {
        /**
         * 文章id
         */
        public static final int ID = 0;
        /**
         * 文章标题
         */
        public static String TITLE = "友链";
    }

    /**
     * 友链状态
     */
    public static class LinkStatus {

        /**
         * 隐藏
         */
        public static final int HIDE = 0;

        /**
         * 显示
         */
        public static final int SHOW = 1;

        public static String getLinkStatusStr(Integer linkStatus) {
            if (linkStatus == null) return UnknowStatus;
            return switch (linkStatus) {
                case HIDE -> "隐藏";
                case SHOW -> "显示";
                default -> UnknowStatus;
            };
        }

    }

    /**
     * 置顶评论id
     */
    public static int TOP_COMMENT_ID = 0;

    /**
     * 评论状态
     */
    public static class CommentStatus {

        /**
         * 待审核
         */
        public static final int NONE = 0;

        /**
         * 已审核
         */
        public static final int PASS = 1;

        /**
         * 已拒绝
         */
        public static final int REFUSE = 2;

        public static String getCommentStatusStr(Integer status) {
            if (status == null) return UnknowStatus;
            return switch (status) {
                case NONE -> "待审核";
                case PASS -> "已审核";
                case REFUSE -> "已拒绝";
                default -> UnknowStatus;
            };
        }
    }

    /**
     * 文章状态
     */
    public static class ArticleStatus {

        /**
         * 草稿
         */
        public static final int DRAFT = 0;

        /**
         * 发布
         */
        public static final int PUBLISH = 1;

        public static String getArticleStatusStr(Integer articleStatus) {
            if (articleStatus == null) return UnknowStatus;
            return switch (articleStatus) {
                case DRAFT -> "草稿";
                case PUBLISH -> "发布";
                default -> UnknowStatus;
            };
        }
    }

    /**
     * 文件存储类型
     */
    public static class StorageType {
        /**
         * 本地
         */
        public static final String LOCAL = "local";

        /**
         * 七牛云
         */
        public static final String QINIU = "qiniu";

        /**
         * 腾讯云
         */
        public static final String TENCENT = "tencent";

    }

    /**
     * AttributeName
     */
    public static class AttributeName {
        /**
         * 后台基础信息
         */
        public static final String TH_BASE_INFO = "base";

        /**
         * 后台数据统计
         */
        public static final String STATS_INFO = "stats";

        /**
         * 验证码
         */
        public static final String CAPTCHA_CODE = "captcha";

        /**
         * 错误信息
         */
        public static final String ERROR_TEMP   = "error";

        /**
         * 当前页码
         */
        public static final String CURRENT_PAGE = "currentPage";

        /**
         * 作者信息
         */
        public static final String AUTHOR_INFO = "author";

        /**
         * 文字详情
         */
        public static final String ARTICLE_INFO = "article";

        /**
         * 分页数据
         */
        public static final String ARTICLE_PAGE = "page";

        /**
         * meta信息
         */
        public static final String META_INFO = "meta";

        /**
         * 归档信息
         */
        public static final String ARCHIVE_YEAR = "archive";

        /**
         * 友链信息
         */
        public static final String LINK = "links";

        /**
         * 设置访客权限
         */
        public static final String SET_VISITOR_AUTH = "setVisitorAuth";


    }

    /**
     * 1yes 0/2no
     */
    public static class YesOrNo {
        public static final Integer YES = 1;
        public static final Integer NO = 0;
        public static final Integer NO2 = 2;
    }

    /**
     * 1yes 0/2no
     */
    public static class YesOrNoStr {
        public static final String YES = "1";
        public static final String NO = "0";
        public static final String NO2 = "2";
    }

    /**
     * 未知状态
     */
    public static String UnknowStatus = "未知";

    /**
     * 权限类型
     */
    public static class AuthorityType {
        /**
         * 管理员权限
         */
        public static final String ADMIN = "ADMIN";

        /**
         * 访客权限
         */
        public static final String VISITOR = "VISITOR";

        /**
         * 未知
         */
        public static final String ERROR = "ERROR";
    }

    /**
     * 用户状态 0正常 1锁定 2禁用
     */
    public static class UserStatus {
        /**
         * 正常
         */
        public static final Integer NORMAL = 0;

        /**
         * 锁定
         */
        public static final Integer LOCKED = 1;

        /**
         * 禁用
         */
        public static final Integer DISABLED = 2;
    }

    /**
     * 用户类型
     */
    public static class UserType {
        /**
         * 普通用户
         */
        public static final int VISITOR = 0;

        /**
         * 系统管理员
         */
        public static final int ADMIN = 1;

        /**
         * 对应角色权限
         *
         * @param n
         */
        public static String[] getAuthorityList(Integer n) {
            if (n == null) return null;
            return switch (n) {
                case VISITOR -> new String[]{AuthorityType.VISITOR};
                case ADMIN -> new String[]{AuthorityType.ADMIN, AuthorityType.VISITOR};
                default -> new String[]{AuthorityType.ERROR};
            };

        }
    }

}
