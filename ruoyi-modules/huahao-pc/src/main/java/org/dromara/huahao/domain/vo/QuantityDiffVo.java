package org.dromara.huahao.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数量差异视图对象
 * 用于返回收料数量验证的详细信息
 *
 * @author Kiro AI Assistant
 * @date 2026-02-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDiffVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 料号
     */
    private String partNo;

    /**
     * 应收数量
     */
    private Integer expected;

    /**
     * 已收数量
     */
    private Integer received;

    /**
     * 差异数量（应收 - 已收）
     */
    private Integer diff;
}
