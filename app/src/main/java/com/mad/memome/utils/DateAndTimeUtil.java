package com.mad.memome.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class deal with time format
 */
public class DateAndTimeUtil {

    private static final SimpleDateFormat DATE_AND_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
    private static final SimpleDateFormat DATE_AND_TIME_FORMAT_NEW = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat READABLE_TIME_24_FORMAT = new SimpleDateFormat(" HH:mm", Locale.getDefault());
    private static final SimpleDateFormat READABLE_TIME_FORMAT = new SimpleDateFormat(" h:mm a", Locale.getDefault());


    public static String toStringReadableDate(Calendar calendar) {
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String toStringReadableTime(Calendar calendar, Context context) {
        if (DateFormat.is24HourFormat(context)) {
            return READABLE_TIME_24_FORMAT.format(calendar.getTime());
        } else {
            return READABLE_TIME_FORMAT.format(calendar.getTime());
        }
    }

    public static Long toLongDateAndTime(Calendar calendar) {
        return Long.parseLong(DATE_AND_TIME_FORMAT.format(calendar.getTime()));
    }

   public static String toStringDateAndTime(Date datetime) {
        return DATE_AND_TIME_FORMAT_NEW.format(datetime);
    }
    public static String toStringDateAndTime(Calendar calendar) {
        return DATE_AND_TIME_FORMAT.format(calendar.getTime());
    }

    public static String toReadableStringDateAndTime(Calendar calendar) {
        return DATE_AND_TIME_FORMAT_NEW.format(calendar.getTime());
    }

    public static Calendar parseDateAndTime(String dateAndTime) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_AND_TIME_FORMAT.parse(dateAndTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar;
    }
}