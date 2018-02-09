package operators.csv;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import beans.DailyReport;
import beans.activities.dailyactivities.GoToBed;
import beans.activities.dailyactivities.WakeUp;
import beans.activities.dailyactivities.meals.Breakfast;
import beans.activities.dailyactivities.meals.Dinner;
import beans.activities.dailyactivities.meals.Lunch;
import beans.participants.routines.Routine.RoutinizedActivities;

public class CSVReport extends CSVCleaner {

	public static int COLUMN_DATE = 0;
	public static int COLUMN_HOUR = 1;
	public static int COLUMN_TIMESTAMP = 2;
	public static int COLUMN_ACTIVITY = 3;
	public static int COLUMN_SCORE = 4;

	public CSVReport(File csvFile) throws Exception {
		super(csvFile);
	}

	@Override
	public boolean exportCSV(File file) throws Exception {
		return false;
	}

	public DailyReport extractReport() {
		Date date = null;
		WakeUp wakeUp = null;
		Breakfast breakfast = null;
		Lunch lunch = null;
		Dinner dinner = null;
		GoToBed gotobed = null;

		String fristDate = this.lines.get(0)[COLUMN_DATE];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = sdf.parse(fristDate.split(" ")[0]);
		} catch (Exception e) {
			System.out.println("Exception while making report: " + e);
		}

		for (String[] line : this.lines) {
			if (line[COLUMN_DATE].equals(fristDate)) {
				if (line[COLUMN_ACTIVITY].trim().equals("wakeup")) {
					wakeUp = new WakeUp(line[COLUMN_TIMESTAMP], Float.parseFloat(line[COLUMN_SCORE]));
				}

				else if (line[COLUMN_ACTIVITY].trim().equals(RoutinizedActivities.BREAKFAST.name())) {
					breakfast = new Breakfast(line[COLUMN_TIMESTAMP], Float.parseFloat(line[COLUMN_SCORE]));
				}

				else if (line[COLUMN_ACTIVITY].trim().equals(RoutinizedActivities.LUNCH.name())) {
					lunch = new Lunch(line[COLUMN_TIMESTAMP], Float.parseFloat(line[COLUMN_SCORE]));
				}

				else if (line[COLUMN_ACTIVITY].trim().equals(RoutinizedActivities.DINNER.name())) {
					dinner = new Dinner(line[COLUMN_TIMESTAMP], Float.parseFloat(line[COLUMN_SCORE]));
				}

				else if (line[COLUMN_ACTIVITY].trim().equals("sleep")) {
					gotobed = new GoToBed(line[COLUMN_TIMESTAMP], Float.parseFloat(line[COLUMN_SCORE]));
				}
			}
		}

		return new DailyReport(date, wakeUp, gotobed, breakfast, lunch, dinner, null);
	}

}
