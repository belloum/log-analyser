package logtool.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import logtool.exceptions.PeriodException;
import logtool.utils.Configuration;
import logtool.utils.Utils;

public class Period {

	private Date mStartDate, mEndDate;

	public Period(final Date pStartDate, final Date pEndDate) throws PeriodException {
		if (pStartDate.before(pEndDate) || pStartDate.equals(pEndDate)) {
			this.mStartDate = pStartDate;
			this.mEndDate = pEndDate;
		} else {
			throw new PeriodException(PeriodException.START_AFTER_END);
		}
	}

	public Period(final long pTSStart, final long pTSEnd) throws PeriodException {
		this(new Date(pTSStart), new Date(pTSEnd));
	}

	public Period(final String pHourStart, final String pHourEnd) throws PeriodException {
		this(Utils.getHourFromString(pHourStart), Utils.getHourFromString(pHourEnd));
	}

	public Date getStartDate() {
		return this.mStartDate;
	}

	public void setStartDate(final Date pStartDate) {
		this.mStartDate = pStartDate;
	}

	public Date getEndDate() {
		return this.mEndDate;
	}

	public void setEndDate(final Date pEndDate) {
		this.mEndDate = pEndDate;
	}

	public boolean contains(final Date pDate) {
		return mStartDate.before(pDate) && mEndDate.after(pDate);
	}

	public String getFriendlyLabel() {
		final SimpleDateFormat sdf = new SimpleDateFormat(Configuration.HOUR_FORMAT);
		return String.format(Locale.FRANCE, "%s - %s", sdf.format(getStartDate()), sdf.format(getEndDate()));
	}
}
