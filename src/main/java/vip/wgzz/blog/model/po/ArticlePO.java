package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/2 10:24
 * @description 文章表
 */
@Data
@Accessors(chain = true)
@TableName("tb_article")
public class ArticlePO {

    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章摘要
     */
    private String articleAbstract;


    /**
     * 文章封面路径
     */
    private String articleCover;

    /**
     * 文章内容
     */
    private String articleContent;
    /**
     * 文章目录
     */
    private String articleCatalog;
    /**
     * 文章md文本
     */
    private String articleMarkdown;

    /**
     * 文章字数
     */
    private Integer wordCount;

    /**
     * 访问量
     */
    private Integer viewCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 开启评论 1是 0否
     */
    private Integer commentOpen;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 文章状态 0草稿 1发布
     */
    private Integer articleStatus;

    /**
     * 文章类型 0普通文章 1友链
     */
    private Integer articleType;

    /**
     * 排序值 时间戳
     */
    private Long sort;

    /**
     * 数据状态 1有效 0无效
     */
    private Integer dataStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    //关联字段------------------------start
    @TableField(exist = false)
    private List<TagPO> tagList;

    /**
     * 按tagId查询
     */
    @TableField(exist = false)
    private Integer tagId;

    //查询字段------------------------end
}
