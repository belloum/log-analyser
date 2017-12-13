package beans;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import beans.devices.Device;
import exceptions.RawLogException;

public class SoftLog {

	private long mTimestamp;
	private float mValue;
	private Device mDevice;

	public SoftLog(long pTimestamp, float pValue, Device pDevice) {
		this.mTimestamp = pTimestamp;
		this.mValue = pValue;
		this.mDevice = pDevice;
	}

	public SoftLog(JSONObject pJSON) throws RawLogException {

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			this.mTimestamp = utcFormat.parse(pJSON.getString("@timestamp")).getTime();
			this.mValue = Float.parseFloat(pJSON.getJSONObject("event").getString("value"));
			this.mDevice = new Device(pJSON.getJSONObject("device"));
		} catch (JSONException exception) {
			String error = new StringBuffer(RawLogException.MALFORMED_LOG)
					.append(String.format(": %s", exception.getMessage())).toString();
			throw new RawLogException(error);
		} catch (ParseException exception) {
			throw new RawLogException(RawLogException.INVALID_TIMESTAMP);
		}

	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long pTimestamp) {
		this.mTimestamp = pTimestamp;
	}

	public float getValue() {
		return mValue;
	}

	public void setValue(float pValue) {
		this.mValue = pValue;
	}

	public Device getDevice() {
		return mDevice;
	}

	public void setDevice(Device pDevice) {
		this.mDevice = pDevice;
	}

	@Override
	public String toString() {
		return "SoftLog [mTimestamp=" + mTimestamp + ", mValue=" + mValue + ", mDevice=" + mDevice + "]";
	}

}
