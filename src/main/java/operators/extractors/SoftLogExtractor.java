package operators.extractors;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import beans.SoftLog;
import beans.TSLimits;
import beans.devices.Device;
import beans.devices.Device.DeviceType;

public class SoftLogExtractor extends FileExtractor {

	private static final String INVALID_THRESHOLD = "Invalid threshold: ";

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

	public static List<SoftLog> cleanUpLogs(List<SoftLog> pLogs, List<String> pDeviceIds, float pThreshold,
			TSLimits pTSLimits) throws ParseException {

		return sortLogsByDate(cleanUpLogs(pLogs, pDeviceIds, pThreshold).stream()
				.filter(log -> log.isBetween(pTSLimits)).collect(Collectors.toList()));
	}

	public static List<SoftLog> ignoreLowConsumptionLogs(List<SoftLog> pLogs, float pThreshold) {
		List<SoftLog> cleanedList = new ArrayList<>();
		pLogs = sortLogsByDate(pLogs);
		boolean inUse = false;
		for (SoftLog log : pLogs) {
			if (!log.getDevice().getType().equals(DeviceType.ElectricMeter))
				cleanedList.add(log);
			else {

				if (!inUse && log.getValue() >= pThreshold) {
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
		return getDeviceIds(pLogs).stream().map(Device::getType).distinct().collect(Collectors.toList());
	}

	public static int getDeviceTypeCount(List<SoftLog> pLogs) {
		return getDeviceTypes(pLogs).size();
	}

	public static List<Device> getDeviceIds(List<SoftLog> pLogs) {
		return pLogs.stream().map(SoftLog::getDevice).distinct().collect(Collectors.toList());
	}

	public static int getDeviceCount(List<SoftLog> pLogs) {
		return getDeviceIds(pLogs).size();
	}

	public static List<String> getDeviceIdsWithType(List<SoftLog> pLogs, DeviceType pType) {
		return getDeviceIds(pLogs).stream().filter(device -> device.getType().equals(pType)).map(Device::getId)
				.collect(Collectors.toList());
	}

	public static List<String> getDays(List<SoftLog> pLogs) {
		return pLogs.stream().map(SoftLog::getDayLabel).distinct().collect(Collectors.toList());
	}

	public static List<String> getMonths(List<SoftLog> pLogs) {
		return pLogs.stream().map(SoftLog::getMonth).distinct().collect(Collectors.toList());
	}

	public static List<SoftLog> sortLogsByDate(List<SoftLog> pLogs) {
		Collections.sort(pLogs, new Comparator<SoftLog>() {
			@Override
			public int compare(SoftLog o1, SoftLog o2) {
				return Long.compare(o1.getDate().getTime(), o2.getDate().getTime());
			}
		});
		return pLogs;
	}

	public static boolean saveLogList(List<SoftLog> pLogs, File pOutputFile) throws JSONException {
		List<String> str = new ArrayList<>();
		pLogs.stream().forEach(log -> str.add(SoftLog.toJSON(log).toString()));
		return saveFile(StringUtils.join(str, "\n"), pOutputFile);
	}

	public static Map<String, List<SoftLog>> sortLogsByMonth(final List<SoftLog> logs) {
		TreeMap<String, List<SoftLog>> map = new TreeMap<>();
		logs.stream().map(SoftLog::getMonth).distinct().forEach(label -> map.put(label,
				logs.stream().filter(log -> log.getMonth().equals(label)).collect(Collectors.toList())));
		return map;
	}

	public static Map<String, List<SoftLog>> sortLogsByDay(final List<SoftLog> logs) {
		TreeMap<String, List<SoftLog>> map = new TreeMap<>();
		logs.stream().map(SoftLog::getDayLabel).distinct().forEach(label -> map.put(label,
				logs.stream().filter(log -> log.getDayLabel().equals(label)).collect(Collectors.toList())));
		return map;
	}
}
