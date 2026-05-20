package org.dromara.huahao.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.bo.NetworkFileBo;
import org.dromara.huahao.domain.vo.NetworkFileVo;

import java.util.List;

public interface NetworkFileService {
    /**
     * 新增
     */
    Boolean insertByBo(NetworkFileBo bo);

    /**
     * 查询网络文件列表
     */
    TableDataInfo<NetworkFileVo> queryPageList(NetworkFileBo bo, PageQuery pageQuery);

    /**
     * 获取网络文件详细信息
     */
    NetworkFileVo queryById(Long id);

    /**
     * 修改网络文件
     */
    Boolean updateByBo(NetworkFileBo bo);

    /**
     * 获取验收单网络共享文件路径
     */
    NetworkFileVo getAcceptanceFilePath();

    /**
     *批删除网络文件
     */
    Boolean deleteByIds(List<Long> ids);
}
