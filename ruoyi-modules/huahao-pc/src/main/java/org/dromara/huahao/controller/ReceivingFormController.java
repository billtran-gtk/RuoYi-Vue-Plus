package org.dromara.huahao.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.huahao.domain.bo.ReceivingFormBo;
import org.dromara.huahao.domain.vo.ReceivingFormVo;
import org.dromara.huahao.domain.vo.QuantityDiffVo;
import org.dromara.huahao.domain.RWithErrorCode;
import org.dromara.huahao.enums.ReceivingErrorCode;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.service.IReceivingFormService;
import org.dromara.huahao.service.IAcceptanceFormService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 收料明细Controller
 *
 * @author Hurj
 * @date 2026-02-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/huahao/receivingForm")
public class ReceivingFormController extends BaseController {

    private final IReceivingFormService receivingFormService;
    private final IAcceptanceFormService acceptanceFormService;

    /**
     * 查询收料明细列表
     */
    //  @SaCheckPermission("huahao:receivingForm:list")
    @GetMapping("/list")
    public TableDataInfo<ReceivingFormVo> list(ReceivingFormBo bo, PageQuery pageQuery) {
        return receivingFormService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出收料明细列表
     */
    //  @SaCheckPermission("huahao:receivingForm:export")
    @Log(title = "收料明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ReceivingFormBo bo, HttpServletResponse response) {
        List<ReceivingFormVo> list = receivingFormService.queryList(bo);
        ExcelUtil.exportExcel(list, "收料明细数据", ReceivingFormVo.class, response);
    }

    /**
     * 生成ASN文件
     * 导出指定验收单号的未提交数据，并将flag更新为1
     *
     * @param accNo 验收单号
     */
    //  @SaCheckPermission("huahao:receivingForm:export")
    @Log(title = "生成ASN文件", businessType = BusinessType.EXPORT)
    @PostMapping("/generateAsn")
    public void generateAsn(@RequestParam String accNo, HttpServletResponse response) {
        try {
            receivingFormService.generateAsnFile(accNo, response);
        } catch (ReceivingException e) {
            // 处理业务异常，返回JSON错误信息
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");

                RWithErrorCode<Void> result = RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
                response.getWriter().write(JsonUtils.toJsonString(result));
            } catch (Exception ex) {
                // 写入响应失败，记录日志
                ex.printStackTrace();
            }
        } catch (Exception e) {
            // 处理其他异常
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");

                RWithErrorCode<Void> result = RWithErrorCode.fail(e.getMessage(), null);
                response.getWriter().write(JsonUtils.toJsonString(result));
            } catch (Exception ex) {
                // 写入响应失败，记录日志
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取收料明细详细信息
     *
     * @param id 主键
     */
    //  @SaCheckPermission("huahao:receivingForm:query")
    @GetMapping("/{id}")
    public R<ReceivingFormVo> getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        return R.ok(receivingFormService.queryById(id));
    }

    /**
     * 新增收料明细
     */
    //  @SaCheckPermission("huahao:receivingForm:add")
    @Log(title = "收料明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ReceivingFormBo bo) {
        return toAjax(receivingFormService.insertByBo(bo));
    }

    /**
     * PDA端新增收料记录
     * 用于PDA扫码后添加收料记录
     *
     * @param bo 收料信息
     * @return 操作结果，data 中包含该验收单和料号的收料数量总和
     */
    @Log(title = "PDA收料", businessType = BusinessType.INSERT)
    @RepeatSubmit(interval = 2000)  // 2秒防重复提交，适应PDA快速扫码场景
    @PostMapping("/pda/add")
    public RWithErrorCode<Integer> pdaAdd(@Validated(AddGroup.class) @RequestBody ReceivingFormBo bo) {
        try {
            Integer totalQuantity = receivingFormService.insertByPda(bo);
            return RWithErrorCode.ok("操作成功", totalQuantity);
        } catch (ReceivingException e) {
            // 处理 PDA 收料业务异常
            return RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            // 处理其他异常
            return RWithErrorCode.fail(e.getMessage(), null);
        }
    }

    /**
     * PDA端完成收料
     * 将验收单状态更新为"2"（已完成）
     *
     * @param accNo 验收单号
     * @return 操作结果
     */
    @Log(title = "PDA完成收料", businessType = BusinessType.UPDATE)
    @RepeatSubmit(interval = 3000)  // 3秒防重复提交
    @PostMapping("/pda/complete")
    public RWithErrorCode<List<QuantityDiffVo>> pdaComplete(
        @NotBlank(message = "验收单号不能为空") @RequestParam String accNo) {
        try {
            List<QuantityDiffVo> diffList = acceptanceFormService.completeReceiving(accNo);

            if (diffList.isEmpty()) {
                // 验证通过，完成收料
                return RWithErrorCode.ok("操作成功", null);
            } else {
                // 数量不一致，返回差异列表
                // 构建中文错误消息（用于调试）
                StringBuilder errorMsg = new StringBuilder();
                for (QuantityDiffVo diff : diffList) {
                    if (errorMsg.length() > 0) {
                        errorMsg.append("；");
                    }
                    errorMsg.append(String.format("料号[%s]应收%d，已收%d",
                        diff.getPartNo(), diff.getExpected(), diff.getReceived()));
                }

                return RWithErrorCode.fail(
                    "收料数量不完整，无法完成收料：" + errorMsg.toString(),
                    ReceivingErrorCode.RECEIVING_QUANTITY_INCOMPLETE.getCode(),
                    diffList
                );
            }
        } catch (ReceivingException e) {
            // 处理 PDA 收料业务异常
            return RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            // 处理其他异常
            return RWithErrorCode.fail(e.getMessage(), null);
        }
    }

    /**
     * 修改收料明细
     */
    //  @SaCheckPermission("huahao:receivingForm:edit")
    @Log(title = "收料明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ReceivingFormBo bo) {
        return toAjax(receivingFormService.updateByBo(bo));
    }

    /**
     * 删除收料明细
     *
     * @param ids 主键串
     */
    //  @SaCheckPermission("huahao:receivingForm:remove")
    @Log(title = "收料明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(receivingFormService.deleteWithValidByIds(Arrays.asList(ids), true));
    }
}
