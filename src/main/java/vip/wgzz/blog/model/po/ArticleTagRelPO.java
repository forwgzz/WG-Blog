package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:47
 * @description 文章标签关联表
 */
@Data
@Accessors(chain = true)
@TableName("tb_article_tag_rel")
public class ArticleTagRelPO {

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 标签id
	 */
	private Integer tagId;

	/**
	 * 文章id
	 */
	private Integer articleId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;


}
