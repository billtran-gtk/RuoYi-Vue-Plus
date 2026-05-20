package org.dromara.huahao.domain.bo;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.huahao.domain.MaterialMapping;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 客户料号与厂内料号对应业务对象 h_material_mapping
 *
 * @author Hurj
 * @date 2026-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MaterialMapping.class, reverseConvertGenerate = false)
public class MaterialMappingBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 客户料号
     */
    @NotBlank(message = "客户料号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String customerMaterialNo;

    /**
     * 厂内料号
     */
    @NotBlank(message = "厂内料号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String factoryMaterialNo;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 备注
     */
    private String remark;

}
