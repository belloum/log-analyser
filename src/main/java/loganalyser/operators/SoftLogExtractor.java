package loganalyser.operators;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import beans.Period;
import beans.devices.Device;
import beans.devices.Device.DeviceType;
import loganalyser.beans.SoftLog;
import loganalyser.utils.Utils;

public class SoftLogExtractor extends FileExtractor {

	private static final String INVALID_THRESHOLD = "Invalid threshold: ";

	public static JSONArray extractJSON(File extractedFile) throws Exception {
		String content = readFile(extractedFile).replace("}\n", "},");
		content = String.format("[%s]", content);
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

	public static List<SoftLog> filterByDay(List<SoftLog> pLogs, String pDayLabel) {
		return sortLogsByDate(
				pLogs.stream().filter(log -> log.getDayLabel().equals(pDayLabel)).collect(Collectors.toList()));
	}

	public static List<SoftLog> filterByHour(List<SoftLog> pLogs, String pHourLabel) {
		return sortLogsByDate(
				pLogs.stream().filter(log -> log.getHourLabel().equals(pHourLabel)).collect(Collectors.toList()));
	}

	public static List<SoftLog> filterByHour(List<SoftLog> pLogs, Period pTSLimits) {
		return sortLogsByDate(pLogs.stream().filter(log -> log.isBetweenHours(pTSLimits)).collect(Collectors.toList()));
	}

	public static List<SoftLog> filterByIds(List<SoftLog> pLogs, List<String> pDeviceIds) {
		return sortList(sortLogsByDate(pLogs.stream().filter(log -> pDeviceIds.contains(log.getDevice().getId()))
				.collect(Collectors.toList())));
	}

	public static List<SoftLog> filterByTypes(List<SoftLog> pLogs, List<DeviceType> pDeviceTypes) {
		return sortList(sortLogsByDate(pLogs.stream().filter(log -> pDeviceTypes.contains(log.getDevice().getType()))
				.collect(Collectors.toList())));
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

				else if (inUse && log.getValue() <= pThreshold) {
					cleanedList.add(log);
					inUse = false;
				}
			}
		}
		return sortLogsByDate(cleanedList);
	}

	/**
	 * Extracts all the types of device from the given list of logs
	 * 
	 * @param pLogs
	 *            list of log
	 * @return the different types, sorted by name
	 */
	public static List<DeviceType> getDeviceTypes(List<SoftLog> pLogs) {
		return sortList(getDevices(pLogs).stream().map(Device::getType).distinct().collect(Collectors.toList()));
	}

	public static int getDeviceTypeCount(List<SoftLog> pLogs) {
		return getDeviceTypes(pLogs).size();
	}

	public static List<Device> getDevices(List<SoftLog> pLogs) {
		List<Device> a = pLogs.stream().map(SoftLog::getDevice).distinct().collect(Collectors.toList());
		a.sort(Comparator.comparing(device -> ((Device) device).getId()));
		return a;
	}

	public static Device getDeviceWithId(List<SoftLog> pLogs, String pDeviceId) {
		return getDevices(pLogs).stream().filter(device -> device.getId().equals(pDeviceId)).findFirst().get();
	}

	public static int getDeviceCount(List<SoftLog> pLogs) {
		return getDevices(pLogs).size();
	}

	public static List<String> getDeviceIdsWithType(List<SoftLog> pLogs, DeviceType pType) {
		return getDevices(pLogs).stream().filter(device -> device.getType().equals(pType)).map(Device::getId)
				.collect(Collectors.toList());
	}

	public static List<String> getDays(List<SoftLog> pLogs) {
		return pLogs.stream().map(SoftLog::getDayLabel).distinct().sorted().collect(Collectors.toList());
	}

	public static Integer getDayCount(List<SoftLog> pLogs) {
		return getDays(pLogs).size();
	}

	public static List<String> getMonths(List<SoftLog> pLogs) {
		return pLogs.stream().map(SoftLog::getMonth).distinct().sorted().collect(Collectors.toList());
	}

	public static Integer getMonthCount(List<SoftLog> pLogs) {
		return getMonths(pLogs).size();
	}

	public static List<SoftLog> sortLogsByDate(List<SoftLog> pLogs) {
		Collections.sort(pLogs, SoftLog.mCompareDate);
		return pLogs;
	}

	public static boolean saveLogList(List<SoftLog> pLogs, File pOutputFile) throws JSONException {
		List<String> str = new ArrayList<>();
		pLogs.stream().forEach(log -> str.add(SoftLog.toJSON(log).toString()));
		return saveFile(StringUtils.join(str, "\n"), pOutputFile);
	}

	public static List<Entry<String, List<SoftLog>>> sortHoursByLog(final List<SoftLog> logs) {
		return SoftLogExtractor.gatherLogsByHour(logs).entrySet().stream()
				.sorted(Map.Entry.comparingByValue((s1, s2) -> s1.size() - s2.size())).collect(Collectors.toList());
	}

	public static List<Entry<Device, List<SoftLog>>> sortDevicesByLog(final List<SoftLog> logs) {
		return SoftLogExtractor.gatherLogsByDevice(logs).entrySet().stream()
				.sorted(Map.Entry.comparingByValue((s1, s2) -> s1.size() - s2.size())).collect(Collectors.toList());
	}

	public static List<Entry<String, List<SoftLog>>> sortDaysByLog(final List<SoftLog> logs) {
		return SoftLogExtractor.gatherLogsByDay(logs).entrySet().stream()
				.sorted(Map.Entry.comparingByValue((s1, s2) -> s1.size() - s2.size())).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<SoftLog>> gatherLogsByMonth(final List<SoftLog> logs) {
		return (Map<String, List<SoftLog>>) Utils
				.sortMap(logs.stream().collect(Collectors.groupingBy(SoftLog::getMonth)));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<SoftLog>> gatherLogsByDay(List<SoftLog> logs) {
		return (Map<String, List<SoftLog>>) Utils
				.sortMap(logs.stream().collect(Collectors.groupingBy(SoftLog::getDayLabel)));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<SoftLog>> gatherLogsByHour(List<SoftLog> logs) {
		return (Map<String, List<SoftLog>>) Utils
				.sortMap(logs.stream().collect(Collectors.groupingBy(SoftLog::getHourLabel)));
	}

	public static Map<Device, List<SoftLog>> gatherLogsByDevice(List<SoftLog> logs) {
		return (Map<Device, List<SoftLog>>) logs.stream().collect(Collectors.groupingBy(SoftLog::getDevice));
	}

	private static <T> List<T> sortList(List<T> pList) {
		if (!pList.isEmpty()) {
			Object firstItem = pList.get(0);

			if (firstItem instanceof Device) {
				pList.sort(Comparator.comparing(log -> ((SoftLog) log).getDevice().getId()));
			} else if (firstItem instanceof DeviceType) {
				pList.sort(Comparator.comparing(log -> ((DeviceType) log).name()));
			} else if (firstItem instanceof DeviceType) {
				pList.sort(Comparator.comparing(log -> ((DeviceType) log).name()));
			}
		}
		return pList;
	}
}
