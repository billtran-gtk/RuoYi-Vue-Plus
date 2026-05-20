package org.dromara.huahao.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.bo.HAcceptanceDetailBo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;

import java.util.List;


/**
 * 验收单明细Mapper接口
 *
 * @author Hurj
 * @date 2026-02-09
 */
public interface AcceptanceDetailMapper extends BaseMapperPlus<HAcceptanceDetail, HAcceptanceDetailVo> {

    /**
     * 查询验收单明细列表
     *
     * @param accNo 验收单编号
     * @return 验收单明细列表
     */
    List<HAcceptanceDetailVo> selectReceivingForm(String accNo);

    /**
     * 分页查询验收单明细列表，包含实收数量
     *
     * @param page 分页对象
     * @param bo 查询条件
     * @return 验收单明细列表
     */
    List<HAcceptanceDetailVo> selectPageListWithReceivedQuantity(Page<HAcceptanceDetailVo> page, HAcceptanceDetailBo bo);

    /**
     * 查询验收单明细总数，包含实收数量统计
     *
     * @param bo 查询条件
     * @return 总数
     */
    Long selectPageListWithReceivedQuantityCount(HAcceptanceDetailBo bo);
}
