package com.limefriends.molde.comm.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUitl {

    public static String fromLongToDate(String date) {
        long millisecond = Long.parseLong(date);
        return DateFormat.format("yyyy년 MM월 dd일 ", new Date(millisecond)).toString();
    }

}
