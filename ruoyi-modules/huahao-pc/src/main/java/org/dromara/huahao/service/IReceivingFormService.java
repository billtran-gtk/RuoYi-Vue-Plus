package org.dromara.huahao.service;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.bo.ReceivingFormBo;
import org.dromara.huahao.domain.vo.ReceivingFormVo;
import org.dromara.huahao.exception.ReceivingException;

import java.util.Collection;
import java.util.List;

/**
 * 收料明细Service接口
 *
 * @author Hurj
 * @date 2026-02-04
 */
public interface IReceivingFormService {

    /**
     * 查询收料明细
     *
     * @param id 主键
     * @return ReceivingFormVo
     */
    ReceivingFormVo queryById(Long id);

    /**
     * 分页查询收料明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<ReceivingFormVo> queryPageList(ReceivingFormBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的收料明细列表
     *
     * @param bo 查询条件
     * @return 列表
     */
    List<ReceivingFormVo> queryList(ReceivingFormBo bo);

    /**
     * 新增收料明细
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean insertByBo(ReceivingFormBo bo);

    /**
     * PDA端新增收料明细
     * 专门用于PDA扫码收料场景
     *
     * @param bo 业务对象
     * @return 该验收单和料号对应的收料数量总和
     */
    Integer insertByPda(ReceivingFormBo bo);

    /**
     * 修改收料明细
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean updateByBo(ReceivingFormBo bo);

    /**
     * 校验并批量删除收料明细信息
     *
     * @param ids 待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 生成ASN文件
     * 导出指定验收单号的未提交数据，并将flag更新为1
     *
     * @param accNo 验收单号
     * @param response HTTP响应对象
     */
    void generateAsnFile(String accNo, HttpServletResponse response) throws ReceivingException;
}
