package beans.activities.dailyactivities;

/**
 * A representation of a daily activity with a name as {@link DailyActivities}
 * and a score
 * 
 * @param name
 *            The {@link DailyActivities} considered
 * @param timestamp
 *            The timestamp of the activity (non-)detection as String
 * @param score
 *            The computed score of the daily breakfast (between 0 and 1)
 *
 * @author Antoine Riché
 * @since 06/14/17
 */
public class DailyActivity {

	/**
	 * Enumeration containing all the daily activities monitored
	 * 
	 * @author ariche
	 * @since 06/14/17
	 *
	 */
	public enum DailyActivities {
		WakeUp, Breakfast, Lunch, Dinner, GoToBed
	}

	DailyActivities name;
	String timestamp;
	float score;

	/**
	 * DailyActivity constructor
	 * 
	 * @param timestamp
	 *            The timestamp of the activity (non-)detection as String
	 * @param score
	 *            The computed score of the daily breakfast (between 0 and 1)
	 */
	public DailyActivity(String timestamp, float score) {
		this.timestamp = timestamp;
		this.score = score;
	}

	/**
	 * Sets the name of the daily activity
	 * 
	 * @param name
	 *            The {@link DailyActivities} considered
	 */
	public void setName(DailyActivities name) {
		this.name = name;
	}

	/**
	 * Format the day to be write in a CSV-file
	 * 
	 * @return The formatted day for CSV-file
	 */
	public String formatForCSV() {
		StringBuilder sBuilder = new StringBuilder();
		if (!this.timestamp.equals("-1") && this.score > 0) {
			sBuilder.append(this.timestamp);
			sBuilder.append(";");
			sBuilder.append(this.score);
			sBuilder.append(";");
		} else
			sBuilder.append(";0;");
		return sBuilder.toString();
	}

	@Override
	public String toString() {
		return "DailyActivity [name=" + name + ", timestamp=" + timestamp + ", score=" + score + "]";
	}

	public String getTimestamp() {
		return timestamp;
	}

	public float getScore() {
		return score;
	}
	
}
