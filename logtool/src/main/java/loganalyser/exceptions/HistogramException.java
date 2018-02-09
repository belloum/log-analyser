package loganalyser.exceptions;

public class HistogramException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String INVALID_INTERVAL = "Invalid interval: must be an integer between 1 and 1440.";

	/**
	 * Instantiates a new <code>HistogramException</code>.
	 *
	 * @param pMessage
	 *            the message
	 */
	public HistogramException(final String pMessage) {
		super(pMessage);
	}

	/**
	 * Instantiates a new <code>HistogramException</code>.
	 *
	 * @param pMessage
	 *            the message
	 * @param pException
	 *            the exception
	 */
	public HistogramException(final String pMessage, final Throwable pException) {
		super(pMessage, pException);
	}

}
