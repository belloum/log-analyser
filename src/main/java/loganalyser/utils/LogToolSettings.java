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

	private static final String LOG4J_APPENDER_ERR_FILE = "log4j.appender.err.File";
	private static final String LOG4J_APPENDER_FILE_FILE = "log4j.appender.file.File";
	private static final String JSON_ERROR_LOG_FILE = "error_log_file";
	private static final String JSON_GENERIC_LOG_FILE = "log_file";
	private static final String LOGS = "logs";

	private static final Logger log = LoggerFactory.getLogger(LogToolSettings.class);

	private static final String NO_LOG_FILE = "No log file specified";
	private static final String SETTINGS_JSON = "settings.json";

	public static void setGenericLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_FILE_FILE, JSON_GENERIC_LOG_FILE, pLogFilename);
		return;
	}

	public static void setErrorLogFile(final String pLogFilename) {
		setLogFile(LOG4J_APPENDER_ERR_FILE, JSON_ERROR_LOG_FILE, pLogFilename);
		return;
	}

	public static String getGenericLogFilepath() {
		return getLogFilePath(JSON_GENERIC_LOG_FILE);
	}

	public static String getErrorLogFilepath() {
		return getLogFilePath(JSON_ERROR_LOG_FILE);
	}

	private static String getLogFilePath(final String pKey) {
		String result;
		try {
			result = getJSONSettings().getJSONObject(LOGS).getString(pKey);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			result = NO_LOG_FILE;
		}
		return result;
	}

	private static void setLogFile(final String pSL4JPropertyKey, final String pJSONKey, final String pLogFilename) {
		try {
			String logFile = Configuration.LOG_FOLDER.getPath().concat(File.separator).concat(pLogFilename);
			logFile = logFile.endsWith(".log") ? logFile : logFile.concat(".log");

			updateLOG4JProperty(pSL4JPropertyKey, logFile);
			updateJSONSettingsLog(pJSONKey, logFile);
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

}
