package exceptions;

public class RequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String INVALID_REQUEST = "Invalid request";
	public static final String VERA_DOES_NOT_MATCH_PATERN = String
			.format("%s: Vera id must be a string of height integers", INVALID_REQUEST);
	public static final String PERIOD_DOES_NOT_MATCH_PATERN = String
			.format("%s: Period must match one of these patterns: yyyy.mm.dd ; yyyy.mm.*", INVALID_REQUEST);

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
