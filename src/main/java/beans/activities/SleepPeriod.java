package beans.activities;

/**
 * A representation of SleepPeriods with start and end time
 * 
 * @param startTs
 *            The start sleeping timestamp as String
 * @param endTs
 *            The end sleeping timestamp as String
 * @author Antoine Riché
 * @since 06/14/17
 */
public class SleepPeriod {

	private String startTs;
	private String endTs;

	/**
	 * SleepPeriod constructor
	 * 
	 * @param startTs
	 *            The start sleeping timestamp as String
	 * @param endTs
	 *            The end sleeping timestamp as String
	 */
	public SleepPeriod(String startTs, String endTs) {
		super();
		this.startTs = startTs;
		this.endTs = endTs;
	}

	/**
	 * Gets the start sleeping timestamp
	 * 
	 * @return The start sleeping timestamp
	 */
	public String getStartTs() {
		return startTs;
	}

	/**
	 * Gets the end sleeping timestamp
	 * 
	 * @return The end sleeping timestamp
	 */
	public String getEndTs() {
		return endTs;
	}

	@Override
	public String toString() {
		return "SleepPeriod [startTs=" + startTs + ", endTs=" + endTs + "]";
	}
	
	

}
