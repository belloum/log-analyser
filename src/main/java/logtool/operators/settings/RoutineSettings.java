package logtool.operators.settings;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public abstract class RoutineSettings extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_HOUR = "00:00";
	protected static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm");

	private final static String START_HOUR = "start_hour";
	private final static String END_HOUR = "end_hour";

	public RoutineSettings() {
		put(START_HOUR, DEFAULT_HOUR);
		put(END_HOUR, DEFAULT_HOUR);
	}

	public RoutineSettings(String pStartHour, String pEndHour) {
		put(START_HOUR, pStartHour);
		put(END_HOUR, pEndHour);
	}

	public String getStartHour() {
		return (String) get(START_HOUR);
	}

	public String getEndHour() {
		return (String) get(END_HOUR);
	}

	public void setStartHour(String pStartHour) {
		put(START_HOUR, pStartHour);
	}

	public void setEndHour(String pEndHour) {
		put(END_HOUR, pEndHour);
	}

	public void validParameters() throws Exception {
		if (!containsKey(START_HOUR) || StringUtils.isEmpty(getStartHour())) {
			throw new Exception("Invalid start hour");
		} else if (!containsKey(END_HOUR) || StringUtils.isEmpty(getEndHour())) {
			throw new Exception("Invalid end hour");
		} else {
			return;
		}
	}

}
