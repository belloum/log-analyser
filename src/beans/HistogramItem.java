package beans;

import java.util.Calendar;
import java.util.Locale;

/**
 * Histogram item representation
 * 
 * @param value
 *            The value of histogram item
 * @param label
 *            The label of histogram item <i>"HH:mm - HH:mm"</i>
 * @param slotStart
 *            The start of slot
 * @param slotEnd
 *            The end of slot
 * 
 * @author Antoine Riché
 * @since 06/09/17
 */

public class HistogramItem {

	int value;
	int slotStart;
	int slotEnd;
	String label;

	/**
	 * 
	 * @param value
	 *            The value of histogram item
	 * @param slotStart
	 *            The start of slot
	 * @param slotEnd
	 *            The end of slot
	 * @param label
	 *            The label of histogram item <i>"HH:mm - HH:mm"</i>
	 */
	public HistogramItem(int value, int slotStart, int slotEnd) {
		super();
		this.value = value;
		this.slotStart = slotStart;
		this.slotEnd = slotEnd;
		this.label = makeLabel();
	}

	/**
	 * 
	 * @return The value of histogram item
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 *            The value of histogram item
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * 
	 * @return The start of slot
	 */
	public int getSlotStart() {
		return slotStart;
	}

	/**
	 * 
	 * @param slotStart
	 *            The start of slot
	 */
	public void setSlotStart(int slotStart) {
		this.slotStart = slotStart;
	}

	/**
	 * 
	 * @return The end of slot
	 */
	public int getSlotEnd() {
		return slotEnd;
	}

	/**
	 * 
	 * @param slotEnd
	 *            The end of slot
	 */
	public void setSlotEnd(int slotEnd) {
		this.slotEnd = slotEnd;
	}

	/**
	 * 
	 * @return The label of histogram item <i>"HH:mm - HH:mm"</i>
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @return The label of histogram item <i>"HH:mm - HH:mm"</i>
	 */
	public String makeLabel() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.slotStart);
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(String.format(Locale.FRANCE, "%02dh%02d", calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE)));
		sBuilder.append(" - ");
		calendar.setTimeInMillis(this.slotEnd);
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		sBuilder.append(String.format(Locale.FRANCE, "%02dh%02d", calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE)));
		return sBuilder.toString();
	}

	@Override
	public String toString() {
		return "HistogramItem [value=" + value + ", slotStart=" + slotStart + ", slotEnd=" + slotEnd + ", label="
				+ label + "]";
	}

}
