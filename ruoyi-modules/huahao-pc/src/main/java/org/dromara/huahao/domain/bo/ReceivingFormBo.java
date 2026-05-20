package org.dromara.huahao.domain.bo;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.huahao.domain.ReceivingForm;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 收料明细业务对象 h_receiving_form
 *
 * @author Hurj
 * @date 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ReceivingForm.class, reverseConvertGenerate = false)
public class ReceivingFormBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * R/C ID
     */
    private String rcId;

    /**
     * 料号
     */
    @NotBlank(message = "料号不能为空", groups = {AddGroup.class, EditGroup.class})
    private String partNo;

    /**
     * 验收单号
     */
    private String accNo;

    /**
     * Quality数量
     */
    private Integer quantity;

    /**
     * Date Code日期代码
     */
    private String dc;

    /**
     * Date Code原始值
     */
    private String dcCodeOriginal;

    /**
     * Lot Code批次代码
     */
    private String lot;

    /**
     * MSL Level湿度敏感等级
     */
    private String msl;

    /**
     * Location库位
     */
    private String location;

    /**
     * AsnNO送货单号
     */
    private String asnNo;

    /**
     * PalletID托盘ID
     */
    private String palletId;

    /**
     * 标签类型（1:常规,2:98/99,3:一维码）
     */
    private Integer label;

    /**
     * 收料时间
     */
    private Date receivingTime;

    /**
     * 是否提交(0-否 1-是)
     */
    private String flag;

    /**
     * 备注
     */
    private String remark;

}
