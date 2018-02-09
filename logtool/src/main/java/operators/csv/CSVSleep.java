package operators.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beans.Day;
import beans.activities.SleepPeriod;

/**
 * Extension of {@link CSVCleaner} to clean and gather {@link SleepPeriod} by
 * day.
 * 
 * @param csvFile
 *            TThe CSV-file which must contains at least 8 columns
 *            <ul>
 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
 *            Sun)</i></li>
 *            <li><b>2. Friendly Start: </b>a friendly representation of the
 *            starting sleep timestamp <i>(e.g: 04:23:27)</i></li>
 *            <li><b>3. Start: </b>the starting sleep timestamp <i>(e.g:
 *            15807)</i></li>
 *            <li><b>4. Activity: </b>the name of activity <i>(e.g: sleep)</i>
 *            </li>
 *            <li><b>5. Friendly End: </b>a friendly representation of the
 *            ending sleep timestamp <i>(e.g: 05:57:30)</i></li>
 *            <li><b>6. End: </b>the ending sleep timestamp <i>(e.g: 21450)</i>
 *            </li>
 *            <li><b>7. Duration: </b>the sleep duration <i>(e.g: for
 *            01:34:03)</i></li>
 *            <li><b>8. Indication: </b>an indication about the sleep period
 *            could be ' * ', ' . ' or ' '
 *            <ul>
 *            <li><b>' * ':</b> means that the period is inside the sleep-slot
 *            </li>
 *            <li><b>' . ':</b> means that the period is across the sleep-slot
 *            </li>
 *            <li><b>' ' :</b> means that the period is outside the sleep-slot
 *            </li>
 *            </ul>
 *            </li>
 *            </ul>
 * @param days
 *            The list of days gathering the sleep periods
 * 
 * @author Antoine Riché
 * @since 06/14/17
 * @see SleepPeriod
 * @see CSVCleaner
 * @see Day
 */
public class CSVSleep extends CSVCleaner {

	/**
	 * Index of the Start field
	 */
	public static int FIELD_START_TS = 2;
	/**
	 * Index of the End field
	 */
	public static int FIELD_END_TS = 5;

	/**
	 * For formating to CSV
	 * 
	 * @since 27/06/17
	 */
	public enum SleepRoom {
		Bedroom, Living
	}

	List<Day> days;

	/**
	 * CSVSleep Constructor
	 * 
	 * @param csvFile
	 *            The CSV-file of the constructor must contains at least 8
	 *            columns
	 *            <ul>
	 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
	 *            Sun)</i></li>
	 *            <li><b>2. Friendly Start: </b>a friendly representation of the
	 *            starting sleep timestamp <i>(e.g: 04:23:27)</i></li>
	 *            <li><b>3. Start: </b>the starting sleep timestamp <i>(e.g:
	 *            15807)</i></li>
	 *            <li><b>4. Activity: </b>the name of activity <i>(e.g:
	 *            sleep)</i></li>
	 *            <li><b>5. Friendly End: </b>a friendly representation of the
	 *            ending sleep timestamp <i>(e.g: 05:57:30)</i></li>
	 *            <li><b>6. End: </b>the ending sleep timestamp <i>(e.g:
	 *            21450)</i></li>
	 *            <li><b>7. Duration: </b>the sleep duration <i>(e.g: for
	 *            01:34:03)</i></li>
	 *            <li><b>8. Indication: </b>an indication about the sleep period
	 *            could be ' * ', ' . ' or ' '
	 *            <ul>
	 *            <li><b>' * ':</b> means that the period is inside the
	 *            sleep-slot</li>
	 *            <li><b>' . ':</b> means that the period is across the
	 *            sleep-slot</li>
	 *            <li><b>' ' :</b> means that the period is outside the
	 *            sleep-slot</li>
	 *            </ul>
	 *            </li>
	 *            </ul>
	 * 
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>CSV-file does not exist</li>
	 *             <li>CSV-file is empty</li>
	 *             <li>CSV-file is malformed</li>
	 *             </ul>
	 * 
	 */
	public CSVSleep(File csvFile) throws Exception {
		super(csvFile);

		if (this.lines.get(0).length < 8)
			throw new Exception("CSV-file is malformed");

		this.days = gatherSleepPeriodsByDay();
	}

	/**
	 * Gets the number of columns of the final cleaned CSV-file, depending on
	 * max sleep periods count per day.
	 * 
	 * @see SleepPeriod
	 * @see Day
	 */
	@Override
	public int getMaxColumns() {
		return (super.getMaxColumns() - 1) * 3 - 1;
	}

	/**
	 * Gathers sleep periods day by day by parsing the CSV-file
	 * 
	 * @return A list of days containing sleep periods
	 * 
	 */
	public List<Day> gatherSleepPeriodsByDay() {
		String dayLabel = "";
		List<Day> days = new ArrayList<>();
		SleepPeriod[] slPeriods = null;

		int index = 0;
		String alternativeEnd = "";

		for (String[] line : this.lines) {

			if (!dayLabel.equals(line[FIELD_DAY])) {
				index = 0;

				if (slPeriods != null)
					days.add(new Day(dayLabel, null, slPeriods));

				slPeriods = new SleepPeriod[getMaxColumns()];

				if (!alternativeEnd.isEmpty()) {
					slPeriods[index] = new SleepPeriod(Day.START_TS, alternativeEnd);
					alternativeEnd = "";
					index++;
				}

				dayLabel = line[FIELD_DAY];
			}

			String start = line[FIELD_START_TS].replaceAll(" ", "");
			start = !start.equals("") ? start : "0";

			String end = line[FIELD_END_TS].replaceAll(" ", "");
			end = !end.equals("") ? end : "0";

			if (Integer.parseInt(start) > Integer.parseInt(end)) {
				alternativeEnd = end;
				end = Day.END_TS;
			}

			slPeriods[index] = new SleepPeriod(start, end);
			index++;

		}

		if (slPeriods != null)
			days.add(new Day(dayLabel, null, slPeriods));

		return days;

	}

	@Override
	public boolean exportCSV(File file) {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Index;");
		sBuilder.append("Day;");
		sBuilder.append("Sleep Periods;");
		for (int i = 1; i < getMaxColumns(); i++)
			sBuilder.append(";");
		int index = 0;

		for (Day day : this.days) {
			sBuilder.append("\n");
			sBuilder.append(index + ";");
			sBuilder.append(day.getLabel() + ";");

			for (SleepPeriod slPeriod : day.getSleeps()) {
				if (slPeriod != null)
					sBuilder.append(slPeriod.getStartTs() + ";");
			}
			sBuilder.append("\n");
			sBuilder.append(index + ";");
			sBuilder.append(";");

			for (SleepPeriod slPeriod : day.getSleeps()) {
				if (slPeriod != null)
					sBuilder.append(slPeriod.getEndTs() + ";");
			}
			index++;
		}

		return saveCSV(sBuilder.toString(), file);
	}

	/**
	 * Format {@link SleepPeriod} gathered by day to a CSV file
	 * 
	 * @param days
	 *            The list of {@link Day} to format
	 * @return The formated String that contains all {@link SleepPeriod}
	 *         gathered by {@link Day}
	 */
	public static String formatAsCSV(List<Day> days, SleepRoom sleepRoom) {

		StringBuilder periods = new StringBuilder();
		StringBuilder y = new StringBuilder();
		for (int i = 0; i < days.size(); i++) {
			Day day = days.get(i);
			SleepPeriod[] sleeps = null;
			switch (sleepRoom) {
			case Bedroom:
				sleeps = day.getSleeps();
				break;
			case Living:
				sleeps = day.getSleepLiving();
				break;
			}

			// for (SleepPeriod sleep : sleeps) {
			// System.out.println(sleep.getStartTs() + " x " +
			// sleep.getEndTs());
			// }

			for (SleepPeriod sleep : sleeps) {
				if (sleep != null) {
					periods.append(sleep.getStartTs() + ";" + sleep.getEndTs() + ";" + ";");
					y.append(i + ";" + i + ";" + ";");
				}
			}
		}
		periods.append("\n");
		periods.append(y.toString());

		return periods.toString();
	}

}