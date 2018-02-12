package loganalyser.utils;

import java.awt.Color;
import java.io.File;
import java.nio.file.Paths;

public class Configuration {

	public static final File ROOT_FOLDER = Paths.get("").toFile().getParentFile();
	public static final File RESOURCES = new File(ROOT_FOLDER, "res");
	public static final File SCRIPTS_FOLDER = new File(RESOURCES, "scripts");
	public static final File CONFIG_FOLDER = new File(ROOT_FOLDER, "config");
	public static final File TEMP_FOLDER = new File(ROOT_FOLDER, "temp");
	public static final File LOG_FOLDER = new File(ROOT_FOLDER, "logs");
	public static final File LOG_PROPERTIES = new File(ROOT_FOLDER, "src/main/resources/log4j.properties");

	public static final File IMG_FOLDER = new File(RESOURCES, "img/");
	public static final File IMAGE_AVATAR = Utils.getImg("avatar.png");
	public static final File IMAGE_CALENDAR = Utils.getImg("calendar.png");
	public static final File IMAGE_COLD = Utils.getImg("cold.png");
	public static final File IMAGE_FILE = Utils.getImg("file.png");
	public static final File IMAGE_HISTO = Utils.getImg("histo.png");
	public static final File IMAGE_LOG_FILE = Utils.getImg("log.png");
	public static final File IMAGE_PODIUM = Utils.getImg("podium.png");
	public static final File IMAGE_ROUTINE = Utils.getImg("routine.png");
	public static final File IMAGE_SENSOR = Utils.getImg("sensors.png");
	public static final File IMAGE_SERVER = Utils.getImg("sensors.png");
	public static final File IMAGE_VERA = Utils.getImg("vera.png");
	public static final File IMAGE_WARM = Utils.getImg("warm.png");

	public static final File TEMP_LOG_FILE = Utils.getTmpFile("temp.json");

	public static final File LOG_FILE = new File(LOG_FOLDER, "storyboard.log");
	public static final File LOG_ERROR_FILE = new File(LOG_FOLDER, "error.log");

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
