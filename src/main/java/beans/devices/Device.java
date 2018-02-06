package beans.devices;

import org.json.JSONException;
import org.json.JSONObject;

import loganalyser.exceptions.RawLogException;

public class Device {

	protected String mId;
	protected String mLocation;
	protected DeviceType mType;

	public enum DeviceType {
		ContactSensor, MotionDetector, ElectricMeter, Other
	}

	public Device(String pId, String pLocation) {
		this.mId = pId;
		this.mLocation = pLocation;
	}

	public Device() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mId == null) ? 0 : mId.hashCode());
		result = prime * result + ((mLocation == null) ? 0 : mLocation.hashCode());
		result = prime * result + ((mType == null) ? 0 : mType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (mId == null) {
			if (other.mId != null)
				return false;
		} else if (!mId.equals(other.mId))
			return false;
		if (mLocation == null) {
			if (other.mLocation != null)
				return false;
		} else if (!mLocation.equals(other.mLocation))
			return false;
		if (mType != other.mType)
			return false;
		return true;
	}

	public Device(JSONObject pJSON) throws RawLogException {
		try {
			this.mId = pJSON.getString("name");
			this.mLocation = pJSON.getString("room");
			if (mId.contains("Motion")) {
				this.mType = DeviceType.MotionDetector;
			} else if (mId.contains("Contact"))
				this.mType = DeviceType.ContactSensor;
			else if (mId.contains("EMeter"))
				this.mType = DeviceType.ElectricMeter;
			else {
				this.mType = DeviceType.Other;
			}
		} catch (JSONException exception) {
			String error = new StringBuffer(RawLogException.MALFORMED_LOG)
					.append(String.format(": %s", exception.getMessage())).toString();
			throw new RawLogException(error);
		}
	}

	public String getId() {
		return this.mId;
	}

	public void setId(String pId) {
		this.mId = pId;
	}

	public String getLocation() {
		return this.mLocation;
	}

	public void setLocation(String pLocation) {
		this.mLocation = pLocation;
	}

	public DeviceType getType() {
		return this.mType;
	}

	public void setType(DeviceType pType) {
		this.mType = pType;
	}

	public Device getDeviceWithJSON(JSONObject jsonDevice) throws Exception {

		String id = jsonDevice.getString("name");
		if (id.contains("Motion"))
			return new MotionDetector(id, jsonDevice.getString("room"));
		else if (id.contains("Contact"))
			return new ContactSensor(id, jsonDevice.getString("room"));
		else if (id.contains("EMeter"))
			return new ElectricMeter(id, jsonDevice.getString("room"));
		else
			return null;

	}

	@Override
	public String toString() {
		return "Device [mId=" + mId + ", mLocation=" + mLocation + ", mType=" + mType + "]";
	}

	public static JSONObject toJSON(Device pDevice) {
		JSONObject json = new JSONObject();
		json.put("location", pDevice.getLocation());
		json.put("id", pDevice.getId());
		return json;
	}

}
