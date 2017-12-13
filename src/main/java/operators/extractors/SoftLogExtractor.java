package operators.extractors;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beans.MyLog;
import beans.SoftLog;
import beans.devices.Device;
import beans.devices.Device.DeviceType;

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
public class SoftLogExtractor extends FileExtractor {

	private static final String INVALID_THRESHOLD = "Invalid threshold: ";

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
			strB.append(SoftLogExtractor.getRequests(period, outputFile, veraId, silent));
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

	public static List<SoftLog> cleanUpLogs(List<SoftLog> pLogs, List<String> pDeviceIds, float pThreshold) {
		if (pThreshold >= 0) {
			List<SoftLog> cleanedList = ignoreLowConsumptionLogs(pLogs, pThreshold);
			return sortLogsByDate(cleanedList.stream().filter(log -> pDeviceIds.contains(log.getDevice().getId()))
					.collect(Collectors.toList()));
		} else {
			throw new IllegalArgumentException(
					new StringBuffer(INVALID_THRESHOLD).append(" ").append(pThreshold).toString());
		}
	}

	public static List<SoftLog> ignoreLowConsumptionLogs(List<SoftLog> pLogs, float pThreshold) {
		List<SoftLog> cleanedList = new ArrayList<>();
		pLogs = sortLogsByDate(pLogs);
		boolean inUse = false;
		for (SoftLog log : pLogs) {
			if (!log.getDevice().getType().equals(DeviceType.ElectricMeter))
				cleanedList.add(log);
			else {

				if (log.getValue() >= pThreshold) {
					cleanedList.add(log);
					inUse = true;
				}

				else if (inUse && log.getValue() <= 1) {
					cleanedList.add(log);
					inUse = false;
				}
			}
		}
		return sortLogsByDate(cleanedList);
	}

	public static List<DeviceType> getDeviceTypes(List<SoftLog> pLogs) {
		List<DeviceType> types = new ArrayList<>();
		pLogs.stream().filter(softLog -> !types.contains(softLog.getDevice().getType())).forEach(log -> {
			types.add(log.getDevice().getType());
		});
		return types;
	}

	public static int getDeviceTypeCount(List<SoftLog> pLogs) {
		return getDeviceTypes(pLogs).size();
	}

	public static List<Device> getDeviceIds(List<SoftLog> pLogs) {
		List<Device> devices = new ArrayList<>();
		List<String> deviceIds = new ArrayList<>();
		pLogs.stream().filter(softLog -> !deviceIds.contains(softLog.getDevice().getId())).forEach(log -> {
			deviceIds.add(log.getDevice().getId());
			devices.add(log.getDevice());
		});
		return devices;
	}

	public static int getDeviceCount(List<SoftLog> pLogs) {
		return getDeviceIds(pLogs).size();
	}

	public static List<String> getDeviceIsdWithTypes(List<SoftLog> pLogs, DeviceType pType) {
		List<String> sensorsId = new ArrayList<>();
		pLogs.stream().filter(softLog -> pType.equals(softLog.getDevice().getType())
				&& !sensorsId.contains(softLog.getDevice().getId())).forEach(log -> {
					sensorsId.add(log.getDevice().getId());
				});
		return sensorsId;
	}

	/**
	 * Gets the number of days in the log list
	 * 
	 * @param logs
	 *            The list of logs
	 * @return The number of day in the list
	 */
	public static List<String> getDays(List<SoftLog> pLogs) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		List<String> dayLabels = new ArrayList<>();
		pLogs.stream().filter(log -> !dayLabels.contains(sdf.format(log.getTimestamp())))
				.forEach(log -> dayLabels.add(sdf.format(log.getTimestamp())));
		return dayLabels;
	}

	/**
	 * Sorts the log list by date
	 * 
	 * @param logs
	 *            The list to sort
	 * @return The sorted list
	 */
	public static List<SoftLog> sortLogsByDate(List<SoftLog> pLogs) {
		Collections.sort(pLogs, new Comparator<SoftLog>() {
			@Override
			public int compare(SoftLog o1, SoftLog o2) {
				return Long.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});
		return pLogs;
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
	public static boolean saveLogList(List<SoftLog> pLogs, File pOutputFile) throws JSONException {
		StringBuilder stringBuilder = new StringBuilder();
		pLogs.forEach((log) -> {
			JSONObject json = new JSONObject();
			json.put("date", new Date(log.getTimestamp()));
			json.put("value", log.getValue());
			json.put("timestamp", log.getTimestamp());
			JSONObject device = new JSONObject();
			device.put("location", log.getDevice().getLocation());
			device.put("id", log.getDevice().getId());
			json.put("device", device);
			stringBuilder.append(json.toString()).append("\n");
		});

		return saveFile(stringBuilder.toString(), pOutputFile);
	}

	/**
	 * Split the given list of logs by month
	 * 
	 * @param logs
	 *            The list of logs to be split
	 * @return A map that contains for key an indicator of the month and logs as
	 *         values.
	 */
	public static HashMap<String, List<SoftLog>> sortLogsByMonth(List<SoftLog> logs) {
		HashMap<String, List<SoftLog>> map = new HashMap<>();
		List<SoftLog> logsOfMonth = new ArrayList<>();
		logs = sortLogsByDate(logs);
		String key;
		Calendar calendar = Calendar.getInstance();
		int currentMonth = -1;
		int logMonth;
		for (SoftLog log : logs) {
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

	public static HashMap<String, List<SoftLog>> sortLogsByDay(List<SoftLog> logs) {
		HashMap<String, List<SoftLog>> map = new HashMap<>();
		List<SoftLog> logsOfDay = new ArrayList<>();
		logs = sortLogsByDate(logs);
		String key;
		Calendar calendar = Calendar.getInstance();
		int currentDay = -1;
		int logDay;
		for (SoftLog log : logs) {
			calendar.setTimeInMillis(log.getTimestamp());
			logDay = calendar.get(Calendar.DAY_OF_YEAR);
			if (logDay != currentDay) {
				if (!logsOfDay.isEmpty()) {
					Calendar cal2 = calendar;
					cal2.add(Calendar.DAY_OF_YEAR, -1);
					key = String.format(Locale.FRANCE, "%d/%02d/%02d", cal2.get(Calendar.YEAR),
							cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH));
					cal2 = null;
					map.put(key, logsOfDay);
				}
				logsOfDay = new ArrayList<>();
				currentDay = logDay;
			}
			logsOfDay.add(log);
		}

		if (!logsOfDay.isEmpty()) {
			key = String.format(Locale.FRANCE, "%d/%02d/%02d", calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
			map.put(key, logsOfDay);
		}

		return map;
	}

}
