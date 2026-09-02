package vip.wgzz.blog.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.dao.UserDao;
import vip.wgzz.blog.model.po.UserPO;

/**
 * @author wgzz
 * @date 2026/8/3 13:46
 * @description 加载用户实现
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserDao userDao;

    /**
     * 根据用户名查询用户详情
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO user = userDao.selectOne(new QueryWrapper<>(new UserPO().setUserCode(username)));
        if (user == null) {
            return null;
        }
        return new LoginUserDetail(user);
    }
}
