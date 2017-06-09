package com.util;

import java.util.Calendar;

/**
 * Created by Logan on 2016/10/21.
 */

public class DateUtil {
    private static int year;
    private static int month;
    private static int day;
    private static int hour;
    private static int minute;

    private static void setDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public static String getDate() {
        setDate();
        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }
}
