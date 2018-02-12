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

public class LogToolSetting {

	private static final Logger log = LoggerFactory.getLogger(LogToolSetting.class);

	// TODO: Update xmlFile dep.xml
	public static void setLogFile(String pLogFileName) {
		log.debug("Updating log file with {}", pLogFileName);
		try {
			String logFile = new StringBuffer(Configuration.LOG_FOLDER.getPath()).append("/").append(pLogFileName)
					.append(".log").toString();
			File logProperties = Configuration.LOG_PROPERTIES;
			FileInputStream in = new FileInputStream(logProperties);
			Properties props = new Properties();
			props.load(in);
			in.close();

			FileOutputStream out = new FileOutputStream(logProperties);
			props.setProperty("log4j.appender.file.File", logFile);
			props.store(out, null);
			out.close();

			log.debug("Update JSONSettings, file exists?{}");
			File settingFile = new File(Configuration.CONFIG_FOLDER, "settings.json");
			JSONObject jSettings = new JSONObject(FileExtractor.readFile(settingFile));
			jSettings.put("log_file", logFile);
			FileExtractor.saveFile(jSettings.toString(), settingFile);
			log.info("Log file has been successfully updated by: {}", logFile);
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
		}

		return;
	}

	public static String getLogFile() {
		// FIXME: constant error
		try {
			JSONObject jSettings = new JSONObject(
					FileExtractor.readFile(new File(Configuration.CONFIG_FOLDER, "settings.json")));
			return jSettings.has("log_file") ? jSettings.getString("log_file") : "no file for logs";
		} catch (JSONException | IOException e) {
			log.error("Oops ! {}", e.getMessage(), e);
			return "no log file";
		}
	}
}
