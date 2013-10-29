/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw>
 *                    Yu Chin Cheng <yccheng@csie.ntut.edu.tw>
 *                    Chien-Tsun Chen <ctchen@ctchen.idv.tw>
 *                    Tsui-Chen She <kay_sher@hotmail.com>
 *                    Chia-Hao Wu<chwu2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/*
 * Created on 2005/5/31
 *
 * Copyright (c) 2004 CSIE National Taipei University of Technology.
 * All Rights Reserved.
 */
package ntut.csie.ezScrum.SaaS.util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author kay
 * 
 */
public class DateUtil {
	public static final String _DEFAULT_TIMEZON = "GMT+8";
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

	public static Date getNowDate() {
		return getTimezoneDate(_DEFAULT_TIMEZON);
	}
	
	public static String getNow() {
		SimpleDateFormat sdf = new SimpleDateFormat(_16DIGIT_DATE_TIME);

		return sdf.format(getTimezoneDate(_DEFAULT_TIMEZON));
	}

	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_DATE_2);

		return sdf.format(getTimezoneDate(_DEFAULT_TIMEZON));
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(_8DIGIT_24TIME);

		return sdf.format(getTimezoneDate(_DEFAULT_TIMEZON));
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

	public static String getCurrentTimeInMySqlTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(_16DIGIT_DATE_TIME_MYSQL);

		return sdf.format(getTimezoneDate(_DEFAULT_TIMEZON));
	}
	public static Date dayFilter(String text) {
		SimpleDateFormat format = new SimpleDateFormat(_8DIGIT_DATE_1);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getTimezoneDate(_DEFAULT_TIMEZON);
	}

	public static Date dayFillter(String text, String type) {
		SimpleDateFormat format = new SimpleDateFormat(type);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getTimezoneDate(_DEFAULT_TIMEZON);
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
	
	
	//stateCode GMT+800
	public static Date getTimezoneDate(String stateCode) {  
		final int GMT8 = 8;
	    Calendar calendar = Calendar.getInstance();  
	    @SuppressWarnings("deprecation")
		Date date = new Date();
	    date.setHours(date.getHours() + GMT8);
	    return date;  
	}
}
