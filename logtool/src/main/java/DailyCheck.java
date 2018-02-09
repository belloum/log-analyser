//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Scanner;
//
//import beans.DailyReport;
//import beans.participants.Participant;
//import operators.csv.CSVReport;
//import operators.extractors.ParticipantExtractor;
//import operators.extractors.ReportExtractor;
//import operators.extractors.RequestExtractor;
//
//public class DailyCheck {
//
//	/* PATH */
//
//	public static final String DAY = "2017_08_18";
//	public static final String PARTICIPANT = "116";
//	public static final String DOMASSIST_PATH = "/Users/ariche/Desktop/DomAssist/2017/";
//	public static final String ROOT_PATH = DOMASSIST_PATH + "Logs/Participants/";
//	public static final String SCRIPT_PATH = DOMASSIST_PATH + "Scripts/";
//	public static final String PARTICIPANT_FOLDER = ROOT_PATH + PARTICIPANT + "/";
//	public static final String LOG_FOLDER = PARTICIPANT_FOLDER + "logs/";
//	public static final String LOG_REPORTS = PARTICIPANT_FOLDER + "reports/";
//	public static final String PARTICIPANTS_ROUTINE = "participant.json";
//	public static final String DAILY_REPORT_EXTRACTED = PARTICIPANT + "_reports.json";
//
//	public static void main(String[] args) throws Exception {
//
//		String yesterday = DAY;
//		String filename = yesterday + ".json";
//		File routines = new File(ROOT_PATH, PARTICIPANTS_ROUTINE);
//		Participant participant = ParticipantExtractor.extractParticipant(routines, PARTICIPANT);
//
//		System.out.println("> Get yesterday requests");
//		filename = yesterday + ".json";
//		getYesterdayRequest(participant, yesterday);
//
//		Scanner sc = new Scanner(System.in);
//		System.out.println("\nFiles are in " + LOG_FOLDER + " ? (y/n)");
//		if (!sc.nextLine().equals("y")) {
//			System.out.println("Cancel");
//			sc.close();
//			return;
//		} else {
//			System.out.println("\nClean logs ? (y/n)");
//			if (sc.next().equals("y")) {
//				System.out.println("\n> Clean up the log");
//				File logFile = new File(LOG_FOLDER, filename);
//			}
//
//			System.out.println("\n> Routine requests");
//			File scoreFile = new File(LOG_FOLDER, filename.replace(".json", "_scores.json"));
//			participant.extractRoutineRequests(SCRIPT_PATH, new File(LOG_FOLDER, filename), scoreFile);
//
//			System.out.println("\nScore file exists ? (y/n)");
//			sc.next();
//			if (!sc.nextLine().equals("y")) {
//				System.out.println("\n> Export Report");
//				CSVReport csvReport = new CSVReport(scoreFile);
//				DailyReport scriptReport = csvReport.extractReport();
//				System.out.println(scriptReport.toString());
//
//				System.out.println("\n> FromExtractionLogs");
//
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(scriptReport.getDate());
//				String keyDate = String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH),
//						calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
//				keyDate = "29/08/2017";
//				DailyReport diasuiteReport = ReportExtractor
//						.extractReport(new File(LOG_REPORTS, DAILY_REPORT_EXTRACTED), keyDate);
//				System.out.println(diasuiteReport.toString());
//				scriptReport.compareTo(diasuiteReport);
//			}
//			sc.close();
//		}
//	}
//
//	private static List<String> getYesterdayRequest(Participant participant, String yesterday) throws Exception {
//
//		String[] periods = new String[] { yesterday.replace(".json", "") };
//		String cmd = RequestExtractor.extractLogsAmongPeriods(Arrays.asList(periods), yesterday + ".json",
//				participant.getVera(), false);
//		System.out.println(cmd);
//
//		return null;
//	}
//}
