package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.convert.ExcelDateConvert;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.SysVersion;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * app安装包版本管理视图对象 sys_version
 *
 * @author Lion Li
 * @date 2026-03-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysVersion.class)
public class SysVersionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
//    @ExcelProperty(value = "主键")
    @ExcelIgnore
    private Long versionId;

    /**
     * 版本号
     */
    @ExcelProperty(value = "版本号")
    private String versionNumber;

    /**
     * 内容
     */
    @ExcelProperty(value = "内容")
    private String content;

    /**
     * 路径
     */
    @ExcelProperty(value = "路径")
    private String url;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间",converter = ExcelDateConvert.class)
    private Date updateTime;
}
