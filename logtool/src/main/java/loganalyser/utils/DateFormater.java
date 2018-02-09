package loganalyser.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import beans.Period;
import loganalyser.exceptions.PeriodException;

public class DateFormater {

	public final static String SHORT_DAY_FORMAT = "yyyy.MM.dd, E";
	public final static String SHORT_MONTH_FORMAT = "yyyy.MM";

	public static String formatDate(long pTimestamp, String pFormat) {
		return formatDate(new Date(pTimestamp), pFormat);
	}

	public static String formatDate(Date pDate, String pFormat) {
		return new SimpleDateFormat(pFormat).format(pDate);
	}

	public static Period getTimestampLimitsOfDay(String pDay) throws ParseException, PeriodException {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new SimpleDateFormat(SHORT_DAY_FORMAT).parse(pDay));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long start = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		long end = calendar.getTimeInMillis();
		return new Period(start, end);
	}
}
