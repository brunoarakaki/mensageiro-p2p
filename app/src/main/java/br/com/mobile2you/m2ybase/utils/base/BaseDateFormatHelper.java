package br.com.mobile2you.m2ybase.utils.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by mobile2you on 11/08/16.
 */
public abstract class BaseDateFormatHelper {
    private Calendar mCal;
    private SimpleDateFormat mStandardSdf;

    public BaseDateFormatHelper(String dateFormat) {
        mStandardSdf = new SimpleDateFormat(dateFormat);
        mCal = Calendar.getInstance();
    }

    public abstract Locale getLocale();

    public String formatDate(String returningFormat, String dateToParse) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(returningFormat, getLocale());
        String formattedDate;
        formattedDate = sdf.format(mStandardSdf.parse(dateToParse));
        return formattedDate;
    }

    public String getShortDate(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.getDisplayName(Calendar.DATE, Calendar.SHORT, getLocale());
    }

    public String getLongDate(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.getDisplayName(Calendar.DATE, Calendar.LONG, getLocale());
    }

    public long getDateInMillis(String stringDate) throws ParseException {
        Date date = mStandardSdf.parse(stringDate);
        return date.getTime();
    }

    public long daysInBetween(String firstDate, String lastDate) throws ParseException {

        Date date1 = mStandardSdf.parse(firstDate);
        Date date2 = mStandardSdf.parse(lastDate);

        long diff = date2.getTime() - date1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int getYear(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.get(Calendar.YEAR);
    }

    public int getYearLastTwoDigits(String date) throws ParseException {
        int returnable = 0;
        mCal.setTime(mStandardSdf.parse(date));
        returnable = mCal.get(Calendar.YEAR);
        returnable = returnable % 100;
        return returnable;
    }

    public long getDaysLeft(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        return daysInBetween(date, calendar.getTime());
    }

    public long daysInBetween(String firstDate, Date lastDate) throws ParseException {
        Date date1 = mStandardSdf.parse(firstDate);

        long diff = date1.getTime() -  lastDate.getTime() ;
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int getHour(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.get(Calendar.MINUTE);
    }

    public String getDayOfTheWeekName(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getLocale());
    }

    public int getDay(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonthNumber(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.get(Calendar.MONTH) + 1;
    }

    public String getLongMonth(String date) throws ParseException {
        mCal.setTime(mStandardSdf.parse(date));
        return mCal.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocale());
    }

    public String getShortMonth(String date) throws ParseException {
        String returnable;
        mCal.setTime(mStandardSdf.parse(date));
        Locale locale = getLocale();
        returnable = mCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale);
        return returnable;
    }
}
