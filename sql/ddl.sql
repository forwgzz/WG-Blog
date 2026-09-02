# mysql 8.0+ 默认执行
# mysql 5.7  需要将 utf8mb4_0900_ai_ci 替换为 utf8mb4_unicode_ci
#                  utf8mb4_0900_as_cs/utf8mb4_0900_bin 替换为 utf8mb4_unicode_cs

# 访问记录表
DROP TABLE IF EXISTS `tb_access_log`;
CREATE TABLE `tb_access_log`
(
    `id`               int unsigned     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`          int unsigned              DEFAULT NULL COMMENT '用户名称',
    `user_name`        varchar(32)               DEFAULT NULL COMMENT '用户名称',
    `user_type`        tinyint unsigned NOT NULL DEFAULT '0' COMMENT '用户类型 0访客 1管理员',
    `user_email`       varchar(100)              DEFAULT NULL COMMENT '用户邮箱',
    `user_ip`          varchar(64)      NOT NULL COMMENT '用户ip',
    `user_address`     varchar(128)              DEFAULT NULL COMMENT '用户地址',
    `access_time`      timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    `access_path`      varchar(100)              DEFAULT NULL COMMENT '访问路径',
    `last_access_path` varchar(100)              DEFAULT NULL COMMENT '上一个访问路径',
    `browser_key`      varchar(255)              DEFAULT NULL COMMENT '浏览器指纹',
    `browser_agent`    varchar(255)              DEFAULT NULL COMMENT '浏览器头',
    `trace_id`         varchar(64)               DEFAULT NULL COMMENT '跟踪id',
    `access_method`    varchar(10)               DEFAULT NULL COMMENT '请求方法',
    PRIMARY KEY (`id`),
    KEY `browser_key_user_ip` (`browser_key`, `user_ip`) USING BTREE,
    KEY `access_time` (`access_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='访问记录表';

# 文章表
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`
(
    `id`               int unsigned     NOT NULL AUTO_INCREMENT COMMENT '文章id',
    `article_title`    varchar(64)      NOT NULL COMMENT '文章标题',
    `article_abstract` varchar(500)              DEFAULT NULL COMMENT '文章摘要',
    `article_cover`    varchar(255)              DEFAULT NULL COMMENT '文章封面路径',
    `article_content`  mediumtext       NOT NULL COMMENT '文章内容',
    `article_catalog`  mediumtext COMMENT '文章目录',
    `article_markdown` mediumtext       NOT NULL COMMENT '文章md文本',
    `view_count`       int unsigned     NOT NULL DEFAULT '0' COMMENT '访问量',
    `word_count`       int unsigned     NOT NULL DEFAULT '0' COMMENT '文章字数',
    `comment_count`    int unsigned     NOT NULL DEFAULT '0' COMMENT '评论数',
    `comment_open`     tinyint unsigned NOT NULL DEFAULT '1' COMMENT '开启评论 1是 0否',
    `category_id`      int unsigned              DEFAULT NULL COMMENT '分类id',
    `category_name`    varchar(64)               DEFAULT NULL COMMENT '分类名称',
    `article_status`   tinyint unsigned NOT NULL DEFAULT '0' COMMENT '文章状态 0草稿 1发布',
    `article_type`     tinyint unsigned NOT NULL DEFAULT '0' COMMENT '文章类型 0普通文章 1友链',
    `sort`             bigint unsigned  NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status`      tinyint unsigned NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time`      timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE,
    KEY `category_id_article_status` (`category_id`, `article_status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文章表';

# 文章文件关联表
DROP TABLE IF EXISTS `tb_article_file_rel`;
CREATE TABLE `tb_article_file_rel`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `file_id`     varchar(64)  NOT NULL COMMENT '文件id',
    `article_id`  int unsigned NOT NULL COMMENT '文章id',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `file_id` (`file_id`),
    KEY `article_id` (`article_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 182
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文章文件关联表';

# 文章标签关联表
DROP TABLE IF EXISTS `tb_article_tag_rel`;
CREATE TABLE `tb_article_tag_rel`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `tag_id`      int unsigned NOT NULL COMMENT '标签id',
    `article_id`  int unsigned NOT NULL COMMENT '文章id',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `tag_id_article_id` (`tag_id`, `article_id`) USING BTREE,
    KEY `article_id_tag_id` (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文章标签关联表';

# 分类表
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category`
(
    `id`            int unsigned     NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `category_name` varchar(64)      NOT NULL COMMENT '分类名称',
    `category_desc` varchar(128)              DEFAULT NULL COMMENT '分类描述',
    `sort`          bigint unsigned  NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status`   tinyint unsigned NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time`   timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `category_name` (`category_name`) USING BTREE,
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='分类表';

# 标签表
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`
(
    `id`          int unsigned     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `tag_name`    varchar(32)      NOT NULL COMMENT '标签名称',
    `sort`        bigint unsigned  NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time` timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `tag_name` (`tag_name`),
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='标签表';

# 评论表
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`
(
    `id`              int unsigned     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `comment_content` varchar(500)     NOT NULL COMMENT '评论内容',
    `article_id`      int unsigned     NOT NULL COMMENT '归属文章id',
    `user_id`         int unsigned              DEFAULT NULL COMMENT '用户id',
    `user_name`       varchar(10)      NOT NULL COMMENT '用户名称',
    `user_avatar`     varchar(50)               DEFAULT NULL COMMENT '用户头像地址',
    `user_email`      varchar(50)      NOT NULL COMMENT '用户邮箱',
    `browser_key`     varchar(255)     NOT NULL COMMENT '浏览器指纹',
    `user_ip`         varchar(64)      NOT NULL COMMENT '用户ip',
    `user_ip_addr`    varchar(128)     NOT NULL COMMENT '用户ip归属地',
    `root_comment_id` int unsigned     NOT NULL COMMENT '顶级评论id 0表示为自身为顶级评论',
    `to_comment_id`   int unsigned     NOT NULL COMMENT '回复评论id 0表示为自身',
    `email_notify`    tinyint unsigned NOT NULL DEFAULT '0' COMMENT '邮件通知 1是 0否',
    `comment_status`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '评论状态 0未审核 1审核通过 2审核拒绝',
    `sort`            bigint unsigned  NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status`     tinyint unsigned NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time`     timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `notify_success`  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '上级评论通知成功 1是 0否',
    PRIMARY KEY (`id`),
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE,
    KEY `article_id_root_comment_id` (`article_id`, `root_comment_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='评论表';

# 文件存储表
DROP TABLE IF EXISTS `tb_file_store`;
CREATE TABLE `tb_file_store`
(
    `id`                varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '文件id',
    `file_name`         varchar(128)                                               NOT NULL COMMENT '文件名称',
    `file_old_name`     varchar(128)                                               NOT NULL COMMENT '文件原名称',
    `file_path`         varchar(255)                                               NOT NULL COMMENT '文件路径',
    `file_type`         varchar(20)                                                NOT NULL COMMENT '文件类型： image图片 video视频 audio音频 code代码  zip压缩包 document文档 code代码 executable程序  other其他',
    `file_content_type` varchar(255)                                               NOT NULL COMMENT '文件内容类型 mine',
    `file_md5`          varchar(64)                                                NOT NULL COMMENT 'md5值',
    `sort`              bigint unsigned                                            NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status`       tinyint unsigned                                           NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time`       timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `file_md5` (`file_md5`),
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='文件存储表';

# 友链表
DROP TABLE IF EXISTS `tb_link`;
CREATE TABLE `tb_link`
(
    `id`          int unsigned                                                NOT NULL AUTO_INCREMENT COMMENT '友链id',
    `link_name`   varchar(64)                                                 NOT NULL COMMENT '友链名称',
    `link_url`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '友链url',
    `link_avatar` varchar(128)                                                NOT NULL COMMENT '友链头像',
    `link_desc`   varchar(128)                                                         DEFAULT NULL COMMENT '友链描述',
    `link_status` tinyint unsigned                                            NOT NULL DEFAULT '1' COMMENT '友链状态 1显示 0隐藏',
    `sort`        bigint unsigned                                             NOT NULL DEFAULT '0' COMMENT '排序值：时间戳',
    `data_status` tinyint unsigned                                            NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time` timestamp                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `link_url` (`link_url`) USING BTREE,
    KEY `sort_create_time` (`sort`, `create_time`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='友链表';

# 配置表
DROP TABLE IF EXISTS `tb_sys_config`;
CREATE TABLE `tb_sys_config`
(
    `id`           int unsigned                                               NOT NULL AUTO_INCREMENT COMMENT 'id',
    `config_code`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '配置编码',
    `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs      NOT NULL COMMENT '配置值',
    `config_name`  varchar(64)                                                NOT NULL DEFAULT '' COMMENT '配置名称',
    `config_desc`  varchar(255)                                                        DEFAULT NULL COMMENT '配置描述',
    `data_status`  tinyint unsigned                                           NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `create_time`  timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `config_code` (`config_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='配置表';
# 默认配置
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('avatarUrl', '/public/pic/tx.png', '头像地址', '默认地址/public/pic/tx.png');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('adminPath', '/admin', '后台管理路径', '默认后台管理路径 http://[ip]/admin');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('webHost', '', '网站域名', '网站域名');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('webTitle', 'WG日记', '网站标题', '网站标题');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('webDescription',
        '喜欢计算机的咸鱼一条，经常抽出空余时间学习各种语言编程以及知识，虽然懂得不多，但是这是我最喜欢的爱好吧。',
        '网站描述', '网站描述');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('showTextTitle', 'WG''s Blog', '逐显标题', '逐显标题');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('showTextDesc', 'Accept yourself as ordinary and do your best to excel.', '逐显描述', '逐显描述');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('loginCaptchaOn', '1', '开启登录验证码', '1开启 0关闭 默认开启');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('aboutRelArticleId', '', '关于页面的文章ID', '关于页面的文章ID');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('webStartTime', '', '网址起始时间', '网址起始时间');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('copyrightYear', '2026', 'Copyright时间', 'Copyright时间');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('footerICP', '', '网站ICP备案', '网站ICP备案号');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('footerPolice', '', '网站公安备案', '网站公安备案号');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('ipLocationApi', 'https://ip9.com.cn/get?ip={ip}', 'IP地址查询API',
        'IP地址查询API 示例：https://ip9.com.cn/get?ip={ip}');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('ipLocationApiIpv6', '1', '是否支持IPV6', '是否支持IPV6');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('ipLocationApiTemplate',
        '{"nation":"$.data.country","province":"$.data.prov","city":"$.data.city","district":"$.data.area","isp":"$.data.isp","ip":"$.data.ip"}',
        'Json解析模板',
        '解析Json模板 结果替换:$.data.country->国家,$.data.prov->省份,$.data.city->城市,$.data.area->区域,$data.ips->运营商,$.data.ip->ip');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('webStats', '', '站长统计脚本', '站长统计脚本');
INSERT INTO tb_sys_config (config_code, config_value, config_name, config_desc)
VALUES ('donateUrl', '', '捐赠图片地址', '捐赠图片地址');

# 用户表
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`
(
    `id`                 int unsigned                                               NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_name`          varchar(32)                                                NOT NULL DEFAULT '' COMMENT '用户名称',
    `user_code`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL DEFAULT '' COMMENT '用户编码',
    `password`           varchar(255)                                                        DEFAULT NULL COMMENT '用户密码',
    `user_email`         varchar(50)                                                NOT NULL COMMENT '用户邮箱',
    `user_qq`            varchar(12)                                                         DEFAULT NULL COMMENT '用户qq',
    `user_avatar`        varchar(50)                                                         DEFAULT NULL COMMENT '用户头像地址',
    `browser_key`        varchar(255)                                               NOT NULL DEFAULT '' COMMENT '用户浏览器指纹',
    `user_type`          tinyint unsigned                                           NOT NULL DEFAULT '0' COMMENT '用户类型 0访客 1管理员',
    `user_status`        tinyint unsigned                                           NOT NULL DEFAULT '0' COMMENT '用户状态 0正常 1锁定 2注销',
    `locked_time`        timestamp                                                  NULL     DEFAULT NULL COMMENT '锁定时间',
    `login_time`         timestamp                                                  NULL     DEFAULT NULL COMMENT '登录时间',
    `login_ip`           varchar(64)                                                NOT NULL DEFAULT '' COMMENT '登录ip',
    `login_address`      varchar(128)                                                        DEFAULT NULL COMMENT '登录地址',
    `last_login_time`    timestamp                                                  NULL     DEFAULT NULL COMMENT '上次登录时间',
    `last_login_ip`      varchar(64)                                                         DEFAULT NULL COMMENT '上次登录ip',
    `last_login_address` varchar(128)                                                        DEFAULT NULL COMMENT '上次登录地址',
    `data_status`        tinyint unsigned                                           NOT NULL DEFAULT '1' COMMENT '数据状态 1有效 0无效',
    `login_nums`         int unsigned                                               NOT NULL DEFAULT '0' COMMENT '登录次数',
    `create_time`        timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        timestamp                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_code` (`user_code`),
    UNIQUE KEY `browser_key` (`browser_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户表';
# 默认用户 # 初始账户：admin 密码：a123456789
INSERT INTO tb_user (user_name, user_code, password, user_email, user_avatar, user_type)
VALUES ('admin', 'admin', '$2a$10$vGYhIMl43uY3XuudJ8tGXeiJBRgst6i1Fxb39db0OIfmh8LRXdJ6O', 'admin@example.com',
        '/public/pic/tx.png', 1);

