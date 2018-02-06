package loganalyser.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import loganalyser.beans.SoftLog;
import loganalyser.operators.FileExtractor;
import loganalyser.operators.SoftLogExtractor;
import loganalyser.ui.tabs.Configurable;

public class Utils implements Configurable {

	public static LinkedHashMap<String, ?> sortMap(Map<String, ?> pMap) {
		return pMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	public static Image scaleImg(File pImgFile, int pWidth, int pHeight) throws IOException {
		return new ImageIcon(ImageIO.read(pImgFile)).getImage().getScaledInstance(pWidth, pHeight,
				java.awt.Image.SCALE_SMOOTH);
	}

	public static void copyToClipboard(String pTextToCopy) {
		final StringSelection stringSelection = new StringSelection(pTextToCopy);
		final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	public static boolean saveTempLogFile(List<SoftLog> pCleanedList) throws JSONException, IOException {
		return SoftLogExtractor.saveLogList(pCleanedList, tempLogFile());
	}

	public static File tempLogFile() throws JSONException, IOException {
		File pFile = new File(new JSONObject(FileExtractor.readFile(Configurable.configurationFile()))
				.getJSONObject("settings").getString("temp_file"));

		if (!pFile.exists()) {
			pFile.createNewFile();
		}

		return pFile;
	}

	private static File logFile() throws JSONException, IOException {
		return new File(new JSONObject(FileExtractor.readFile(Configurable.configurationFile()))
				.getJSONObject("settings").getString("log_file"));
	}

	private static File errorLogFile() throws JSONException, IOException {
		return new File(new JSONObject(FileExtractor.readFile(Configurable.configurationFile()))
				.getJSONObject("settings").getString("error_log_file"));
	}

	@SuppressWarnings("rawtypes")
	public static void log(String pMsg, Class pClass) {
		try {
			updateLogFile(logFile(), pMsg, pClass);
		} catch (JSONException | IOException e) {
			System.err.println("Unable to write on log file: " + e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void errorLog(String pMsg, Class pClass) {
		try {
			updateLogFile(errorLogFile(), pMsg, pClass);
		} catch (JSONException | IOException e) {
			System.err.println("Unable to write on log file: " + e);
		}
	}

	@SuppressWarnings("rawtypes")
	private static void updateLogFile(File pFile, String pMsg, Class pClass) {
		try {
			if (!pFile.exists()) {
				pFile.createNewFile();
			}

			FileOutputStream fop = new FileOutputStream(pFile, true);
			if (StringUtils.isNotEmpty(pMsg)) {
				fop.write(String.format("[%s][%s] %s", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()),
						pClass.getSimpleName(), pMsg).getBytes());
			}

			fop.write(System.getProperty("line.separator").getBytes());
			fop.flush();
			fop.close();
		} catch (IOException e) {
			System.err.println("Unable to write on log file: " + e);
		}
	}

	@Override
	public String configurationSection() {
		return null;
	}
}
