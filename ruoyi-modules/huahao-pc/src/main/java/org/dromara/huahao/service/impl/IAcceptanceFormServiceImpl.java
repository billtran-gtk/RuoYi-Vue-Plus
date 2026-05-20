package org.dromara.huahao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.HAcceptanceForm;
import org.dromara.huahao.domain.ReceivingForm;
import org.dromara.huahao.domain.bo.HAcceptanceFormBo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;
import org.dromara.huahao.domain.vo.HAcceptanceFormVo;
import org.dromara.huahao.domain.vo.QuantityDiffVo;
import org.dromara.huahao.enums.ReceivingErrorCode;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.mapper.AcceptanceFormMapper;
import org.dromara.huahao.mapper.AcceptanceDetailMapper;
import org.dromara.huahao.mapper.ReceivingFormMapper;
import org.dromara.huahao.service.IAcceptanceFormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IAcceptanceFormServiceImpl implements IAcceptanceFormService {
    private final AcceptanceFormMapper baseMapper;
    private final AcceptanceDetailMapper acceptanceDetailMapper;
    private final ReceivingFormMapper receivingFormMapper;

    @Override
    public void saveDetailList(HAcceptanceDetail hAcceptanceDetail, String fileName) {
        HAcceptanceForm hAcceptanceForm = new HAcceptanceForm();
        hAcceptanceForm.setAccNo(hAcceptanceDetail.getReceiptNo());
        // todo
        hAcceptanceForm.setFileName(hAcceptanceDetail.getReceiptNo() + "--" + hAcceptanceDetail.getAsnNo());
        baseMapper.insert(hAcceptanceForm);
    }

    @Override
    public List<HAcceptanceFormVo> selectOptionList() {
        return baseMapper.selectVoList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<String> getAccNoList() {
        List<HAcceptanceForm> list = baseMapper.selectList(
            new LambdaQueryWrapper<HAcceptanceForm>()
                .select(HAcceptanceForm::getAccNo)
                .orderByDesc(HAcceptanceForm::getCreateTime)
        );
        List<String> accNoList = new ArrayList<>();
        for (HAcceptanceForm form : list) {
            if (form.getAccNo() != null && !form.getAccNo().isEmpty()) {
                accNoList.add(form.getAccNo());
            }
        }
        return accNoList;
    }

    @Override
    public List<HAcceptanceFormVo> getAccNoOptions() {
        return baseMapper.selectVoList(
            new LambdaQueryWrapper<HAcceptanceForm>()
                .select(HAcceptanceForm::getAccNo, HAcceptanceForm::getFileName)
                .orderByDesc(HAcceptanceForm::getCreateTime)
        );
    }

    @Override
    public TableDataInfo<HAcceptanceFormVo> queryPageList(HAcceptanceFormVo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<HAcceptanceForm> lqw = buildQueryWrapper(bo);
        Page<HAcceptanceFormVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<HAcceptanceForm> buildQueryWrapper(HAcceptanceFormVo bo) {
        LambdaQueryWrapper<HAcceptanceForm> lqw = new LambdaQueryWrapper<>();
        lqw.like(bo.getAccNo() != null && !bo.getAccNo().isEmpty(), HAcceptanceForm::getAccNo, bo.getAccNo());
        lqw.eq(bo.getStatus() != null && !bo.getStatus().isEmpty(), HAcceptanceForm::getStatus, bo.getStatus());
        lqw.like(bo.getFileName() != null && !bo.getFileName().isEmpty(), HAcceptanceForm::getFileName, bo.getFileName());

        // 日期范围查询
        if (bo.getParams() != null) {
            lqw.between(bo.getParams().get("beginCreateTime") != null && bo.getParams().get("endCreateTime") != null,
                HAcceptanceForm::getCreateTime, bo.getParams().get("beginCreateTime"), bo.getParams().get("endCreateTime"));
        }

        lqw.orderByDesc(HAcceptanceForm::getCreateTime);
        return lqw;
    }

    /**
     * PDA端完成收料
     * 将验收单状态更新为"2"（已完成）
     * 验证所有料号的应收数量是否等于已收数量
     *
     * @param accNo 验收单号
     * @return 数量差异列表（如果为空表示验证通过并完成收料）
     */
    @Override
    public List<QuantityDiffVo> completeReceiving(String accNo) {
        // 参数校验
        if (StringUtils.isBlank(accNo)) {
            throw new ReceivingException(
                ReceivingErrorCode.ACC_NO_REQUIRED.getMessage(),
                ReceivingErrorCode.ACC_NO_REQUIRED.getCode()
            );
        }

        // 查询验收单
        LambdaQueryWrapper<HAcceptanceForm> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HAcceptanceForm::getAccNo, accNo);

        HAcceptanceForm acceptanceForm = baseMapper.selectOne(wrapper);

        // 验收单不存在
        if (acceptanceForm == null) {
            throw new ReceivingException(
                ReceivingErrorCode.ACC_NO_NOT_FOUND.getMessage() + "：" + accNo,
                ReceivingErrorCode.ACC_NO_NOT_FOUND.getCode()
            );
        }

        // 检查当前状态
        String currentStatus = acceptanceForm.getStatus();

        // 如果已经是"已完成"状态，直接返回成功
        if ("2".equals(currentStatus)) {
            return new ArrayList<>();
        }

        // 如果是"未开始"状态，提示需要先开始收料
        if ("0".equals(currentStatus)) {
            throw new ReceivingException(
                ReceivingErrorCode.ACC_NOT_STARTED.getMessage(),
                ReceivingErrorCode.ACC_NOT_STARTED.getCode()
            );
        }

        // 验证所有料号的应收数量是否等于已收数量
        List<QuantityDiffVo> diffList = validateReceivingQuantity(accNo);

        // 如果有数量不一致的料号，返回差异列表
        if (!diffList.isEmpty()) {
            return diffList;
        }

        // 更新状态为"2"（已完成）
        acceptanceForm.setStatus("2");
        baseMapper.updateById(acceptanceForm);

        return new ArrayList<>();
    }

    @Override
    public List<HAcceptanceFormVo> queryList(HAcceptanceFormBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<HAcceptanceForm> lqw = Wrappers.lambdaQuery();
        // 收料时间范围查询
        if (params != null) {
            lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
                HAcceptanceForm::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        }
        lqw.orderByDesc(HAcceptanceForm::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByIds(List<Long> ids) {
        ids.forEach(id -> {
            HAcceptanceFormVo hAcceptanceFormVo = baseMapper.selectVoById(id);
            // 删除验收单明细
            LambdaQueryWrapper<HAcceptanceDetail> detailWrapper = Wrappers.lambdaQuery();
            detailWrapper.eq(HAcceptanceDetail::getReceiptNo, hAcceptanceFormVo.getAccNo());
            acceptanceDetailMapper.delete(detailWrapper);

            // 删除收料单
            LambdaQueryWrapper<ReceivingForm> receivingFormWrapper = Wrappers.lambdaQuery();
            receivingFormWrapper.eq(ReceivingForm::getAccNo, hAcceptanceFormVo.getAccNo());
            receivingFormMapper.delete(receivingFormWrapper);
        });
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 验证验收单的应收数量和已收数量是否一致
     *
     * @param accNo 验收单号
     * @return 数量差异列表（如果为空表示验证通过）
     */
    private List<QuantityDiffVo> validateReceivingQuantity(String accNo) {
        // 1. 查询验收单明细（应收数量）
        LambdaQueryWrapper<HAcceptanceDetail> detailWrapper = Wrappers.lambdaQuery();
        detailWrapper.eq(HAcceptanceDetail::getReceiptNo, accNo);
        List<HAcceptanceDetail> detailList = acceptanceDetailMapper.selectList(detailWrapper);

        if (detailList == null || detailList.isEmpty()) {
            throw new ReceivingException(
                ReceivingErrorCode.ACC_DETAIL_NOT_FOUND.getMessage(),
                ReceivingErrorCode.ACC_DETAIL_NOT_FOUND.getCode()
            );
        }

        // 2. 构建应收数量 Map（料号 -> 应收数量）
        Map<String, Integer> expectedQuantityMap = new HashMap<>();
        for (HAcceptanceDetail detail : detailList) {
            String partNo = detail.getPartNo();
            Integer quantity = detail.getQuantity();
            if (partNo != null && quantity != null) {
                expectedQuantityMap.put(partNo, quantity);
            }
        }

        // 3. 查询收料明细（已收数量）
        LambdaQueryWrapper<ReceivingForm> receivingWrapper = Wrappers.lambdaQuery();
        receivingWrapper.eq(ReceivingForm::getAccNo, accNo);
        List<ReceivingForm> receivingList = receivingFormMapper.selectList(receivingWrapper);

        // 4. 构建已收数量 Map（料号 -> 已收数量总和）
        Map<String, Integer> receivedQuantityMap = new HashMap<>();
        for (ReceivingForm receiving : receivingList) {
            String partNo = receiving.getPartNo();
            Integer quantity = receiving.getQuantity();
            if (partNo != null && quantity != null) {
                receivedQuantityMap.put(partNo,
                    receivedQuantityMap.getOrDefault(partNo, 0) + quantity);
            }
        }

        // 5. 验证每个料号的应收数量和已收数量是否一致
        List<QuantityDiffVo> diffList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : expectedQuantityMap.entrySet()) {
            String partNo = entry.getKey();
            Integer expectedQty = entry.getValue();
            Integer receivedQty = receivedQuantityMap.getOrDefault(partNo, 0);

            if (!expectedQty.equals(receivedQty)) {
                QuantityDiffVo diff = new QuantityDiffVo();
                diff.setPartNo(partNo);
                diff.setExpected(expectedQty);
                diff.setReceived(receivedQty);
                diff.setDiff(expectedQty - receivedQty);
                diffList.add(diff);
            }
        }

        return diffList;
    }
}
