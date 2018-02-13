package loganalyser.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.operators.FileExtractor;

public class LogToolSettings {

	private static final Logger log = LoggerFactory.getLogger(LogToolSettings.class);

	private static final String LOG_FOLDER = "log_folder";
	private static final String LOG4J_APPENDER_ERR_FILE = "log4j.appender.err.File";
	private static final String LOG4J_APPENDER_FILE_FILE = "log4j.appender.file.File";
	private static final String JSON_ERROR_LOG_FILE = "error_log_file";
	private static final String JSON_GENERIC_LOG_FILE = "log_file";
	private static final String LOGS = "logs";
	private static final String PARTICIPANTS = "participants";
	private static final String GENERIC = "generic";
	private static final String JSON_HISTOGRAM_FOLDER = "histogram_folder";
	private static final String JSON_PARTICIPANT_ROUTINE_FILE = "routine_file";
	private static final String NO_LOG_FILE = "No log file specified";
	private static final String SETTINGS_JSON = "settings.json";

	private static ParticipantSettingsListener mLogSettingsListener;

	public static String getSavedHistogramsFolder() {
		return getGenericJSONProperty(JSON_HISTOGRAM_FOLDER);
	}

	public static void setLogToolLogFolder(final File pLogFolder) {
		try {
			updateJSONSettingsLog(LOG_FOLDER, pLogFolder.getAbsolutePath());

			// Deal with error log file
			setGenericLogFile(getGenericLogFilename());
			// Deal with generic log file
			setErrorLogFile(getErrorLogFilename());

			log.info("Log folder has been successfully updated by: {}", pLogFolder.getPath());

		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
	}

	public static void setParticipantLogsFolder(final File pParticipantLogFolder) {
		try {
			updateJSONSettingsParticipant(LOG_FOLDER, pParticipantLogFolder.getAbsolutePath());
			log.info("Participant logs folder has been successfully updated by: {}", pParticipantLogFolder.getPath());
			if (mLogSettingsListener != null) {
				mLogSettingsListener.participantLogsFolderUpdated(pParticipantLogFolder);
			}
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
	}

	public static void setSavedHistogramsFolder(final File pSavedHistogramFolder) {
		try {
			updateJSONSettingsGeneric(JSON_HISTOGRAM_FOLDER, pSavedHistogramFolder.getAbsolutePath());
			log.info("Saved histograms folder has been successfully updated by: {}", pSavedHistogramFolder.getPath());
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
	}

	public static void setParticipantRoutineFile(final File pParticipantRoutineFile) {
		try {
			if (!pParticipantRoutineFile.getParentFile().exists()) {
				pParticipantRoutineFile.mkdirs();
			}
			if (!pParticipantRoutineFile.exists()) {
				FileExtractor.saveFile(new JSONObject().toString(), pParticipantRoutineFile);
			}
			updateJSONSettingsParticipant(JSON_PARTICIPANT_ROUTINE_FILE, pParticipantRoutineFile.getName());
			log.info("Participant routine file has been successfully updated by: {}",
					pParticipantRoutineFile.getPath());
			if (mLogSettingsListener != null) {
				mLogSettingsListener.participantRoutineFileUpdated(pParticipantRoutineFile);
			}
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
	}

	public static String getParticipantLogFolder() {
		return getParticipantJSONProperty(LOG_FOLDER);
	}

	public static String getParticipantRoutineFile() {
		return getParticipantJSONProperty(JSON_PARTICIPANT_ROUTINE_FILE);
	}

	public static void setGenericLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_FILE_FILE, JSON_GENERIC_LOG_FILE, pLogFilename);
		return;
	}

	public static void setErrorLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_ERR_FILE, JSON_ERROR_LOG_FILE, pLogFilename);
		return;
	}

	public static String getLogToolLogFolder() {
		return getLogJSONProperty(LOG_FOLDER);
	}

	public static String getGenericLogFilename() {
		return getLogJSONProperty(JSON_GENERIC_LOG_FILE);
	}

	public static String getErrorLogFilename() {
		return getLogJSONProperty(JSON_ERROR_LOG_FILE);
	}

	private static String getLogJSONProperty(final String pKey) {
		String result;
		try {
			result = getJSONSettings().getJSONObject(LOGS).getString(pKey);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			result = NO_LOG_FILE;
		}
		return result;
	}

	private static String getParticipantJSONProperty(final String pKey) {
		String result;
		try {
			result = getJSONSettings().getJSONObject(PARTICIPANTS).getString(pKey);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			result = NO_LOG_FILE;
		}
		return result;
	}

	private static String getGenericJSONProperty(final String pKey) {
		String result;
		try {
			result = getJSONSettings().getJSONObject(GENERIC).getString(pKey);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			result = NO_LOG_FILE;
		}
		return result;
	}

	private static void setLogFile(final String pSL4JPropertyKey, final String pJSONKey, final String pLogFilename) {
		try {
			String logFile = getLogToolLogFolder().concat(File.separator).concat(pLogFilename);
			logFile = logFile.endsWith(".log") ? logFile : logFile.concat(".log");

			updateLOG4JProperty(pSL4JPropertyKey, logFile);
			updateJSONSettingsLog(pJSONKey, new File(logFile).getName());
			log.info("Log file has been successfully updated by: {}", logFile);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
		return;
	}

	private static void updateLOG4JProperty(final String pKey, final Object pValue) {
		final File logProperties = Configuration.LOG_PROPERTIES;
		FileInputStream in;
		FileOutputStream out;
		try {
			in = new FileInputStream(logProperties);
			final Properties props = new Properties();
			props.load(in);
			in.close();

			out = new FileOutputStream(logProperties);
			props.setProperty(pKey, pValue.toString());
			props.store(out, null);
			out.close();
			Utils.resetLOG4JProperties(props);
		} catch (final IOException e) {
			log.error("Exception while trying to update log property `{}` with value `{}`! {}", pKey, pValue, e);
		}
		return;
	}

	private static void updateJSONSettingsLog(final String pKey, final Object pValue)
			throws JSONException, IOException {
		final JSONObject jSettings = getJSONSettings();
		jSettings.getJSONObject(LOGS).put(pKey, pValue);
		saveJSONSettings(jSettings);
		return;
	}

	private static void updateJSONSettingsParticipant(final String pKey, final Object pValue)
			throws JSONException, IOException {
		final JSONObject jSettings = getJSONSettings();
		jSettings.getJSONObject(PARTICIPANTS).put(pKey, pValue);
		saveJSONSettings(jSettings);
		return;
	}

	private static void updateJSONSettingsGeneric(final String pKey, final Object pValue)
			throws JSONException, IOException {
		final JSONObject jSettings = getJSONSettings();
		jSettings.getJSONObject(GENERIC).put(pKey, pValue);
		saveJSONSettings(jSettings);
		return;
	}

	private static JSONObject getJSONSettings() throws JSONException, IOException {
		return new JSONObject(FileExtractor.readFile(getSettingsFile()));
	}

	private static File getSettingsFile() {
		return new File(Configuration.CONFIG_FOLDER, SETTINGS_JSON);
	}

	private static void saveJSONSettings(final JSONObject jSettings) {
		FileExtractor.saveFile(jSettings.toString(), getSettingsFile());
		return;
	}

	public static void addParticipantSettingsListener(final ParticipantSettingsListener pListener) {
		mLogSettingsListener = pListener;
	}

	public interface ParticipantSettingsListener {
		void participantLogsFolderUpdated(File participantLogFolder);

		void participantRoutineFileUpdated(File participantRoutineFile);
	}
}
