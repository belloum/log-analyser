package utils;

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
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import operators.extractors.FileExtractor;
import ui.last.tabs.ConfigurableTab;

public class Utils implements ConfigurableTab {

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

	public static void log(String pMsg) {
		try {
			updateLogFile(new File(new JSONObject(FileExtractor.readFile(ConfigurableTab.configurationFile()))
					.getJSONObject("settings").getString("log_file")), pMsg);
		} catch (JSONException | IOException e) {
			System.err.println("Unable to write on log file: " + e);
		}
	}

	public static void errorLog(String pMsg) {
		try {
			updateLogFile(new File(new JSONObject(FileExtractor.readFile(ConfigurableTab.configurationFile()))
					.getJSONObject("settings").getString("error_log_file")), pMsg);
		} catch (JSONException | IOException e) {
			System.err.println("Unable to write on log file: " + e);
		}
	}

	private static void updateLogFile(File pFile, String pMsg) {
		try {
			if (!pFile.exists()) {
				pFile.createNewFile();
			}

			FileOutputStream fop = new FileOutputStream(pFile, true);
			if (StringUtils.isNotEmpty(pMsg)) {
				fop.write(String.format("[%s] %s", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()), pMsg)
						.getBytes());
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
