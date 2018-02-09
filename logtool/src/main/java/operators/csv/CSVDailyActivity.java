package operators.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beans.Day;
import beans.activities.dailyactivities.DailyActivity;
import beans.activities.dailyactivities.DailyActivity.DailyActivities;
import beans.activities.dailyactivities.GoToBed;
import beans.activities.dailyactivities.WakeUp;
import beans.activities.dailyactivities.meals.Breakfast;
import beans.activities.dailyactivities.meals.Dinner;
import beans.activities.dailyactivities.meals.Lunch;

/**
 * Extension of {@link CSVCleaner} to clean and gather {@link DailyActivity} by
 * day.
 * 
 * @param csvFile
 *            The CSV-file which must contains at least 5 columns
 *            <ul>
 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
 *            Sun)</i></li>
 *            <li><b>2. Friendly Hour: </b>a friendly representation of the hour
 *            of activity<i>(e.g: 04:23:27)</i></li>
 *            <li><b>3. Time: </b>the timestamp while activity has been detected
 *            <i>(e.g: 15807)</i></li>
 *            <li><b>4. Score: </b>the score of activity (from 0 - failure - to
 *            1 - success -)<i>(e.g: 0,8)</i></li>
 *            <li><b>5. Markers : </b>list of markers used for activity
 *            detection <i>(e.g: M1[4] at 07:50:35, M2[1] at 07:56:14)</i></li>
 *            </li>
 *            </ul>
 * @param days
 *            The list of days gathering the score
 * 
 * @author Antoine Riché
 * @since 06/14/17
 * @see DailyActivity
 * @see CSVCleaner
 * @see Day
 */
public class CSVDailyActivity extends CSVCleaner {

	/**
	 * Index of the Timestamp field
	 */
	public static int FIELD_TIMESTAMP = 2;
	/**
	 * Index of the Score field
	 */
	public static int FIELD_SCORE = 4;

	DailyActivities activity;

	/**
	 * CSVSleep Constructor
	 * 
	 * @param csvFile
	 *            The CSV-file of the constructor must contains at least 5
	 *            columns
	 *            <ul>
	 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
	 *            Sun)</i></li>
	 *            <li><b>2. Friendly Hour: </b>a friendly representation of the
	 *            hour of activity<i>(e.g: 04:23:27)</i></li>
	 *            <li><b>3. Time: </b>the timestamp while activity has been
	 *            detected <i>(e.g: 15807)</i></li>
	 *            <li><b>4. Score: </b>the score of activity (from 0 - failure -
	 *            to 1 - success -)<i>(e.g: 0,8)</i></li>
	 *            <li><b>5. Markers : </b>list of markers used for activity
	 *            detection <i>(e.g: M1[4] at 07:50:35, M2[1] at 07:56:14)</i>
	 *            </li></li>
	 *            </ul>
	 * @param dailyActivity
	 *            The {@link DailyActivity} of the CSV-file
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>CSV-file does not exist</li>
	 *             <li>CSV-file is empty</li>
	 *             <li>CSV-file is malformed</li>
	 *             </ul>
	 * 
	 */
	public CSVDailyActivity(File csvFile, DailyActivities dailyActivity) throws Exception {
		super(csvFile);
		this.activity = dailyActivity;

		if (this.lines.get(0).length < 5)
			throw new Exception("CSV-file is malformed");
	}

	@Override
	public boolean exportCSV(File file) throws Exception {

		StringBuilder sBuilder = new StringBuilder(getHeaders());
		int index = 0;

		for (String[] line : this.lines) {
			sBuilder.append("\n");
			sBuilder.append(index + ";");
			sBuilder.append(line[FIELD_DAY] + ";");
			sBuilder.append(line[FIELD_SCORE].replace(".", ",") + ";");
			sBuilder.append(line[FIELD_TIMESTAMP] + ";");
			sBuilder.append("\n");
			sBuilder.append(index + ";");
			index++;
		}

		return saveCSV(sBuilder.toString(), file);
	}

	/**
	 * Gathers sleep periods day by day by parsing the CSV-file
	 * 
	 * @return A list of days containing sleep periods
	 * 
	 */

	/**
	 * Gathers activity scores day by day by parsing the CSV-file
	 * 
	 * @return A list of days containing activity scores
	 * 
	 */
	public List<Day> gatherDays() {

		List<Day> days = new ArrayList<Day>();
		for (String[] line : this.lines) {

			String time = line[FIELD_TIMESTAMP];
			float score = Float.parseFloat(line[FIELD_SCORE]);

			Day day = new Day(line[FIELD_DAY]);
			switch (this.activity) {
			case Breakfast:
				day.setBreakfast(new Breakfast(time, score));
				break;
			case Dinner:
				day.setDinner(new Dinner(time, score));
				break;
			case GoToBed:
				day.setGoToBed(new GoToBed(time, score));
				break;
			case Lunch:
				day.setLunch(new Lunch(time, score));
				break;
			case WakeUp:
				day.setWakeUp(new WakeUp(time, score));
				break;
			default:
				break;
			}
			days.add(day);
		}

		return days;
	}

}
