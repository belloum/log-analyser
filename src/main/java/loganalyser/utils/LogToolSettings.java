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
	private static final String LOG4J_APPENDER_GENERIC_FILE = "log4j.appender.file.File";
	private static final String JSON_ERROR_LOG_FILE = "error_log_file";
	private static final String JSON_GENERIC_LOG_FILE = "log_file";
	private static final String LOGS = "logs";
	private static final String PARTICIPANTS = "participants";
	private static final String GENERIC = "generic";
	private static final String JSON_HISTOGRAM_FOLDER = "histogram_folder";
	private static final String JSON_CSV_FOLDER = "csv_folder";
	private static final String JSON_CLEANED_LOGS_FOLDER = "cleaned_logs_folder";
	private static final String JSON_PARTICIPANT_ROUTINE_FILE = "routine_file";
	private static final String NO_PROPERTY = "No property";
	private static final String SETTINGS_JSON = "settings.json";

	private static ParticipantSettingsListener mLogSettingsListener;

	/**
	 * Get the settings-file
	 * 
	 * @return the settings-file
	 */
	private static File getSettingsFile() {
		return new File(Configuration.CONFIG_FOLDER, SETTINGS_JSON);
	}

	/**
	 * Override the settings-file with the content given in JSON format
	 * 
	 * @param jSettings
	 *            the JSON Representation of the new settings
	 */
	private static void saveSettings(final JSONObject jSettings) {
		FileExtractor.saveFile(jSettings.toString(), getSettingsFile());
		return;
	}

	/**
	 * Extract the settings in a JSON format
	 * 
	 * @return the JSONObject containing the settings
	 * @throws JSONException
	 *             if the settings-file content can not be formated in JSON format
	 * @throws IOException
	 *             if the settings-file does not exist
	 */
	private static JSONObject getJSONSettings() throws JSONException, IOException {
		return new JSONObject(FileExtractor.readFile(getSettingsFile()));
	}

	/**
	 * Parse settings-file as JSON and get specified JSON-Child
	 * 
	 * @param pJSONChildKey
	 *            the name (key) of the JSON-Child
	 * @return the JSON-Child
	 */
	private static JSONObject getSpecificSettingsJSON(final String pJSONChildKey) {
		JSONObject pJResult = new JSONObject();
		try {
			pJResult = getJSONSettings().getJSONObject(pJSONChildKey);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}
		return pJResult;
	}

	/**
	 * Get the specified property for the given JSON-Child
	 * 
	 * @param pJSON
	 *            the JSON-Child containing the specified property
	 * @param pPropertyKey
	 *            the property name
	 * @return the value of the specified property
	 */
	private static String getProperty(final JSONObject pJSON, final String pPropertyKey) {
		String result;
		try {
			result = pJSON.getString(pPropertyKey);
		} catch (final JSONException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			result = NO_PROPERTY;
		}
		return result;
	}

	/**
	 * Update and save JSON-Child into JSON-Settings
	 * 
	 * @param pJSONChildKey
	 *            the JSON-Child key to update
	 * @param pPropertyKey
	 *            the property key in the JSON_Child to update
	 * @param pPropertyValue
	 *            the new value of the specified property
	 */
	private static void updateJSONSettings(final String pJSONChildKey, final String pPropertyKey,
			final Object pPropertyValue) {
		try {
			final JSONObject jSettings = getJSONSettings();
			jSettings.getJSONObject(pJSONChildKey).put(pPropertyKey, pPropertyValue);
			saveSettings(jSettings);
		} catch (JSONException | IOException e) {
			log.error("Oops ! The property `{}` in `{}` could not have been updated to `{}` because of: {}",
					pPropertyKey, pJSONChildKey, pPropertyValue, e.getMessage(), e);
		}
		return;
	}

	/*
	 * LOGTOOL-LOG PROPERTIES
	 */

	/**
	 * Get the LogTool-log-settings as JSON
	 * 
	 * @return the LogTool-log-settings
	 */
	private static JSONObject getLogToolLogSettings() {
		return getSpecificSettingsJSON(LOGS);
	}

	/**
	 * Get the specified LogTool-log-property value
	 * 
	 * @param pPropertyKey
	 *            the property name
	 * @return the value of the specified property
	 */
	private static String getLogToolLogProperty(final String pPropertyKey) {
		return getProperty(getLogToolLogSettings(), pPropertyKey);
	}

	/**
	 * Get the LogTool-log folder
	 * 
	 * @return the LogTool log folder
	 */
	public static String getLogToolLogFolder() {
		return getLogToolLogProperty(LOG_FOLDER);
	}

	/**
	 * Get the LogTool generic log file
	 * 
	 * @return the LogTool generic file
	 */
	public static String getGenericLogFilename() {
		return getLogToolLogProperty(JSON_GENERIC_LOG_FILE);
	}

	/**
	 * Get the LogTool error log file
	 * 
	 * @return the LogTool error file
	 */
	public static String getErrorLogFilename() {
		return getLogToolLogProperty(JSON_ERROR_LOG_FILE);
	}

	/**
	 * Update the LogTool-log property with the given value
	 * 
	 * @param pPropertyKey
	 *            the property key to update
	 * @param pPropertyValue
	 *            the new value of the given property
	 */
	private static void updateLogToolLogProperty(final String pPropertyKey, final String pPropertyValue) {
		updateJSONSettings(LOGS, pPropertyKey, pPropertyValue);
		log.info("LogTool property `{}` has been successfully updated to `{}`", pPropertyKey, pPropertyValue);
	}

	/**
	 * Update the LogTool-log folder property by updating JSON and SL4J properties
	 * file
	 * 
	 * @param pLogFolder
	 *            the new LogTool-log folder
	 */
	public static void setLogToolLogFolder(final File pLogFolder) {
		updateLogToolLogProperty(LOG_FOLDER, pLogFolder.getAbsolutePath());
		// Deal with error log file
		setGenericLogFile(getGenericLogFilename());
		// Deal with generic log file
		setErrorLogFile(getErrorLogFilename());
	}

	/**
	 * Update LogTool-log file in JSON-Child and in SL4J properties file
	 * 
	 * @param pSL4JPropertyKey
	 *            the property key for SL4J
	 * @param pJSONPropertyKey
	 *            the property key for JSON
	 * @param pLogFilename
	 *            the new LogTool-log file name
	 */
	private static void setLogFile(final String pSL4JPropertyKey, final String pJSONPropertyKey,
			final String pLogFilename) {
		final String logFile = getLogToolLogFolder().concat(File.separator).concat(formatLogFilename(pLogFilename));
		updateLOG4JProperty(pSL4JPropertyKey, logFile);
		updateLogToolLogProperty(pJSONPropertyKey, new File(logFile).getName());
		return;
	}

	/**
	 * Update the specified SL4J-property with the given value
	 * 
	 * @param pSL4JPropertyKey
	 *            the SL4J-property key to update
	 * @param pSL4JPropertyValue
	 *            the new value of the given property
	 */
	private static void updateLOG4JProperty(final String pSL4JPropertyKey, final Object pSL4JPropertyValue) {
		final File logProperties = Configuration.LOG_PROPERTIES;
		FileInputStream in;
		FileOutputStream out;
		try {
			in = new FileInputStream(logProperties);
			final Properties props = new Properties();
			props.load(in);
			in.close();

			out = new FileOutputStream(logProperties);
			props.setProperty(pSL4JPropertyKey, pSL4JPropertyValue.toString());
			props.store(out, null);
			out.close();
			Utils.resetLOG4JProperties(props);
		} catch (final IOException e) {
			log.error("Can not update SL4J property `{}` with value `{}`! {}", pSL4JPropertyKey, pSL4JPropertyValue, e);
		}
		return;
	}

	/**
	 * Update the LogTool-log generic file
	 * 
	 * @param pLogFilename
	 *            the new generic file name
	 */
	public static void setGenericLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_GENERIC_FILE, JSON_GENERIC_LOG_FILE, pLogFilename);
		return;
	}

	/**
	 * Update the LogTool-log error file
	 * 
	 * @param pLogFilename
	 *            the new error file name
	 */
	public static void setErrorLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_ERR_FILE, JSON_ERROR_LOG_FILE, pLogFilename);
		return;
	}

	/**
	 * Add the correct extension of the given log filename if it does not end with
	 * the good one
	 * 
	 * @param pLogFilename
	 *            the log filename
	 * @return the formated filename
	 */
	private static String formatLogFilename(final String pLogFilename) {
		return pLogFilename.endsWith(".log") ? pLogFilename : pLogFilename.concat(".log");
	}

	/*
	 * PARTICIPANT PROPERTIES
	 */

	/**
	 * Get the Participant-settings as JSON
	 * 
	 * @return the Participant-settings
	 */
	private static JSONObject getParticipantSettings() {
		return getSpecificSettingsJSON(PARTICIPANTS);
	}

	/**
	 * Get the specified Participant-property value
	 * 
	 * @param pPropertyKey
	 *            the property name
	 * @return the value of the specified property
	 */
	private static String getParticipantProperty(final String pPropertyKey) {
		return getProperty(getParticipantSettings(), pPropertyKey);
	}

	/**
	 * Get the Participant logs folder
	 * 
	 * @return the Participant logs folder
	 */
	public static String getParticipantLogsFolder() {
		return getParticipantProperty(LOG_FOLDER);
	}

	/**
	 * Get the Participant routine file
	 * 
	 * @return the Participant routine file
	 */
	public static String getParticipantRoutineFile() {
		return getParticipantProperty(JSON_PARTICIPANT_ROUTINE_FILE);
	}

	/**
	 * Update the Participant property with the given value
	 * 
	 * @param pPropertyKey
	 *            the property key to update
	 * @param pPropertyValue
	 *            the new value of the given property
	 */
	private static void updateParticipantProperty(final String pPropertyKey, final String pPropertyValue) {
		updateJSONSettings(PARTICIPANTS, pPropertyKey, pPropertyValue);
		log.info("Participant property `{}` has been successfully updated to `{}`", pPropertyKey, pPropertyValue);
	}

	/**
	 * Update the Participant logs folder
	 * 
	 * @param pParticipantLogFolder
	 *            the new value of the Participant logs folder
	 */
	public static void setParticipantLogsFolder(final File pParticipantLogFolder) {
		updateParticipantProperty(LOG_FOLDER, pParticipantLogFolder.getAbsolutePath());
		if (mLogSettingsListener != null) {
			mLogSettingsListener.participantLogsFolderUpdated(pParticipantLogFolder);
		}
	}

	/**
	 * Update the Participant routine file. If the selected file does not exist,
	 * 
	 * @param pParticipantRoutineFile
	 *            the new Participant routine file
	 */
	public static void setParticipantRoutineFile(final File pParticipantRoutineFile) {
		updateParticipantProperty(JSON_PARTICIPANT_ROUTINE_FILE, pParticipantRoutineFile.getName());
		if (mLogSettingsListener != null) {
			mLogSettingsListener.participantRoutineFileUpdated(pParticipantRoutineFile);
		}
	}

	/*
	 * GENERIC PROPERTIES
	 */

	/**
	 * Get the Generic-settings as JSON
	 * 
	 * @return the Generic-settings
	 */
	private static JSONObject getGenericSettings() {
		return getSpecificSettingsJSON(GENERIC);
	}

	/**
	 * Get the specified Generic-property value
	 * 
	 * @param pPropertyKey
	 *            the property name
	 * @return the value of the specified property
	 */
	private static String getGenericProperty(final String pPropertyKey) {
		return getProperty(getGenericSettings(), pPropertyKey);
	}

	/**
	 * Get the saved histograms folder
	 * 
	 * @return the save histograms folder
	 */
	public static String getSavedHistogramsFolder() {
		return getGenericProperty(JSON_HISTOGRAM_FOLDER);
	}

	/**
	 * Get the saved CSV folder
	 * 
	 * @return the save CSV folder
	 */
	public static String getSavedCSVFolder() {
		return getGenericProperty(JSON_CSV_FOLDER);
	}

	/**
	 * Get the saved cleaned logs folder
	 * 
	 * @return the save cleaned logs folder
	 */
	public static String getCleanedLogsFolder() {
		return getGenericProperty(JSON_CLEANED_LOGS_FOLDER);
	}

	/**
	 * Update the Generic property with the given value
	 * 
	 * @param pPropertyKey
	 *            the property key to update
	 * @param pPropertyValue
	 *            the new value of the given property
	 */
	private static void updateGenericProperty(final String pPropertyKey, final String pPropertyValue) {
		updateJSONSettings(GENERIC, pPropertyKey, pPropertyValue);
		log.info("Generic property `{}` has been successfully updated to `{}`", pPropertyKey, pPropertyValue);
	}

	/**
	 * Update the saved histograms folder
	 * 
	 * @param pSavedHistogramFolder
	 *            the new saved histograms folder
	 */
	public static void setSavedHistogramsFolder(final File pSavedHistogramFolder) {
		updateGenericProperty(JSON_HISTOGRAM_FOLDER, pSavedHistogramFolder.getAbsolutePath());
	}

	/**
	 * Update the saved CSV folder
	 * 
	 * @param pSavedCSVFolder
	 *            the new saved CSV folder
	 */
	public static void setSavedCSVFolder(final File pSavedCSVFolder) {
		updateGenericProperty(JSON_CSV_FOLDER, pSavedCSVFolder.getAbsolutePath());
	}

	/**
	 * Update the saved cleaned logs folder
	 * 
	 * @param pSavedCleanedLogsFolder
	 *            the new saved cleaned logs folder
	 */
	public static void setSavedCleanedLogsFolder(final File pSavedCleanedLogsFolder) {
		updateGenericProperty(JSON_CLEANED_LOGS_FOLDER, pSavedCleanedLogsFolder.getAbsolutePath());
	}

	/*
	 * INTERFACES
	 */

	/**
	 * Register a listener for Participant-settings updates
	 * 
	 * @param pListener
	 *            the listener
	 */
	public static void addParticipantSettingsListener(final ParticipantSettingsListener pListener) {
		mLogSettingsListener = pListener;
	}

	/**
	 * Interface which notifies Participant-settings updates
	 * 
	 * @author ariche
	 *
	 */
	public interface ParticipantSettingsListener {
		void participantLogsFolderUpdated(File participantLogFolder);

		void participantRoutineFileUpdated(File participantRoutineFile);
	}
}
