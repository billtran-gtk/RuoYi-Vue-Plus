package org.dromara.huahao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.HAcceptanceQr;
import org.dromara.huahao.domain.HAcceptanceRcPallet;
import org.dromara.huahao.domain.MaterialMapping;
import org.dromara.huahao.domain.bo.AcceptanceQrCodeBo;
import org.dromara.huahao.domain.bo.HAcceptanceDetailBo;
import org.dromara.huahao.domain.vo.AcceptanceQrCodeVo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;
import org.dromara.huahao.domain.vo.MaterialMappingVo;
import org.dromara.huahao.domain.vo.ReceivingFormVo;
import org.dromara.huahao.enums.ReceivingErrorCode;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.mapper.*;
import org.dromara.huahao.service.IAcceptanceDetailService;
import org.dromara.huahao.utils.DCLabelParser;
import org.dromara.huahao.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试单表Service业务层处理
 *
 * @author Lion Li
 * @date 2021-07-26
 */
@RequiredArgsConstructor
@Service
public class AcceptanceDetailServiceImpl implements IAcceptanceDetailService {

    private final AcceptanceDetailMapper baseMapper;
    private final AcceptanceQrMapper acceptanceQrMapper;
    private final AcceptanceRcPalletMapper acceptanceRcPalletMapper;
    private final MaterialMappingMapper materialMappingMapper;
    private final ReceivingFormMapper receivingFormMapper;

    @Value("${huahao.manufacturer}")
    private String manufacturer;
    @Value("${huahao.generate-type}")
    private String type;
    private final String GENERATE_TYPE = "ISO";

    @Override
    public TableDataInfo<HAcceptanceDetailVo> queryPageList(HAcceptanceDetailBo bo, PageQuery pageQuery) {
        Page<HAcceptanceDetailVo> page = pageQuery.build();

        // 查询总数
        Long count = baseMapper.selectPageListWithReceivedQuantityCount(bo);

        // 查询分页数据
        List<HAcceptanceDetailVo> list = baseMapper.selectPageListWithReceivedQuantity(page, bo);

        TableDataInfo<HAcceptanceDetailVo> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(count);

        return tableDataInfo;
    }

    @Override
    public List<HAcceptanceDetailVo> queryList(HAcceptanceDetailBo bo) {
        Page<HAcceptanceDetailVo> page = new Page<>();
        page.setCurrent(1); // 第一页
        page.setSize(Integer.MAX_VALUE); // 获取所有数据
        return baseMapper.selectPageListWithReceivedQuantity(page, bo);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<HAcceptanceDetail> buildQueryWrapper(HAcceptanceDetailBo bo) {
        LambdaQueryWrapper<HAcceptanceDetail> lqw = new LambdaQueryWrapper<>();
        lqw.like(bo.getReceiptNo() != null, HAcceptanceDetail::getReceiptNo, bo.getReceiptNo());
        lqw.like(bo.getAsnNo() != null, HAcceptanceDetail::getAsnNo, bo.getAsnNo());
        lqw.like(bo.getPartNo() != null, HAcceptanceDetail::getPartNo, bo.getPartNo());
        return lqw;
    }

    @Override
    public HAcceptanceDetailVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public int insertByBo(HAcceptanceDetailBo bo) {
        HAcceptanceDetail hAcceptanceDetail = MapstructUtils.convert(bo, HAcceptanceDetail.class);
        return baseMapper.insert(hAcceptanceDetail);
    }

    @Override
    public int updateByBo(HAcceptanceDetailBo bo) {
        HAcceptanceDetail hAcceptanceDetail = MapstructUtils.convert(bo, HAcceptanceDetail.class);
        return baseMapper.updateById(hAcceptanceDetail);
    }

    @Override
    public int deleteById(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public JSONObject selectDetail(String accNo) {
        List<HAcceptanceDetailVo> hAcceptanceDetailVos = baseMapper.selectReceivingForm(accNo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", hAcceptanceDetailVos);
        List<ReceivingFormVo> receivingFormVos = receivingFormMapper.selectPallet(accNo);
        jsonObject.put("pallet", CollectionUtils.isEmpty(receivingFormVos)? null: receivingFormVos.get(0).getPalletId());
        return jsonObject;
    }

    @Override
    public String generatePalletId() {
        StringBuilder palletId = new StringBuilder();
        // 使用ISO标准,还有美国标准，可以换方法
        String date;
        if (GENERATE_TYPE.equals(type)) {
            date = DateUtils.generateYearWeekFormat(LocalDate.now());
        } else {
            date = DateUtils.generateUSYearWeekFormat(LocalDate.now());
        }
        palletId.append("PL");
        palletId.append(date);
        // 厂商代码
        palletId.append(manufacturer);
        palletId.append("P");
        palletId.append(DateUtils.generateSerialNumber());
        HAcceptanceRcPallet hAcceptanceRcPallet = acceptanceRcPalletMapper.selectOne(new LambdaQueryWrapper<HAcceptanceRcPallet>().eq(HAcceptanceRcPallet::getPalletId, palletId.toString()));
        if (hAcceptanceRcPallet == null) {
            return palletId.toString();
        } else {
            return generatePalletId();
        }
    }

    @Override
    public String generateRcId() {
        StringBuilder rcId = new StringBuilder();
        // 使用ISO标准,还有美国标准，可以换方法
        String date;
        if (GENERATE_TYPE.equals(type)) {
            date = DateUtils.generateYearWeekFormat(LocalDate.now());
        } else {
            date = DateUtils.generateUSYearWeekFormat(LocalDate.now());
        }
        rcId.append(date);
        // 厂商代码
        rcId.append(manufacturer);
        rcId.append(DateUtils.generateSerialNumber());
        HAcceptanceRcPallet hAcceptanceRcPallet = acceptanceRcPalletMapper.selectOne(new LambdaQueryWrapper<HAcceptanceRcPallet>().eq(HAcceptanceRcPallet::getRcId, rcId.toString()));
        if (hAcceptanceRcPallet == null) {
            return rcId.toString();
        } else {
            return generateRcId();
        }

    }

    @Override
    public AcceptanceQrCodeVo queryByQrCode(AcceptanceQrCodeBo qrCodeBo) {
        HashMap<String, Object> map = new HashMap<>();
        //先根据二维码内容判断是什么类型的格式
        if (StringUtils.countMatches(qrCodeBo.getQrContent(), "||") == 4) {
            dealPN1(qrCodeBo, map);
        } else if (StringUtils.countMatches(qrCodeBo.getQrContent(), "$") == 9) {
            String[] split = qrCodeBo.getQrContent().split("\\$");
            editQrCodeBo(qrCodeBo);
            if ("1".equals(qrCodeBo.getTab()) && split.length > 8) {
                // 对应需求文档需求三
                dealPN8(qrCodeBo, map);
            } else {
                if (!"2".equals(qrCodeBo.getTab())) {
                    throw new ServiceException("请选择正确的标签");
                }
                // 对应需求文档需求二(带@)
                dealPN9(qrCodeBo, map);
            }
        } else if (StringUtils.countMatches(qrCodeBo.getQrContent(), "$") == 10) {
            // 有三种10个$情形，后续根据选择的tab判断
            String[] split = qrCodeBo.getQrContent().split("\\$");
            editQrCodeBo(qrCodeBo);
            // 需求六 06開頭且為Uitra廠商
            if (qrCodeBo.getQrContent().startsWith("Ultra") && split[2].startsWith("06")) {
                dealPN4(qrCodeBo, map);
            } else {
                if ("1".equals(qrCodeBo.getTab()) && split.length > 9) {
                    // 对应需求文档需求三
                    dealPN2(qrCodeBo, map);
                } else {
                    if (!"2".equals(qrCodeBo.getTab())) {
                        throw new ServiceException("请选择正确的标签");
                    }
                    // 对应需求文档需求二(带@)
                    dealPN3(qrCodeBo, map);
                }
            }
            // 12个"$"
        } else if (StringUtils.countMatches(qrCodeBo.getQrContent(), "$") == 12) {
            editQrCodeBo(qrCodeBo);
            dealPN5(qrCodeBo, map);
        } else if (StringUtils.countMatches(qrCodeBo.getQrContent(), "$") == 11) {
            String[] split = qrCodeBo.getQrContent().split("\\$");
            editQrCodeBo(qrCodeBo);
            if ("1".equals(qrCodeBo.getTab()) && split.length > 9) {
                dealPN6(qrCodeBo, map);
            } else {
                if (!"2".equals(qrCodeBo.getTab())) {
                    throw new ServiceException("请选择正确的标签");
                }
                // 对应需求文档需求二(带@)
                dealPN7(qrCodeBo, map);
            }
        }
        // 将解析前后数据进行留存vo解析后bo解析前
        AcceptanceQrCodeVo vo = BeanUtil.toBean(map.get("vo"), AcceptanceQrCodeVo.class);
        AcceptanceQrCodeVo bo = BeanUtil.toBean(map.get("bo"), AcceptanceQrCodeVo.class);
        HAcceptanceDetailVo hAcceptanceDetailVo = baseMapper.selectVoOne(new LambdaQueryWrapper<HAcceptanceDetail>().eq(HAcceptanceDetail::getReceiptNo, qrCodeBo.getReceiptNo()).eq(HAcceptanceDetail::getPartNo, vo.getPnNo()));
        if (hAcceptanceDetailVo != null) {
            vo.setMsl(hAcceptanceDetailVo.getMsl());
            vo.setLocator(hAcceptanceDetailVo.getLocator());
        }
        // 根据提前维护信息做固定pn码转换
        MaterialMappingVo materialMappingVo = materialMappingMapper.selectVoOne(new LambdaQueryWrapper<MaterialMapping>().eq(MaterialMapping::getCustomerMaterialNo, bo.getPnNo()));
        if (materialMappingVo != null) {
            vo.setPnNo(materialMappingVo.getFactoryMaterialNo());
        }

        // 检查料号是否存在于当前验收单中
        checkReceiptNo(qrCodeBo.getReceiptNo(), vo.getPnNo());

        HAcceptanceQr hAcceptanceQr = new HAcceptanceQr();
        hAcceptanceQr.setQrContent(qrCodeBo.getQrContent());
        hAcceptanceQr.setPnBefore(bo.getPnNo());
        hAcceptanceQr.setPnAfter(vo.getPnNo());
        hAcceptanceQr.setQtyBefore(bo.getQty());
        hAcceptanceQr.setQtyAfter(vo.getQty());
        hAcceptanceQr.setDcBefore(bo.getDcCode());
        vo.setDcCodeOriginal(bo.getDcCode());
        hAcceptanceQr.setDcAfter(vo.getDcCode());
        hAcceptanceQr.setLotBefore(bo.getLotNo());
        hAcceptanceQr.setLotAfter(vo.getLotNo());
        acceptanceQrMapper.insert(hAcceptanceQr);
        return vo;
    }

    public void editQrCodeBo(AcceptanceQrCodeBo qrCodeBo) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        HAcceptanceDetailVo hAcceptanceDetailVo = baseMapper.selectVoOne(new LambdaQueryWrapper<HAcceptanceDetail>()
            .eq(HAcceptanceDetail::getReceiptNo, qrCodeBo.getReceiptNo())
            .like(HAcceptanceDetail::getPartNo, split[2].trim()));
        if (hAcceptanceDetailVo != null) {
            qrCodeBo.setPartNo(hAcceptanceDetailVo.getPartNo().substring(0, 4));
        } else {
            qrCodeBo.setPartNo(split[2].trim());
        }
    }

    public void checkReceiptNo(String receiptNo, String partNo) {
        // 检查料号是否存在于当前验收单中
        LambdaQueryWrapper<HAcceptanceDetail> detailWrapper = Wrappers.lambdaQuery();
        detailWrapper.eq(HAcceptanceDetail::getReceiptNo, receiptNo);
        detailWrapper.eq(HAcceptanceDetail::getPartNo, partNo);
        Long detailCount = baseMapper.selectCount(detailWrapper);

        if (detailCount == 0) {
            // 料号不在验收单中
            throw new ReceivingException(
                ReceivingErrorCode.PART_NO_NOT_IN_ACCEPTANCE.getMessage(),
                ReceivingErrorCode.PART_NO_NOT_IN_ACCEPTANCE.getCode()
            );
        }
    }

    @Override
    public AcceptanceQrCodeVo queryByOneQrCode(AcceptanceQrCodeBo qrCodeBo) {
        // 二維碼內容無需要的PQDL信息、無2D、無Gemtek PN
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        String content = qrCodeBo.getQrContent();
        if (!Character.isDigit(qrCodeBo.getQrContent().charAt(0))) {
            String qrContent = qrCodeBo.getQrContent();
            int firstDigitIndex = 0;
            for (int i = 0; i < qrContent.length(); i++) {
                if (Character.isDigit(qrContent.charAt(i))) {
                    firstDigitIndex = i;
                    break;
                }
            }
            content = qrContent.substring(firstDigitIndex);
        }
        // 只有第一次生成rcid，后面不生成
        if (1 == qrCodeBo.getIndex()) {
            vo.setRcId(generateRcId());
            vo.setPnNo(content);
            // 根据提前维护信息做固定pn码转换
            MaterialMappingVo materialMappingVo = materialMappingMapper.selectVoOne(new LambdaQueryWrapper<MaterialMapping>()
                .eq(MaterialMapping::getCustomerMaterialNo, vo.getPnNo()));
            if (materialMappingVo != null) {
                vo.setPnNo(materialMappingVo.getFactoryMaterialNo());
            }
            HAcceptanceDetailVo hAcceptanceDetailVo = baseMapper.selectVoOne(new LambdaQueryWrapper<HAcceptanceDetail>().eq(HAcceptanceDetail::getReceiptNo, qrCodeBo.getReceiptNo()).like(HAcceptanceDetail::getPartNo, vo.getPnNo()));
            if (hAcceptanceDetailVo != null) {
                vo.setMsl(hAcceptanceDetailVo.getMsl());
                vo.setLocator(hAcceptanceDetailVo.getLocator());
            }
            // 检查料号是否存在于当前验收单中
            checkReceiptNo(qrCodeBo.getReceiptNo(), vo.getPnNo());

        } else if (2 == qrCodeBo.getIndex()) {
            vo.setQty(Integer.parseInt(content));
        } else if (3 == qrCodeBo.getIndex()) {
            vo.setDcCode(DCLabelParser.autoParseDC(content).dc);
            vo.setDcCodeOriginal(content);
        } else if (4 == qrCodeBo.getIndex()) {
            vo.setLotNo(content);
        }

        return vo;
    }

    @Override
    public void saveBatch(List<HAcceptanceDetail> list) throws Exception {
        // 一个验收单只能有一个验收单号，同时校验库里是否存在
        // 校验 list 是否为空
        if (list == null || list.isEmpty()) {
            throw new Exception("importDataNullFailed");
        }

        // 校验 list 中所有 receiptNo 是否一致（使用分组统计）
        Map<String, Long> receiptNoCount = list.stream()
            .collect(Collectors.groupingBy(
                HAcceptanceDetail::getReceiptNo,
                Collectors.counting()));

        if (receiptNoCount.size() > 1) {
            throw new Exception("importDataDiffFailed");
        }

        Long count = baseMapper.selectCount(new LambdaQueryWrapper<HAcceptanceDetail>().eq(HAcceptanceDetail::getReceiptNo, list.get(0).getReceiptNo()));
        if (count > 0) {
            throw new Exception("importDataSysFailed");
        }

        baseMapper.insertBatch(list);
    }

    public void dealPN1(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\|\\|");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        // rcId生成
        vo.setRcId(generateRcId());
        vo.setPnNo(split[1].trim().substring(1));
        vo.setQty(Integer.parseInt(split[2].trim().substring(1)));
        // 需要判断dcCode是否是YYWW格式，若不是做转换
        vo.setDcCode(split[3].trim().substring(1));
        vo.setLotNo(split[4].trim().substring(1));
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[1].trim());
        bo.setQty(Integer.parseInt(split[2].trim().substring(1)));
        bo.setDcCode(split[3].trim().substring(1));
        bo.setLotNo(split[4].trim());
        map.put("bo", bo);
    }

    public void dealPN2(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());
        try {
            vo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        vo.setDcCode(DCLabelParser.autoParseDC(split[5].trim()).dc);
        vo.setLotNo(split.length > 9 ? split[9].trim() : "");
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5].trim());
        bo.setLotNo(split.length > 9 ? split[9].trim() : "");
        map.put("bo", bo);
    }

    public void dealPN3(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());

        try {
            vo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        // 获取当前日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        vo.setDcCode(DCLabelParser.autoParseDC(dateStr.substring(2)).dc);
        try {
            vo.setLotNo(split[9].trim() + "@" + split[5].trim());
        } catch (Exception e) {
            vo.setLotNo("@" + split[5].trim());
        }
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5].trim());
        try {
            bo.setLotNo(split[9].trim());
        } catch (Exception e) {
            bo.setLotNo("");
        }
        map.put("bo", bo);
    }

    public void dealPN4(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());
        try {
            vo.setQty(Integer.parseInt(split[5].trim()));
        } catch (Exception e) {
            String numericStr = split[5].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        vo.setDcCode(DCLabelParser.autoParseDC(split[6].trim()).dc);
        try {
            vo.setLotNo(split[10].trim());
        } catch (ArrayIndexOutOfBoundsException e) {
            vo.setLotNo("");
        }
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        bo.setQty(Integer.parseInt(split[5].trim()));
        try {
            bo.setQty(Integer.parseInt(split[5].trim()));
        } catch (Exception e) {
            String numericStr = split[5].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[6].trim());
        try {
            bo.setLotNo(split[10].trim());
        } catch (ArrayIndexOutOfBoundsException e) {
            bo.setLotNo("");
        }
        map.put("bo", bo);
    }

    public void dealPN5(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());
        try {
            vo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }

        try {
            if ("PALwonn".equals(split[0].trim())) {
                vo.setLotNo(split[6].trim());
                vo.setDcCode(DCLabelParser.autoParseDC(split[5].trim()).dc);
            } else {
                vo.setLotNo(split[11].trim());
                vo.setDcCode(DCLabelParser.autoParseDC(split[7].trim()).dc);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            vo.setLotNo("");
            vo.setDcCode("");
        }
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }

        try {
            if ("PALwonn".equals(split[0].trim())) {
                bo.setLotNo(split[6].trim());
                bo.setDcCode(split[5].trim());
            } else {
                bo.setLotNo(split[11].trim());
                bo.setDcCode(split[7].trim());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            bo.setLotNo("");
            bo.setDcCode("");
        }
        map.put("bo", bo);
    }

    public void dealPN6(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());
        try {
            vo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        vo.setDcCode(DCLabelParser.autoParseDC(split[5].trim()).dc);
        vo.setLotNo(split.length > 9 ? split[9].trim() : "");
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5]);
        bo.setLotNo(split.length > 9 ? split[9].trim() : "");
        map.put("bo", bo);
    }

    public void dealPN7(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());

        try {
            vo.setQty(Integer.parseInt(split[4]));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        // 获取当前日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        vo.setDcCode(DCLabelParser.autoParseDC(dateStr.substring(2)).dc);
        try {
            vo.setLotNo(split[9].trim() + "@" + split[5].trim());
        } catch (Exception e) {
            vo.setLotNo("@" + split[5].trim());
        }
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5].trim());
        try {
            bo.setLotNo(split[9].trim());
        } catch (Exception e) {
            bo.setLotNo("");
        }
        map.put("bo", bo);
    }

    public void dealPN8(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());
        try {
            vo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        vo.setDcCode(DCLabelParser.autoParseDC(split[5].trim()).dc);
        vo.setLotNo(split.length > 8 ? split[6].trim() : "");
        vo.setMsl(StringUtils.isNotBlank(qrCodeBo.getMsl()) ? qrCodeBo.getMsl() : "NA");
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5].trim());
        bo.setLotNo(split.length > 8 ? split[6].trim() : "");
        map.put("bo", bo);
    }

    public void dealPN9(AcceptanceQrCodeBo qrCodeBo, HashMap<String, Object> map) {
        String[] split = qrCodeBo.getQrContent().split("\\$");
        AcceptanceQrCodeVo vo = new AcceptanceQrCodeVo();
        vo.setRcId(generateRcId());
        vo.setPnNo(qrCodeBo.getPartNo().substring(0, 4) + "-" + split[2].trim());

        try {
            vo.setQty(Integer.parseInt(split[4]));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            vo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        // 获取当前日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        vo.setDcCode(DCLabelParser.autoParseDC(dateStr.substring(2)).dc);
        try {
            vo.setLotNo(split[6].trim() + "@" + split[5].trim());
        } catch (Exception e) {
            vo.setLotNo("@" + split[5].trim());
        }
        map.put("vo", vo);

        // 保存源数据（解析前）
        AcceptanceQrCodeVo bo = new AcceptanceQrCodeVo();
        bo.setPnNo(split[2].trim());
        try {
            bo.setQty(Integer.parseInt(split[4].trim()));
        } catch (Exception e) {
            String numericStr = split[4].trim().replaceAll("[^0-9]", "");
            bo.setQty(StringUtils.isNotBlank(numericStr) ? Integer.parseInt(numericStr) : 0);
        }
        bo.setDcCode(split[5].trim());
        try {
            bo.setLotNo(split[6].trim());
        } catch (Exception e) {
            bo.setLotNo("");
        }
        map.put("bo", bo);
    }

}
