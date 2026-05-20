package org.dromara.huahao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Map;

import org.dromara.huahao.constant.ErrorCodeConstants;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.NetworkFile;
import org.dromara.huahao.domain.ReceivingForm;
import org.dromara.huahao.domain.HAcceptanceForm;
import org.dromara.huahao.domain.bo.ReceivingFormBo;
import org.dromara.huahao.domain.vo.NetworkFileVo;
import org.dromara.huahao.domain.vo.ReceivingFormVo;
import org.dromara.huahao.domain.vo.AsnExportVo;
import org.dromara.huahao.domain.vo.AsnExportNoPalletVo;
import org.dromara.huahao.enums.ReceivingErrorCode;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.mapper.NetworkFileMapper;
import org.dromara.huahao.mapper.ReceivingFormMapper;
import org.dromara.huahao.mapper.AcceptanceFormMapper;
import org.dromara.huahao.mapper.AcceptanceDetailMapper;
import org.dromara.huahao.service.IReceivingFormService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 收料明细Service业务层处理
 *
 * @author Hurj
 * @date 2026-02-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReceivingFormServiceImpl implements IReceivingFormService {

    private final ReceivingFormMapper baseMapper;
    private final AcceptanceFormMapper acceptanceFormMapper;
    private final AcceptanceDetailMapper acceptanceDetailMapper;
    private final NetworkFileMapper networkFileMapper;
//    @Value("${huahao.network-folder-asn-path}")
//    private String asnFilePath;

    /**
     * 查询收料明细
     *
     * @param id 主键
     * @return ReceivingFormVo
     */
    @Override
    public ReceivingFormVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询收料明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public TableDataInfo<ReceivingFormVo> queryPageList(ReceivingFormBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ReceivingForm> lqw = buildQueryWrapper(bo);
        Page<ReceivingFormVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的收料明细列表
     *
     * @param bo 查询条件
     * @return 列表
     */
    @Override
    public List<ReceivingFormVo> queryList(ReceivingFormBo bo) {
        LambdaQueryWrapper<ReceivingForm> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ReceivingForm> buildQueryWrapper(ReceivingFormBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ReceivingForm> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getRcId()), ReceivingForm::getRcId, bo.getRcId());
        lqw.like(StringUtils.isNotBlank(bo.getPartNo()), ReceivingForm::getPartNo, bo.getPartNo());
        lqw.like(StringUtils.isNotBlank(bo.getAccNo()), ReceivingForm::getAccNo, bo.getAccNo());
        lqw.like(StringUtils.isNotBlank(bo.getAsnNo()), ReceivingForm::getAsnNo, bo.getAsnNo());
        lqw.eq(StringUtils.isNotBlank(bo.getFlag()), ReceivingForm::getFlag, bo.getFlag());
        // 收料时间范围查询
        lqw.between(params.get("beginReceivingTime") != null && params.get("endReceivingTime") != null,
            ReceivingForm::getReceivingTime, params.get("beginReceivingTime"), params.get("endReceivingTime"));
        // 排序：按收料时间倒序，然后按创建时间倒序
        lqw.orderByDesc(ReceivingForm::getReceivingTime);
        lqw.orderByDesc(ReceivingForm::getCreateTime);
        return lqw;
    }

    /**
     * 新增收料明细
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    @Override
    public Boolean insertByBo(ReceivingFormBo bo) {
        ReceivingForm add = MapstructUtils.convert(bo, ReceivingForm.class);

        // 设置默认值
        if (add.getFlag() == null) {
            add.setFlag("0");  // 默认未提交
        }
        if (add.getReceivingTime() == null) {
            add.setReceivingTime(new Date());  // 默认当前时间
        }

        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * PDA端新增收料明细
     * 专门用于PDA扫码收料场景
     *
     * @param bo 业务对象
     * @return 该验收单和料号对应的收料数量总和
     */
    @Override
    public Integer insertByPda(ReceivingFormBo bo) {
        ReceivingForm add = MapstructUtils.convert(bo, ReceivingForm.class);

        // PDA端特殊处理：强制设置默认值
        add.setFlag("0");  // 强制设置为未提交
        add.setReceivingTime(new Date());  // 强制设置为当前时间

        // PDA端可以添加额外的业务逻辑
        // 例如：验证库位是否存在、检查料号是否有效等
        validEntityBeforePdaSave(add);

        // 如果 msl 为空，从验收单明细中获取
        if (StringUtils.isBlank(add.getMsl()) && StringUtils.isNotBlank(add.getAccNo()) && StringUtils.isNotBlank(add.getPartNo())) {
            String mslFromAcceptance = getMslFromAcceptance(add.getAccNo(), add.getPartNo());
            if (StringUtils.isNotBlank(mslFromAcceptance)) {
                add.setMsl(mslFromAcceptance);
            } else {
                // 如果验收单里也没有正确的数据，则设置为"NA"
                add.setMsl("NA");
            }
        }

        // 检查该料号的应收数量是否已满足
        checkQuantityBeforeAdd(add);

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());

            // 业务逻辑：更新验收单状态
            // 如果验收单号不为空，且验收单状态为"0"（未开始），则更新为"1"（收料中）
            if (StringUtils.isNotBlank(add.getAccNo())) {
                updateAcceptanceFormStatus(add.getAccNo());
            }

            // 查询并返回该验收单和料号对应的收料数量总和
            return getTotalQuantity(add.getAccNo(), add.getPartNo());
        }

        // 插入失败返回0
        return 0;
    }

    /**
     * 检查该料号的应收数量是否已满足
     * 如果已收数量 >= 应收数量，则抛出异常
     *
     * @param entity 收料实体
     */
    private void checkQuantityBeforeAdd(ReceivingForm entity) {
        // 1. 查询验收单明细，获取应收数量
        LambdaQueryWrapper<HAcceptanceDetail> detailWrapper = Wrappers.lambdaQuery();
        detailWrapper.eq(HAcceptanceDetail::getReceiptNo, entity.getAccNo());
        detailWrapper.eq(HAcceptanceDetail::getPartNo, entity.getPartNo());

        HAcceptanceDetail detail = acceptanceDetailMapper.selectOne(detailWrapper);

        if (detail == null) {
            // 验收单明细不存在
            throw new ReceivingException(
                ReceivingErrorCode.ACC_DETAIL_NOT_FOUND.getMessage(),
                ReceivingErrorCode.ACC_DETAIL_NOT_FOUND.getCode()
            );
        }

        Integer expectedQuantity = detail.getQuantity();  // 应收数量
        if (expectedQuantity == null) {
            expectedQuantity = 0;
        }

        // 2. 查询已收数量
        Integer receivedQuantity = getTotalQuantity(entity.getAccNo(), entity.getPartNo());

        // 3. 检查是否已满足
        if (receivedQuantity >= expectedQuantity) {
            // 已收数量 >= 应收数量，不允许继续添加
            throw new ReceivingException(
                String.format("料号[%s]应收数量已满足，无法继续添加收料明细", entity.getPartNo()),
                ReceivingErrorCode.RECEIVING_QUANTITY_SATISFIED.getCode()
            );
        }

        // 4. 检查本次收料后是否会超过应收数量
        Integer currentQuantity = entity.getQuantity();
        if (currentQuantity == null) {
            currentQuantity = 0;
        }

        if (receivedQuantity + currentQuantity > expectedQuantity) {
            // 本次收料后会超过应收数量，给出警告信息
            throw new ReceivingException(
                String.format("料号[%s]本次收料数量[%d]将超过应收数量，应收[%d]，已收[%d]，剩余[%d]",
                    entity.getPartNo(),
                    currentQuantity,
                    expectedQuantity,
                    receivedQuantity,
                    expectedQuantity - receivedQuantity),
                ReceivingErrorCode.RECEIVING_QUANTITY_EXCEED.getCode()
            );
        }
    }

    /**
     * 查询指定验收单和料号的收料数量总和
     *
     * @param accNo  验收单号
     * @param partNo 料号
     * @return 数量总和
     */
    private Integer getTotalQuantity(String accNo, String partNo) {
        LambdaQueryWrapper<ReceivingForm> wrapper = Wrappers.lambdaQuery();

        // 如果验收单号不为空，添加验收单号条件
        if (StringUtils.isNotBlank(accNo)) {
            wrapper.eq(ReceivingForm::getAccNo, accNo);
        }

        // 料号条件（必须）
        wrapper.eq(ReceivingForm::getPartNo, partNo);

        // 查询所有符合条件的记录
        List<ReceivingForm> list = baseMapper.selectList(wrapper);

        // 计算数量总和
        int totalQuantity = 0;
        for (ReceivingForm form : list) {
            if (form.getQuantity() != null) {
                totalQuantity += form.getQuantity();
            }
        }

        return totalQuantity;
    }

    /**
     * 更新验收单状态
     * 如果验收单状态为"0"（未开始），则更新为"1"（收料中）
     *
     * @param accNo 验收单号
     */
    private void updateAcceptanceFormStatus(String accNo) {
        // 查询验收单
        LambdaQueryWrapper<HAcceptanceForm> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HAcceptanceForm::getAccNo, accNo);
        wrapper.eq(HAcceptanceForm::getStatus, "0");  // 只查询状态为"0"的

        HAcceptanceForm acceptanceForm = acceptanceFormMapper.selectOne(wrapper);

        // 如果找到状态为"0"的验收单，则更新为"1"
        if (acceptanceForm != null) {
            acceptanceForm.setStatus("1");  // 更新为"收料中"
            acceptanceFormMapper.updateById(acceptanceForm);
        }
    }

    /**
     * 从验收单明细中获取 MSL
     *
     * @param accNo  验收单号
     * @param partNo 料号
     * @return MSL 值，如果未找到则返回 null
     */
    private String getMslFromAcceptance(String accNo, String partNo) {
        LambdaQueryWrapper<HAcceptanceDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HAcceptanceDetail::getReceiptNo, accNo);
        wrapper.eq(HAcceptanceDetail::getPartNo, partNo);

        HAcceptanceDetail detail = acceptanceDetailMapper.selectOne(wrapper);

        if (detail != null) {
            return detail.getMsl();
        }

        return null;
    }

    /**
     * 修改收料明细
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    @Override
    public Boolean updateByBo(ReceivingFormBo bo) {
        ReceivingForm update = MapstructUtils.convert(bo, ReceivingForm.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(ReceivingForm entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * PDA端保存前的数据校验
     * 可以添加PDA特有的校验逻辑
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforePdaSave(ReceivingForm entity) {
        // 基础校验
        validEntityBeforeSave(entity);

        // PDA端特殊校验

        // 1. 检查rc_id不能为空（必填）
        if (StringUtils.isBlank(entity.getRcId())) {
            throw new ReceivingException(
                ReceivingErrorCode.RC_ID_REQUIRED.getMessage(),
                ReceivingErrorCode.RC_ID_REQUIRED.getCode()
            );
        }

        // 2. 检查rc_id是否已存在（防止重复提交）
        LambdaQueryWrapper<ReceivingForm> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ReceivingForm::getRcId, entity.getRcId());
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ReceivingException(
                ReceivingErrorCode.RC_ID_DUPLICATE.getMessage(),
                ReceivingErrorCode.RC_ID_DUPLICATE.getCode()
            );
        }

        // 3. 检查料号不能为空
        if (StringUtils.isBlank(entity.getPartNo())) {
            throw new ReceivingException(
                ReceivingErrorCode.PART_NO_REQUIRED.getMessage(),
                ReceivingErrorCode.PART_NO_REQUIRED.getCode()
            );
        }

        // 4. 检查验收单号不能为空
        if (StringUtils.isBlank(entity.getAccNo())) {
            throw new ReceivingException(
                ReceivingErrorCode.ACC_NO_REQUIRED.getMessage(),
                ReceivingErrorCode.ACC_NO_REQUIRED.getCode()
            );
        }

        // 5. 检查料号是否存在于当前验收单中
        LambdaQueryWrapper<HAcceptanceDetail> detailWrapper = Wrappers.lambdaQuery();
        detailWrapper.eq(HAcceptanceDetail::getReceiptNo, entity.getAccNo());
        detailWrapper.eq(HAcceptanceDetail::getPartNo, entity.getPartNo());
        Long detailCount = acceptanceDetailMapper.selectCount(detailWrapper);

        if (detailCount == 0) {
            // 料号不在验收单中
            throw new ReceivingException(
                String.format("料号[%s]不在验收单[%s]中，无法添加收料明细",
                    entity.getPartNo(), entity.getAccNo()),
                ReceivingErrorCode.PART_NO_NOT_IN_ACCEPTANCE.getCode()
            );
        }

        // 6. 检查label值是否合法（1:常规, 2:ASUS 98/99, 3:一维码）
        if (entity.getLabel() == null || entity.getLabel() < 1 || entity.getLabel() > 3) {
            throw new ReceivingException(
                ReceivingErrorCode.LABEL_INVALID.getMessage(),
                ReceivingErrorCode.LABEL_INVALID.getCode()
            );
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        // 物理删除：取消 @TableLogic 注解后，直接删除数据库记录
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 生成ASN文件
     * 导出指定验收单号的未提交数据，并将flag更新为1
     *
     * @param accNo    验收单号
     * @param response HTTP响应对象
     */
    @Override
    public void generateAsnFile(String accNo, HttpServletResponse response) throws ReceivingException {
        // 1. 查询指定验收单号的未提交数据（flag=0）
        LambdaQueryWrapper<ReceivingForm> lqw = Wrappers.lambdaQuery();
        lqw.eq(ReceivingForm::getAccNo, accNo);
        lqw.eq(ReceivingForm::getFlag, "0");
        lqw.orderByAsc(ReceivingForm::getReceivingTime);

        List<ReceivingFormVo> dataList = baseMapper.selectVoList(lqw);

        if (dataList.isEmpty()) {
            throw new ReceivingException("没有找到未提交的数据", ErrorCodeConstants.NO_UNSUBMITTED_DATA);
        }

        // 2. 检查是否需要显示 Pallet 列
        // 只有 label=2（ASUS 98/99）且 palletId 不为空时，才显示 Pallet 列
        // label=1（常规）、label=3（一维码）不显示 Pallet 列
        boolean hasPallet = false;
        for (ReceivingFormVo vo : dataList) {
            // 只有 label=2 且 palletId 不为空，才需要显示 Pallet 列
            if (vo.getLabel() != null && vo.getLabel() == 2 &&
                vo.getPalletId() != null && !vo.getPalletId().isEmpty()) {
                hasPallet = true;
                break;
            }
        }

        // 3. 转换为ASN格式的数据
        // 生成本地文件名
        String fileName = generateLocalFileName(accNo);
        NetworkFileVo networkFileVo = networkFileMapper.selectVoOne(new LambdaQueryWrapper<NetworkFile>()
            .eq(NetworkFile::getType, "2")
            .eq(NetworkFile::getIsEnable, "0")
            .orderByDesc(NetworkFile::getCreateTime)
            .last("LIMIT 1")
        );
        if (networkFileVo == null || StringUtils.isEmpty(networkFileVo.getFilePath())) {
            throw new ReceivingException("生成ASN文件失败", ErrorCodeConstants.GENERATE_ASN_FILE_FAILED);
        }
        log.info("文件名：{}", fileName);
        if (hasPallet) {
            // 有Pallet数据，使用完整的AsnExportVo
            List<AsnExportVo> asnDataList = new ArrayList<>();
            for (ReceivingFormVo vo : dataList) {
                AsnExportVo asnVo = new AsnExportVo();
                asnVo.setAsn(vo.getAsnNo());           // ASN
                asnVo.setCartonId("R"+vo.getRcId());       // Carton ID (料盘/箱号)
                asnVo.setPart("P"+vo.getPartNo());         // Part (料号)
                asnVo.setQty("Q"+vo.getQuantity());        // Qty (数量)
                asnVo.setDate("D"+vo.getDc());             // Date (生产周期)
                asnVo.setLot("L"+vo.getLot());             // Lot (批次号)
                // Pallet: 只有 label=2（ASUS 98/99）时才使用 palletId，其他情况为空
                if (vo.getLabel() != null && vo.getLabel() == 2) {
                    asnVo.setPallet(vo.getPalletId());  // label=2 时使用 palletId
                } else {
                    asnVo.setPallet("");  // label=1、3 时 Pallet 为空
                }
                asnDataList.add(asnVo);
            }

            // 导出包含Pallet列的Excel文件
            try {
                // 先保存到本地磁盘
                saveAsnFile(asnDataList, fileName, AsnExportVo.class, networkFileVo.getFilePath());
                log.info("保存ASN文件1成功：{}", fileName);
                // 再通过HTTP响应导出
                ExcelUtil.exportExcel(asnDataList, "ASN", AsnExportVo.class, response);
            } catch (Exception e) {
                throw new ReceivingException("生成ASN文件失败", ErrorCodeConstants.GENERATE_ASN_FAILED);
            }
        } else {
            // 没有Pallet数据，使用不含Pallet列的AsnExportNoPalletVo
            List<AsnExportNoPalletVo> asnDataList = new ArrayList<>();
            for (ReceivingFormVo vo : dataList) {
                AsnExportNoPalletVo asnVo = new AsnExportNoPalletVo();
                asnVo.setAsn(vo.getAsnNo());           // ASN
                asnVo.setCartonId("R"+vo.getRcId());       // Carton ID (料盘/箱号)
                asnVo.setPart("P"+vo.getPartNo());         // Part (料号)
                asnVo.setQty("Q"+vo.getQuantity());        // Qty (数量)
                asnVo.setDate("D"+vo.getDc());             // Date (生产周期)
                asnVo.setLot("L"+vo.getLot());             // Lot (批次号)
                asnDataList.add(asnVo);
            }

            // 导出不含Pallet列的Excel文件
            try {
                // 先保存到本地磁盘
                saveAsnFile(asnDataList, fileName, AsnExportNoPalletVo.class, networkFileVo.getFilePath());
                log.info("保存ASN文件2成功：{}", fileName);
                // 再通过HTTP响应导出
                ExcelUtil.exportExcel(asnDataList, "ASN", AsnExportNoPalletVo.class, response);
            } catch (Exception e) {
                throw new ReceivingException("生成ASN文件失败", ErrorCodeConstants.GENERATE_ASN_FAILED);
            }
        }

        // 4. 更新flag为1（已提交）
        List<Long> ids = dataList.stream().map(ReceivingFormVo::getId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            ReceivingForm updateEntity = new ReceivingForm();
            updateEntity.setFlag("1");
            baseMapper.update(updateEntity, Wrappers.lambdaQuery(ReceivingForm.class).in(ReceivingForm::getId, ids));
        }
    }

    /**
     * 生成本地文件名
     * 格式: ASN_验收单号_时间戳.xlsx
     *
     * @param accNo 验收单号
     * @return 生成的文件名
     */
    private String generateLocalFileName(String accNo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("ASN_%s_%s.xlsx", accNo, timestamp);
    }

    /**
     * 保存ASN文件到本地磁盘
     *
     * @param dataList 数据列表
     * @param fileName 文件名
     * @param clazz    数据类型class
     * @param <T>      泛型类型
     * @throws Exception 保存异常
     */
    private <T> void saveAsnFile(List<T> dataList, String fileName, Class<T> clazz, String asnFilePath) throws Exception {
        // 确保目录存在
        File directory = new File(asnFilePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 构造完整文件路径
        String fullPath = asnFilePath + File.separator + fileName;
        File file = new File(fullPath);

        // 使用ExcelUtil导出到文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            ExcelUtil.exportExcel(dataList, "ASN", clazz, fos);
        }
    }
}
