package org.dromara.huahao.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.domain.R;

import java.io.Serial;

/**
 * 带错误码的响应对象
 * 用于支持前端国际化
 *
 * @author Kiro AI Assistant
 * @date 2026-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RWithErrorCode<T> extends R<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码，用于前端国际化
     */
    private String errorCode;

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> RWithErrorCode<T> ok(String msg, T data) {
        RWithErrorCode<T> r = new RWithErrorCode<>();
        r.setCode(SUCCESS);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 返回失败消息
     *
     * @param msg 返回内容
     * @param errorCode 错误码
     * @return 失败消息
     */
    public static <T> RWithErrorCode<T> fail(String msg, String errorCode) {
        RWithErrorCode<T> r = new RWithErrorCode<>();
        r.setCode(FAIL);
        r.setMsg(msg);
        r.setErrorCode(errorCode);
        return r;
    }

    /**
     * 返回失败消息（带数据）
     *
     * @param msg 返回内容
     * @param errorCode 错误码
     * @param data 数据对象
     * @return 失败消息
     */
    public static <T> RWithErrorCode<T> fail(String msg, String errorCode, T data) {
        RWithErrorCode<T> r = new RWithErrorCode<>();
        r.setCode(FAIL);
        r.setMsg(msg);
        r.setErrorCode(errorCode);
        r.setData(data);
        return r;
    }
}
