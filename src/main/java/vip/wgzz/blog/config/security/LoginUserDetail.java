package vip.wgzz.blog.config.security;

import cn.hutool.core.bean.BeanUtil;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.UserPO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/3 13:40
 * @description 重写登录用户详情类
 */
@Getter
public class LoginUserDetail implements UserDetails , Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LoginUserInfo user;

    private String password;

    /**
     * 正常登陆用户
     * @param user 用户信息
     */
    public LoginUserDetail(UserPO user) {
        if (user == null) {
            throw new BaseException("用户不能为空");
        }
        this.password = user.getPassword();
        this.user = BeanUtil.copyProperties(user, LoginUserInfo.class).setUserId(user.getId());
    }

    /**
     * 更新登陆用户信息
     * @param user 用户信息
     */
    public LoginUserDetail(LoginUserInfo user) {
        if (user == null) {
            throw new BaseException("用户不能为空");
        }
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //获取用户类型对应角色
        String[] authorityList = BaseConstants.UserType.getAuthorityList(user.getUserType());
        return Arrays.stream(authorityList).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUserCode();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !BaseConstants.UserStatus.LOCKED.equals(user.getUserStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

