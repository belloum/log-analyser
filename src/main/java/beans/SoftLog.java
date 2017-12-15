package beans;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import beans.devices.ContactSensor.ContactSensorState;
import beans.devices.Device;
import beans.devices.ElectricMeter.ElectricMeterState;
import beans.devices.MotionDetector.MotionDetectorState;
import exceptions.RawLogException;
import utils.DateFormater;

public class SoftLog {

	private Date mDate;
	private float mValue;
	private String mState;
	private Device mDevice;

	public SoftLog(Date pDate, float pValue, Device pDevice) {
		this.mDate = pDate;
		this.mValue = pValue;
		this.mDevice = pDevice;
	}

	public SoftLog(JSONObject pJSON) throws RawLogException {

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			this.mDate = utcFormat.parse(pJSON.getString("@timestamp"));
			this.mDevice = new Device(pJSON.getJSONObject("device"));
			this.mValue = Float.parseFloat(pJSON.getJSONObject("event").getString("value"));
			switch (this.mDevice.getType()) {
			case ContactSensor:
				this.mState = this.mValue == 0 ? ContactSensorState.Close.name() : ContactSensorState.Open.name();
				break;
			case MotionDetector:
				this.mState = this.mValue == 0 ? MotionDetectorState.Absence.name()
						: MotionDetectorState.Presence.name();
				break;
			case ElectricMeter:
				this.mState = this.mValue == 0 ? ElectricMeterState.TurnOff.name() : ElectricMeterState.TurnOn.name();
				break;
			default:
				this.mState = "unknown";
				break;
			}
		} catch (JSONException exception) {
			String error = new StringBuffer(RawLogException.MALFORMED_LOG)
					.append(String.format(": %s", exception.getMessage())).toString();
			throw new RawLogException(error);
		} catch (ParseException exception) {
			throw new RawLogException(RawLogException.INVALID_TIMESTAMP);
		}

	}

	public Date getDate() {
		return mDate;
	}

	public void setTimestamp(Date pDate) {
		this.mDate = pDate;
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

	public String getDayLabel() {
		return DateFormater.formatDate(this.mDate, DateFormater.SHORT_DAY_FORMAT);
	}

	public String getMonth() {
		return DateFormater.formatDate(this.mDate, DateFormater.SHORT_MONTH_FORMAT);
	}

	public boolean isBetween(TSLimits pTsLimits) {
		return pTsLimits.contains(this.mDate);
	}

	@Override
	public String toString() {
		return "SoftLog [mDate=" + mDate + ", mValue=" + mValue + ", mMeaning=" + mState + ", mDevice=" + mDevice + "]";
	}
	
	public static Comparator<SoftLog> mCompareDate = new Comparator<SoftLog>() {
		@Override
		public int compare(SoftLog o1, SoftLog o2) {
			return Long.compare(o1.getDate().getTime(), o2.getDate().getTime());
		}
	};

	public static JSONObject toJSON(SoftLog pSoftLog) {
		JSONObject json = new JSONObject();
		json.put("date", pSoftLog.getDate());
		json.put("value", pSoftLog.getValue());
		json.put("timestamp", pSoftLog.getDate().getTime());
		json.put("device", Device.toJSON(pSoftLog.getDevice()));
		json.put("state", pSoftLog.mState);
		return json;
	}

}
