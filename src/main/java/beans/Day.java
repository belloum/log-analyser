package beans;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import beans.activities.Exit;
import beans.activities.SleepPeriod;
import beans.activities.dailyactivities.DailyActivity;
import beans.activities.dailyactivities.DailyActivity.DailyActivities;
import beans.activities.dailyactivities.GoToBed;
import beans.activities.dailyactivities.WakeUp;
import beans.activities.dailyactivities.meals.Breakfast;
import beans.activities.dailyactivities.meals.Dinner;
import beans.activities.dailyactivities.meals.Lunch;
import loganalyser.beans.SoftLog;
import operators.csv.CSVCleaner;
import operators.csv.CSVDailyActivity;
import operators.csv.CSVExit;
import operators.csv.CSVSleep;

/**
 * @param label
 *            The label <i>(e.g: 2017-03-12 Sun)</i>
 * @param exits
 *            The list of {@link Exit} extract with {@link CSVExit}
 * @param sleeps
 *            The list of {@link SleepPeriod} extract with {@link CSVSleep}
 * @param sleeps
 *            The list of {@link SleepPeriod} in living extract with
 *            {@link CSVSleep}
 * @param wakeUp
 *            The {@link WakeUp} score extract with {@link CSVDailyActivity}
 * @param breakfast
 *            The {@link Breakfast} score extract with {@link CSVDailyActivity}
 * @param lunch
 *            The {@link Lunch} score extract with {@link CSVDailyActivity}
 * @param dinner
 *            The {@link Dinner} score extract with {@link CSVDailyActivity}
 * @param goToBed
 *            The {@link GoToBed} score extract with {@link CSVDailyActivity}
 * @param logs
 *            The list of {@link MyLog} for the day
 * @param initTS
 *            The timestamp at 0:00
 * 
 * @see Exit
 * @see SleepPeriod
 * @see DailyActivity
 * @see CSVDailyActivity
 * @see CSVExit
 * @see CSVSleep
 * @see CSVCleaner
 * 
 * @author Antoine Riché
 * @since 06/14/17
 */
public class Day {

	/**
	 * Min timestamp for one day
	 */
	public static String START_TS = "0";
	/**
	 * Max timestamp for one days
	 */
	public static String END_TS = "86400";

	public enum LabelFormat {
		DD_MM_YY, DD_MM, DD_MM_YYYY, YYYY_MM_DD,
	}

	String label;
	Exit[] exits;
	SleepPeriod[] sleeps;
	SleepPeriod[] sleepLiving;
	WakeUp wakeUp;
	Breakfast breakfast;
	Lunch lunch;
	Dinner dinner;
	GoToBed goToBed;

	/**
	 * @since 19/06/17
	 */
	List<SoftLog> logs;
	/**
	 * @since 19/06/17
	 */
	long initTS;

	/**
	 * Day constructor
	 * 
	 * @param label
	 *            The label <i>(e.g: 2017-03-12 Sun)</i>
	 */
	public Day(String label) {
		this.label = label;
	}

	/**
	 * Day constructor
	 * 
	 * @param label
	 *            The label <i>(e.g: 2017-03-12 Sun)</i>
	 * @param exits
	 *            The list of {@link Exit} extract with {@link CSVExit}
	 * @param sleeps
	 *            The list of {@link SleepPeriod} extract with {@link CSVSleep}
	 */
	public Day(String label, Exit[] exits, SleepPeriod[] sleeps) {
		this.label = label;
		this.exits = exits;
		this.sleeps = sleeps;
	}

	/**
	 * Gets the label of the day
	 * 
	 * @return The label <i>(e.g: 2017-03-12 Sun)</i>
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the {@link Exit} of the day
	 * 
	 * @return The list of {@link Exit} extract with {@link CSVExit}
	 */
	public Exit[] getExits() {
		return exits;
	}

	/**
	 * Gets the {@link SleepPeriod} of the day
	 * 
	 * @return The list of {@link SleepPeriod} extract with {@link CSVSleep}
	 */
	public SleepPeriod[] getSleeps() {
		return sleeps;
	}

	/**
	 * Sets the daily {@link WakeUp} score
	 * 
	 * @param wakeUp
	 *            The {@link WakeUp} score extract with {@link CSVDailyActivity}
	 */
	public void setWakeUp(WakeUp wakeUp) {
		this.wakeUp = wakeUp;
	}

	/**
	 * Sets the daily {@link Breakfast} score
	 * 
	 * @param breakfast
	 *            The {@link Breakfast} score extract with
	 *            {@link CSVDailyActivity}
	 */
	public void setBreakfast(Breakfast breakfast) {
		this.breakfast = breakfast;
	}

	/**
	 * Sets the daily {@link Lunch} score
	 * 
	 * @param lunch
	 *            The {@link Lunch} score extract with {@link CSVDailyActivity}
	 */
	public void setLunch(Lunch lunch) {
		this.lunch = lunch;
	}

	/**
	 * Sets the daily {@link Dinner} score
	 * 
	 * @param dinner
	 *            The {@link Dinner} score extract with {@link CSVDailyActivity}
	 */
	public void setDinner(Dinner dinner) {
		this.dinner = dinner;
	}

	/**
	 * Sets the daily {@link GoToBed} score
	 * 
	 * @param goToBed
	 *            The {@link GoToBed} score extract with
	 *            {@link CSVDailyActivity}
	 */
	public void setGoToBed(GoToBed goToBed) {
		this.goToBed = goToBed;
	}

	/**
	 * Gets the {@link SleepPeriod} in living of the day
	 * 
	 * @return The list of {@link SleepPeriod} in living extract with
	 *         {@link CSVSleep}
	 */
	public SleepPeriod[] getSleepLiving() {
		return sleepLiving;
	}

	/**
	 * Sets the daily {@link SleepPeriod} in living of the day
	 * 
	 * @param goToBed
	 *            The list of {@link SleepPeriod} in living extract with
	 *            {@link CSVSleep}
	 */
	public void setSleepLiving(SleepPeriod[] sleepLiving) {
		this.sleepLiving = sleepLiving;
	}

	/**
	 * Fill the day with {@link DailyActivity} scores, {@link SleepPeriod} list
	 * and {@link Exit} list
	 * 
	 * @param wakeUpCSV
	 *            CSV-file that contains {@link WakeUp} scores
	 * @param breakfastCSV
	 *            CSV-file that contains {@link Breakfast} scores
	 * @param lunchCSV
	 *            CSV-file that contains {@link Lunch} scores
	 * @param dinnerCSV
	 *            CSV-file that contains {@link Dinner} scores
	 * @param goToBedCSV
	 *            CSV-file that contains {@link GoToBed} scores
	 * @param exitCSV
	 *            CSV-file that contains {@link Exit} list
	 * @param sleepCSV
	 *            CSV-file that contains {@link SleepPeriod} list
	 * @param sleepCSV
	 *            CSV-file that contains {@link SleepPeriod} in living list
	 * @throws Exception
	 *             Exceptions are thrown if one of the CSV-file
	 *             <ul>
	 *             <li>does not exist</li>
	 *             <li>is malformed</li>
	 *             </ul>
	 *             Exceptions are thrown if one of the CSV-file does not exist
	 */
	public void fillDay(File wakeUpCSV, File breakfastCSV, File lunchCSV, File dinnerCSV, File goToBedCSV, File exitCSV,
			File sleepCSV, File sleepLivingCSV) throws Exception {

		CSVDailyActivity cDailyActivity = new CSVDailyActivity(wakeUpCSV, DailyActivities.WakeUp);
		List<Day> wakeUps = cDailyActivity.gatherDays();
		cDailyActivity = new CSVDailyActivity(breakfastCSV, DailyActivities.Breakfast);
		List<Day> breakfasts = cDailyActivity.gatherDays();
		cDailyActivity = new CSVDailyActivity(lunchCSV, DailyActivities.Lunch);
		List<Day> lunches = cDailyActivity.gatherDays();
		cDailyActivity = new CSVDailyActivity(dinnerCSV, DailyActivities.Dinner);
		List<Day> dinners = cDailyActivity.gatherDays();
		cDailyActivity = new CSVDailyActivity(goToBedCSV, DailyActivities.GoToBed);
		List<Day> goToBeds = cDailyActivity.gatherDays();

		this.wakeUp = new WakeUp("-1", 0);
		this.breakfast = new Breakfast("-1", 0);
		this.lunch = new Lunch("-1", 0);
		this.dinner = new Dinner("-1", 0);
		this.goToBed = new GoToBed("-1", 0);

		for (Day d : wakeUps) {
			if (this.label.equals(d.label)) {
				this.wakeUp = d.wakeUp;
			}
		}
		for (Day d : breakfasts) {
			if (this.label.equals(d.label)) {
				this.breakfast = d.breakfast;
				break;
			}
		}
		for (Day d : lunches) {
			if (this.label.equals(d.label)) {
				this.lunch = d.lunch;
				break;
			}
		}
		for (Day d : dinners) {
			if (this.label.equals(d.label)) {
				this.dinner = d.dinner;
				break;
			}
		}
		for (Day d : goToBeds) {
			if (this.label.equals(d.label)) {
				this.goToBed = d.goToBed;
				break;
			}
		}

		CSVExit cExit = new CSVExit(exitCSV);
		List<Day> exits = cExit.gatherExitsByDay();
		this.exits = new Exit[cExit.getMaxColumns()];

		for (Day d : exits) {
			if (this.label.equals(d.label)) {
				this.exits = d.exits;
				break;
			}
		}

		CSVSleep cSleep = new CSVSleep(sleepCSV);
		List<Day> sleeps = cSleep.gatherSleepPeriodsByDay();
		this.sleeps = new SleepPeriod[cSleep.getMaxColumns()];

		for (Day d : sleeps) {
			if (this.label.equals(d.label)) {
				this.sleeps = d.sleeps;
				break;
			}
		}

		CSVSleep cSleepLiving = new CSVSleep(sleepLivingCSV);
		List<Day> sleepsLiving = cSleepLiving.gatherSleepPeriodsByDay();
		this.sleepLiving = new SleepPeriod[cSleepLiving.getMaxColumns()];

		for (Day d : sleepsLiving) {
			if (this.label.equals(d.label)) {
				this.sleepLiving = d.sleeps;
				break;
			}
		}

	}

	/**
	 * Format the day to be write in a CSV-file
	 * 
	 * @param index
	 *            The index of the day into the loop. This index is important
	 *            for the graphic drawings
	 * @return The formatted day for CSV-file
	 */
	public String formatForCSV(int index) {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(index + ";");
		sBuilder.append(this.label + ";");

		sBuilder.append(";");
		sBuilder.append(this.wakeUp.formatForCSV());
		sBuilder.append(this.breakfast.formatForCSV());
		sBuilder.append(this.lunch.formatForCSV());
		sBuilder.append(this.dinner.formatForCSV());
		sBuilder.append(this.goToBed.formatForCSV());

		return sBuilder.toString();
	}

	/**
	 * Formats the day label according to the {@link LabelFormat} in parameters
	 * 
	 * @param format
	 *            The {@link LabelFormat} of the output label
	 * @return The formatted label
	 * @since 06/16/17
	 */
	public static String formatLabelDay(LabelFormat format) {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		String formattedLabel;
		switch (format) {
		case DD_MM:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d", day, month);
			break;
		case DD_MM_YY:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%02d", day, month, (year - 2000));
			break;
		case DD_MM_YYYY:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
			break;
		case YYYY_MM_DD:
			formattedLabel = String.format(Locale.FRANCE, "%d/%02d/%02d", year, month, day);
			break;
		default:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
			break;
		}

		return formattedLabel;
	}

	/**
	 * Formats the label of the day which contains the timestamp passed in
	 * parameters according to the {@link LabelFormat} in parameters
	 * 
	 * @param format
	 *            The {@link LabelFormat} of the output label
	 * @param timestamp
	 *            The timestamp of the day to format (in milliseconds)
	 * @return The formatted label
	 * @since 06/16/17
	 */
	public static String formatLabelDay(LabelFormat format, long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		String formattedLabel;
		switch (format) {
		case DD_MM:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d", day, month);
			break;
		case DD_MM_YY:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%02d", day, month, (year - 2000));
			break;
		case DD_MM_YYYY:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
			break;
		case YYYY_MM_DD:
			formattedLabel = String.format(Locale.FRANCE, "%d/%02d/%02d", year, month, day);
			break;
		default:
			formattedLabel = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
			break;
		}

		return formattedLabel;
	}

	@Override
	public String toString() {
		return "Day [label=" + label + ", exits=" + Arrays.toString(exits) + ", sleeps=" + Arrays.toString(sleeps)
				+ ", sleepLiving=" + Arrays.toString(sleepLiving) + ", wakeUp=" + wakeUp + ", breakfast=" + breakfast
				+ ", lunch=" + lunch + ", dinner=" + dinner + ", goToBed=" + goToBed + ", logs=" + logs + ", initTS="
				+ initTS + "]";
	}

}
