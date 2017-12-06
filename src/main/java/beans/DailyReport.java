package beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beans.activities.Exit;
import beans.activities.dailyactivities.GoToBed;
import beans.activities.dailyactivities.WakeUp;
import beans.activities.dailyactivities.meals.Breakfast;
import beans.activities.dailyactivities.meals.Dinner;
import beans.activities.dailyactivities.meals.Lunch;
import operators.extractors.ReportExtractor;

public class DailyReport {

	private Date date;
	private WakeUp wakeUp;
	private GoToBed goToBed;
	private Breakfast breakfast;
	private Lunch lunch;
	private Dinner dinner;
	private List<Exit> exits;

	public DailyReport(Date pDate, WakeUp pWakeUp, GoToBed pGoToBed, Breakfast pBreakfast, Lunch pLunch, Dinner pDinner,
			List<Exit> pExits) {
		this.date = pDate;
		this.wakeUp = pWakeUp;
		this.breakfast = pBreakfast;
		this.lunch = pLunch;
		this.dinner = pDinner;
		this.goToBed = pGoToBed;
		this.exits = pExits;
	}

	public DailyReport(JSONObject jsonReport) throws Exception {
		JSONObject activity;
		JSONArray arrActivity;

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.date = utcFormat.parse(jsonReport.getString("@timestamp"));
		jsonReport = ReportExtractor.extractJSONReport(jsonReport);

		try {

			if (jsonReport.has("WAKEUP")) {
				activity = jsonReport.getJSONObject("WAKEUP");
				this.wakeUp = new WakeUp(activity.getString("time"), Float.parseFloat(activity.getString("score")));
			}

			if (jsonReport.has("BREAKFAST")) {
				activity = jsonReport.getJSONObject("BREAKFAST");
				this.breakfast = new Breakfast(activity.getString("time"),
						Float.parseFloat(activity.getString("score")));
			}

			if (jsonReport.has("LUNCH")) {
				activity = jsonReport.getJSONObject("LUNCH");
				this.lunch = new Lunch(activity.getString("time"), Float.parseFloat(activity.getString("score")));
			}

			if (jsonReport.has("DINNER")) {
				activity = jsonReport.getJSONObject("DINNER");
				this.dinner = new Dinner(activity.getString("time"), Float.parseFloat(activity.getString("score")));
			}

			if (jsonReport.has("GOTOBED")) {
				activity = jsonReport.getJSONObject("GOTOBED");
				this.goToBed = new GoToBed(activity.getString("time"), Float.parseFloat(activity.getString("score")));
			}

			if (jsonReport.has("EXIT")) {
				arrActivity = jsonReport.getJSONArray("EXIT");
				this.exits = new ArrayList<>();
				for (int i = 0; i < arrActivity.length(); i++) {
					this.exits.add(new Exit(arrActivity.getJSONObject(i)));
				}
			}

			// if (jsonReport.has("TOILET")) {
			// arrActivity = jsonReport.getJSONArray("TOILET");
			// this.exits = new ArrayList<>();
			// for(int i = 0 ; i < arrActivity.length() ; i++){
			// this.exits.add(new Exit(arrActivity.getJSONObject(i)));
			// }
			// }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "DailyReport [date=" + date + ", wakeUp=" + wakeUp + ", goToBed=" + goToBed + ", breakfast=" + breakfast
				+ ", lunch=" + lunch + ", dinner=" + dinner + ", exits=" + exits + "]";
	}

	public Date getDate() {
		return date;
	}

	public WakeUp getWakeUp() {
		return wakeUp;
	}

	public GoToBed getGoToBed() {
		return goToBed;
	}

	public Breakfast getBreakfast() {
		return breakfast;
	}

	public Lunch getLunch() {
		return lunch;
	}

	public Dinner getDinner() {
		return dinner;
	}

	public List<Exit> getExits() {
		return exits;
	}

	public boolean compareTo(DailyReport diasuiteReport) {
		String msg;

		if (diasuiteReport == null) {
			msg = "Compared to null report";
			System.out.println(msg);
			return false;
		}

		if (diasuiteReport.getWakeUp() == null
				|| diasuiteReport.getWakeUp().getScore() != this.getWakeUp().getScore()) {
			msg = diasuiteReport.getWakeUp() == null ? "Compared to wake up with null socre !"
					: String.format("Wake up have different scores: %f (this) and %f (diasuiteReport)",
							this.getWakeUp().getScore(), diasuiteReport.getWakeUp().getScore());
			System.out.println(msg);
			return false;
		}

		if (diasuiteReport.getBreakfast() == null
				|| diasuiteReport.getBreakfast().getScore() != this.breakfast.getScore()) {
			msg = diasuiteReport.getBreakfast() == null ? "Compared to breakfast with null socre !"
					: String.format("Breakfast have different scores: %f (this) and %f (diasuiteReport)",
							this.getBreakfast().getScore(), diasuiteReport.getBreakfast().getScore());
			System.out.println(msg);
			return false;
		}

		if (diasuiteReport.getLunch() == null || diasuiteReport.getLunch().getScore() != this.lunch.getScore()) {
			msg = diasuiteReport.getLunch() == null ? "Compared to lunch with null socre !"
					: String.format("Lunch have different scores: %f (this) and %f (diasuiteReport)",
							this.getLunch().getScore(), diasuiteReport.getLunch().getScore());
			System.out.println(msg);
			return false;
		}

		if (diasuiteReport.getDinner() == null || diasuiteReport.getDinner().getScore() != this.dinner.getScore()) {
			msg = diasuiteReport.getDinner() == null ? "Compared to dinner with null socre !"
					: String.format("Dinner have different scores: %f (this) and %f (diasuiteReport)",
							this.getDinner().getScore(), diasuiteReport.getDinner().getScore());
			System.out.println(msg);
			return false;
		}

		if (diasuiteReport.getGoToBed() == null
				|| diasuiteReport.getGoToBed().getScore() != this.getGoToBed().getScore()) {
			msg = diasuiteReport.getGoToBed() == null ? "Compared to go bed with null socre !"
					: String.format("GoBed have different scores: %f (this) and %f (diasuiteReport)",
							this.getGoToBed().getScore(), diasuiteReport.getGoToBed().getScore());
			System.out.println(msg);
			return false;
		}

		return true;
	}

}
