package loganalyser.operators.settings;

public class WakeUpAndGoBedSettings extends RoutineSettings {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String DELAY_BETWEEN_MARKERS = "delay_between_markers";
	private final static String ROUTINE_MARKER = "routine_marker";
	private final static String BEDROOM_MARKER = "bedroom_marker";

	public WakeUpAndGoBedSettings() {
		super();
	}

	public WakeUpAndGoBedSettings(String pStartHour, String pEndHour, String pBedroomMarker, String pRoutineMarker,
			int pDelayInMinutes) {
		super(pStartHour, pEndHour);
		put(DELAY_BETWEEN_MARKERS, pDelayInMinutes);
		put(ROUTINE_MARKER, pRoutineMarker);
		put(BEDROOM_MARKER, pBedroomMarker);
	}

	public String getBedroomMarker() {
		return (String) get(BEDROOM_MARKER);
	}

	public String getRoutineMarker() {
		return (String) get(ROUTINE_MARKER);
	}

	public Integer getDelayBetweenMarker() {
		return (Integer) get(DELAY_BETWEEN_MARKERS);
	}

	public void setBedroomMarker(String pBedroomMarker) {
		put(BEDROOM_MARKER, pBedroomMarker);
	}

	public void setRoutineMarker(String pRoutineMarker) {
		put(ROUTINE_MARKER, pRoutineMarker);
	}

	public void setDelayBetweenMarker(int pDelayInMinutes) {
		put(DELAY_BETWEEN_MARKERS, pDelayInMinutes);
	}

}
