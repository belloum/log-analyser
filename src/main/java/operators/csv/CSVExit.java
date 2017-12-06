package operators.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beans.Day;
import beans.activities.Exit;

/**
 * Extension of {@link CSVCleaner} to clean and gather {@link Exit} by day.
 * 
 * @param csvFile
 *            The CSV-file which must contains at least 6 columns
 *            <ul>
 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
 *            Sun)</i></li>
 *            <li><b>2. Friendly Exit: </b>a friendly representation of the exit
 *            timestamp <i>(e.g: 04:23:27)</i></li>
 *            <li><b>3. Exit: </b>the exit timestamp <i>(e.g: 15807)</i></li>
 *            <li><b>4. Activity: </b>the name of activity <i>(e.g: outage)</i>
 *            </li>
 *            <li><b>5. Friendly Entrance: </b>a friendly representation of the
 *            entrance timestamp <i>(e.g: 05:57:30)</i></li>
 *            <li><b>6. Entrance: </b>the entrance timestamp <i>(e.g: 21450)</i>
 *            </li>
 *            </ul>
 * @param days
 *            The list of days gathering the exits
 * 
 * @author Antoine Riché
 * @since 06/14/17
 * @see Exit
 * @see CSVCleaner
 * @see Day
 */
public class CSVExit extends CSVCleaner {

	/**
	 * Index of the Exit field
	 */
	public static int FIELD_EXIT_TS = 2;
	/**
	 * Index of the Entrance field
	 */
	public static int FIELD_ENTRANCE_TS = 6;
	/**
	 * Index of Entrance Date
	 */
	public static int FIELD_ENTRANCE_DAY = 4;

	List<Day> days;

	/**
	 * CSVExit Constructor
	 * 
	 * @param csvFile
	 *            The CSV-file of the constructor must contains at least 6
	 *            columns
	 *            <ul>
	 *            <li><b>1. Day: </b>the label of the day <i>(e.g: 2017-03-12
	 *            Sun)</i></li>
	 *            <li><b>2. Friendly Exit: </b>a friendly representation of the
	 *            exit timestamp <i>(e.g: 04:23:27)</i></li>
	 *            <li><b>3. Exit: </b>the exit timestamp <i>(e.g: 15807)</i>
	 *            </li>
	 *            <li><b>4. Activity: </b>the name of activity <i>(e.g:
	 *            outage)</i></li>
	 *            <li><b>5. Friendly Entrance: </b>a friendly representation of
	 *            the entrance timestamp <i>(e.g: 05:57:30)</i></li>
	 *            <li><b>6. Entrance: </b>the entrance timestamp <i>(e.g:
	 *            21450)</i></li>
	 *            </ul>
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>CSV-file does not exist</li>
	 *             <li>CSV-file is empty</li>
	 *             <li>CSV-file is malformed</li>
	 *             </ul>
	 */
	public CSVExit(File csvFile) throws Exception {
		super(csvFile);

		if (this.lines.get(0).length < 6)
			throw new Exception("CSV-file is malformed");

		this.days = gatherExitsByDay();
	}

	/**
	 * Gets the number of columns of the final cleaned CSV-file, depending on
	 * max {@link Exit} count per day.
	 * 
	 * @see Exit
	 * @see Day
	 */
	@Override
	public int getMaxColumns() {
		return super.getMaxColumns() * 3;
	}

	/**
	 * Gathers exits day by day by parsing the CSV-file
	 * 
	 * @return A list of days containing exits
	 * 
	 */
	public List<Day> gatherExitsByDay() throws Exception {
		String dayLabel = "";
		List<Day> days = new ArrayList<>();
		Exit[] exits = null;

		int index = 0;
		String alternativeEnd = "";
		String entranceDay = "";

		for (String[] line : this.lines) {

			if (!dayLabel.equals(line[FIELD_DAY])) {
				index = 0;

				if (exits != null)
					days.add(new Day(dayLabel, exits, null));

				exits = new Exit[getMaxColumns()];

				if (!alternativeEnd.isEmpty()) {
					if (entranceDay.equals(line[FIELD_DAY])) {
						System.out.println("Participant did not sleep at night: " + line[FIELD_DAY]);
						exits[index] = new Exit(Day.START_TS, alternativeEnd);
						alternativeEnd = "";
					} else {
						exits[index] = new Exit(Day.START_TS, Day.END_TS);
//						/System.out.println("Participant is out all day long: " + line[FIELD_DAY]);
					}
					index++;
				}

				dayLabel = line[FIELD_DAY];
			}

			String start = line[FIELD_EXIT_TS];
			String end = line[FIELD_ENTRANCE_TS].replaceAll(" ", "");

			if (!line[FIELD_DAY].trim().equals(line[FIELD_ENTRANCE_DAY].trim())) {
				alternativeEnd = end;
				entranceDay = line[FIELD_ENTRANCE_DAY].trim();
				end = Day.END_TS;
			}

			exits[index] = new Exit(start, end);
			index++;
		}

		if (exits != null)
			days.add(new Day(dayLabel, exits, null));

		return days;
	}

	@Override
	public boolean exportCSV(File file) throws Exception {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Days;");
		sBuilder.append("Exits;");
		for (int i = 1; i < getMaxColumns(); i++)
			sBuilder.append(";");
		sBuilder.append("Entrances;");
		for (int i = 1; i < getMaxColumns(); i++)
			sBuilder.append(";");

		for (Day day : this.days) {
			sBuilder.append("\n");
			sBuilder.append(day.getLabel() + ";");
			for (Exit exit : day.getExits()) {
				if (exit != null)
					sBuilder.append(exit.getExitTs());
				sBuilder.append(";");
			}
			for (Exit exit : day.getExits()) {
				if (exit != null)
					sBuilder.append(exit.getEntranceTs());
				sBuilder.append(";");
			}
		}

		return saveCSV(sBuilder.toString(), file);
	}

	/**
	 * Format {@link Exit} gathered by day to a CSV file
	 * 
	 * @param days
	 *            The list of {@link Exit} to format
	 * @return The formated String that contains all {@link Exit} gathered by
	 *         {@link Day}
	 */
	public static String formatAsCSV(List<Day> days) {

		StringBuilder periods = new StringBuilder();
		StringBuilder y = new StringBuilder();
		for (int i = 0; i < days.size(); i++) {
			Day day = days.get(i);
			for (Exit exit : day.getExits()) {
				if (exit != null) {
					periods.append(exit.getExitTs() + ";" + exit.getEntranceTs() + ";" + ";");
					y.append(i + ";" + i + ";" + ";");
				}
			}
		}
		periods.append("\n");
		periods.append(y.toString());

		return periods.toString();
	}
}
