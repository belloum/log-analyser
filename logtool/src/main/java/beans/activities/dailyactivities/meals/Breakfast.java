package beans.activities.dailyactivities.meals;

import beans.activities.dailyactivities.DailyActivity;
import beans.activities.dailyactivities.DailyActivity.DailyActivities;

/**
 * A Breakfast representation extending {@link DailyActivity} with a name as {@link DailyActivities} and a score 
 * 
 * @param timestamp
 *            The timestamp of the activity (non-)detection as String
 * @param score
 *            The computed score of the daily breakfast (between 0 and 1)
 * @see DailyActivity
 * @author Antoine Riché
 * @since 06/14/17
 */
public class Breakfast extends DailyActivity {

	/**
	 * Breakfast constructor
	 * 
	 * @param timestamp
	 *            The timestamp of the activity (non-)detection as String
	 * @param score
	 *            The computed score of the daily breakfast (between 0 and 1)
	 */
	public Breakfast(String timestamp, float score) {
		super(timestamp, score);
		this.setName(DailyActivities.Breakfast);
	}

}
