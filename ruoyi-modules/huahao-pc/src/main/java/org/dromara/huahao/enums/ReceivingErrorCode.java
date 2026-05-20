package org.dromara.huahao.enums;

/**
 * PDA收料接口错误码枚举
 * 用于前端国际化
 *
 * @author Kiro AI Assistant
 * @date 2026-02-10
 */
public enum ReceivingErrorCode {

    /**
     * RC ID不能为空
     */
    RC_ID_REQUIRED("RC_ID_REQUIRED", "RC ID不能为空"),

    /**
     * RC ID已存在
     */
    RC_ID_DUPLICATE("RC_ID_DUPLICATE", "RC ID已存在，请勿重复提交"),

    /**
     * 料号不能为空
     */
    PART_NO_REQUIRED("PART_NO_REQUIRED", "料号不能为空"),

    /**
     * 标签类型不合法
     */
    LABEL_INVALID("LABEL_INVALID", "标签类型不合法"),

    /**
     * 验收单号不能为空
     */
    ACC_NO_REQUIRED("ACC_NO_REQUIRED", "验收单号不能为空"),

    /**
     * 验收单不存在
     */
    ACC_NO_NOT_FOUND("ACC_NO_NOT_FOUND", "验收单不存在"),

    /**
     * 验收单尚未开始收料
     */
    ACC_NOT_STARTED("ACC_NOT_STARTED", "验收单尚未开始收料，无法完成"),

    /**
     * 收料数量不完整
     */
    RECEIVING_QUANTITY_INCOMPLETE("RECEIVING_QUANTITY_INCOMPLETE", "收料数量不完整，无法完成收料"),

    /**
     * 收料数量已满足
     */
    RECEIVING_QUANTITY_SATISFIED("RECEIVING_QUANTITY_SATISFIED", "收料数量已满足，无法继续添加"),

    /**
     * 收料数量将超过应收数量
     */
    RECEIVING_QUANTITY_EXCEED("RECEIVING_QUANTITY_EXCEED", "收料数量将超过应收数量"),

    /**
     * 验收单明细不存在
     */
    ACC_DETAIL_NOT_FOUND("ACC_DETAIL_NOT_FOUND", "验收单明细不存在，无法完成收料"),

    /**
     * 料号不在验收单中
     */
    PART_NO_NOT_IN_ACCEPTANCE("PART_NO_NOT_IN_ACCEPTANCE", "料号不在当前验收单中");

    private final String code;
    private final String message;

    ReceivingErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
