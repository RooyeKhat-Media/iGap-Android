/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.CalendarShamsi;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;

public class HelperCalander {

    public static boolean isPersianUnicode = false;
    public static boolean isLanguagePersian = false;
    public static boolean isLanguageArabic = false;

    public static String getPersianCalander(int year, int mounth, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, mounth, day);

        CalendarShamsi shamsi = new CalendarShamsi(c.getTime());

        String time = shamsi.year + "/" + shamsi.month + "/" + shamsi.date;

        return isLanguagePersian ? convertToUnicodeFarsiNumber(time) : time;
    }

    public static String getArabicCalender(int year, int mounth, int day) {

        GregorianCalendar gCal = new GregorianCalendar(year, mounth, day);
        Locale ar = new Locale("ar");
        Calendar uCal = new UmmalquraCalendar(ar);
        uCal.setTime(gCal.getTime());         // Used to properly format 'yy' pattern

        uCal.get(Calendar.YEAR);                                      // 1435
        uCal.getDisplayName(Calendar.MONTH, Calendar.LONG, ar);       // رجب
        uCal.get(Calendar.DAY_OF_MONTH);

        String time = uCal.get(Calendar.YEAR) + "/" + uCal.getDisplayName(Calendar.MONTH, Calendar.LONG, ar) + "/" + uCal.get(Calendar.DAY_OF_MONTH);
        return isLanguageArabic ? convertToUnicodeFarsiNumber(time) : time;
    }

    public static String getPersianCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getPersianCalander(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getArabicCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getArabicCalender(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    public static int isTimeHijri() {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);
        return sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);
    }

    public static String checkHijriAndReturnTime(long time) {

        String result = "";

        if (isTimeHijri() == 1) {
            result = getPersianCalander(time * DateUtils.SECOND_IN_MILLIS);

        } else if (isTimeHijri() == 2) {
            result = getArabicCalander(time * DateUtils.SECOND_IN_MILLIS);
        } else {

            if (HelperCalander.isLanguageArabic) {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MM yyyy");
                String[] _date = result.split(" ");
                if (_date.length > 2) {
                    result = _date[2] + " " + convertEnglishMonthNameToArabic(Integer.parseInt(_date[1])) + " " + _date[0];
                }
            } else if (HelperCalander.isLanguagePersian) {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MM yyyy");
                String[] _date = result.split(" ");
                if (_date.length > 2) {
                    result = _date[2] + " " + convertEnglishMonthNameToPersian(Integer.parseInt(_date[1])) + " " + _date[0];
                }
            } else {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MMM yyyy");
            }
        }

        return result;
    }

    public static String milladyDate(long time) {
        return TimeUtils.toLocal(time, "dd_MM_yyyy");
    }


    public static String convertEnglishMonthNameToArabic(int month) {

        Calendar cal = new UmmalquraCalendar();
        return cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("ar"));
    }

    public static String convertEnglishMonthNameToPersian(int month) {
        String result = "";

        switch (month) {
            case 1:
                result = "ژانویه";
                break;
            case 2:
                result = "فوریه";
                break;
            case 3:
                result = "مارس";
                break;
            case 4:
                result = "آوریل";
                break;
            case 5:
                result = "مه";
                break;
            case 6:
                result = "ژوئن";
                break;
            case 7:
                result = "ژوئیه";
                break;
            case 8:
                result = "اوت";
                break;
            case 9:
                result = "سپتامبر";
                break;
            case 10:
                result = "اکتبر";
                break;
            case 11:
                result = "نوامبر";
                break;
            case 12:
                result = "دسامبر";
                break;
        }

        return result;
    }

    public static String getPersianMonthName(int month) {

        String result = "";

        switch (month) {

            case 1:
                result = G.context.getString(R.string.farvardin);
                break;
            case 2:
                result = G.context.getString(R.string.ordibehst);
                break;
            case 3:
                result = G.context.getString(R.string.khordad);
                break;
            case 4:
                result = G.context.getString(R.string.tir);
                break;
            case 5:
                result = G.context.getString(R.string.mordad);
                break;
            case 6:
                result = G.context.getString(R.string.shahrivar);
                break;
            case 7:
                result = G.context.getString(R.string.mehr);
                break;
            case 8:
                result = G.context.getString(R.string.aban);
                break;
            case 9:
                result = G.context.getString(R.string.azar);
                break;
            case 10:
                result = G.context.getString(R.string.dey);
                break;
            case 11:
                result = G.context.getString(R.string.bahman);
                break;
            case 12:
                result = G.context.getString(R.string.esfand);
                break;
        }

        return result;
    }

    public static String getArabicMonthName(int month) {

        String result = "";

        switch (month) {

            case 0:
                result = G.context.getString(R.string.moharam);
                break;
            case 1:
                result = G.context.getString(R.string.safar);
                break;
            case 2:
                result = G.context.getString(R.string.rabio_aval);
                break;
            case 3:
                result = G.context.getString(R.string.rabi_sani);
                break;
            case 4:
                result = G.context.getString(R.string.jamado_aval);
                break;
            case 5:
                result = G.context.getString(R.string.jamado_sani);
                break;
            case 6:
                result = G.context.getString(R.string.rajab);
                break;
            case 7:
                result = G.context.getString(R.string.shaban);
                break;
            case 8:
                result = G.context.getString(R.string.ramezan);
                break;
            case 9:
                result = G.context.getString(R.string.shaval);
                break;
            case 10:
                result = G.context.getString(R.string.zighade);
                break;
            case 11:
                result = G.context.getString(R.string.zihaje);
                break;
        }

        return result;
    }


    public static String convertToUnicodeFarsiNumber(String text) {

        String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

        if (text == null) return "";

        if (text.length() == 0) {
            return "";
        }

        String out = "";

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }
        }

        return out;
    }

    public static String getTimeForMainRoom(long time) {

        Calendar current = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));

        String output = "";

        if (current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {

            if (HelperCalander.isPersianUnicode) {
                output = "\u200F" + HelperCalander.getClocktime(time, true);
            } else {
                output = HelperCalander.getClocktime(time, true);
            }

        } else if (current.get(Calendar.DAY_OF_YEAR) < (date.get(Calendar.DAY_OF_YEAR) + 7)) {// just week name

            if (HelperCalander.isTimeHijri() == 1) {
                output = getPersianStringDay(date.get(Calendar.DAY_OF_WEEK));
            } else if (HelperCalander.isTimeHijri() == 2) {
                output = getArabicStringDay(date.get(Calendar.DAY_OF_WEEK));
            } else {
                output = TimeUtils.toLocal(date.getTimeInMillis(), "EEE");
            }
        } else {

            if (HelperCalander.isTimeHijri() == 1) {

                CalendarShamsi shamsi = new CalendarShamsi(date.getTime());

                if (HelperCalander.isPersianUnicode) {
                    output = shamsi.date + " " + HelperCalander.getPersianMonthName(shamsi.month);
                } else {
                    output = HelperCalander.getPersianMonthName(shamsi.month) + " " + shamsi.date;
                }

            } else if (HelperCalander.isTimeHijri() == 2) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);

                GregorianCalendar gCal = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Locale ar = new Locale("ar");
                Calendar uCal = new UmmalquraCalendar(ar);
                uCal.setTime(gCal.getTime());// Used to properly format 'yy' pattern

                if (HelperCalander.isPersianUnicode) {
                    output = uCal.get(Calendar.DAY_OF_MONTH) + " " + getArabicMonthName(uCal.get(Calendar.MONTH));
                } else {

                    output = getArabicMonthName(uCal.get(Calendar.MONTH)) + " " + uCal.get(Calendar.DAY_OF_MONTH);
                }
            } else {

                if (HelperCalander.isLanguageArabic) {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "MM dd");
                    String[] _date = output.split(" ");
                    if (_date.length > 1) {
                        output = _date[1] + " " + convertEnglishMonthNameToArabic(Integer.parseInt(_date[0]));
                    }
                } else if (HelperCalander.isLanguagePersian) {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "MM dd");
                    String[] _date = output.split(" ");
                    if (_date.length > 1) {
                        output = _date[1] + " " + convertEnglishMonthNameToPersian(Integer.parseInt(_date[0]));
                    }
                } else {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "dd MMM");
                }
            }
        }

        return isPersianUnicode ? convertToUnicodeFarsiNumber(output) : output;
    }

    public static String getClocktime(Long timeinMili, boolean ltr) {

        String result;

        if (HelperCalander.isPersianUnicode) {
            result = TimeUtils.toLocal(timeinMili, "h:mm a");
            String[] _date = result.split(" ");
            if (_date.length > 1) {
                if (ltr) {
                    result = "\u200F" + _date[0] + " " + (_date[1].toLowerCase().equals("pm") ? G.context.getString(R.string.pm) : G.context.getString(R.string.am));
                } else {
                    result = "\u200F" + _date[0] + " " + (_date[1].toLowerCase().equals("pm") ? G.context.getString(R.string.pm) : G.context.getString(R.string.am));
                }
            }
        } else {
            result = TimeUtils.toLocal(timeinMili, "h:mm a");
        }

        return result;
    }

    private static String getPersianStringDay(int dayOfWeek) {

        String result = "";

        switch (dayOfWeek) {

            case 1:
                result = G.context.getString(R.string.sunday);
                break;
            case 2:
                result = G.context.getString(R.string.monday);
                break;
            case 3:
                result = G.context.getString(R.string.tuesday);
                break;
            case 4:
                result = G.context.getString(R.string.wednesday);
                break;
            case 5:
                result = G.context.getString(R.string.thursday);
                break;
            case 6:
                result = G.context.getString(R.string.friday);
                break;
            case 7:
                result = G.context.getString(R.string.saturday);
                break;
        }

        return result;
    }

    private static String getArabicStringDay(int dayOfWeek) {

        Calendar cal = new UmmalquraCalendar();
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("ar"));
    }
}
