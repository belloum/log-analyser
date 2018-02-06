package loganalyser.exceptions;

public class RequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String INVALID_REQUEST = "Invalid request";
	public static final String VERA_DOES_NOT_MATCH_PATERN = String
			.format("%s: Vera id must be a string of eight integers", INVALID_REQUEST);
	public static final String NO_OUTPUT_FILE = String.format("%s: Please provide an output file name",
			INVALID_REQUEST);
	public static final String PERIOD_DOES_NOT_MATCH_PATERN = String
			.format("%s: Period must match one of these patterns: yyyy.mm.dd ; yyyy.mm.*", INVALID_REQUEST);
	public static final String INVALID_START_DAY_FORMAT = String.format("%s: %s must match pattern: yyyy.mm.dd",
			INVALID_REQUEST, "Start day");
	public static final String INVALID_END_DAY_FORMAT = String.format("%s: %s must match pattern: yyyy.mm.dd",
			INVALID_REQUEST, "End day");
	public static final String INVALID_PERIOD = String.format("%s: Start date is after end date", INVALID_REQUEST);

	/**
	 * Instantiates a new <code>RequestException</code>.
	 *
	 * @param pMessage
	 *            the message
	 */
	public RequestException(final String pMessage) {
		super(pMessage);
	}

	/**
	 * Instantiates a new <code>RequestException</code>.
	 *
	 * @param pMessage
	 *            the message
	 * @param pException
	 *            the exception
	 */
	public RequestException(final String pMessage, final Throwable pException) {
		super(pMessage, pException);
	}

	public RequestException(final String pMessage, final Exception pException) {
		super(pMessage, pException);
	}

}
