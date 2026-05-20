package org.dromara.huahao.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentStyle;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.BooleanEnum;
import cn.idev.excel.enums.poi.BorderStyleEnum;
import cn.idev.excel.enums.poi.FillPatternTypeEnum;
import cn.idev.excel.enums.poi.HorizontalAlignmentEnum;
import cn.idev.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ASN导出视图对象（不含Pallet列）
 * 用于生成ASN Excel文件，当所有数据的Pallet都为空时使用
 *
 * @author Hurj
 * @date 2026-02-09
 */
@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(
    fontName = "Arial",
    fontHeightInPoints = 11,
    bold = BooleanEnum.TRUE
)
@ContentStyle(
    horizontalAlignment = HorizontalAlignmentEnum.CENTER,
    verticalAlignment = VerticalAlignmentEnum.CENTER,
    borderTop = BorderStyleEnum.THIN,
    borderBottom = BorderStyleEnum.THIN,
    borderLeft = BorderStyleEnum.THIN,
    borderRight = BorderStyleEnum.THIN
)
public class AsnExportNoPalletVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ASN单号 - 黄色背景
     */
    @ExcelProperty(value = "ASN")
    @ColumnWidth(20)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 13,  // 黄色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String asn;

    /**
     * 料盘/箱号 (Carton ID) - 绿色背景
     */
    @ExcelProperty(value = "Rarton ID")
    @ColumnWidth(20)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 42,  // 绿色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String cartonId;

    /**
     * 料号 (Part) - 绿色背景
     */
    @ExcelProperty(value = "Part")
    @ColumnWidth(20)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 42,  // 绿色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String part;

    /**
     * 数量 (Qty) - 绿色背景
     */
    @ExcelProperty(value = "Qty")
    @ColumnWidth(12)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 42,  // 绿色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String qty;

    /**
     * 生产周期 (Date Code) - 绿色背景
     */
    @ExcelProperty(value = "Date")
    @ColumnWidth(12)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 42,  // 绿色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String date;

    /**
     * 批次号 (Lot Code) - 绿色背景
     */
    @ExcelProperty(value = "Lot")
    @ColumnWidth(15)
    @HeadStyle(
        fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillForegroundColor = 42,  // 绿色背景
        horizontalAlignment = HorizontalAlignmentEnum.CENTER,
        verticalAlignment = VerticalAlignmentEnum.CENTER,
        borderTop = BorderStyleEnum.THIN,
        borderBottom = BorderStyleEnum.THIN,
        borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN
    )
    private String lot;
}
