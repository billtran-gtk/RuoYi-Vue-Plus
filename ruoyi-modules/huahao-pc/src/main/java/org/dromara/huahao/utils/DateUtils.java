package org.dromara.huahao.utils;

import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Random;

public class DateUtils {

    // 包含大写字母和数字的字符集
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 5;
    private static final Random RANDOM = new SecureRandom();

    /**
     * 根据日期生成年份和周数的格式
     * @param date 输入的日期
     * @param startOfWeek 一周的开始（1=周一，7=周日）
     * @param minDaysInFirstWeek 一年的第一周最少包含的天数（通常为4或7）
     * @return 格式化的字符串，如2601
     */
    public static String generateYearWeekFormat(LocalDate date, int startOfWeek, int minDaysInFirstWeek) {
        if (date == null) {
            throw new IllegalArgumentException("日期不能为空");
        }

        // 获取年份的最后两位
        int year = date.getYear() % 100;

        // 创建 WeekFields 对象，指定一周的开始和最小天数
        WeekFields weekFields = WeekFields.of(DayOfWeek.of(startOfWeek), minDaysInFirstWeek);

        // 使用日期的年份来计算周数，而不是基于周的年份
        // 这样可以确保 2023 年 12 月 31 日计算的是 2023 年的第几周
        int weekNumber = date.get(weekFields.weekOfYear());

        // 处理周数为 0 的情况（当日期属于上一年的最后一周时）
        if (weekNumber == 0) {
            // 获取上一年的最后一周
            LocalDate lastDayOfLastYear = LocalDate.of(date.getYear() - 1, 12, 31);
            weekNumber = lastDayOfLastYear.get(weekFields.weekOfYear());
        }

        return String.format("%02d%02d", year, weekNumber);
    }

    /**
     * 默认使用ISO标准（周一为一周开始，第一周最少4天）
     */
    public static String generateYearWeekFormat(LocalDate date) {
        return generateYearWeekFormat(date, 1, 4);
    }

    /**
     * 美国标准（周日为一周开始，第一周最少1天）
     */
    public static String generateUSYearWeekFormat(LocalDate date) {
        return generateYearWeekFormat(date, 7, 1);
    }

    /**
     * 生成5位大写字母和数字的随机序列号
     * @return 5位随机序列号
     */
    public static String generateSerialNumber() {
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
