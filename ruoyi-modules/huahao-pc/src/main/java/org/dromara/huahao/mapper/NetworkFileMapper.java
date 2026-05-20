package org.dromara.huahao.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.huahao.domain.NetworkFile;
import org.dromara.huahao.domain.vo.NetworkFileVo;

@org.apache.ibatis.annotations.Mapper
@org.springframework.stereotype.Repository
public interface NetworkFileMapper extends BaseMapperPlus<NetworkFile, NetworkFileVo> {
}
