package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vip.wgzz.blog.model.po.AccessLogPO;

/**
 * @author wgzz
 * @date 2026/8/2 16:56
 * @description 用户日志
 */
@Mapper
public interface AccessLogDao extends BaseMapper<AccessLogPO> {

    /**
     * browser_key + ip 视为同一用户
     * @return 获取访客数
     */
    @Select("SELECT COUNT(DISTINCT COALESCE(browser_key, ''), user_ip ) FROM tb_access_log")
    Long getVisitorCount();
}
