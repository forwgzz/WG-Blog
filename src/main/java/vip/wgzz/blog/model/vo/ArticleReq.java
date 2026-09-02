package vip.wgzz.blog.model.vo;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author wgzz
 * @date 2026/8/14 10:21
 * @description 文件请求
 */
@Data
@Accessors(chain = true)
public class ArticleReq {

    /**
     * 文章Id
     */
    private Integer id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 50, message = "标题长度不能超过50")
    private String articleTitle;


    /**
     * 文章摘要
     */
    @Size(max = 300, message = "摘要长度不能超过300")
    private String articleAbstract;

    /**
     * 文章封面路径
     */
    private String articleCover;


    /**
     * 文章内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String articleContent;

    /**
     * 文章目录
     */
    private String articleCatalog;

    /**
     * 文章md文本
     */
    @NotBlank(message = "文章md文本不能为空")
    private String articleMarkdown;

    /**
     * 开启评论 1是 0否
     */
    @Max(value = 1, message = "评论状态异常")
    @Min(value = 0, message = "评论状态异常")
    private int commentOpen;

    /**
     * 分类id
     */
    @NotNull(message = "文章分类不能为空")
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签id集合
     */
    private List<Integer> tagIdList;

    /**
     * 新增标签名称集合
     */
    private List<String> newTagNameList;

    /**
     * 文章状态 0草稿 1发布
     */
    @Max(value = 1, message = "文章状态异常")
    @Min(value = 0, message = "文章状态异常")
    private int articleStatus;

    /**
     * 引用文件id集合
     */
    private Set<String> fileIdList;

}
