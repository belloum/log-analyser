package loganalyser.beans.activityresults;

import java.util.Date;

public class MealResult extends ActivityResult {

	private int markerIActivations;
	private int markerIIActivations;

	public MealResult(Date pDate, float pScore, Date pPublishHour) {
		super(pDate, pScore, pPublishHour);
		this.markerIActivations = this.markerIIActivations = 0;
	}

	public MealResult(String pAlgoResult) {
		super(pAlgoResult);
		String result[] = pAlgoResult.split(";");
		String activI = result[5].split(",")[0].replace("M1[", "").trim();
		this.markerIActivations = Integer.parseInt(activI.substring(0, activI.indexOf("]")));

		String activII = result[5].split(",")[1].replace("M2[", "").trim();
		this.markerIIActivations = Integer.parseInt(activII.substring(0, activII.indexOf("]")).trim());
	}

	public int getMarkerIActivations() {
		return markerIActivations;
	}

	public int getMarkerIIActivations() {
		return markerIIActivations;
	}

}
