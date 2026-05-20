package org.dromara.huahao.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.huahao.constant.ErrorCodeConstants;
import org.dromara.huahao.domain.MaterialMapping;
import org.dromara.huahao.domain.bo.MaterialMappingBo;
import org.dromara.huahao.domain.vo.MaterialMappingImportVo;
import org.dromara.huahao.domain.vo.MaterialMappingVo;
import org.dromara.huahao.service.IMaterialMappingService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 客户料号与厂内料号对应Controller
 *
 * @author Hurj
 * @date 2026-02-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/huahao/materialMapping")
public class MaterialMappingController extends BaseController {

    private final IMaterialMappingService materialMappingService;

    /**
     * 查询客户料号与厂内料号对应列表
     */
    //  @SaCheckPermission("huahao:materialMapping:list")
    @GetMapping("/list")
    public TableDataInfo<MaterialMappingVo> list(MaterialMappingBo bo, PageQuery pageQuery) {
        return materialMappingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出客户料号与厂内料号对应列表
     *
     */
    //  @SaCheckPermission("huahao:materialMapping:export")
    @Log(title = "客户料号与厂内料号对应", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MaterialMappingBo bo, HttpServletResponse response) {
        List<MaterialMappingVo> list = materialMappingService.queryList(bo);
        // 手动转换为导入导出格式（只包含：No. | 客户料号 | 厂内料号）
        List<MaterialMappingImportVo> exportList = list.stream().map(vo -> {
            MaterialMappingImportVo importVo = new MaterialMappingImportVo();
            importVo.setSort(vo.getSort());
            importVo.setCustomerMaterialNo(vo.getCustomerMaterialNo());
            importVo.setFactoryMaterialNo(vo.getFactoryMaterialNo());
            return importVo;
        }).collect(java.util.stream.Collectors.toList());
        ExcelUtil.exportExcel(exportList, "料号对应数据", MaterialMappingImportVo.class, response);
    }

    /**
     * 获取客户料号与厂内料号对应详细信息
     *
     * @param id 主键
     */
    //  @SaCheckPermission("huahao:materialMapping:query")
    @GetMapping("/{id}")
    public R<MaterialMappingVo> getInfo(@NotNull(message = "主键不能为空")
                                        @PathVariable Long id) {
        return R.ok(materialMappingService.queryById(id));
    }

    /**
     * 根据客户料号查询厂内料号
     */
    @GetMapping("/getFactoryNo")
    public R<String> getFactoryNoByCustomerNo(@NotNull(message = "厂内料号不能为空")
                                              @RequestParam String customerMaterialNo) {
        return R.ok(materialMappingService.getFactoryNoByCustomerNo(customerMaterialNo));
    }

    /**
     * 新增客户料号与厂内料号对应
     */
    //  @SaCheckPermission("huahao:materialMapping:add")
    @Log(title = "客户料号与厂内料号对应", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MaterialMappingBo bo) {
        return toAjax(materialMappingService.insertByBo(bo));
    }

    /**
     * 修改客户料号与厂内料号对应
     */
    //  @SaCheckPermission("huahao:materialMapping:edit")
    @Log(title = "客户料号与厂内料号对应", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MaterialMappingBo bo) {
        return toAjax(materialMappingService.updateByBo(bo));
    }

    /**
     * 删除客户料号与厂内料号对应
     *
     * @param ids 主键串
     */
    //  @SaCheckPermission("huahao:materialMapping:remove")
    @Log(title = "客户料号与厂内料号对应", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(materialMappingService.deleteWithValidByIds(Arrays.asList(ids), true));
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @Log(title = "料号对应", businessType = BusinessType.IMPORT)
    //  @SaCheckPermission("huahao:materialMapping:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file) throws Exception {
        ExcelResult<MaterialMappingImportVo> excelResult = null;
        try {
            excelResult = ExcelUtil.importExcel(
                file.getInputStream(),
                MaterialMappingImportVo.class,
                true
            );
        } catch (Exception e) {
            // 返回错误码，由前端根据语言显示对应的错误消息
            return R.fail(ErrorCodeConstants.IMPORT_DATA_FAILED);
        }
        List<MaterialMapping> list = MapstructUtils.convert(excelResult.getList(), MaterialMapping.class);
        materialMappingService.saveBatch(list);
        return R.ok(excelResult.getAnalysis());
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "料号对应数据", MaterialMappingImportVo.class, response);
    }
}
