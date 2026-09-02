package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:46
 * @description 文章文件关联表
 */
@Data
@Accessors(chain = true)
@TableName("tb_article_file_rel")
public class ArticleFileRelPO {

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 文章id
	 */
	private Integer articleId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;


}
