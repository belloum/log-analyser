package loganalyser.exceptions;

public class PeriodException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String INVALID_PERIOD = "Invalid period";
	public static final String START_AFTER_END = String.format("%s: The period ends before it starts.", INVALID_PERIOD);
	public static final String UNPARSABLE = String.format("Period can not be parse");
	public static final String INVALID_HOUR = String.format("Invalid hour");
	public static final String INVALID_MINUTE = String.format("Invalid minute");

	/**
	 * Instantiates a new <code>PeriodException</code>.
	 *
	 * @param pMessage
	 *            the message
	 */
	public PeriodException(final String pMessage) {
		super(pMessage);
	}

	/**
	 * Instantiates a new <code>PeriodException</code>.
	 *
	 * @param pMessage
	 *            the message
	 * @param pException
	 *            the exception
	 */
	public PeriodException(final String pMessage, final Throwable pException) {
		super(pMessage, pException);
	}

	public PeriodException(final String pMessage, final Exception pException) {
		super(pMessage, pException);
	}

}
