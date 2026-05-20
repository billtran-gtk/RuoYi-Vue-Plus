package org.dromara.huahao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("h_acceptance_detail")
public class HAcceptanceDetail extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 验收单号
     */
    private String receiptNo;
    /**
     * 供应商号
     */
    private String asnNo;
    /**
     * 料号
     */
    private String partNo;
    /**
     * 应收数量
     */
    private Integer quantity;
    /**
     * 温湿度级别
     */
    private String msl;

    /**
     * 仓位
     */
    private String locator;

}
