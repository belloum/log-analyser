package loganalyser.beans;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import beans.devices.Device;
import beans.devices.Device.DeviceType;
import loganalyser.exceptions.RawLogException;
import loganalyser.operators.LogExtractorListener;
import loganalyser.operators.RawLogFormater;
import loganalyser.operators.SoftLogExtractor;

public class LogFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mUser;
	private String mVeraId;
	private List<SoftLog> mSoftLogs;
	private List<Device> mDevices;
	private List<DeviceType> mDeviceTypes;
	private Integer mDayCount;

	public LogFile(File pFile) throws RawLogException {
		this(pFile, null);
	}

	public LogFile(File pFile, LogExtractorListener pListener) throws RawLogException {
		super(pFile.getPath());

		if (pListener != null) {
			pListener.startExtraction();
		}

		this.mSoftLogs = SoftLogExtractor.sortLogsByDate(RawLogFormater.extractLogs(pFile, pListener));

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

		this.mUser = RawLogFormater.extractUserId(pFile);
		if (pListener != null) {
			pListener.userExtracted(this.mUser);
		}

		this.mVeraId = RawLogFormater.extractVeraId(pFile);
		if (pListener != null) {
			pListener.veraExtracted(this.mVeraId);
		}
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

	public List<SoftLog> getLogByDevice(String pDeviceId) {
		return SoftLogExtractor.filterByIds(this.mSoftLogs, Collections.singletonList(pDeviceId));
	}

	public List<SoftLog> getLogByDay(String pDay) {
		return SoftLogExtractor.filterByDay(this.mSoftLogs, pDay);
	}

	public List<SoftLog> getLogByHour(String pHour) {
		return SoftLogExtractor.filterByHour(this.mSoftLogs, pHour);
	}

	public List<SoftLog> getLogsByDevice(String pDeviceId) {
		return SoftLogExtractor.filterByIds(this.mSoftLogs, Collections.singletonList(pDeviceId));
	}

	public List<SoftLog> getLogsByDevice(List<String> pDeviceIds) {
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
