package logtool.operators;

import java.util.List;

import logtool.beans.devices.Device;
import logtool.beans.devices.Device.DeviceType;

public interface LogExtractorListener {
	void startExtraction();

	void validateRawLogFile();

	void formatLogs();

	void userExtracted(String pUser);

	void veraExtracted(String pVera);

	void startLogExtraction();

	void ignoreLowConsumptionLogs();

	void cleanLogsResult(int pOldCount, int pNewCount);

	void saveCleanFile();

	void logExtractionProgress(int pProgress);

	void deviceExtracted(List<Device> pDevices);

	void devieTypeExtracted(List<DeviceType> pDeviceType);

	void dayExtracted(int dayCount);
}
