package org.dromara.huahao.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * PDA收料业务异常
 * 支持错误码（用于前端国际化）
 *
 * @author Kiro AI Assistant
 * @date 2026-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReceivingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码（用于前端国际化）
     */
    private String errorCode;

    /**
     * 错误提示（中文，用于调试）
     */
    private String message;

    public ReceivingException(String message, String errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
