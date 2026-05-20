package org.dromara.common.excel.convert;

public class ExcelDateTimeConvert extends AbstractExcelDateConvert {

    public ExcelDateTimeConvert() {
        super("yyyy-MM-dd HH:mm:ss");
    }

    public ExcelDateTimeConvert(String pattern) {
        super(pattern);
    }
}
