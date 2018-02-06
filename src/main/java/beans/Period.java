package beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import loganalyser.exceptions.PeriodException;

public class Period {

	private static final SimpleDateFormat mSDFormat = new SimpleDateFormat("HH:mm");
	private Date mStartDate, mEndDate;

	public Period(Date pStartDate, Date pEndDate) throws PeriodException {
		if (pStartDate.before(pEndDate) || pStartDate.equals(pEndDate)) {
			this.mStartDate = pStartDate;
			this.mEndDate = pEndDate;
		} else {
			throw new PeriodException(PeriodException.START_AFTER_END);
		}
	}

	public Period(long pTSStart, long pTSEnd) throws PeriodException {
		this(new Date(pTSStart), new Date(pTSEnd));
	}

	public Period(String pHourStart, String pHourEnd) throws PeriodException {

		int sH, sM, eH, eM;
		try {
			sH = Integer.parseInt(pHourStart.split(":")[0]);
			sM = Integer.parseInt(pHourStart.split(":")[1]);
		} catch (ArrayIndexOutOfBoundsException exception) {
			throw exception;
		}
		try {
			eH = Integer.parseInt(pHourEnd.split(":")[0]);
			eM = Integer.parseInt(pHourEnd.split(":")[1]);
		} catch (ArrayIndexOutOfBoundsException exception) {
			throw new PeriodException(PeriodException.UNPARSABLE, exception);
		}
		if (sH > 23 || sH < 0) {
			throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_HOUR, sH));
		} else if (sM > 59 || sM < 0) {
			throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_MINUTE, sM));
		} else if (eH > 23 || eH < 0) {
			throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_HOUR, eH));
		} else if (eM > 59 || eM < 0) {
			throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_MINUTE, eM));
		} else {
			Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(mSDFormat.parse(pHourStart));
				this.mStartDate = calendar.getTime();

				calendar.setTime(mSDFormat.parse(pHourEnd));
				if (calendar.get(Calendar.HOUR_OF_DAY) != 23 || calendar.get(Calendar.MINUTE) != 59) {
					calendar.add(Calendar.MINUTE, 1);
				} else {
					calendar.set(Calendar.SECOND, 59);
					calendar.set(Calendar.MILLISECOND, 99);
				}
				this.mEndDate = calendar.getTime();
			} catch (ParseException exception) {
				throw new PeriodException(PeriodException.UNPARSABLE, exception);
			}
		}
	}

	public Date getStartDate() {
		return this.mStartDate;
	}

	public void setStartDate(Date pStartDate) {
		this.mStartDate = pStartDate;
	}

	public Date getEndDate() {
		return this.mEndDate;
	}

	public void setEndDate(Date pEndDate) {
		this.mEndDate = pEndDate;
	}

	public boolean contains(Date pDate) {
		return mStartDate.before(pDate) && mEndDate.after(pDate);
	}

	@Override
	public String toString() {
		return "TSLimits [mStartDate=" + mStartDate + ", mEndDate=" + mEndDate + "]";
	}

	public static String getFriendlyLabel(Period pTsLimits) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return String.format(Locale.FRANCE, "%s - %s", sdf.format(pTsLimits.getStartDate()),
				sdf.format(pTsLimits.getEndDate()));
	}
}
