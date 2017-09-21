import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import beans.MyLog;
import beans.participants.Participant;
import operators.csv.CSVCleaner;
import operators.extractors.HistogramsExtractor;
import operators.extractors.LogExtractor;
import operators.extractors.ParticipantExtractor;

public class Main {

	//Bzt - month by month from june
	// REFAIRE BZT
	
	/* CONFIG */
	public static final String PARTICIPANT = "117";
	public static final String[] PERIODS = new String[] { "2017.09.*"};
	//public static final String[] PERIODS = new String[] { "2017.04.*" };
	//public static final String[] PERIODS = new String[] { "2017.05.*"};
	//public static final String[] PERIODS = new String[] { "2017.06.*"};
	//public static final String[] PERIODS = new String[] { "2017.07.*"};
	//public static final String[] PERIODS = new String[] { "2017.08.*" };
	public static final Integer ELEC_THRESHOLD = 10;
	public static final Integer HISTO_SLOT = 60;
	public static final boolean SILENT_REQUESTS = false;

	/* EXECUTION */
	public static final boolean STOP_AFTER_PARTICIPANT = true;
	public static final boolean SAVE_ALL_DAYS = true;
	public static final boolean STOP_AFTER_REQUEST = false;
	public static final boolean DRAW_HISTOGRAMS = false;
	public static final boolean DRAW_ACTIVITY_GRAPH = false;

	/* PATH */
	public static final String DOMASSIST_PATH = "/Users/ariche/Desktop/DomAssist/2017/";
	public static final String ROOT_PATH = DOMASSIST_PATH + "Logs/Participants/";
	public static final String SCRIPT_PATH = DOMASSIST_PATH + "Scripts/";
	public static final String PARTICIPANT_FOLDER = ROOT_PATH + PARTICIPANT + "/";
	public static final String LOG_FOLDER = PARTICIPANT_FOLDER + "logs/";
	public static final String GRAPHIC_FOLDER = PARTICIPANT_FOLDER + "graphics/";
	public static final String ACTIVITIES_FOLDER = PARTICIPANT_FOLDER + "activities/";

	/* FILE NAMES */
	public static final String PARTICIPANTS_ROUTINE = "participant.json";
	public static String EXTRACTED_FILE = "logs.json";
	public static final String GLOBAL_JSON_FILE = EXTRACTED_FILE.replace(".json", "") + "_" + PARTICIPANT + ".json";
	public static final String CSV_SENSOR_USE_FILE = PARTICIPANT + "_Sensors.csv";
	public static final String CSV_BILAN_FILE = PARTICIPANT + "_Bilan_" + PARTICIPANT + ".csv";

	public static void main(String[] args) throws Exception {

		File routines = new File(ROOT_PATH, PARTICIPANTS_ROUTINE);
		Participant participant = ParticipantExtractor.extractParticipant(routines, PARTICIPANT);

		/*
		 * 1) Extraction des rapports
		 */
		//System.out.println("\n> Extract reports");
		//System.out.println(ReportExtractor.getRequests(PERIODS[5], ReportType.DAILY, false));

		/*
		 * 2) Création des requetes
		 */
		System.out.println("\n> Extract request");
		getLogExtractionRequest(PERIODS, EXTRACTED_FILE, participant.getVera(), SILENT_REQUESTS);

		if (STOP_AFTER_REQUEST)
			return;

		/*
		 * 3) Récupération des logs
		 */
		System.out.println("\n> Clean logs");
		List<MyLog> cleanLogs = cleanUpLogs(new File(LOG_FOLDER, EXTRACTED_FILE),
				new File(LOG_FOLDER, GLOBAL_JSON_FILE), ELEC_THRESHOLD);

		/*
		 * 4) Save day logs
		 */
		if (SAVE_ALL_DAYS) {
			HashMap<String, List<MyLog>> days = LogExtractor.sortLogsByDay(cleanLogs);
			Iterator<String> itr = days.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String filename = key.replace("/", "_") + ".json";
				LogExtractor.saveLogList(days.get(key), new File(LOG_FOLDER, filename));
				System.out.println(String.format("%s has been saved in %s", filename, LOG_FOLDER));
			}
		}

		/*
		 * 5) Save Sensor csv file
		 */
		System.out.println("\n> Extract SensorUse CSV");
		HistogramsExtractor hExtractor = new HistogramsExtractor(cleanLogs, HISTO_SLOT);
		extractCsvFile(new File(PARTICIPANT_FOLDER, CSV_SENSOR_USE_FILE), hExtractor);

		if (DRAW_HISTOGRAMS) {
			System.out.println("\n> Tracés des histogrammes");
			drawHistograms(hExtractor, PERIODS);
		}

		if (DRAW_ACTIVITY_GRAPH) {
			System.out.println("\n> Tracé du graphiques d'activités ");
			drawActivityGraph(new File(ACTIVITIES_FOLDER), new File(PARTICIPANT_FOLDER, CSV_BILAN_FILE));
		}
	}

	public static String getLogExtractionRequest(String[] periodsToExtract, String outputFile, String veraId,
			boolean silentRequests) throws Exception {

		String cmd = LogExtractor.getRequests(periodsToExtract, outputFile, veraId, silentRequests);
		System.out.println(cmd);
		return cmd;
	}

	public static List<MyLog> cleanUpLogs(File extractedLogs, File cleanedFile, int electricThreshold)
			throws Exception {

		if (!extractedLogs.exists()) {
			System.err.println("Le fichier de log est introuvable.\nMerci de le placer dans le repertoire "
					+ extractedLogs.getParent());
			return null;
		}

		List<MyLog> logs = LogExtractor.extractListFromFiles(new File[] { extractedLogs });

		int sizeBefore = logs.size();
		logs = LogExtractor.ignoreLowConsumptionLogs(logs, electricThreshold);
		System.out.println(LogExtractor.displayExtractionInfos(logs, sizeBefore));

		boolean success = LogExtractor.saveLogList(logs, cleanedFile);
		System.out.println(String.format("\n%s has been saved in %s", cleanedFile.getName(),
				cleanedFile.getParentFile().getPath()));

		return success ? logs : null;
	}

	private static File extractCsvFile(File csvSensorUse, HistogramsExtractor hExtractor) {
		return hExtractor.exportCsv(csvSensorUse) ? csvSensorUse : null;
	}

	private static void drawHistograms(HistogramsExtractor hExtractor, String[] periods) throws Exception {
		for (String period : periods) {
			hExtractor.saveHistograms(new File(GRAPHIC_FOLDER), "timestamp", "occurrence", period);
			hExtractor.saveHistogram(new File(GRAPHIC_FOLDER), "timestamp", "occurrences",
					new String[] { "ContactS_Fridge", "EMeter_Microwave" }, period);
		}
	}

	private static void drawActivityGraph(File activityFolder, File activitycsvFile) throws Exception {
		if (!new File(ACTIVITIES_FOLDER).exists()) {
			System.err.println(String.format("Le répertoire %s est introuvable.", ACTIVITIES_FOLDER));
			return;
		}

		try {
			CSVCleaner.exportGeneralCSV(ACTIVITIES_FOLDER, activitycsvFile);
			System.out
					.println(String.format("\n%s has been saved in %s", activitycsvFile.getName(), PARTICIPANT_FOLDER));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

}
