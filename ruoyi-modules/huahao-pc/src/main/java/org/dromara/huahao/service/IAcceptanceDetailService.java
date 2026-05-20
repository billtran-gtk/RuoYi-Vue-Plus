package org.dromara.huahao.service;

import cn.hutool.json.JSONObject;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.bo.AcceptanceQrCodeBo;
import org.dromara.huahao.domain.bo.HAcceptanceDetailBo;
import org.dromara.huahao.domain.vo.AcceptanceQrCodeVo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;

import java.util.List;

/**
 * 测试单表Service接口
 *
 * @author Lion Li
 * @date 2021-07-26
 */
public interface IAcceptanceDetailService {

    /**
     * 查询列表
     */
    TableDataInfo<HAcceptanceDetailVo> queryPageList(HAcceptanceDetailBo bo, PageQuery pageQuery);

    /**
     * 查询列表（不分页）
     */
    List<HAcceptanceDetailVo> queryList(HAcceptanceDetailBo bo);

    /**
     * 批量保存
     */
    void saveBatch(List<HAcceptanceDetail> list) throws  Exception;

    /**
     * 根据id查询
     */
    HAcceptanceDetailVo queryById(Long id);

    /**
     * 新增
     */
    int insertByBo(HAcceptanceDetailBo bo);

    /**
     * 更新
     */
    int updateByBo(HAcceptanceDetailBo bo);

    /**
     * 删除
     */
    int deleteById(Long id);

    /**
     * 根据验收单号查询验收单详情
     */
    JSONObject selectDetail(String accNo);

    /**
     * 生成palletId
     */
    String generatePalletId();
    /**
     * 生成rcId
     */
    String generateRcId();

    /**
     * 扫描二维码
     */
    AcceptanceQrCodeVo queryByQrCode(AcceptanceQrCodeBo bo);

    /**
     * 扫描一维码
     */
    AcceptanceQrCodeVo queryByOneQrCode(AcceptanceQrCodeBo bo);
}
