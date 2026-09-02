package vip.wgzz.blog.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author wgzz
 * @date 2026/8/2 10:25
 * @description 标签表
 */
@Data
@Accessors(chain = true)
@TableName("tb_tag")
public class TagPO {

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	/**
	 * 标签名称
	 */
	private String tagName;

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

	//附加字段
	/**
	 * 关联文章数量
	 */
	@TableField(exist = false)
	private Integer number;

	/**
	 * 关联文章ids
	 */
	@TableField(exist = false)
	private String articleIds;


}
