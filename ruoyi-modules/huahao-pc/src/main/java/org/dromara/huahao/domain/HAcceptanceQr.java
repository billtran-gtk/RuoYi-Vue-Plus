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
@TableName("h_acceptance_qr")
public class HAcceptanceQr extends BaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 二维码内容
     */
    private String qrContent;
    /**
     * 料号解析前
     */
    private String pnBefore;
    /**
     * 料号解析后
     */
    private String pnAfter;
    /**
     * 数量解析前
     */
    private Integer qtyBefore;
    /**
     * 数量解析后
     */
    private Integer qtyAfter;
    /**
     * 出厂日期解析前
     */
    private String dcBefore;
    /**
     * 出厂日期解析后
     */
    private String dcAfter;
    /**
     * 批次号解析前
     */
    private String lotBefore;
    /**
     * 批次号解析后
     */
    private String lotAfter;
}
