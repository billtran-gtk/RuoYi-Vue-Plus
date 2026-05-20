package org.dromara.huahao.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.huahao.domain.MaterialMapping;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 客户料号与厂内料号对应视图对象 h_material_mapping
 *
 * @author Hurj
 * @date 2026-02-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MaterialMapping.class)
public class MaterialMappingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Integer sort;

    /**
     * 客户料号
     */
    @ExcelProperty(value = "客户料号")
    private String customerMaterialNo;

    /**
     * 厂内料号
     */
    @ExcelProperty(value = "厂内料号")
    private String factoryMaterialNo;

    /**
     * 客户名称
     */
    @ExcelProperty(value = "客户名称")
    private String customerName;

    /**
     * 物料描述
     */
    @ExcelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
