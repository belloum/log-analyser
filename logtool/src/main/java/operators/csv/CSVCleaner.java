package operators.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import beans.Day;
import beans.activities.Exit;
import beans.activities.SleepPeriod;
import beans.activities.dailyactivities.DailyActivity;
import beans.activities.dailyactivities.DailyActivity.DailyActivities;
import beans.activities.dailyactivities.GoToBed;
import beans.activities.dailyactivities.WakeUp;
import beans.activities.dailyactivities.meals.Breakfast;
import beans.activities.dailyactivities.meals.Dinner;
import beans.activities.dailyactivities.meals.Lunch;
import operators.csv.CSVSleep.SleepRoom;

/**
 * An abstract CSV handler to clean and aggregate by day datas such as exits,
 * sleep periods and daily activity scores
 * 
 * @param csvFile
 *            The CSV-file to be handled
 * @param lines
 *            The lines of the CSV-file
 * @see CSVExit
 * @see CSVSleep
 * @see SleepPeriod
 * @see Exit
 * @see DailyActivity
 * @see Day
 * @author Antoine Riché
 * @since 06/14/17
 *
 */
public abstract class CSVCleaner {

	/**
	 * Index of the Day field
	 */
	public static int FIELD_DAY = 0;

	public static int COLUMN_WAKEUP = 0;
	public static int COLUMN_BREAKFAST = 1;
	public static int COLUMN_LUNCH = 2;
	public static int COLUMN_DINNER = 3;
	public static int COLUMN_GOBED = 4;
	public static int COLUMN_EXIT = 5;
	public static int COLUMN_SLEEP = 6;
	public static int COLUMN_SLEEPLIVING = 7;

	File csvFile;
	List<String[]> lines;
	CSVReader csvReader;

	/**
	 * CSVExit Constructor
	 * 
	 * @param csvFile
	 *            The CSV-file to be handled
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>CSV-file does not exist</li>
	 *             <li>CSV-file is empty</li>
	 *             </ul>
	 */
	@SuppressWarnings("deprecation")
	public CSVCleaner(File csvFile) throws Exception {

		if (!csvFile.exists())
			throw new Exception("CSV-file does not exist");

		this.csvFile = csvFile;

		csvReader = new CSVReader(new FileReader(csvFile), ';');
		this.lines = csvReader.readAll();
		csvReader.close();

		if (this.lines.isEmpty())
			throw new Exception("CSV file does not contain any data");
	}

	/**
	 * Gets the CSV-file
	 * 
	 * @return The CSV-file
	 */
	public File getCsvFile() {
		return csvFile;
	}

	/**
	 * Gets the CSV-content as list of String[]
	 * 
	 * @return The CSV content as a list of String[]
	 */
	public List<String[]> getLines() {
		return this.lines;
	}

	/**
	 * Parse all the CSV and returns the number of columns of the cleaned CSV file
	 * 
	 * @return Number of necessary columns
	 */
	public int getMaxColumns() {
		String day = "";
		int maxColumns = 0;
		int column = 0;

		for (String[] line : this.lines) {
			String label = line[FIELD_DAY];
			if (!day.equals(label)) {

				if (column > maxColumns)
					maxColumns = column;

				day = label;
				column = 1;
			} else
				column++;
		}
		return maxColumns;
	}

	/**
	 * Gets informations such as number of lines, columns and days of the CSV-file
	 * 
	 * @return A string that can be print
	 * 
	 */
	public String getInformation() {
		StringBuilder sBuilder = new StringBuilder(this.csvFile.getName());
		sBuilder.append("\n");
		sBuilder.append("Lines: " + this.lines.size());
		sBuilder.append("\n");
		sBuilder.append("Columns: " + this.getMaxColumns());
		sBuilder.append("\n");
		sBuilder.append("Days: " + this.getNbDays());
		return sBuilder.toString();
	}

	/**
	 * Parse the CSV-file to
	 * 
	 * @return the number of days of the CSV-file
	 */
	public int getNbDays() {
		String day = "";
		int nbDays = 0;

		for (String[] line : this.lines) {
			String label = line[FIELD_DAY];
			if (!day.equals(label)) {
				nbDays++;
				day = label;
			}
		}
		return nbDays;
	}

	/**
	 * Saves a string into a CSV file.
	 * 
	 * @param csvContent
	 *            The string that contains the final cleaned CSV-file
	 * @param outputfile
	 *            The name followed by the date of the output file.
	 * @return
	 *         <ul>
	 *         <li><b>true </b>if the file is correctly saved</li>
	 *         <li><b>false </b>otherwise</li>
	 *         </ul>
	 */
	public static boolean saveCSV(String csvContent, File outputfile) {

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "utf-8"));
			writer.write(csvContent.replaceAll("null", ""));
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			System.out.println("Exception while writing content: " + e);
			return false;
		}
	}

	/**
	 * Get the cleaned CSV-file from the CSV-file in parameter. This method must be
	 * implemented to deal with:
	 * <ul>
	 * <li>{@link SleepPeriod} in {@link CSVSleep}</li>
	 * <li>{@link DailyActivity} in {@link CSVDailyActivity}</li>
	 * <li>{@link Exit} in {@link CSVExit}</li>
	 * </ul>
	 * 
	 * @param file
	 *            The output file
	 * @return
	 *         <ul>
	 *         <li><b>true </b>if the file is correctly saved</li>
	 *         <li><b>false </b>otherwise</li>
	 *         </ul>
	 */
	public abstract boolean exportCSV(File file) throws Exception;

	/**
	 * Export the final cleaned CSV-file with {@link SleepPeriod} list (extracted
	 * with {@link CSVSleep}), {@link Exit} list (extracted with {@link CSVExit})
	 * and {@link DailyActivity} scores (extracted with
	 * {@link CSVDailyActivity}).<br>
	 * </br>
	 * Use this method to get aggregated datas along the same period as the
	 * BreakfastCSV-file period. If some days are missing in the other CSV-file, the
	 * {@link DailyActivity} scores are null and the {@link Exit} list and the
	 * {@link SleepPeriod} list are emprty.<br>
	 * </br>
	 * 
	 * @param participantFolder
	 *            Path of the folder containing the CSV-files
	 *            <ul>
	 *            <li>{@link WakeUp}: Wakeup.csv</li>
	 *            <li>{@link Breakfast}: Breakfast.csv</li>
	 *            <li>{@link Lunch}: Lunch.csv</li>
	 *            <li>{@link Dinner}: Dinner.csv</li>
	 *            <li>{@link GoToBed}: Gotobed.csv</li>
	 *            <li>{@link Exit}: Exits.csv</li>
	 *            <li>{@link SleepPeriod}: Sleepperiods.csv</li>
	 *            </ul>
	 *            <b><i>IF ONE OF THESE CSV-FILES DOES NOT EXIST, THE EXPORT IS
	 *            ABORTED.</b></i> <br>
	 *            </br>
	 * @param outputfile
	 *            The output file
	 * 
	 *            <br>
	 *            </br>
	 * 
	 * @return
	 *         <ul>
	 *         <li><b>true </b>if the file is correctly saved</li>
	 *         <li><b>false </b>otherwise</li>
	 *         </ul>
	 * @throws Exception
	 *             Exceptions are thrown if one of the CSV-file
	 *             <ul>
	 *             <li>does not exist</li>
	 *             <li>is malformed</li>
	 *             </ul>
	 */
	public static boolean exportGeneralCSV(String participantFolder, File outputFile) throws Exception {

		File[] activityCsv = new File[] { new File(participantFolder, "Wakeup.csv"),
				new File(participantFolder, "Breakfast.csv"), new File(participantFolder, "Lunch.csv"),
				new File(participantFolder, "Dinner.csv"), new File(participantFolder, "Gotobed.csv"),
				new File(participantFolder, "Exits.csv"), new File(participantFolder, "Sleepperiods.csv"),
				new File(participantFolder, "Sleepliving.csv") };

		for (File csv : activityCsv) {
			if (!csv.exists())
				throw new IllegalArgumentException(
						String.format("%s does not exist in %s", csv.getName(), csv.getParentFile().getPath()));
		}

		CSVDailyActivity cDailyActivity = new CSVDailyActivity(activityCsv[COLUMN_BREAKFAST],
				DailyActivities.Breakfast);
		List<String[]> lines = cDailyActivity.lines;

		StringBuilder sBuilder = new StringBuilder(CSVCleaner.getHeaders());
		sBuilder.append("\n");

		List<Day> days = new ArrayList<>();
		int index = 0;
		for (String[] line : lines) {
			Day day = new Day(line[FIELD_DAY]);
			day.fillDay(activityCsv[COLUMN_WAKEUP], activityCsv[COLUMN_BREAKFAST], activityCsv[COLUMN_LUNCH],
					activityCsv[COLUMN_DINNER], activityCsv[COLUMN_GOBED], activityCsv[COLUMN_EXIT],
					activityCsv[COLUMN_SLEEP], activityCsv[COLUMN_SLEEPLIVING]);
			days.add(day);
			sBuilder.append(day.formatForCSV(index));
			sBuilder.append("\n");
			index++;
		}

		sBuilder.append("\n\nSleep\n");
		sBuilder.append(CSVSleep.formatAsCSV(days, SleepRoom.Bedroom));

		sBuilder.append("\n\nSleep Living\n");
		sBuilder.append(CSVSleep.formatAsCSV(days, SleepRoom.Living));

		sBuilder.append("\n\nExits\n");
		sBuilder.append(CSVExit.formatAsCSV(days));

		sBuilder.append("\n\nx-axis\n");
		for (int i = 0; i < 24; i++)
			sBuilder.append(i * 3600 + ";");
		sBuilder.append("\n");
		for (int i = 0; i < 24; i++)
			sBuilder.append("0;");

		sBuilder.append("\n\ny-axis\n");
		for (int i = 0; i < days.size(); i++)
			sBuilder.append("0;");
		sBuilder.append("\n");
		for (int i = 0; i < days.size(); i++)
			sBuilder.append(i + ";");

		return saveCSV(sBuilder.toString(), outputFile);

	}

	/**
	 * Gets headers of the final cleaned CSV-file
	 * 
	 * @return The list of headers of the final cleaned CSV-file
	 * @see SleepPeriod
	 * @see Exit
	 */
	public static String getHeaders() {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("Days;;;");
		sBuilder.append("Wake up;;");
		sBuilder.append("Breakfast;;");
		sBuilder.append("Lunch;;");
		sBuilder.append("Dinner;;");
		sBuilder.append("Go to bed;;");
		sBuilder.append("Sleep Bed;;;");
		sBuilder.append("Sleep Living;;;");
		sBuilder.append("Exits;;;");
		sBuilder.append(";");
		sBuilder.append("x-axis;;;");
		sBuilder.append(";");
		sBuilder.append("y-axis;;;");

		return sBuilder.toString();
	}
}
