package loganalyser.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.beans.SoftLog;
import loganalyser.operators.FileExtractor;
import loganalyser.operators.SoftLogExtractor;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	public static LinkedHashMap<String, ?> sortMap(final Map<String, ?> pMap) {
		return pMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	public static Image scaleImg(final File pImg, final int pWidth, final int pHeight) throws IOException {
		return new ImageIcon(ImageIO.read(pImg)).getImage().getScaledInstance(pWidth, pHeight,
				java.awt.Image.SCALE_SMOOTH);
	}

	public static void copyToClipboard(final String pTextToCopy) {
		final StringSelection stringSelection = new StringSelection(pTextToCopy);
		final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	public static boolean saveTempLogFile(final List<SoftLog> pCleanedList) throws JSONException, IOException {
		return SoftLogExtractor.saveLogList(pCleanedList, tempLogFile());
	}

	public static File tempLogFile() throws JSONException, IOException {
		final File pFile = Configuration.TEMP_LOG_FILE;

		if (!pFile.getParentFile().exists()) {
			pFile.getParentFile().mkdirs();
		}

		if (!pFile.exists()) {
			pFile.createNewFile();
			pFile.deleteOnExit();
		}

		return pFile;
	}

	public static File getConfigurationFile() {
		return new File(Configuration.CONFIG_FOLDER, "settings.json");
	}

	public static String getLogFilename() {
		JSONObject jConfig;
		try {
			jConfig = new JSONObject(FileExtractor.readFile(getConfigurationFile()));
			return jConfig.getString("log_file");
		} catch (JSONException | IOException e) {
			log.error("Exception while getting log filename {}", e.getMessage(), e);
			return null;
		}
	}

	public static void resetLOG4JProperties(Properties pProps) {
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(pProps);
		log.debug("Reset SL4J configuration");
	}

	// private static File logFile() throws JSONException, IOException {
	// return new
	// File(Configurable.configurationJSON().getJSONObject("settings").getString("log_file"));
	// }

	// private static File errorLogFile() throws JSONException, IOException {
	// return new
	// File(Configurable.configurationJSON().getJSONObject("settings").getString("error_log_file"));
	// }

	// @SuppressWarnings("rawtypes")
	// public static void log(final String pMsg, final Class pClass) {
	// try {
	// updateLogFile(logFile(), pMsg, pClass);
	// } catch (JSONException | IOException e) {
	// System.err.println("Unable to write on log file: " + e);
	// }
	// }

	// @SuppressWarnings("rawtypes")
	// public static void errorLog(final String pMsg, final Class pClass) {
	// try {
	// updateLogFile(errorLogFile(), pMsg, pClass);
	// } catch (JSONException | IOException e) {
	// System.err.println("Unable to write on log file: " + e);
	// }
	// }

	// @SuppressWarnings("rawtypes")
	// private static void updateLogFile(final File pFile, final String pMsg, final
	// Class pClass) {
	// try {
	// if (!pFile.getParentFile().exists()) {
	// pFile.getParentFile().mkdirs();
	// }
	//
	// if (!pFile.exists()) {
	// pFile.createNewFile();
	// }
	//
	// final FileOutputStream fop = new FileOutputStream(pFile, true);
	// if (StringUtils.isNotEmpty(pMsg)) {
	// fop.write(String.format("[%s][%s] %s", new SimpleDateFormat("yyyy.MM.dd
	// HH:mm:ss").format(new Date()),
	// pClass.getSimpleName(), pMsg).getBytes());
	// }
	//
	// fop.write(System.getProperty("line.separator").getBytes());
	// fop.flush();
	// fop.close();
	// } catch (final IOException e) {
	// System.err.println("Unable to write on log file: " + e);
	// }
	// }

	public static File getImg(final String pImgName) {
		return new File(Configuration.IMG_FOLDER, pImgName);
	}

	public static File getTmpFile(final String pTempName) {
		return new File(Configuration.TEMP_FOLDER, pTempName);
	}

	public static File getLogFile(final String pLogFile) {
		return new File(Configuration.LOG_FOLDER, pLogFile);
	}

	public static File getConfigFile(final String pConfigName) {
		return new File(Configuration.CONFIG_FOLDER, pConfigName);
	}

	// @Override
	// public String configurationFilename() {
	//
	// return "settings.json";
	// }
	//
	// @Override
	// public String configurationSection() {
	// return null;
	// }

}
