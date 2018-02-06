package loganalyser.beans;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import beans.devices.ContactSensor.ContactSensorState;
import beans.Period;
import beans.devices.Device;
import beans.devices.ElectricMeter.ElectricMeterState;
import beans.devices.MotionDetector.MotionDetectorState;
import loganalyser.exceptions.PeriodException;
import loganalyser.exceptions.RawLogException;
import loganalyser.utils.DateFormater;

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

	public String getDeviceId() {
		return mDevice.getId();
	}

	public void setDevice(Device pDevice) {
		this.mDevice = pDevice;
	}

	public String getHourLabel() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.mDate);
		return String.format("%02d:00 - %02d:00", calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.HOUR_OF_DAY) + 1);
		// return String.format("%s:00", new SimpleDateFormat("HH").format(this.mDate));
	}

	public String getDayLabel() {
		return new SimpleDateFormat("yyyy.MM.dd, E", Locale.ENGLISH).format(this.mDate);
	}

	public String getMonth() {
		return DateFormater.formatDate(this.mDate, DateFormater.SHORT_MONTH_FORMAT);
	}

	public boolean isBetweenDates(Period pTsLimits) {
		return pTsLimits.contains(this.mDate);
	}

	public boolean isBetweenHours(Period pTsLimits) {
		Calendar calendar = Calendar.getInstance();

		Calendar cStart = Calendar.getInstance();
		cStart.setTime(pTsLimits.getStartDate());

		Calendar cEnd = Calendar.getInstance();
		cEnd.setTime(pTsLimits.getEndDate());

		calendar.setTime(this.mDate);
		calendar.set(Calendar.HOUR_OF_DAY, cStart.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, cStart.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, cStart.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, cStart.get(Calendar.MILLISECOND));
		Date bb = calendar.getTime();

		calendar.set(Calendar.HOUR_OF_DAY, cEnd.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, cEnd.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, cEnd.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, cEnd.get(Calendar.MILLISECOND));
		Date cc = calendar.getTime();
		try {
			return isBetweenDates(new Period(bb, cc));
		} catch (PeriodException e) {
			return false;
		}
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
