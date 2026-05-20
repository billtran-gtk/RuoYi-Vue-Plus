package org.dromara.huahao.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.MaterialMapping;
import org.dromara.huahao.domain.bo.MaterialMappingBo;
import org.dromara.huahao.domain.vo.MaterialMappingVo;

import java.util.Collection;
import java.util.List;

/**
 * 客户料号与厂内料号对应Service接口
 *
 * @author Hurj
 * @date 2026-02-03
 */
public interface IMaterialMappingService {

    /**
     * 查询客户料号与厂内料号对应
     *
     * @param id 主键
     * @return MaterialMappingVo
     */
    MaterialMappingVo queryById(Long id);

    /**
     * 分页查询客户料号与厂内料号对应列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MaterialMappingVo> queryPageList(MaterialMappingBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的客户料号与厂内料号对应列表
     *
     * @param bo 查询条件
     * @return 列表
     */
    List<MaterialMappingVo> queryList(MaterialMappingBo bo);

    /**
     * 新增客户料号与厂内料号对应
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean insertByBo(MaterialMappingBo bo);

    /**
     * 修改客户料号与厂内料号对应
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean updateByBo(MaterialMappingBo bo);

    /**
     * 校验并批量删除客户料号与厂内料号对应信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 批量保存客户料号与厂内料号对应
     *
     * @param list 实体列表
     * @return 是否成功
     */
    Boolean saveBatch(List<MaterialMapping> list);

    /**
     * 根据客户料号获取厂内料号
     *
     * @param customerNo 客户料号
     * @return 厂内料号
     */
    String getFactoryNoByCustomerNo(String customerNo);
}
