package org.dromara.huahao.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptanceQrCodeVo {
    /**
     * rcId(生成)
     */
    private String rcId;
    /**
     * PN号（生成）
     */
    private String pnNo;
    /**
     * 数量
     */
    private Integer qty;
    /**
     * 日期代码（转换后）
     */
    private String dcCode;
    /**
     * 日期代码（原始）
     */
    private String dcCodeOriginal;
    /**
     * 批次号
     */
    private String lotNo;

    /**
     * msl
     */
    private String msl;

    /**
     * 批次号
     */
    private String locator;
}
