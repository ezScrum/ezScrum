package ntut.csie.ezScrum.web.support;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kay
 * 
 */
public class DateUtil {
	public static final String _16DIGIT_DATE_TIME = "yyyy/MM/dd-HH:mm:ss";
	public static final String _16DIGIT_DATE_TIME_2 = "yyyyMMddHHmmss";
	public static final String _16DIGIT_DATE_TIME_MYSQL = "yyyy-MM-dd HH:mm:ss";
	public static final String _8DIGIT_24TIME = "HH:mm:ss";
	public static final String _8DIGIT_DATE_1 = "yyyy/MM/dd";
	public static final String _8DIGIT_DATE_2 = "yyyy-MM-dd";

	public static final String FRONT_DIRECTION = "front";
	public static final String BACK_DIRECTION = "back";

	public static String formatTime(long ms) {
		long secs = ms / 1000;
		long min = secs / 60;
		secs = secs % 60;
		ms = ms % 1000;

		if (min > 0) {
			return min + " minutes " + secs + " seconds " + ms + " ms";
		}

		return secs + " seconds " + ms + " ms";
	}

	public static String getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(_16DIGIT_DATE_TIME);

		return sdf.format(new Date());
	}

	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_DATE_2);

		return sdf.format(new Date());
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_24TIME);

		return sdf.format(new Date());
	}

	public static String formatByHMS(int hh, int mm, int ss) {
		return fillWithZero(hh) + ":" + fillWithZero(mm) + ":"
				+ fillWithZero(ss);
	}

	private static String fillWithZero(int aData) {
		if (aData < 10) {
			return "0" + aData;
		}

		return aData + "";
	}

	public static String formatByDate(Date aDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_DATE_2);

		return sdf.format(aDate);
	}

	public static String formatBySlashForm(Date aDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_DATE_1);

		return sdf.format(aDate);
	}

	public static String format16ByDate(Date aDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(_16DIGIT_DATE_TIME);

		return sdf.format(aDate);
	}

	public static String format(Date aDate, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat(type);

		return sdf.format(aDate);
	}
	
	public static String format(Long dateMs, String type) {
		Date date = new Date(dateMs);
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		return sdf.format(date);
	}

	public static Time hmsToTime(int hh, int mm, int ss) {
		long theLongTime = (hh * 3600) + (mm * 60) + ss;
		Time theTime = new Time(theLongTime);

		return theTime;
	}

	public static int getHours(int minutes) {
		int hr = minutes / 60;

		return hr;
	}

	public static int getMinutes(int minutes) {
		int minute = minutes % 60;

		return minute;
	}

	public static Time minutesToTime(int minutes) {
		int hr = getHours(minutes);
		int minute = getMinutes(minutes);

		return hmsToTime(hr, minute, 0);
	}

	public static Calendar dateToCalendar(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.YEAR, year);

		return cal;
	}
public static String getCurrentTimeInMySqlTime()
{
	SimpleDateFormat sdf = new SimpleDateFormat(_16DIGIT_DATE_TIME_MYSQL);

	return sdf.format(new Date());
	}
	public static Date dayFilter(String text) {
		SimpleDateFormat format = new SimpleDateFormat(_8DIGIT_DATE_1);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date dayFillter(String text, String type) {
		SimpleDateFormat format = new SimpleDateFormat(type);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date parse(String text, String type) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(type);
		return format.parse(text);
	}

	// �N�ɶ��h���ѥH�U����T
	public static Date dayFilter(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(_8DIGIT_DATE_1);
		return dayFilter(format.format(date));
	}

	public static Date nearWorkDate(Date date, String direction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// �Y���P����άP����
		while (isHoliday(calendar.getTime())) {
			if (direction.equals(FRONT_DIRECTION))
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			else if (direction.equals(BACK_DIRECTION))
				calendar.add(Calendar.DAY_OF_WEEK, 1);
			else
				break;
		}

		return calendar.getTime();
	}

	public static int numWorkDay(Date start, Date end) {
		int count = 0;
		Calendar index = Calendar.getInstance();
		index.setTime(start);
		while (index.getTimeInMillis() <= end.getTime()) {
			if (!isHoliday(index.getTime()))
				count++;
			index.add(Calendar.DATE, 1);
		}
		return count;
	}

	public static boolean isHoliday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return true;
		return false;
	}
}
