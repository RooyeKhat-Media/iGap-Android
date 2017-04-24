/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.module;

import android.content.Context;
import com.iGap.R;
import com.iGap.helper.HelperCalander;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * convert unix time to local time
     *
     * @param unixTime unix time is 13 characters (milliseconds), if you passed seconds, remember
     *                 to
     *                 multiply by 1000L
     * @param format   String format
     * @return String formatted time in local
     */
    public static String toLocal(long unixTime, String format) {
        return new SimpleDateFormat(format, Locale.US).format(unixTime);
    }

    /**
     * get current local time in milliseconds
     *
     * @return Long local time in milliseconds
     */
    public static long currentLocalTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    private static Calendar getYesterdayCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal;
    }

    public static String getChatSettingsTimeAgo(Context context, Date comingDate) {
        Calendar current = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        Calendar yesterday = getYesterdayCalendar();
        date.setTime(comingDate);

        String output = "";

        if (current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            output = context.getString(R.string.today);
        } else if (yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            output = context.getString(R.string.yesterday);
        } else {

            if (HelperCalander.isTimeHijri()) {

                CalendarShamsi shamsi = new CalendarShamsi(date.getTime());

                if (HelperCalander.isLanguagePersian) {
                    output = shamsi.date + " " + HelperCalander.getPersianMonthName(shamsi.month) + " " + shamsi.year;
                } else {
                    output = shamsi.year + " " + HelperCalander.getPersianMonthName(shamsi.month) + " " + shamsi.date;
                }

            } else {

                if (HelperCalander.isLanguagePersian) {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "dd MM yyyy");
                    String[] _date = output.split(" ");
                    if (_date.length > 2) {
                        output = _date[2] + " " + HelperCalander.convertEnglishMonthNameToPersian(Integer.parseInt(_date[1])) + " " + _date[0];
                    }
                } else {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "dd MMM yyyy");
                }

            }
        }

        //else //noinspection WrongConstant
        //    if (current.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR) + 1 && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
        //        output = String.format("%1$s %2$s", new SimpleDateFormat("EEE", Locale.getDefault()).format(date.getTimeInMillis()), date.get(Calendar.DAY_OF_MONTH));
        //    } else if (current.get(Calendar.YEAR) < date.get(Calendar.YEAR)) {
        //        output = String.format("%1$s-%2$s-%3$s", new SimpleDateFormat("MM", Locale.getDefault()).format(date.getTimeInMillis()),
        //            date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.YEAR));
        //    } else {
        //        output = String.format("%1$s %2$s", new SimpleDateFormat("MMMM", Locale.getDefault()).format(date.getTimeInMillis()), date.get(Calendar.DAY_OF_MONTH));
        //    }

        return HelperCalander.isLanguagePersian ? HelperCalander.convertToUnicodeFarsiNumber(output) : output;

    }
}