package org.dromara.common.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * Excel 日期格式转换器抽象基类
 *
 * @author Lion Li
 */
public abstract class AbstractExcelDateConvert implements Converter<Date> {

    /**
     * 日期格式
     */
    private final String pattern;

    protected AbstractExcelDateConvert(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) {
        // 读取时的转换逻辑，如果需要可以重写
        return null;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Date date, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (date == null) {
            return new WriteCellData<>("");
        }
        String formattedDate = DateFormatUtils.format(date, pattern);
        return new WriteCellData<>(formattedDate);
    }
}
