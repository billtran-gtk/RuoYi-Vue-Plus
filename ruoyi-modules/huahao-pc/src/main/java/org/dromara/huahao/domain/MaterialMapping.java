package org.dromara.huahao.domain;

import com.baomidou.mybatisplus.annotation.*;
import org.dromara.common.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 客户料号与厂内料号对应对象 h_material_mapping
 *
 * @author Hurj
 * @date 2026-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("h_material_mapping")
public class MaterialMapping extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 客户料号
     */
    private String customerMaterialNo;

    /**
     * 厂内料号
     */
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

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;

}
