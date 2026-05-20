package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.SysVersion;
import org.dromara.system.domain.bo.SysVersionBo;
import org.dromara.system.domain.vo.SysVersionVo;

import java.util.Collection;
import java.util.List;

public interface ISysVersionService {
    /**
     * 获取最新app安装包版本
     */
    SysVersion getLatestAPK();

    /**
     * 查询app安装包版本管理
     *
     * @param versionId 主键
     * @return app安装包版本管理
     */
    SysVersionVo queryById(Long versionId);

    /**
     * 分页查询app安装包版本管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return app安装包版本管理分页列表
     */
    TableDataInfo<SysVersionVo> queryPageList(SysVersionBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的app安装包版本管理列表
     *
     * @param bo 查询条件
     * @return app安装包版本管理列表
     */
    List<SysVersionVo> queryList(SysVersionBo bo);

    /**
     * 新增app安装包版本管理
     *
     * @param bo app安装包版本管理
     * @return 是否新增成功
     */
    Boolean insertByBo(SysVersionBo bo);

    /**
     * 修改app安装包版本管理
     *
     * @param bo app安装包版本管理
     * @return 是否修改成功
     */
    Boolean updateByBo(SysVersionBo bo);

    /**
     * 校验并批量删除app安装包版本管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
