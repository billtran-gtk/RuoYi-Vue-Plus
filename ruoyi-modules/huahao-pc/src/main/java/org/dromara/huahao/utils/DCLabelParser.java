package org.dromara.huahao.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DCLabelParser {
    @Value("${huahao.generate-type}")
    private static String type;
    private static final String GENERATE_TYPE = "ISO";
    // 月份映射表 - 第一种格式
    private static final Map<Character, Integer> MONTH_MAP_1 = new HashMap<>();
    private static final Map<Character, Integer> DAY_MAP_1 = new HashMap<>();

    // 月份映射表 - 第二种格式
    private static final Map<Character, Integer> MONTH_MAP_2 = new HashMap<>();
    private static final Map<Character, Integer> DAY_MAP_2 = new HashMap<>();

    // 周别映射表
    private static final Map<Character, Integer> WEEK_MAP = new HashMap<>();

    static {
        // 初始化第一种格式的映射
        initMonthMap1();
        initDayMap1();
        initMonthMap2();
        initDayMap2();
        initWeekMap();
    }

    private static void initMonthMap1() {
        MONTH_MAP_1.put('1', 1);
        MONTH_MAP_1.put('2', 2);
        MONTH_MAP_1.put('3', 3);
        MONTH_MAP_1.put('4', 4);
        MONTH_MAP_1.put('5', 5);
        MONTH_MAP_1.put('6', 6);
        MONTH_MAP_1.put('7', 7);
        MONTH_MAP_1.put('8', 8);
        MONTH_MAP_1.put('9', 9);
        MONTH_MAP_1.put('A', 10);
        MONTH_MAP_1.put('B', 11);
        MONTH_MAP_1.put('C', 12);
        MONTH_MAP_1.put('D', 13);
        MONTH_MAP_1.put('E', 14);
        MONTH_MAP_1.put('F', 15);
        MONTH_MAP_1.put('G', 16);
        MONTH_MAP_1.put('H', 17);
        MONTH_MAP_1.put('J', 18);
        MONTH_MAP_1.put('K', 19);
        MONTH_MAP_1.put('L', 20);
        MONTH_MAP_1.put('M', 21);
        MONTH_MAP_1.put('N', 22);
        MONTH_MAP_1.put('O', 23);
        MONTH_MAP_1.put('P', 24);
        MONTH_MAP_1.put('R', 25);
        MONTH_MAP_1.put('S', 26);
        MONTH_MAP_1.put('T', 27);
        MONTH_MAP_1.put('V', 28);
        MONTH_MAP_1.put('W', 29);
        MONTH_MAP_1.put('X', 30);
        MONTH_MAP_1.put('Y', 31);
    }

    private static void initDayMap1() {
        DAY_MAP_1.put('1', 1);
        DAY_MAP_1.put('2', 2);
        DAY_MAP_1.put('3', 3);
        DAY_MAP_1.put('4', 4);
        DAY_MAP_1.put('5', 5);
        DAY_MAP_1.put('6', 6);
        DAY_MAP_1.put('7', 7);
        DAY_MAP_1.put('8', 8);
        DAY_MAP_1.put('9', 9);
        DAY_MAP_1.put('A', 10);
        DAY_MAP_1.put('B', 11);
        DAY_MAP_1.put('C', 12);
        DAY_MAP_1.put('D', 13);
        DAY_MAP_1.put('E', 14);
        DAY_MAP_1.put('F', 15);
        DAY_MAP_1.put('G', 16);
        DAY_MAP_1.put('H', 17);
        DAY_MAP_1.put('J', 18);
        DAY_MAP_1.put('K', 19);
        DAY_MAP_1.put('L', 20);
        DAY_MAP_1.put('M', 21);
        DAY_MAP_1.put('N', 22);
        DAY_MAP_1.put('P', 23);
        DAY_MAP_1.put('R', 24);
        DAY_MAP_1.put('S', 25);
        DAY_MAP_1.put('T', 26);
        DAY_MAP_1.put('V', 27);
        DAY_MAP_1.put('W', 28);
        DAY_MAP_1.put('X', 29);
        DAY_MAP_1.put('Y', 30);
        DAY_MAP_1.put('Z', 31);
    }

    private static void initMonthMap2() {
        MONTH_MAP_2.put('1', 1);
        MONTH_MAP_2.put('2', 2);
        MONTH_MAP_2.put('3', 3);
        MONTH_MAP_2.put('4', 4);
        MONTH_MAP_2.put('5', 5);
        MONTH_MAP_2.put('6', 6);
        MONTH_MAP_2.put('7', 7);
        MONTH_MAP_2.put('8', 8);
        MONTH_MAP_2.put('9', 9);
        MONTH_MAP_2.put('A', 10);
        MONTH_MAP_2.put('B', 11);
        MONTH_MAP_2.put('C', 12);
        MONTH_MAP_2.put('D', 13);
        MONTH_MAP_2.put('E', 14);
        MONTH_MAP_2.put('F', 15);
        MONTH_MAP_2.put('G', 16);
        MONTH_MAP_2.put('H', 17);
        MONTH_MAP_2.put('J', 18);
        MONTH_MAP_2.put('K', 19);
        MONTH_MAP_2.put('L', 20);
        MONTH_MAP_2.put('M', 21);
        MONTH_MAP_2.put('N', 22);
        MONTH_MAP_2.put('O', 23);
        MONTH_MAP_2.put('P', 24);
        MONTH_MAP_2.put('R', 25);
        MONTH_MAP_2.put('S', 26);
        MONTH_MAP_2.put('T', 27);
        MONTH_MAP_2.put('V', 28);
        MONTH_MAP_2.put('W', 29);
        MONTH_MAP_2.put('X', 30);
        MONTH_MAP_2.put('Y', 31);
    }

    private static void initDayMap2() {
        DAY_MAP_2.put('1', 1);
        DAY_MAP_2.put('2', 2);
        DAY_MAP_2.put('3', 3);
        DAY_MAP_2.put('4', 4);
        DAY_MAP_2.put('5', 5);
        DAY_MAP_2.put('6', 6);
        DAY_MAP_2.put('7', 7);
        DAY_MAP_2.put('8', 8);
        DAY_MAP_2.put('9', 9);
        DAY_MAP_2.put('A', 10);
        DAY_MAP_2.put('B', 11);
        DAY_MAP_2.put('C', 12);
        DAY_MAP_2.put('D', 13);
        DAY_MAP_2.put('E', 14);
        DAY_MAP_2.put('F', 15);
        DAY_MAP_2.put('G', 16);
        DAY_MAP_2.put('H', 17);
        DAY_MAP_2.put('J', 18);
        DAY_MAP_2.put('K', 19);
        DAY_MAP_2.put('L', 20);
        DAY_MAP_2.put('M', 21);
        DAY_MAP_2.put('N', 22);
        DAY_MAP_2.put('O', 23);
        DAY_MAP_2.put('P', 24);
        DAY_MAP_2.put('R', 25);
        DAY_MAP_2.put('S', 26);
        DAY_MAP_2.put('T', 27);
        DAY_MAP_2.put('V', 28);
        DAY_MAP_2.put('W', 29);
        DAY_MAP_2.put('X', 30);
        DAY_MAP_2.put('Y', 31);
    }

    private static void initWeekMap() {
        WEEK_MAP.put('1', 1);
        WEEK_MAP.put('2', 2);
        WEEK_MAP.put('3', 3);
        WEEK_MAP.put('4', 4);
        WEEK_MAP.put('5', 5);
        WEEK_MAP.put('6', 6);
        WEEK_MAP.put('7', 7);
        WEEK_MAP.put('8', 8);
        WEEK_MAP.put('9', 9);
        WEEK_MAP.put('a', 10);
        WEEK_MAP.put('b', 11);
        WEEK_MAP.put('c', 12);
        WEEK_MAP.put('d', 13);
        WEEK_MAP.put('e', 14);
        WEEK_MAP.put('f', 15);
        WEEK_MAP.put('g', 16);
        WEEK_MAP.put('h', 17);
        WEEK_MAP.put('j', 18);
        WEEK_MAP.put('k', 19);
        WEEK_MAP.put('n', 20);
        WEEK_MAP.put('p', 21);
        WEEK_MAP.put('r', 22);
        WEEK_MAP.put('s', 23);
        WEEK_MAP.put('t', 24);
        WEEK_MAP.put('u', 25);
        WEEK_MAP.put('v', 26);
        WEEK_MAP.put('x', 27);
        WEEK_MAP.put('y', 28);
        WEEK_MAP.put('z', 29);
        WEEK_MAP.put('A', 30);
        WEEK_MAP.put('B', 31);
        WEEK_MAP.put('C', 32);
        WEEK_MAP.put('D', 33);
        WEEK_MAP.put('E', 34);
        WEEK_MAP.put('F', 35);
        WEEK_MAP.put('G', 36);
        WEEK_MAP.put('H', 37);
        WEEK_MAP.put('J', 38);
        WEEK_MAP.put('K', 39);
        WEEK_MAP.put('L', 40);
        WEEK_MAP.put('M', 41);
        WEEK_MAP.put('N', 42);
        WEEK_MAP.put('P', 43);
        WEEK_MAP.put('Q', 44);
        WEEK_MAP.put('R', 45);
        WEEK_MAP.put('S', 46);
        WEEK_MAP.put('T', 47);
        WEEK_MAP.put('U', 48);
        WEEK_MAP.put('V', 49);
        WEEK_MAP.put('W', 50);
        WEEK_MAP.put('X', 51);
        WEEK_MAP.put('Y', 52);
        WEEK_MAP.put('Z', 53);
    }

    /**
     * 将日期转换为YYWW格式的DC码
     */
    public static String convertDateToDC(LocalDate localDate) {
        if (GENERATE_TYPE.equals(type)) {
            return DateUtils.generateYearWeekFormat(localDate);
        } else {
            return DateUtils.generateUSYearWeekFormat(localDate);
        }
    }

    /**
     * DC格式1: 5码-字母开头
     * 例子: H5BTM -> 2025年11月26日 -> DC:2544
     */
    public static DCParseResult parseDCFormat1(String dcCode) {
        if (dcCode == null || dcCode.length() != 5 || !Character.isLetter(dcCode.charAt(0))) {
            return new DCParseResult(false, "格式错误: 必须是5码且字母开头");
        }

        try {
            char secondChar = dcCode.charAt(1);
            int year = 2020 + (secondChar - '0'); // 5->2025, 6->2026...

            char monthChar = dcCode.charAt(2);
            char dayChar = dcCode.charAt(3);

            if (!MONTH_MAP_1.containsKey(monthChar) || !DAY_MAP_1.containsKey(dayChar)) {
                return new DCParseResult(false, "包含无效字符");
            }

            int month = MONTH_MAP_1.get(monthChar);
            int day = DAY_MAP_1.get(dayChar);

            LocalDate date = LocalDate.of(year, month, day);
            String dc = convertDateToDC(date);

            return new DCParseResult(true, dc, date, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式2: 5码-数字开头
     * 例子: 5A9HB -> 2025年10月9日 -> DC:2544
     */
    public static DCParseResult parseDCFormat2(String dcCode) {
        if (dcCode == null || dcCode.length() != 5 || !Character.isDigit(dcCode.charAt(0))) {
            return new DCParseResult(false, "格式错误: 必须是5码且数字开头");
        }

        try {
            int year = 2020 + (dcCode.charAt(0) - '0'); // 5->2025, 6->2026...

            char monthChar = dcCode.charAt(1);
            char dayChar = dcCode.charAt(2);

            if (!MONTH_MAP_2.containsKey(monthChar) || !DAY_MAP_2.containsKey(dayChar)) {
                return new DCParseResult(false, "包含无效字符");
            }

            int month = MONTH_MAP_2.get(monthChar);
            int day = DAY_MAP_2.get(dayChar);

            LocalDate date = LocalDate.of(year, month, day);
            String dc = convertDateToDC(date);

            return new DCParseResult(true, dc, date, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式3: 6码-年份+周别
     * 例子: 202543 -> DC:2543
     */
    public static DCParseResult parseDCFormat3(String dcCode) {
        if (dcCode == null || dcCode.length() != 6 || !dcCode.matches("\\d+")) {
            return new DCParseResult(false, "格式错误: 必须是6位数字");
        }

        try {
            String yearStr = dcCode.substring(0, 4);
            String weekStr = dcCode.substring(4, 6);

            int year = Integer.parseInt(yearStr);
            int week = Integer.parseInt(weekStr);

            // 直接取后四位作为DC
            String dc = dcCode.substring(2);

            return new DCParseResult(true, dc, null, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式4: 6码-年份+周期+线体
     * 例子: 25467B -> DC:2546
     */
    public static DCParseResult parseDCFormat4(String dcCode) {
        if (dcCode == null || dcCode.length() != 6) {
            return new DCParseResult(false, "格式错误: 必须是6码");
        }

        try {
            // 取前4位作为DC
            String dc = dcCode.substring(0, 4);

            if (!dc.matches("\\d+")) {
                return new DCParseResult(false, "前4位必须是数字");
            }

            return new DCParseResult(true, dc, null, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式5: 8码-年+月+日+线体
     * 例子: 25090102 -> 2025年9月1日 -> DC:2537
     */
    public static DCParseResult parseDCFormat5(String dcCode) {
        if (dcCode == null || dcCode.length() != 8 || !dcCode.matches("\\d+")) {
            return new DCParseResult(false, "格式错误: 必须是8位数字");
        }

        try {
            int yearPrefix = LocalDate.now().getYear() / 100;
            int year = yearPrefix * 100 + Integer.parseInt(dcCode.substring(0, 2));
            int month = Integer.parseInt(dcCode.substring(2, 4));
            int day = Integer.parseInt(dcCode.substring(4, 6));

            LocalDate date = LocalDate.of(year, month, day);
            String dc = convertDateToDC(date);

            return new DCParseResult(true, dc, date, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式6: 六码-年+月+日
     * 例子: 250617 -> 2025年6月17日 -> DC:2526
     */
    public static DCParseResult parseDCFormat6(String dcCode) {
        if (dcCode == null || dcCode.length() != 6 || !dcCode.matches("\\d+")) {
            return new DCParseResult(false, "格式错误: 必须是6位数字");
        }

        try {
            int yearPrefix = LocalDate.now().getYear() / 100;
            int year = yearPrefix * 100 + Integer.parseInt(dcCode.substring(0, 2));
            int month = Integer.parseInt(dcCode.substring(2, 4));
            int day = Integer.parseInt(dcCode.substring(4, 6));

            LocalDate date = LocalDate.of(year, month, day);
            String dc = convertDateToDC(date);

            return new DCParseResult(true, dc, date, dcCode);

        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式7: A2025.6.9
     * 例子: A2025.6.9 -> 2025年6月9日 -> DC:2524
     */
    public static DCParseResult parseDCFormat7(String dcCode) {
        if (dcCode == null || !dcCode.startsWith("A")) {
            return new DCParseResult(false, "格式错误: 必须以A开头且长度至少10位");
        }

        try {
            // 验证格式: A + 年份(4位) + . + 月份(1-2位) + . + 日期(1-2位)
            if (!dcCode.matches("A\\d{4}\\.\\d{1,2}\\.\\d{1,2}")) {
                return new DCParseResult(false, "格式错误: 必须符合Ayyyy.m.d格式");
            }

            // 解析年月日
            String[] parts = dcCode.substring(1).split("\\."); // 去掉开头的A
            if (parts.length != 3) {
                return new DCParseResult(false, "格式错误: 日期部分必须包含年月日");
            }

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // 验证日期有效性
            if (month < 1 || month > 12) {
                return new DCParseResult(false, "月份必须在1-12之间");
            }
            if (day < 1 || day > 31) {
                return new DCParseResult(false, "日期必须在1-31之间");
            }

            LocalDate date = LocalDate.of(year, month, day);
            String dc = convertDateToDC(date);

            return new DCParseResult(true, dc, date, dcCode);

        } catch (NumberFormatException e) {
            return new DCParseResult(false, "日期格式错误: 包含非数字字符");
        } catch (Exception e) {
            return new DCParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * DC格式8: 26044
     * 例子: 26044  -> DC:2604
     */
    public static DCParseResult parseDCFormat8(String dcCode) {
        //取前4位作为DC
        return new DCParseResult(true, dcCode.substring(0, 4), null, dcCode);
    }

    /**
     * DC格式9: 6aA
     * 例子: 6aA  -> DC:2604
     */
    public static DCParseResult parseDCFormat9(String dcCode) {
        //取前2位解析
        try {
            // 获取当前年份前三位
            int yearPrefix = LocalDate.now().getYear() / 100;
            int year = yearPrefix + (dcCode.charAt(0) - '0'); // 5->2025, 6->2026...

            char weekChar = dcCode.charAt(1);
            if (!WEEK_MAP.containsKey(weekChar)) {
                return new DCParseResult(false, "包含无效字符");
            }

            int week = WEEK_MAP.get(weekChar);
            return new DCParseResult(true, String.valueOf(year) + week, null, dcCode);
        } catch (Exception e) {
            return new DCParseResult(true, dcCode.substring(0, 3), null, dcCode);
        }

    }

    /**
     * 自动识别并解析DC格式
     */
    public static DCParseResult autoParseDC(String dcCode) {
        if (dcCode == null || dcCode.isEmpty()) {
            return new DCParseResult(false, "DC码为空");
        }

        int length = dcCode.length();

        switch (length) {
            case 3:
                return parseDCFormat9(dcCode);
            case 5:
                if (Character.isLetter(dcCode.charAt(0))) {
                    return parseDCFormat1(dcCode);
                } else if (Character.isDigit(dcCode.charAt(0))) {
                    // 判断是否纯数字
                    if (StringUtils.isNumeric(dcCode)) {
                        return parseDCFormat8(dcCode);
                    } else {
                        return parseDCFormat2(dcCode);
                    }

                }
                break;

            case 6:
                if (dcCode.matches("\\d+")) {
                    // 尝试格式6，如果失败则用格式3
                    DCParseResult result6 = parseDCFormat6(dcCode);
                    if (result6.success) {
                        return result6;
                    }
                    return parseDCFormat3(dcCode);
                } else {
                    return parseDCFormat4(dcCode);
                }

            case 8:
                if (dcCode.matches("\\d+")) {
                    return parseDCFormat5(dcCode);
                }
                break;

            case 9, 10, 11:
                if (Character.isLetter(dcCode.charAt(0)) && dcCode.startsWith("A")) {
                    return parseDCFormat7(dcCode);
                }
                break;
            default:
                return new DCParseResult(true, dcCode, null, dcCode);
        }

        return new DCParseResult(false, "无法识别的DC格式");
    }

    // Label格式解析类
    public static class LabelParseResult {
        public boolean success;
        public String partNumber;
        public String quantity;
        public String dateCode;
        public String lotCode;
        public String originalDC;
        public String errorMessage;

        public LabelParseResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public LabelParseResult(boolean success, String partNumber, String quantity,
                                String dateCode, String lotCode, String originalDC) {
            this.success = success;
            this.partNumber = partNumber;
            this.quantity = quantity;
            this.dateCode = dateCode;
            this.lotCode = lotCode;
            this.originalDC = originalDC;
        }
    }

    /**
     * Label需求1: 4个"||"格式
     * 例子: R2515IMD0100088||P96A0-15000-13471000||Q50||D2551||LSO25120520
     */
    public static LabelParseResult parseLabelFormat1(String labelContent) {
        if (labelContent == null || !labelContent.contains("||")) {
            return new LabelParseResult(false, "格式错误: 必须包含||分隔符");
        }

        try {
            String[] parts = labelContent.split("\\|\\|");
            if (parts.length < 5) {
                return new LabelParseResult(false, "格式错误: 必须包含5个部分");
            }

            // 解析P、Q、D、L部分
            String pPart = findPart(parts, "P");
            String qPart = findPart(parts, "Q");
            String dPart = findPart(parts, "D");
            String lPart = findPart(parts, "L");

            if (pPart == null || qPart == null || dPart == null || lPart == null) {
                return new LabelParseResult(false, "缺少必要的P、Q、D、L信息");
            }

            // 解析DC
            DCParseResult dcResult = autoParseDC(dPart.substring(1));
            if (!dcResult.success) {
                return new LabelParseResult(false, "DC解析失败: " + dcResult.errorMessage);
            }

            return new LabelParseResult(true,
                pPart.substring(1), // Part Number
                qPart.substring(1), // Quantity
                dcResult.dc,        // Date Code
                lPart.substring(1), // Lot Code
                dPart.substring(1)  // Original DC
            );

        } catch (Exception e) {
            return new LabelParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * Label需求2: 10个"$"字符格式 - ASUS 98/99材料
     * 例子: Cooler Master Co.,Ltd$LC-02331-01-GP2$13070-03830900$RTX5090...$2$2551$CN$5.84KG$7.30KG$空白$
     */
    public static LabelParseResult parseLabelFormat2(String labelContent) {
        return parseDollarSeparatedLabel(labelContent, 10, 2, 4, 5, true);
    }

    /**
     * Label需求3: 10个"$"字符格式 - ASUS 97材料
     * 例子: WIESON$AC3167-0340-009-H0$12022-00221700$...$1800$2541$CN$4.54KG$10.20KG$1100121516$
     */
    public static LabelParseResult parseLabelFormat3(String labelContent) {
        return parseDollarSeparatedLabel(labelContent, 10, 2, 4, 9, false);
    }

    /**
     * Label需求4: 12个"$"字符格式 - 0800开头材料
     * 例子: Victory Giant Technology (Huizhou) Co., Ltd.$S51908PRCQ6A2$08003-26325100$...$150$0$150$2543$CN$$$202511-0001$3
     */
    public static LabelParseResult parseLabelFormat4(String labelContent) {
        return parseDollarSeparatedLabel(labelContent, 12, 2, 4, 7, false);
    }

    /**
     * Label需求6: 10个"$"字符格式 - Ultra厂商
     * 例子: Ultra$PS7101-51$06113-00850000$PHISON$...$4500$2518$TW$6KG$6.8KG$3YZHB000001BB
     */
    public static LabelParseResult parseLabelFormat6(String labelContent) {
        return parseDollarSeparatedLabel(labelContent, 10, 2, 5, 10, false);
    }

    private static LabelParseResult parseDollarSeparatedLabel(String labelContent, int expectedParts,
                                                              int pnIndex, int qtyIndex, int dcIndex, boolean useCurrentDate) {
        if (labelContent == null || !labelContent.contains("$")) {
            return new LabelParseResult(false, "格式错误: 必须包含$分隔符");
        }

        try {
            String[] parts = labelContent.split("\\$");
            if (parts.length < expectedParts) {
                return new LabelParseResult(false,
                    String.format("格式错误: 需要%d个部分，实际只有%d个", expectedParts, parts.length));
            }

            String partNumber = parts[pnIndex];
            String quantity = parts[qtyIndex];
            String originalDC = parts[dcIndex];

            // Part Number转换
            if (partNumber != null && !partNumber.isEmpty()) {
                partNumber = convertPartNumber(partNumber);
            }

            String dateCode;
            if (useCurrentDate) {
                // 使用当前日期计算DC
                dateCode = convertDateToDC(LocalDate.now());
            } else {
                // 解析原始DC
                DCParseResult dcResult = autoParseDC(originalDC);
                if (!dcResult.success) {
                    return new LabelParseResult(false, "DC解析失败: " + dcResult.errorMessage);
                }
                dateCode = dcResult.dc;
            }

            return new LabelParseResult(true, partNumber, quantity, dateCode, "", originalDC);

        } catch (Exception e) {
            return new LabelParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    /**
     * Label需求5: 特殊格式，需要刷P、Q、D、L
     * 例子: [)&gt;RS06GSP06018-03040100_x001d_Q3000_x001d_1T0318295ZHG_x001d_N3_x001d_5W5S_x0014_RJHR251RSEOT
     */
    public static LabelParseResult parseLabelFormat5(String labelContent) {
        // 这里需要根据实际的分隔符和格式进行解析
        // 由于格式不固定，需要更复杂的正则表达式匹配

        try {
            // 提取P、Q、D、L信息（这里需要根据实际格式调整）
            String pValue = extractValue(labelContent, "P");
            String qValue = extractValue(labelContent, "Q");
            String dValue = extractValue(labelContent, "D");
            String lValue = extractValue(labelContent, "L");

            if (pValue == null || qValue == null || dValue == null || lValue == null) {
                return new LabelParseResult(false, "缺少必要的P、Q、D、L信息");
            }

            // 解析DC
            DCParseResult dcResult = autoParseDC(dValue);
            if (!dcResult.success) {
                return new LabelParseResult(false, "DC解析失败: " + dcResult.errorMessage);
            }

            return new LabelParseResult(true, pValue, qValue, dcResult.dc, lValue, dValue);

        } catch (Exception e) {
            return new LabelParseResult(false, "解析错误: " + e.getMessage());
        }
    }

    // 辅助方法
    private static String findPart(String[] parts, String prefix) {
        for (String part : parts) {
            if (part.startsWith(prefix)) {
                return part;
            }
        }
        return null;
    }

    private static String convertPartNumber(String originalPN) {
        // 料号转换逻辑，根据需求添加96A1/96A0等
        // 这里需要根据具体业务规则实现
        return originalPN; // 暂时返回原值
    }

    private static String extractValue(String content, String field) {
        // 根据字段名提取值的逻辑
        // 这里需要根据实际格式实现
        Pattern pattern = Pattern.compile(field + "([^_]+)");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * DC解析结果类
     */
    public static class DCParseResult {
        public boolean success;
        public String dc;
        public LocalDate date;
        public String originalCode;
        public String errorMessage;

        public DCParseResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public DCParseResult(boolean success, String dc, LocalDate date, String originalCode) {
            this.success = success;
            this.dc = dc;
            this.date = date;
            this.originalCode = originalCode;
        }
    }

    // 测试方法
    public static void main(String[] args) {
        // 测试DC格式解析
        System.out.println("=== DC格式解析测试 ===");

        testDCFormat("H5BTM", 1);  // 格式1
        testDCFormat("5A9HB", 2);  // 格式2
        testDCFormat("202543", 3); // 格式3
        testDCFormat("25467B", 4); // 格式4
        testDCFormat("25090102", 5); // 格式5
        testDCFormat("250617", 6); // 格式6

        // 测试Label格式解析
        System.out.println("\n=== Label格式解析测试 ===");

        String label1 = "R2515IMD0100088||P96A0-15000-13471000||Q50||D2551||LSO25120520";
        testLabelFormat(label1, 1);
    }

    private static void testDCFormat(String code, int formatType) {
        DCParseResult result = null;
        switch (formatType) {
            case 1:
                result = parseDCFormat1(code);
                break;
            case 2:
                result = parseDCFormat2(code);
                break;
            case 3:
                result = parseDCFormat3(code);
                break;
            case 4:
                result = parseDCFormat4(code);
                break;
            case 5:
                result = parseDCFormat5(code);
                break;
            case 6:
                result = parseDCFormat6(code);
                break;
        }

        System.out.printf("格式%d: %s -> %s%n", formatType, code,
            result.success ? result.dc : "失败: " + result.errorMessage);
    }

    private static void testLabelFormat(String content, int formatType) {
        LabelParseResult result = null;
        switch (formatType) {
            case 1:
                result = parseLabelFormat1(content);
                break;
            case 2:
                result = parseLabelFormat2(content);
                break;
            case 3:
                result = parseLabelFormat3(content);
                break;
            case 4:
                result = parseLabelFormat4(content);
                break;
            case 5:
                result = parseLabelFormat5(content);
                break;
            case 6:
                result = parseLabelFormat6(content);
                break;
        }

        System.out.printf("Label格式%d: %s%n", formatType,
            result.success ? "成功" : "失败: " + result.errorMessage);
        if (result.success) {
            System.out.printf("  PN: %s, QTY: %s, DC: %s, LOT: %s%n",
                result.partNumber, result.quantity, result.dateCode, result.lotCode);
        }
    }
}
