package loganalyser.beans;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;

import loganalyser.beans.devices.Device;
import loganalyser.beans.devices.Device.DeviceType;
import loganalyser.exceptions.LogExtractorException;
import loganalyser.operators.LogExtractorListener;
import loganalyser.operators.SoftLogExtractor;

public class LogFile extends File {

	private static final long serialVersionUID = 1L;

	private final String mUser;
	private final String mVeraId;
	private final List<SoftLog> mSoftLogs;
	private final List<Device> mDevices;
	private final List<DeviceType> mDeviceTypes;
	private final Integer mDayCount;

	public LogFile(final File pFile) throws LogExtractorException {
		this(pFile, null);
	}

	public LogFile(final File pFile, final LogExtractorListener pListener) throws LogExtractorException {
		super(pFile.getPath());

		if (pListener != null) {
			pListener.startExtraction();
		}

		SoftLogExtractor.addLogExtractorListener(pListener);

		final JSONArray logs = SoftLogExtractor.extractJSONFromRawLogFile(pFile);
		final List<SoftLog> extractLogs = SoftLogExtractor.formatJSONArrayToSoftLogs(logs);
		this.mSoftLogs = SoftLogExtractor.sortLogsByDate(extractLogs);

		this.mDevices = SoftLogExtractor.getDevices(this.mSoftLogs);
		if (pListener != null) {
			pListener.deviceExtracted(this.mDevices);
		}

		this.mDeviceTypes = SoftLogExtractor.getDeviceTypes(this.mSoftLogs);
		if (pListener != null) {
			pListener.devieTypeExtracted(this.mDeviceTypes);
		}

		this.mDayCount = SoftLogExtractor.getDayCount(this.mSoftLogs);
		if (pListener != null) {
			pListener.dayExtracted(this.mDayCount);
		}

		this.mUser = SoftLogExtractor.extractUserId(logs.getJSONObject(0));
		if (pListener != null) {
			pListener.userExtracted(this.mUser);
		}

		this.mVeraId = SoftLogExtractor.extractVeraId(logs.getJSONObject(0));
		if (pListener != null) {
			pListener.veraExtracted(this.mVeraId);
		}

		SoftLogExtractor.removeLogExtractorListener();
	}

	public List<SoftLog> getSoftLogs() {
		return mSoftLogs;
	}

	public int getLogCount() {
		return mSoftLogs.size();
	}

	public List<Device> getDevices() {
		return mDevices;
	}

	public int getDeviceCount() {
		return mDevices.size();
	}

	public List<DeviceType> getDeviceTypes() {
		return mDeviceTypes;
	}

	public int getDeviceTypeCount() {
		return mDeviceTypes.size();
	}

	public Integer getDayCount() {
		return mDayCount;
	}

	public List<String> getDays() {
		return SoftLogExtractor.getDays(mSoftLogs);
	}

	public float getMeanLogByDay() {
		return (float) getSoftLogs().size() / (float) getDayCount();
	}

	public String getPeriod() {
		return this.mSoftLogs.isEmpty() ? "No period"
				: String.format("%s - %s", this.mSoftLogs.get(0).getDayLabel(),
						this.mSoftLogs.get(this.mSoftLogs.size() - 1).getDayLabel());
	}

	public List<SoftLog> getLogByDevice(final String pDeviceId) {
		return SoftLogExtractor.filterByIds(this.mSoftLogs, Collections.singletonList(pDeviceId));
	}

	public List<SoftLog> getLogByDay(final String pDay) {
		return SoftLogExtractor.filterByDay(this.mSoftLogs, pDay);
	}

	public List<SoftLog> getLogByHour(final String pHour) {
		return SoftLogExtractor.filterByHour(this.mSoftLogs, pHour);
	}

	public List<SoftLog> getLogsByDevice(final String pDeviceId) {
		return SoftLogExtractor.filterByIds(this.mSoftLogs, Collections.singletonList(pDeviceId));
	}

	public List<SoftLog> getLogsByDevice(final List<String> pDeviceIds) {
		return SoftLogExtractor.filterByIds(this.mSoftLogs, pDeviceIds);
	}

	public List<Entry<String, List<SoftLog>>> sortHoursByLog() {
		return SoftLogExtractor.sortHoursByLog(this.mSoftLogs);
	}

	public String getTopHour() {
		return sortHoursByLog().get(sortHoursByLog().size() - 1).getKey();
	}

	public String getFlopHour() {
		return sortHoursByLog().get(0).getKey();
	}

	public List<Entry<Device, List<SoftLog>>> sortDevicesByLog() {
		return SoftLogExtractor.sortDevicesByLog(this.mSoftLogs);
	}

	public Device getTopSender() {
		return sortDevicesByLog().get(getDevices().size() - 1).getKey();
	}

	public Device getFlopSender() {
		return sortDevicesByLog().get(0).getKey();
	}

	public List<Entry<String, List<SoftLog>>> sortDaysByLog() {
		return SoftLogExtractor.sortDaysByLog(this.mSoftLogs);
	}

	public String getTopDay() {
		return sortDaysByLog().get(getDayCount() - 1).getKey();
	}

	public String getFlopDay() {
		return sortDaysByLog().get(0).getKey();
	}

	public String getUserId() {
		return this.mUser;
	}

	public String getVeraId() {
		return this.mVeraId;
	}

}
