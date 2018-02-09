package loganalyser.beans.activityresults;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityResult {

	protected Date date;
	protected float score;
	protected boolean success;
	protected Date publishHour;

	private static final SimpleDateFormat mSDFDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	private static final SimpleDateFormat mSDFHOUR = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);

	public ActivityResult(Date pDate, float pScore, Date pPublishHour) {
		this.date = pDate;
		this.score = pScore;
		this.success = pScore >= 0.8f;
		this.publishHour = pPublishHour;
	}

	public ActivityResult(String pAlgoResult) {
		try {
			String result[] = pAlgoResult.split(";");
			this.date = mSDFDay.parse(result[0]);
			this.score = Float.parseFloat(result[4].trim());
			this.success = score >= 0.8f;
			this.publishHour = mSDFHOUR.parse(result[1].trim());
		} catch (Exception e) {
			System.err.println("Line was: " + pAlgoResult);
			System.err.println("Exception while creating ActivityResult from algoLine: " + e);
		}
	}

	public Date getDate() {
		return date;
	}

	public float getScore() {
		return score;
	}

	public boolean isSuccess() {
		return success;
	}

	public Date getPublishHour() {
		return publishHour;
	}

	@Override
	public String toString() {
		return "ActivityResult [date=" + date + ", score=" + score + ", success=" + success + ", publishHour="
				+ publishHour + "]";
	}

}
