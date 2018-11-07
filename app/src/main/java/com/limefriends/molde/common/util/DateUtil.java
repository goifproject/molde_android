package com.limefriends.molde.common.util;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtil {

    public static String fromLongToDate(String date) {
        long millisecond = Long.parseLong(date);
        return DateFormat.format("yyyy년 MM월 dd일 ", new Date(millisecond)).toString();
    }

    public static String fromLongToDate2(String date) {
        long millisecond = Long.parseLong(date);
        return DateFormat.format("yyyy. MM. dd", new Date(millisecond)).toString();
    }

}
