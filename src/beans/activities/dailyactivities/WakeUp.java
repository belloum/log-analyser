package beans.activities.dailyactivities;

import beans.activities.dailyactivities.DailyActivity.DailyActivities;

/**
 * A Wake up representation extending {@link DailyActivity} with a name as
 * {@link DailyActivities} and a score
 * 
 * @param timestamp
 *            The timestamp of the activity (non-)detection as String
 * @param score
 *            The computed score of the daily wake up (between 0 and 1)
 * @see DailyActivity
 * @author Antoine Riché
 * @since 06/14/17
 */
public class WakeUp extends DailyActivity {

	/**
	 * WakeUp constructor
	 * 
	 * @param timestamp
	 *            The timestamp of the activity (non-)detection as String
	 * @param score
	 *            The computed score of the daily wake up (between 0 and 1)
	 */
	public WakeUp(String timestamp, float score) {
		super(timestamp, score);
		this.setName(DailyActivities.WakeUp);
	}

}
