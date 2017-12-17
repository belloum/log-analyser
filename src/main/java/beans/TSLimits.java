package beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TSLimits {

	private static final SimpleDateFormat mSDFormat = new SimpleDateFormat("HH:mm");
	private long mTSStart, mTSEnd;

	public TSLimits(long pTSStart, long pTSEnd) {
		this.mTSStart = pTSStart;
		this.mTSEnd = pTSEnd;
	}

	public TSLimits(String pHourStart, String pHourEnd) throws ParseException {

		// TODO ArrayIndexOfBoundsException
		int sH = Integer.parseInt(pHourStart.split(":")[0]);
		int sM = Integer.parseInt(pHourStart.split(":")[1]);
		int eH = Integer.parseInt(pHourEnd.split(":")[0]);
		int eM = Integer.parseInt(pHourEnd.split(":")[1]);
		if (sH > 23 || sH < 0) {
			throw new IllegalArgumentException(String.format("Invalid hour: %d", sH));
		} else if (sM > 59 || sM < 0) {
			throw new IllegalArgumentException(String.format("Invalid minute: %d", sM));
		} else if (eH > 23 || eH < 0) {
			throw new IllegalArgumentException(String.format("Invalid hour: %d", eH));
		} else if (eM > 59 || eM < 0) {
			throw new IllegalArgumentException(String.format("Invalid minute: %d", eM));
		} else {
			Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(mSDFormat.parse(pHourStart));
				this.mTSStart = calendar.getTimeInMillis();

				calendar.setTime(mSDFormat.parse(pHourEnd));
				if (calendar.get(Calendar.HOUR_OF_DAY) != 23 || calendar.get(Calendar.MINUTE) != 59) {
					calendar.add(Calendar.MINUTE, 1);
				} else {
					calendar.set(Calendar.SECOND, 59);
					calendar.set(Calendar.MILLISECOND, 99);
				}
				this.mTSEnd = calendar.getTimeInMillis();
			} catch (ParseException exception) {
				throw exception;
			}
		}
	}

	public long getTSStart() {
		return mTSStart;
	}

	public void setTSStart(long pTSStart) {
		this.mTSStart = pTSStart;
	}

	public long getTSEnd() {
		return mTSEnd;
	}

	public void setTSEnd(long pTSEnd) {
		this.mTSEnd = pTSEnd;
	}

	public TSLimits() {
	}

	public boolean contains(Date pDate) {
		return new Date(this.mTSStart).before(pDate) && new Date(this.mTSEnd).after(pDate);
	}

	@Override
	public String toString() {
		return "TSLimits [mTSStart=" + mTSStart + ", mTSEnd=" + mTSEnd + "]";
	}

	public static String getFriendlyLabel(TSLimits pTsLimits) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return String.format(Locale.FRANCE, "%s - %s", sdf.format(pTsLimits.getTSStart()),
				sdf.format(pTsLimits.getTSEnd()));
	}
}
