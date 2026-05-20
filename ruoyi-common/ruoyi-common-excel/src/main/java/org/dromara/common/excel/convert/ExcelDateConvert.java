package org.dromara.common.excel.convert;

public class ExcelDateConvert extends AbstractExcelDateConvert {

    public ExcelDateConvert() {
        super("yyyy-MM-dd");
    }

    public ExcelDateConvert(String pattern) {
        super(pattern);
    }
}
