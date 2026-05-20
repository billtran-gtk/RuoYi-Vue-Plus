package org.dromara.huahao.domain;

import com.baomidou.mybatisplus.annotation.*;
import org.dromara.common.tenant.core.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * 收料明细对象 h_receiving_form
 *
 * @author Hurj
 * @date 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("h_receiving_form")
public class ReceivingForm extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * R/C ID
     */
    private String rcId;

    /**
     * 料号
     */
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
     * 标签类型（0:常规,1:98/99）
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

    /**
     * 删除标志（已取消逻辑删除，改为物理删除）
     */
    private String delFlag;

}
