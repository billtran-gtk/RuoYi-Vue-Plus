package org.dromara.huahao.domain.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.huahao.domain.ReceivingForm;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 收料明细视图对象 h_receiving_form
 *
 * @author Hurj
 * @date 2026-02-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ReceivingForm.class)
public class ReceivingFormVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelIgnore
//    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * R/C ID
     */
    @ExcelProperty(value = "R/C ID")
    private String rcId;

    /**
     * 料号
     */
    @ExcelProperty(value = "料号")
    private String partNo;

    /**
     * 验收单号
     */
    @ExcelProperty(value = "验收单号")
    private String accNo;

    /**
     * Quality数量
     */
    @ExcelProperty(value = "数量")
    @ColumnWidth(10)
    private Integer quantity;

    /**
     * Date Code日期代码
     */
    @ExcelProperty(value = "Date Code")
    private String dc;

    /**
     * 原dcCode
     */
    @ExcelProperty(value = "Date Code Original")
    private String dcCodeOriginal;

    /**
     * Lot Code批次代码
     */
    @ExcelProperty(value = "Lot Code")
    private String lot;

    /**
     * MSL Level湿度敏感等级
     */
    @ExcelProperty(value = "MSL Level")
    private String msl;

    /**
     * Location库位
     */
    @ExcelProperty(value = "库位")
    @ColumnWidth(10)
    private String location;

    /**
     * AsnNO送货单号
     */
    @ExcelProperty(value = "送货单号")
    private String asnNo;

    /**
     * PalletID托盘ID
     */
    @ExcelProperty(value = "Pallet ID")
    private String palletId;

    /**
     * 标签类型（1:常规,2:98/99,3:一维码）
     */
    @ExcelProperty(value = "标签类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=常规,2=98/99,3=一维码")
    private Integer label;

    /**
     * 收料时间
     */
    @ExcelProperty(value = "收料时间")
    @ColumnWidth(20)
    private Date receivingTime;

    /**
     * 是否提交(0-否 1-是)
     */
    @ExcelProperty(value = "是否提交", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=否,1=是")
    private String flag;

    /**
     * 备注
     */
    @ExcelIgnore
//    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private Date createTime;
}
