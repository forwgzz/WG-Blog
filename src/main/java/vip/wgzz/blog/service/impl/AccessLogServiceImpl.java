package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.LoginUserUtils;
import vip.wgzz.blog.dao.AccessLogDao;
import vip.wgzz.blog.model.bo.AccessInfo;
import vip.wgzz.blog.model.bo.LoginUserInfo;
import vip.wgzz.blog.model.po.AccessLogPO;
import vip.wgzz.blog.model.vo.AccessPageQuery;
import vip.wgzz.blog.service.AccessLogService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wgzz
 * @date 2026/8/16 16:51
 * @description 访问日志Service实现
 */
@Slf4j
@Service
public class AccessLogServiceImpl extends ServiceImpl<AccessLogDao, AccessLogPO> implements AccessLogService {


    /**
     * @param browserKey 指纹
     * @return 根据指纹查询访客
     */

    @Override
    public AccessLogPO getVisitorByKey(String browserKey) {
        if (StrUtil.isBlank(browserKey)) {
            return null;
        }
        // 查询日志表 最近的一条记录
        LambdaQueryWrapper<AccessLogPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AccessLogPO::getBrowserKey, browserKey);
        lqw.orderByDesc(AccessLogPO::getAccessTime);
        lqw.last("LIMIT 1");
        AccessLogPO accessLogPO = getOne(lqw);
        if (accessLogPO == null) {
            return null;
        }
        return accessLogPO;
    }


    /**
     * 插入评论用户日志
     *
     * @param user 用户信息
     */
    @Override
    public void insertCommentUserLog(LoginUserInfo user) {
        try {
            // 日志表信息
            AccessLogPO accessLogPO = new AccessLogPO()
                    .setUserName(user.getUserName())
                    .setUserType(user.getUserType())
                    .setUserEmail(user.getUserEmail())
                    .setBrowserKey(user.getBrowserKey())
                    .setBrowserAgent(user.getBrowserInfo().getAgent())
                    .setAccessTime(LocalDateTime.now())
                    .setUserIp(user.getLoginIp())
                    .setUserAddress(user.getLoginAddress());
            save(accessLogPO);

            // 删除原始登录信息
            LoginUserUtils.clearLoginUser();
        }catch (Exception e) {
            log.error("插入评论用户日志失败", e);
        }
    }

    /**
     * @param pageQuery 分页参数
     * @return 分页
     */
    @Override
    public IPage<AccessInfo> getAccessLogPage(AccessPageQuery pageQuery) {
        // 查询条件
        LambdaQueryWrapper<AccessLogPO> lqw = new LambdaQueryWrapper<>();
        // 用户类型
        lqw.eq(pageQuery.getUserType() != null, AccessLogPO::getUserType, pageQuery.getUserType());
        // 用户名
        lqw.like(StrUtil.isNotBlank(pageQuery.getUserName()), AccessLogPO::getUserName, pageQuery.getUserName());
        // 用户邮箱
        lqw.like(StrUtil.isNotBlank(pageQuery.getUserEmail()), AccessLogPO::getUserEmail, pageQuery.getUserEmail());
        // 登录ip
        lqw.like(StrUtil.isNotBlank(pageQuery.getUserIp()), AccessLogPO::getUserIp, pageQuery.getUserIp());
        // 登录地址
        lqw.like(StrUtil.isNotBlank(pageQuery.getUserAddress()), AccessLogPO::getUserAddress, pageQuery.getUserAddress());
        // 浏览器指纹
        lqw.like(StrUtil.isNotBlank(pageQuery.getBrowserKey()), AccessLogPO::getBrowserKey, pageQuery.getBrowserKey());
        if(pageQuery.getBrowserKeyBlank() != null){
            if (BaseConstants.YesOrNo.YES.equals(pageQuery.getBrowserKeyBlank())) {
                lqw.isNull(AccessLogPO::getBrowserKey);
            } else if (BaseConstants.YesOrNo.NO.equals(pageQuery.getBrowserKeyBlank())) {
                lqw.isNotNull(AccessLogPO::getBrowserKey);
            }
        }
        // 浏览器头
        lqw.like(StrUtil.isNotBlank(pageQuery.getBrowserAgent()), AccessLogPO::getBrowserAgent, pageQuery.getBrowserAgent());
        // 跟踪ID
        lqw.like(StrUtil.isNotBlank(pageQuery.getTraceId()), AccessLogPO::getTraceId, pageQuery.getTraceId());
        // 访问路径
        lqw.like(StrUtil.isNotBlank(pageQuery.getAccessPath()), AccessLogPO::getAccessPath, pageQuery.getAccessPath());
        // 访问时间
        if (pageQuery.getDateRange() != null && pageQuery.getDateRange().size() == 2) {
            lqw.between(AccessLogPO::getAccessTime,
                    pageQuery.getDateRange().get(0),
                    pageQuery.getDateRange().get(1).withNano(999_000_000));
        }

        // 排序
        lqw.orderByDesc(AccessLogPO::getAccessTime);
        // 分页查询
        Page<AccessLogPO> userLogPOPage = page(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        return userLogPOPage.convert(accessLogPO -> BeanUtil.copyProperties(accessLogPO, AccessInfo.class));
    }

    /**
     * 删除
     *
     * @param ids 访客日志id列表
     */
    @Override
    public void deleteVisitorByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("访客日志id不能为空");
        }
        // 删除
        removeByIds(ids);
    }



}
