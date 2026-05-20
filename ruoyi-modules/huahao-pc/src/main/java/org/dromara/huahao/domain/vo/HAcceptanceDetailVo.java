package org.dromara.huahao.domain.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelRequired;
import org.dromara.huahao.domain.HAcceptanceDetail;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = HAcceptanceDetail.class)
public class HAcceptanceDetailVo {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 验收单号
     */
    @ExcelRequired
    @NotNull(message = "验收单号不能为空")
    @ExcelProperty(value = "RECEIPT_NO")
    private String receiptNo;
    /**
     * 供应商号
     */
    @ExcelRequired
    @NotNull(message = "供应商号不能为空")
    @ExcelProperty(value = "ASN_NO")
    private String asnNo;
    /**
     * 料号
     */
    @ExcelRequired
    @NotNull(message = "料号不能为空")
    @ExcelProperty(value = "PART_NO")
    private String partNo;
    /**
     * 应收数量
     */
    @ExcelRequired
    @NotNull(message = "应收数量不能为空")
    @ExcelProperty(value = "QUANTITY")
    @ColumnWidth(10)
    private Integer quantity;
    /**
     * 温湿度级别
     */
    @ExcelRequired
    @NotNull(message = "温湿度级别不能为空")
    @ExcelProperty(value = "MSL")
    @ColumnWidth(10)
    private String msl;

    /**
     * 仓位
     */
//    @ExcelRequired
//    @NotNull(message = "仓位不能为空")
    @ExcelProperty(value = "LOCATOR")
    @ColumnWidth(10)
    private String locator;

    /**
     * 已收数量
     */
    @ExcelRequired
    @ExcelProperty(value = "ReceivedQuantity", index = -1)
    private Integer receivedQuantity;
}
