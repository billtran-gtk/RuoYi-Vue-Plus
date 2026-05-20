package org.dromara.huahao.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.bo.HAcceptanceFormBo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;
import org.dromara.huahao.domain.vo.HAcceptanceFormVo;
import org.dromara.huahao.domain.vo.QuantityDiffVo;

import java.util.List;

public interface IAcceptanceFormService {
    /**
     * 导入验收单
     *
     * @param hAcceptanceDetail 验收单详情
     * @param fileName          文件名
     */
    void saveDetailList(HAcceptanceDetail hAcceptanceDetail, String fileName);

    /**
     * 获取验收单下拉列表
     *
     * @return 验收单下拉列表
     */
    List<HAcceptanceFormVo> selectOptionList();

    /**
     * 获取所有验收单号列表
     *
     * @return 验收单号列表
     */
    List<String> getAccNoList();

    /**
     * 获取验收单下拉选项列表（包含文件名和验收单号）
     *
     * @return 验收单选项列表
     */
    List<HAcceptanceFormVo> getAccNoOptions();

    /**
     * 分页查询验收单列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HAcceptanceFormVo> queryPageList(HAcceptanceFormVo bo, PageQuery pageQuery);

    /**
     * PDA端完成收料
     * 将验收单状态更新为"2"（已完成）
     *
     * @param accNo 验收单号
     * @return 数量差异列表（如果为空表示验证通过并完成收料）
     */
    List<QuantityDiffVo> completeReceiving(String accNo);

    /**
     * 查询验收单列表
     *
     * @param bo 查询条件
     * @return 验收单列表
     */
    List<HAcceptanceFormVo> queryList(HAcceptanceFormBo bo);

    /**
     * 删除验收单
     *
     * @param ids 主键串
     * @return 删除结果
     */
    Boolean deleteByIds(List<Long> ids);
}
