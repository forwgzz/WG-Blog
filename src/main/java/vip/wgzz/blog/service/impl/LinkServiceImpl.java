package vip.wgzz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.util.ValidatorUtils;
import vip.wgzz.blog.dao.LinkDao;
import vip.wgzz.blog.model.bo.LinkInfo;
import vip.wgzz.blog.model.po.LinkPO;
import vip.wgzz.blog.model.vo.LinkReq;
import vip.wgzz.blog.model.vo.PageQuery;
import vip.wgzz.blog.model.vo.TopReq;
import vip.wgzz.blog.service.LinkService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wgzz
 * @date 2026/8/16 14:20
 * @description 友链Service实现
 */
@Slf4j
@Service
public class LinkServiceImpl implements LinkService {

    @Resource
    private LinkDao linkDao;

    /**
     * @param pageQuery 分页参数
     * @return 分页
     */
    @Override
    public IPage<LinkInfo> getLinkPage(PageQuery pageQuery) {
        // 查询条件
        LambdaQueryWrapper<LinkPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StrUtil.isNotBlank(pageQuery.getSearchStr()), LinkPO::getLinkName, pageQuery.getSearchStr());
        // 排序
        lqw.orderByDesc(LinkPO::getSort).orderByAsc(LinkPO::getId);
        // 分页查询
        Page<LinkPO> categoryPOPage = linkDao.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), lqw);
        return categoryPOPage.convert(categoryPO -> BeanUtil.copyProperties(categoryPO, LinkInfo.class));
    }

    /**
     * 新增
     *
     * @param link 友链信息
     */
    @Override
    public void addLink(LinkReq link) {
        // 校验
        ValidatorUtils.validate(link);
        // 链接重复
        LambdaQueryWrapper<LinkPO> lqw = new LambdaQueryWrapper<LinkPO>()
                .eq(LinkPO::getLinkUrl, link.getLinkUrl());
        if (linkDao.selectCount(lqw) > 0) {
            log.error("友链地址重复:{}", JSONUtil.toJsonStr(link));
            throw new BaseException("友链地址重复");
        }
        // 新增
        LinkPO linkPO = BeanUtil.copyProperties(link, LinkPO.class);
        linkPO.setId(null);
        linkDao.insert(linkPO);
    }

    /**
     * 更新
     *
     * @param link 友链信息
     */
    @Override
    public void updateLink(LinkReq link) {
        // 校验
        ValidatorUtils.validate(link);
        if (link.getId() == null) {
            log.error("友链id不能为空:{}", JSONUtil.toJsonStr(link));
            throw new BaseException("友链id不能为空");
        }
        LinkPO linkOld = linkDao.selectById(link.getId());
        if (linkOld == null) {
            log.error("无效的友链id:{}", JSONUtil.toJsonStr(link));
            throw new BaseException("无效的友链id");
        }
        // 链接重复
        if (!Objects.equals(link.getLinkUrl(), linkOld.getLinkUrl())) {
            LambdaQueryWrapper<LinkPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(LinkPO::getLinkUrl, link.getLinkUrl());
            lqw.ne(LinkPO::getId, link.getId());
            if (linkDao.selectCount(lqw) > 0) {
                log.error("友链地址重复:{}", JSONUtil.toJsonStr(link));
                throw new BaseException("友链地址重复");
            }
        }

        // 更新
        LinkPO linkPO = BeanUtil.copyProperties(link, LinkPO.class);
        linkDao.updateById(linkPO);
    }

    /**
     * 置顶
     *
     * @param topReq 置顶参数
     */
    @Override
    public void topLink(TopReq topReq) {
        if (topReq.getId() == null) {
            throw new BaseException("友链id不能为空");
        }
        LinkPO linkPO = new LinkPO().setId(topReq.getId())
                .setSort(Optional.ofNullable(topReq.getSort()).orElse(System.currentTimeMillis()));
        int i = linkDao.updateById(linkPO);
        if (i != 1) {
            log.error("友链id不存在:{}", JSONUtil.toJsonStr(topReq));
            throw new BaseException("友链id不存在");
        }
    }

    /**
     * 删除
     *
     * @param ids 友链id列表
     */
    @Override
    public void deleteLinkByIds(List<Integer> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new BaseException("友链id不能为空");
        }
        // 删除
        linkDao.deleteByIds(ids);
    }

    /**
     * @return 友链列表（仅查询可显示友链）
     */
    @Override
    public List<LinkInfo> getLinkShowList() {
        // 状态为显示
        LambdaQueryWrapper<LinkPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(LinkPO::getLinkStatus, BaseConstants.LinkStatus.SHOW);
        lqw.orderByDesc(LinkPO::getSort).orderByAsc(LinkPO::getId);
        // 查询
        List<LinkPO> linkPOS = linkDao.selectList(lqw);
        if (CollectionUtil.isEmpty(linkPOS)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(linkPOS, LinkInfo.class);
    }
}
