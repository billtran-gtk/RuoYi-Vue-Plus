package org.dromara.huahao.domain.bo;

import lombok.Data;

@Data
public class AcceptanceQrCodeBo {
    /*
     * 二维码内容
     */
    private String qrContent;

    /*
     * 料号（有特殊情况需要根据料号判断是96A1还是96A0）
     */
    private String partNo;
    /*
     * msl
     */
    private String msl;

    /*
     * 标签（常规标签1	ASUS 98/99标签2 一维码标签3）
     */
    private String tab;

    /*
     * 标记一维码标签扫的次数
     */
    private Integer index;

    /*
     * 验收单号
     */
    private String receiptNo;
}
