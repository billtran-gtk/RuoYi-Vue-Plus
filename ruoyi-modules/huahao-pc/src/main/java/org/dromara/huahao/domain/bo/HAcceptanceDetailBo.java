package org.dromara.huahao.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.huahao.domain.HAcceptanceDetail;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = HAcceptanceDetail.class, reverseConvertGenerate = false)
public class HAcceptanceDetailBo extends BaseEntity {

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
