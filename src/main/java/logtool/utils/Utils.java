package logtool.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logtool.beans.SoftLog;
import logtool.exceptions.PeriodException;
import logtool.operators.SoftLogExtractor;

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

	public static Date getHourFromString(final String pHour) throws PeriodException {
		if (!Configuration.HOUR_PATTERN.matcher(pHour).matches()) {
			throw new PeriodException(PeriodException.INVALID_PERIOD.concat(": "));
		} else {
			final int hour = Integer.parseInt(pHour.split(":")[0]);
			final int minute = Integer.parseInt(pHour.split(":")[1]);
			if (hour > 23) {
				throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_HOUR, hour));
			} else if (minute > 59) {
				throw new PeriodException(String.format("%s: %d", PeriodException.INVALID_MINUTE, minute));
			} else {
				try {
					return new SimpleDateFormat(Configuration.HOUR_FORMAT).parse(pHour);
				} catch (final ParseException e) {
					throw new PeriodException(String.format("%s: %s", PeriodException.UNPARSABLE, pHour));
				}
			}
		}
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

	public static void resetLOG4JProperties(final Properties pProps) {
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(pProps);
		log.debug("Reset SL4J configuration");
	}

	public static File getTmpFile(final String pTempName) {
		return new File(Configuration.TEMP_FOLDER, pTempName);
	}

	public static File getConfigFile(final String pConfigName) {
		return new File(Configuration.CONFIG_FOLDER, pConfigName);
	}

	/**
	 * Add the correct extension after the given filename if it does not end with
	 * the good one
	 * 
	 * @param pFilename
	 *            the filename
	 * @return the formated filename
	 */
	public static String addFileExtension(final String pFilename, final String pFileExtension) {
		final String extension = String.format(".%s", pFileExtension);
		return pFilename.endsWith(extension) ? pFilename : pFilename.concat(extension);
	}

}
