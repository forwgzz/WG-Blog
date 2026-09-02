package vip.wgzz.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import vip.wgzz.blog.model.po.UserPO;

/**
 * @author wgzz
 * @date 2026/8/2 16:56
 * @description 用户Dao
 */
@Mapper
public interface UserDao extends BaseMapper<UserPO> {

    void updateLoginUser(@Param("user") UserPO user);
}
