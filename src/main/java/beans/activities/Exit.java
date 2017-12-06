package beans.activities;

import java.util.Calendar;

import org.json.JSONObject;

/**
 * A representation of Exit with start and end time
 * 
 * @param exitTs
 *            The exit timestamp as String
 * @param entranceTs
 *            The entrance timestamp as String
 * @author Antoine Riché
 * @since 06/14/17
 */
public class Exit {

	private String exitTs;
	private String entranceTs;

	/**
	 * Exit constructor
	 * 
	 * @param exitTs
	 *            The exit timestamp as String
	 * @param entranceTs
	 *            The entrance timestamp as String
	 */
	public Exit(String exitTs, String entranceTs) {
		this.exitTs = exitTs;
		this.entranceTs = entranceTs;
	}
	
	public Exit(JSONObject jExit) throws Exception{
		JSONObject date = jExit.getJSONObject("beginning");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, date.getInt("day"));
		calendar.set(Calendar.MONTH, date.getInt("month") - 1);
		calendar.set(Calendar.YEAR, date.getInt("year"));
		calendar.set(Calendar.HOUR_OF_DAY, date.getInt("hour"));
		calendar.set(Calendar.MINUTE, date.getInt("minute"));
		calendar.set(Calendar.SECOND, date.getInt("second"));
		calendar.set(Calendar.MILLISECOND, 0);
		this.exitTs = ""+calendar.getTimeInMillis();
		
		date = jExit.getJSONObject("end");
		calendar.set(Calendar.DAY_OF_MONTH, date.getInt("day"));
		calendar.set(Calendar.MONTH, date.getInt("month") - 1);
		calendar.set(Calendar.YEAR, date.getInt("year"));
		calendar.set(Calendar.HOUR_OF_DAY, date.getInt("hour"));
		calendar.set(Calendar.MINUTE, date.getInt("minute"));
		calendar.set(Calendar.SECOND, date.getInt("second"));
		calendar.set(Calendar.MILLISECOND, 0);
		this.entranceTs = ""+calendar.getTimeInMillis();
	}

	/**
	 * Gets the exit timestamp
	 * 
	 * @return The exit timestamp as String
	 */
	public String getExitTs() {
		return exitTs;
	}

	/**
	 * Gets the entrance timestamp
	 * 
	 * @return The entrance timestamp as String
	 */
	public String getEntranceTs() {
		return entranceTs;
	}

	@Override
	public String toString() {
		return "Exit [exitTs=" + exitTs + ", entranceTs=" + entranceTs + "]";
	}
	
}
