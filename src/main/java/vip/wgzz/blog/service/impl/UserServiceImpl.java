package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.common.util.ValidatorUtils;
import vip.wgzz.blog.dao.UserDao;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.UserPO;
import vip.wgzz.blog.model.vo.PasswordReq;
import vip.wgzz.blog.model.vo.UserReq;
import vip.wgzz.blog.service.UserService;

import java.util.Objects;

/**
 * @author wgzz
 * @date 2026/8/4 10:38
 * @description 用户Service实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {


    @Resource
    private UserDao userDao;


    @Resource
    private PasswordEncoder passwordEncoder;


    /**
     * @return 管理员用户
     */
    @Override
    public LoginUserInfo getAdminUser() {
        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<>(new UserPO().setUserType(BaseConstants.UserType.ADMIN));
        UserPO userPO = userDao.selectOne(queryWrapper);
        if (userPO == null) {
            throw new BaseException("没有管理员用户");
        }
        return BeanUtil.copyProperties(userPO, LoginUserInfo.class);
    }


    /**
     * 更新密码
     *
     * @param passwordReq 密码
     */
    @Override
    public void updatePassword(PasswordReq passwordReq) {
        // 校验
        ValidatorUtils.validate(passwordReq);
        if (!Objects.equals(passwordReq.getNewPassword(), passwordReq.getConfirmPassword())) {
            throw new BaseException("新密码和确认密码一致");
        }

        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(loginUser.getUserId(), passwordReq.getUserId())) {
            throw new BaseException("用户未登录");
        }

        // 查找用户
        UserPO userPO = userDao.selectById(passwordReq.getUserId());
        if (userPO == null) {
            throw new BaseException("用户不存在");
        }

        if (!passwordEncoder.matches(passwordReq.getOldPassword(), userPO.getPassword())) {
            throw new BaseException("旧密码不正确");
        }
        // 更新
        userDao.updateById(new UserPO()
                .setId(userPO.getId())
                .setPassword(passwordEncoder.encode(passwordReq.getNewPassword())));
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    @Override
    public void updateUserInfo(UserReq user) {
        // 校验
        ValidatorUtils.validate(user);

        LoginUserInfo loginUser = LoginUserUtils.getLoginUser();
        if (loginUser == null || !Objects.equals(loginUser.getUserId(), user.getUserId())) {
            throw new BaseException("用户未登录");
        }
        // 更新
        userDao.updateById(new UserPO()
                .setId(user.getUserId())
                .setUserName(user.getUserName())
                .setUserCode(user.getUserCode())
                .setUserEmail(user.getUserEmail())
                .setUserQq(user.getUserQq())
                .setUserAvatar(user.getUserAvatar()));
    }

}
