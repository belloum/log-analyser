package operators.extractors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beans.MyLog;
import beans.devices.Sensor;
import beans.devices.Sensor.Type;

/**
 * LogExtractor enables to
 * <ul>
 * <li>Format requests so as to extract logs from production server</li>
 * <li>Format extracted file from production server as a JSONArray</li>
 * <li>Format extracted file from production server as a list of {@link MyLog}
 * </li>
 * </ul>
 * 
 * 
 * @author Antoine Riché
 * @since 06/19/17
 *
 */
public class LogExtractor extends FileExtractor {

	/**
	 * Gets an elasticdump request which enables to extract logs of the date and
	 * participant specified in parameters
	 * 
	 * @param formattedPeriod
	 *            The formatted period to extract
	 *            <ul>
	 *            <li>For a day: YYYY.MM.DD</li>
	 *            <li>For a month: YYYY.MM.*</li>
	 *            </ul>
	 * @param veraId
	 *            The vera id must be a 8-number-length string
	 * @param silent
	 *            A boolean for displaying output of elasticdump cmd
	 * @return The requests to be launched from production server for log
	 *         extraction
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>formattedPeriod does not match the specified formats</li>
	 *             <li>vera does not match the specified formats</li>
	 *             </ul>
	 */
	public static String getRequests(String formattedPeriod, String veraId, boolean silent) throws Exception {
		Pattern dayPattern = Pattern.compile("\\d{4}.\\d{2}.\\d{2}$");
		Pattern monthPattern = Pattern.compile("\\d{4}.\\d{2}.[*]$");
		Pattern veraPattern = Pattern.compile("\\d{8}$");
		Matcher dayMatcher = dayPattern.matcher(formattedPeriod);
		Matcher monthMatcher = monthPattern.matcher(formattedPeriod);
		Matcher veraMatcher = veraPattern.matcher(veraId);

		if (!dayMatcher.find() && !monthMatcher.find())
			throw new Exception("Invalid period");
		else if (!veraMatcher.find())
			throw new Exception("Invalid vera id");
		else {
			StringBuilder fileBuilder = new StringBuilder(formattedPeriod.replace(".", "_"));
			fileBuilder.append(".json");
			String outputFile = fileBuilder.toString();
			String silentMod = silent ? " --quiet" : "";

			return String.format(Locale.FRANCE,
					"elasticdump --input=http://localhost:9200/logstash-%s/event --sourceOnly --output=%s --searchBody '{\"query\":{\"term\":{\"vera_serial\":\"%s\"}}}'%s",
					formattedPeriod, outputFile, veraId, silentMod);
		}
	}

	/**
	 * Gets an elasticdump request which enables to extract logs of the date and
	 * participant specified in parameters
	 * 
	 * @param formattedPeriod
	 *            The formatted period to extract
	 *            <ul>
	 *            <li>For a day: YYYY.MM.DD</li>
	 *            <li>For a month: YYYY.MM.*</li>
	 *            </ul>
	 * @param outputFile
	 *            The output file which contains extracted logs
	 * @param veraId
	 *            The vera id must be a 8-number-length string
	 * @param silent
	 *            A boolean for displaying output of elasticdump cmd
	 * @return The requests to be launched from production server for log
	 *         extraction
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>formattedPeriod does not match the specified formats</li>
	 *             <li>vera does not match the specified formats</li>
	 *             </ul>
	 */
	public static String getRequests(String formattedPeriod, String outputFile, String veraId, boolean silent)
			throws Exception {
		StringBuilder request = new StringBuilder(getRequests(formattedPeriod, veraId, silent));
		request.append("; ");
		request.append("cat ");
		request.append(formattedPeriod.replace(".", "_"));
		request.append(".json");
		request.append(" >> ");
		request.append(outputFile);
		request.append(";");
		request.append("echo ");
		request.append(String.format("%s has been extracted", formattedPeriod));
		return request.toString();
	}

	/**
	 * Gets an elasticdump request which enables to extract logs of the periods
	 * and participant specified in parameters
	 * 
	 * @param periods
	 *            Table of periods to extract
	 *            <ul>
	 *            <li>For a day: YYYY.MM.DD</li>
	 *            <li>For a month: YYYY.MM.*</li>
	 *            </ul>
	 * @param outputFile
	 *            The output file which contains extracted logs
	 * @param veraId
	 *            The vera id must be a 8-number-length string
	 * @param silent
	 *            A boolean for displaying output of elasticdump cmd
	 * @return The requests to be launched from production server for log
	 *         extraction
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>formattedPeriod does not match the specified formats</li>
	 *             <li>vera does not match the specified formats</li>
	 *             </ul>
	 */
	public static String getRequests(String[] periods, String outputFile, String veraId, boolean silent)
			throws Exception {

		StringBuilder strB = new StringBuilder();
		for (String period : periods) {
			strB.append(LogExtractor.getRequests(period, outputFile, veraId, silent));
			strB.append(";");
		}
		strB.append("du -sh ");
		strB.append(outputFile);
		return strB.toString();
	}

	/**
	 * Extract JSONArray from the file extracted from elastic search
	 * 
	 * @param extractedFile
	 *            The file extracted from ElasticSearch
	 * @return The content of the extracted file as JSONArray
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>Extracted file does not exist</li>
	 *             <li>File content can not be cast to JSONArray</li>
	 *             </ul>
	 */
	public static JSONArray extractJSON(File extractedFile) throws Exception {
		String content = readFile(extractedFile);
		content = "[" + content;
		content = content.replace("}\n", "},");
		content += "]";

		return new JSONArray(content);
	}

	/**
	 * Gets {@link MyLog} from a file extracted from production server
	 * 
	 * @param extractedFile
	 *            The file extracted from Elastic Search
	 * @return The list of {@link MyLog}
	 * @throws Exception
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>Extracted file does not exist</li>
	 *             <li>Content of the file can not be red</li>
	 *             <li>File content can not be cast to JSONArray</li>
	 *             </ul>
	 */
	public static List<MyLog> extractLogs(File extractedFile) throws Exception {
		List<MyLog> myLogs = new ArrayList<>();
		JSONArray logs = extractJSON(extractedFile);
		for (int i = 0; i < logs.length(); i++) {
			JSONObject log = logs.getJSONObject(i);
			if (MyLog.isAValidLog(log) && new MyLog(log).getDevice() != null) {
				myLogs.add(new MyLog(log));
			}
		}

		return sortLogsByDate(myLogs);
	}

	/**
	 * Enables to set a minimal consumption threshold in order to ignore low
	 * value for electric consumption.
	 * 
	 * @param listToSorted
	 *            The list of logs to be sorted
	 * @param consoTreshold
	 *            The minimal electric consumption threshold
	 * @return A list of logs cleaned with high enough electric consumption
	 */
	public static List<MyLog> ignoreLowConsumptionLogs(List<MyLog> listToSorted, float consumptionTreshold) {
		List<MyLog> cleanedList = new ArrayList<>();
		// int removed = 0;
		// int oldSize = 0;
		listToSorted = sortLogsByDate(listToSorted);
		boolean inUse = false;
		for (MyLog log : listToSorted) {
			if (!log.getDevice().getType().equals(Sensor.Type.ElectricMeter))
				cleanedList.add(log);
			else {

				if (log.getValue() >= consumptionTreshold) {
					cleanedList.add(log);
					inUse = true;
				}

				else if (inUse && log.getValue() <= 1) {
					cleanedList.add(log);
					inUse = false;
				}

				// else
				// removed++;
				// }
				// if
				// (!log.getDevice().getType().equals(Sensor.Type.ElectricMeter)
				// || log.getValue() >= consumptionTreshold) {
				// oldSize++;
			}
		}
		return sortLogsByDate(cleanedList);
	}

	/**
	 * Gets all sensor types of a given list of logs
	 * 
	 * @param logs
	 *            The list of logs to be treated
	 * @return The list of all sensor types that appear in the given list
	 */
	public static List<Sensor.Type> getAllSensorTypes(List<MyLog> logs) {
		List<Sensor.Type> types = new ArrayList<>();
		for (MyLog log : logs) {
			if (!types.contains(log.getDevice().getType()))
				types.add(log.getDevice().getType());
		}
		return types;
	}

	/**
	 * Gets sensors id with a given type
	 * 
	 * @param logs
	 *            The list of logs to be treated
	 * @param type
	 *            The type of device
	 * @return The list of all sensor ids matching the given type
	 */
	public static List<String> getSensorsIdWithTypes(List<MyLog> logs, Sensor.Type type) {
		List<String> sensorsId = new ArrayList<>();
		for (MyLog log : logs) {
			if (log.getDevice().getType().equals(type) && !sensorsId.contains(log.getDevice().getId()))
				sensorsId.add(log.getDevice().getId());
		}
		return sensorsId;
	}

	/**
	 * Gets only logs sent by the given {@link Type}
	 * 
	 * @param logs
	 *            The list of logs to be treated
	 * @param sensorType
	 *            the {@link Type} to be sorted
	 * @return The list of logs sent by the given sensor {@link Type}
	 */
	public static List<MyLog> sortBySensorType(List<MyLog> logs, Sensor.Type sensorType) {
		List<MyLog> sortedList = new ArrayList<>();
		logs = sortLogsByDate(logs);
		for (MyLog log : logs) {
			if (log.getDevice().getType().equals(sensorType))
				sortedList.add(log);
		}

		return sortLogsByDate(sortedList);
	}

	/**
	 * Gets the number of days in the log list
	 * 
	 * @param logs
	 *            The list of logs
	 * @return The number of day in the list
	 */
	public static int getDayNumber(List<MyLog> logs) {

		int currentDay = -1;
		int logDay = 0;
		int total = 0;
		Calendar calendar = Calendar.getInstance();

		logs = sortLogsByDate(logs);

		for (MyLog log : logs) {
			calendar.setTimeInMillis(log.getTimestamp());
			logDay = calendar.get(Calendar.DAY_OF_YEAR);
			if (logDay != currentDay) {
				currentDay = logDay;
				total++;
			}
		}
		return total;
	}

	/**
	 * Sorts the log list by date
	 * 
	 * @param logs
	 *            The list to sort
	 * @return The sorted list
	 */
	public static List<MyLog> sortLogsByDate(List<MyLog> logs) {
		Collections.sort(logs, new Comparator<MyLog>() {
			@Override
			public int compare(MyLog o1, MyLog o2) {
				return Long.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});
		return logs;
	}

	/**
	 * Saves the given list of logs as a JSON-file.
	 * 
	 * @param logs
	 *            The list of logs to save
	 * @param outputFile
	 *            The output JSON-file
	 * @return
	 *         <ul>
	 *         <li><b>true </b>if the file is correctly saved</li>
	 *         <li><b>false </b>otherwise</li>
	 *         </ul>
	 * @throws JSONException
	 *             Exceptions are thrown if
	 *             <ul>
	 *             <li>the JSON which represents logs can not be created</li>
	 *             <li>the JSON-file can not be saved</li>
	 *             </ul>
	 */
	public static boolean saveLogList(List<MyLog> logs, File outputFile) throws JSONException {
		StringBuilder stringBuilder = new StringBuilder();
		JSONObject json;
		for (MyLog log : logs) {
			json = new JSONObject();
			json.put("date", new Date(log.getTimestamp()));
			json.put("value", log.getValue());
			json.put("timestamp", log.getTimestamp());
			JSONObject device = new JSONObject();
			device.put("location", log.getDevice().getLocation());
			device.put("id", log.getDevice().getId());
			json.put("device", device);
			stringBuilder.append(json.toString());
			stringBuilder.append("\n");
		}

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
			writer.write(stringBuilder.toString());
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			System.out.println("Exception while writing content: " + e);
			return false;
		}
	}

	/**
	 * Split the given list of logs by month
	 * 
	 * @param logs
	 *            The list of logs to be split
	 * @return A map that contains for key an indicator of the month and logs as
	 *         values.
	 */
	public static HashMap<String, List<MyLog>> sortLogsByMonth(List<MyLog> logs) {
		HashMap<String, List<MyLog>> map = new HashMap<>();
		List<MyLog> logsOfMonth = new ArrayList<>();
		logs = sortLogsByDate(logs);
		String key;
		Calendar calendar = Calendar.getInstance();
		int currentMonth = -1;
		int logMonth;
		for (MyLog log : logs) {
			calendar.setTimeInMillis(log.getTimestamp());
			logMonth = calendar.get(Calendar.MONTH);
			if (logMonth != currentMonth) {
				if (!logsOfMonth.isEmpty()) {
					key = String.format(Locale.FRANCE, "%02d/%d", currentMonth + 1, calendar.get(Calendar.YEAR));
					map.put(key, logsOfMonth);
				}
				logsOfMonth = new ArrayList<>();
				currentMonth = logMonth;
			}
			logsOfMonth.add(log);
		}

		if (!logsOfMonth.isEmpty()) {
			key = String.format(Locale.FRANCE, "%02d/%d", currentMonth + 1, calendar.get(Calendar.YEAR));
			map.put(key, logsOfMonth);
		}

		return map;
	}
	
	public static HashMap<String, List<MyLog>> sortLogsByDay(List<MyLog> logs) {
		HashMap<String, List<MyLog>> map = new HashMap<>();
		List<MyLog> logsOfDay = new ArrayList<>();
		logs = sortLogsByDate(logs);
		String key;
		Calendar calendar = Calendar.getInstance();
		int currentDay = -1;
		int logDay;
		for (MyLog log : logs) {
			calendar.setTimeInMillis(log.getTimestamp());
			logDay = calendar.get(Calendar.DAY_OF_YEAR);
			if (logDay != currentDay) {
				if (!logsOfDay.isEmpty()) {
					Calendar cal2 = calendar;
					cal2.add(Calendar.DAY_OF_YEAR, -1);
					key = String.format(Locale.FRANCE, "%d/%02d/%02d", cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH));
					cal2 = null;
					map.put(key, logsOfDay);
				}
				logsOfDay = new ArrayList<>();
				currentDay = logDay;
			}
			logsOfDay.add(log);
		}

		if (!logsOfDay.isEmpty()) {
			key = String.format(Locale.FRANCE, "%d/%02d/%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
			map.put(key, logsOfDay);
		}

		return map;
	}

	/**
	 * Extract logs from files
	 * 
	 * @param files
	 *            The files containing the logs
	 * @return The list which contains all logs
	 * @throws Exception
	 *             Exception is thrown if a file is malformed
	 * @since 27/06/2017
	 */
	public static List<MyLog> extractListFromFiles(File[] files) throws Exception {

		List<MyLog> logs = new ArrayList<>();

		for (File file : files)
			logs.addAll(extractLogs(file));

		return sortLogsByDate(logs);
	}

	/**
	 * Return the extraction info
	 * 
	 * @param logs
	 *            The list of logs
	 * @return The info about the extraction
	 */
	public static String displayExtractionInfos(List<MyLog> logs, int sizeBeforeCleaning) {

		// General
		StringBuilder strB = new StringBuilder("Before\tAfter\tVariation");
		strB.append("\n");
		float rate = 100 - ((float) logs.size() / (float) sizeBeforeCleaning) * 100;
		strB.append(String.format("%d\t%d\t-%.2f%%", sizeBeforeCleaning, logs.size(), rate));
		strB.append("\n\n");

		// By sensor type
		strB.append("Sensors type\tLogs\tSensors");
		strB.append("\n");
		for (Type type : getAllSensorTypes(logs)) {
			List<String> ids = getSensorsIdWithTypes(logs, type);
			strB.append(String.format("%s\t%d\t%d\t%s", type.name(), sortBySensorType(logs, type).size(), ids.size(),
					ids.toString()));
			strB.append("\n");
		}
		strB.append("\n");

		// By month
		HashMap<String, List<MyLog>> map = LogExtractor.sortLogsByMonth(logs);
		strB.append(String.format("Month\t\tDays\tLogs", LogExtractor.getDayNumber(logs), logs.size()));
		strB.append("\n");
		for (Iterator<Entry<String, List<MyLog>>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, List<MyLog>> entry = iterator.next();
			strB.append(entry.getKey());
			List<MyLog> monthLogs = entry.getValue();
			strB.append(String.format("\t\t%02d", LogExtractor.getDayNumber(monthLogs)));
			strB.append(String.format("\t%d", monthLogs.size()));
			strB.append("\n");
		}
		strB.append(String.format("Total\t\t%d\t%d", LogExtractor.getDayNumber(logs), logs.size()));
		return strB.toString();
	}
}
