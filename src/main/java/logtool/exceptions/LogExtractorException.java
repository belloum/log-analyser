package logtool.exceptions;

public class LogExtractorException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String FILE_UNFOUND = "No such file";
	public static final String INVALID_RAWLOG = "Invalid rawlog";
	public static final String MALFORMED_LOG = "Malformed log";
	public static final String INVALID_TIMESTAMP = "Can not parse json date";
	public static final String INVALID_DEVICE = "Invalid device type";

	/**
	 * Instantiates a new <code>LogExtractorException</code>.
	 *
	 * @param pMessage
	 *            the message
	 */
	public LogExtractorException(final String pMessage) {
		super(pMessage);
	}

	/**
	 * Instantiates a new <code>LogExtractorException</code>.
	 *
	 * @param pMessage
	 *            the message
	 * @param pException
	 *            the exception
	 */
	public LogExtractorException(final String pMessage, final Throwable pException) {
		super(pMessage, pException);
	}

}
