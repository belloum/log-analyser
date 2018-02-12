package loganalyser.operators.settings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import loganalyser.beans.devices.Device;

public class MealSettings extends RoutineSettings {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String PRIMARY_MARKERS = "primary_markers";
	private final static String SECONDARY_MARKERS = "secondary_markers";

	public MealSettings() {
		super();
		put(PRIMARY_MARKERS, new ArrayList<>());
		put(SECONDARY_MARKERS, new ArrayList<>());
	}

	public MealSettings(String pStartHour, String pEndHour, List<Device> pPrimaryMarkers,
			List<Device> pSecondaryMarkers) {
		super(pStartHour, pEndHour);
		put(PRIMARY_MARKERS, pPrimaryMarkers);
		put(SECONDARY_MARKERS, pSecondaryMarkers);
	}

	@SuppressWarnings("unchecked")
	public List<Device> getPrimaryMarkers() {
		return (List<Device>) get(PRIMARY_MARKERS);
	}

	@SuppressWarnings("unchecked")
	public List<Device> getSecondaryMarkers() {
		return (List<Device>) get(SECONDARY_MARKERS);
	}

	public void setPrimaryMarkers(List<Device> pPrimaryMarkers) {
		put(PRIMARY_MARKERS, pPrimaryMarkers);
	}

	public void setSecondaryMarkers(List<Device> pSecondaryMarkers) {
		put(SECONDARY_MARKERS, pSecondaryMarkers);
	}

	@Override
	public void validParameters() throws Exception {
		super.validParameters();
		if (HOUR_FORMAT.parse(getStartHour()).after(HOUR_FORMAT.parse(getEndHour()))) {
			throw new Exception("Start time is after end time");
		} else if (HOUR_FORMAT.parse(getStartHour()).equals(HOUR_FORMAT.parse(getEndHour()))) {
			throw new Exception("Start time and end time are equals");
		} else if (!containsKey(PRIMARY_MARKERS) || StringUtils.isEmpty(get(PRIMARY_MARKERS).toString())
				|| getPrimaryMarkers().isEmpty()) {
			throw new Exception("Invalid primary markers");
		} else if (!containsKey(SECONDARY_MARKERS) || StringUtils.isEmpty(get(SECONDARY_MARKERS).toString())
				|| getSecondaryMarkers().isEmpty()) {
			throw new Exception("Invalid secondary markers");
		}
	}

}
