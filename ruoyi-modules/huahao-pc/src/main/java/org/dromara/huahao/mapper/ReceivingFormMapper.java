package org.dromara.huahao.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.huahao.domain.ReceivingForm;
import org.dromara.huahao.domain.vo.ReceivingFormVo;

import java.util.List;

/**
 * 收料明细Mapper接口
 *
 * @author Hurj
 * @date 2026-02-04
 */
public interface ReceivingFormMapper extends BaseMapperPlus<ReceivingForm, ReceivingFormVo> {
    List<ReceivingFormVo> selectPallet(String accNo);
}
