package org.dromara.huahao.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.huahao.domain.MaterialMapping;

import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

/**
 * 料号对应导入VO
 *
 * @author Hurj
 * @date 2026-02-03
 */
@Data
@NoArgsConstructor
@AutoMapper(target = MaterialMapping.class)
public class MaterialMappingImportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排序号
     */
    @ExcelProperty(value = "No.", index = 0, converter = cn.idev.excel.converters.integer.IntegerNumberConverter.class)
    private Integer sort;

    /**
     * 客户料号
     */
    @NotBlank(message = "客户料号不能为空")
    @ExcelProperty(value = "客户料号", index = 1)
    private String customerMaterialNo;

    /**
     * 厂内料号
     */
    @NotBlank(message = "厂内料号不能为空")
    @ExcelProperty(value = "厂内料号", index = 2)
    private String factoryMaterialNo;

}
