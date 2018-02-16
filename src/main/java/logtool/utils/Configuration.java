package logtool.utils;

import java.awt.Color;
import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Configuration {

	/*
	 * FORMATS
	 */
	public static final String HOUR_FORMAT = "HH:mm";

	/*
	 * PATTERNS
	 */
	public static final Pattern USER_PATTERN = Pattern.compile("^domassist[a-zA-Z0-9]{1,5}$");
	public static final Pattern VERA_PATTERN = Pattern.compile("^\\d{8}$");
	public static final Pattern HOUR_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");

	public static final File ROOT_FOLDER = Paths.get("").toFile().getParentFile();
	public static final File RESOURCES_FOLDER = new File(ROOT_FOLDER, "res");
	public static final File CONFIG_FOLDER = new File(ROOT_FOLDER, "config");
	public static final File TEMP_FOLDER = new File(ROOT_FOLDER, "temp");

	public static final File SCRIPTS_FOLDER = new File(RESOURCES_FOLDER, "scripts");
	public static final File PARTICIPANT_FOLDER = new File(RESOURCES_FOLDER, "participants");

	public static final File LOG_PROPERTIES = new File(ROOT_FOLDER, "src/main/resources/log4j.properties");

	/*
	 * IMAGES
	 */
	public static final File IMG_FOLDER = new File(RESOURCES_FOLDER, "img/");
	public static final File IMAGE_AVATAR = new File(IMG_FOLDER, "avatar.png");
	public static final File IMAGE_CALENDAR = new File(IMG_FOLDER, "calendar.png");
	public static final File IMAGE_COLD = new File(IMG_FOLDER, "cold.png");
	public static final File IMAGE_FILE = new File(IMG_FOLDER, "file.png");
	public static final File IMAGE_HISTO = new File(IMG_FOLDER, "histo.png");
	public static final File IMAGE_LOG_FILE = new File(IMG_FOLDER, "log.png");
	public static final File IMAGE_PODIUM = new File(IMG_FOLDER, "podium.png");
	public static final File IMAGE_ROUTINE = new File(IMG_FOLDER, "routine.png");
	public static final File IMAGE_SENSOR = new File(IMG_FOLDER, "sensors.png");
	public static final File IMAGE_SERVER = new File(IMG_FOLDER, "server.png");
	public static final File IMAGE_VERA = new File(IMG_FOLDER, "vera.png");
	public static final File IMAGE_WARM = new File(IMG_FOLDER, "warm.png");

	public static final File TEMP_LOG_FILE = Utils.getTmpFile("temp.json");

	public static final Color RED_COLOR = Color.decode("#F44336");

	// REQUEST
	public static final String DEFAULT_OUTPUT_FILENAME = "output.json";

	// UI DIMENSIONS
	public static final Integer MARGINS = 40;

	public static final Integer MAX_WIDTH = 1200;
	public static final Integer MAX_HEIGHT = 500;
	public static final Integer LEFT_MENU_WIDTH = MAX_WIDTH / 8;

	public static final Integer BUTTON_WIDTH = 100;
	public static final Integer LABEL_WIDTH_LONG = 200;
	public static final Integer LABEL_WIDTH_LITTLE = 100;
	public static final Integer LABEL_WIDTH_MEDIUM = 150;
	public static final Integer PADDING = 5;
	public static final Integer ITEM_HEIGHT = 20;
}
