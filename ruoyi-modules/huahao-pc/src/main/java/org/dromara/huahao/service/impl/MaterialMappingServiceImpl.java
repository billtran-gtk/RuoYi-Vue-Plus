package org.dromara.huahao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.MaterialMapping;
import org.dromara.huahao.domain.bo.MaterialMappingBo;
import org.dromara.huahao.domain.vo.MaterialMappingVo;
import org.dromara.huahao.mapper.MaterialMappingMapper;
import org.dromara.huahao.service.IMaterialMappingService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 客户料号与厂内料号对应Service业务层处理
 *
 * @author Hurj
 * @date 2026-02-03
 */
@RequiredArgsConstructor
@Service
public class MaterialMappingServiceImpl implements IMaterialMappingService {

    private final MaterialMappingMapper baseMapper;

    /**
     * 查询客户料号与厂内料号对应
     *
     * @param id 主键
     * @return MaterialMappingVo
     */
    @Override
    public MaterialMappingVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询客户料号与厂内料号对应列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public TableDataInfo<MaterialMappingVo> queryPageList(MaterialMappingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MaterialMapping> lqw = buildQueryWrapper(bo);
        Page<MaterialMappingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的客户料号与厂内料号对应列表
     *
     * @param bo 查询条件
     * @return 列表
     */
    @Override
    public List<MaterialMappingVo> queryList(MaterialMappingBo bo) {
        LambdaQueryWrapper<MaterialMapping> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MaterialMapping> buildQueryWrapper(MaterialMappingBo bo) {
        LambdaQueryWrapper<MaterialMapping> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCustomerMaterialNo()), MaterialMapping::getCustomerMaterialNo, bo.getCustomerMaterialNo());
        lqw.like(StringUtils.isNotBlank(bo.getFactoryMaterialNo()), MaterialMapping::getFactoryMaterialNo, bo.getFactoryMaterialNo());
        lqw.like(StringUtils.isNotBlank(bo.getCustomerName()), MaterialMapping::getCustomerName, bo.getCustomerName());
        // 排序：优先按 sort 正序，然后按更新时间倒序，最后按创建时间倒序
        lqw.orderByAsc(MaterialMapping::getSort);
        lqw.orderByDesc(MaterialMapping::getUpdateTime);
        lqw.orderByDesc(MaterialMapping::getCreateTime);
        return lqw;
    }

    /**
     * 新增客户料号与厂内料号对应
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    @Override
    public Boolean insertByBo(MaterialMappingBo bo) {
        MaterialMapping add = MapstructUtils.convert(bo, MaterialMapping.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改客户料号与厂内料号对应
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    @Override
    public Boolean updateByBo(MaterialMappingBo bo) {
        MaterialMapping update = MapstructUtils.convert(bo, MaterialMapping.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(MaterialMapping entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量保存客户料号与厂内料号对应
     *
     * @param list 实体列表
     * @return 是否成功
     */
    @Override
    public Boolean saveBatch(List<MaterialMapping> list) {
        return baseMapper.insertBatch(list);
    }

    @Override
    public String getFactoryNoByCustomerNo(String customerNo) {
        MaterialMappingVo materialMappingVo = baseMapper.selectVoOne(new LambdaQueryWrapper<MaterialMapping>().eq(MaterialMapping::getCustomerMaterialNo, customerNo));
        return materialMappingVo!= null ? materialMappingVo.getFactoryMaterialNo() : null;
    }
}
