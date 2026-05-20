package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysVersion;
import org.dromara.system.domain.bo.SysVersionBo;
import org.dromara.system.domain.vo.SysVersionVo;
import org.dromara.system.mapper.SysVersionMapper;
import org.dromara.system.service.ISysVersionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysVersionServiceImpl implements ISysVersionService {
    private final SysVersionMapper baseMapper;

    @Override
    public SysVersion getLatestAPK() {
        LambdaQueryWrapper<SysVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysVersion::getDelFlag, "0");
        queryWrapper.orderByDesc(SysVersion::getUpdateTime);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询app安装包版本管理
     *
     * @param versionId 主键
     * @return app安装包版本管理
     */
    @Override
    public SysVersionVo queryById(Long versionId) {
        return baseMapper.selectVoById(versionId);
    }

    /**
     * 分页查询app安装包版本管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return app安装包版本管理分页列表
     */
    @Override
    public TableDataInfo<SysVersionVo> queryPageList(SysVersionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysVersion> lqw = buildQueryWrapper(bo);
        Page<SysVersionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的app安装包版本管理列表
     *
     * @param bo 查询条件
     * @return app安装包版本管理列表
     */
    @Override
    public List<SysVersionVo> queryList(SysVersionBo bo) {
        LambdaQueryWrapper<SysVersion> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysVersion> buildQueryWrapper(SysVersionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysVersion> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysVersion::getVersionId);
        lqw.eq(StringUtils.isNotBlank(bo.getVersionNumber()), SysVersion::getVersionNumber, bo.getVersionNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), SysVersion::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), SysVersion::getUrl, bo.getUrl());
        return lqw;
    }

    /**
     * 新增app安装包版本管理
     *
     * @param bo app安装包版本管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysVersionBo bo) {
        SysVersion add = MapstructUtils.convert(bo, SysVersion.class);
        validEntityBeforeSave(add);
        add.setUpdateTime(new Date());
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setVersionId(add.getVersionId());
        }
        return flag;
    }

    /**
     * 修改app安装包版本管理
     *
     * @param bo app安装包版本管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysVersionBo bo) {
        SysVersion update = MapstructUtils.convert(bo, SysVersion.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysVersion entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除app安装包版本管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
