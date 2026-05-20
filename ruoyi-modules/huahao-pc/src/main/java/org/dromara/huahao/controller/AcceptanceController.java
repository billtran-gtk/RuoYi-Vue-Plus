package org.dromara.huahao.controller;

import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.huahao.constant.ErrorCodeConstants;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.HAcceptanceForm;
import org.dromara.huahao.domain.RWithErrorCode;
import org.dromara.huahao.domain.bo.AcceptanceQrCodeBo;
import org.dromara.huahao.domain.bo.HAcceptanceDetailBo;
import org.dromara.huahao.domain.bo.HAcceptanceFormBo;
import org.dromara.huahao.domain.vo.AcceptanceQrCodeVo;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;
import org.dromara.huahao.domain.vo.HAcceptanceFormVo;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.service.IAcceptanceDetailService;
import org.dromara.huahao.service.IAcceptanceFormService;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/pda/acceptance")
public class AcceptanceController extends BaseController {

    private final IAcceptanceDetailService acceptanceDetailService;

    private final IAcceptanceFormService acceptanceFormService;

    /**
     * 查询验收单列表
     */
    @GetMapping("/list")
    public TableDataInfo<HAcceptanceDetailVo> list(HAcceptanceDetailBo bo, PageQuery pageQuery) {
        return acceptanceDetailService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取验收单详细信息
     *
     * @param id 验收单ID
     */
    @GetMapping("/{id}")
    public R<HAcceptanceDetailVo> getInfo(@PathVariable @NotNull(message = "主键不能为空") Long id) {
        return R.ok(acceptanceDetailService.queryById(id));
    }

    /**
     * 新增验收单
     */
    @PostMapping()
    public R<Void> add(@Validated @RequestBody HAcceptanceDetailBo bo) {
        return toAjax(acceptanceDetailService.insertByBo(bo));
    }

    /**
     * 修改验收单
     */
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody HAcceptanceDetailBo bo) {
        return toAjax(acceptanceDetailService.updateByBo(bo));
    }

    /**
     * 删除验收单
     *
     * @param id 验收单ID
     */
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        return toAjax(acceptanceDetailService.deleteById(id));
    }

    /**
     * 导入验收单数据
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file) throws Exception {
        ExcelResult<HAcceptanceDetailVo> excelResult = null;
        try {
            excelResult = ExcelUtil.importExcel(file.getInputStream(), HAcceptanceDetailVo.class, true);
        } catch (Exception e) {
            // 返回错误码，由前端根据语言显示对应的错误消息
            return R.fail(ErrorCodeConstants.IMPORT_DATA_FAILED);
        }
        List<HAcceptanceDetail> list = MapstructUtils.convert(excelResult.getList(), HAcceptanceDetail.class);
        acceptanceDetailService.saveBatch(list);
        // 一个验收单号文件里只会有一个验收单号，所以直接取第一个
        if (!CollectionUtils.isEmpty(list)) {
            acceptanceFormService.saveDetailList(list.get(0), file.getOriginalFilename());
        }
        return R.ok(excelResult.getAnalysis());
    }

    /**
     * 获取验收单下拉列表
     */
    @GetMapping("/select/option/list")
    public R<List<HAcceptanceFormVo>> selectOptionList() {
        return R.ok(acceptanceFormService.selectOptionList());
    }

    /**
     * 获取验收单号列表（用于下拉框筛选）
     */
    @GetMapping("/accNoList")
    public R<List<String>> getAccNoList() {
        return R.ok(acceptanceFormService.getAccNoList());
    }

    /**
     * 获取验收单下拉选项列表（包含文件名和验收单号）
     */
    @GetMapping("/accNoOptions")
    public R<List<HAcceptanceFormVo>> getAccNoOptions() {
        return R.ok(acceptanceFormService.getAccNoOptions());
    }

    /**
     * 查询验收单导入报告列表（分页）
     */
    @GetMapping("/form/list")
    public TableDataInfo<HAcceptanceFormVo> listAcceptanceForm(PageQuery pageQuery, HAcceptanceFormVo bo) {
        return acceptanceFormService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取验收单详情列表
     */
    @GetMapping("/select/detail")
    public R<JSONObject> selectDetail(@RequestParam String accNo) {
        return R.ok(acceptanceDetailService.selectDetail(accNo));
    }

    /**
     * 生成palletId
     */
    @GetMapping("/generate/palletId")
    public R<String> generatePalletId() {
        return R.ok(null, acceptanceDetailService.generatePalletId());
    }

    /**
     * 生成rcId
     */
    @GetMapping("/generate/rcId")
    public R<String> generateRcId() {
        return R.ok(null, acceptanceDetailService.generateRcId());
    }

    /**
     * 扫描二维码
     */
    @PostMapping("/scan/QR")
    public RWithErrorCode<AcceptanceQrCodeVo> scanQR(@RequestBody AcceptanceQrCodeBo bo) {
        try {
            return RWithErrorCode.ok("操作成功", acceptanceDetailService.queryByQrCode(bo));
        } catch (ReceivingException e) {
            return RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            // 处理其他异常
            return RWithErrorCode.fail(e.getMessage(), null);
        }
    }

    /**
     * 扫描一维码
     */
    @PostMapping("/scan/one/QR")
    public RWithErrorCode<AcceptanceQrCodeVo> scanOneQR(@RequestBody AcceptanceQrCodeBo bo) {
        try {
            return RWithErrorCode.ok("操作成功", acceptanceDetailService.queryByOneQrCode(bo));
        } catch (ReceivingException e) {
            return RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            // 处理其他异常
            return RWithErrorCode.fail(e.getMessage(), null);
        }
    }

    /**
     * 导出验收单数据
     */
    @Log(title = "验收单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HAcceptanceDetailBo bo, HttpServletResponse response) {
        // 导出所有数据，不分页
        List<HAcceptanceDetailVo> list = acceptanceDetailService.queryList(bo);
        ExcelUtil.exportExcel(list, "验收单数据", HAcceptanceDetailVo.class, response);
    }

    /**
     * 导出验收单数据
     */
    @PostMapping("/form/export")
    public void formExport(HAcceptanceFormBo bo, HttpServletResponse response) {
        // 导出所有数据，不分页
        List<HAcceptanceFormVo> list = acceptanceFormService.queryList(bo);
        ExcelUtil.exportExcel(list, "验收单数据", HAcceptanceFormVo.class, response);
    }

    /**
     * 删除验收单数据
     */
    @DeleteMapping("/form/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(acceptanceFormService.deleteByIds(Arrays.asList(ids)));
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "验收单数据", HAcceptanceDetailVo.class, response);
    }
}
