package loganalyser.utils;

import java.awt.Color;
import java.io.File;

public class Configuration {

	public static final File RESOURCES_FOLDER = new File("src/main/resources/");
	public static final File SCRIPTS_FOLDER = new File(RESOURCES_FOLDER, "scripts/");
	public static final File IMAGES_FOLDER = new File(RESOURCES_FOLDER, "img/");

	public static final File PARTICIPANT_FILE = new File(RESOURCES_FOLDER, "userboxes.json");
	public static final File RAW_LOG_FILE = new File(RESOURCES_FOLDER, "raw-logs.json");

	public static final File IMAGE_LOG_FILE = new File(IMAGES_FOLDER, "log.png");
	public static final File IMAGE_FILE = new File(IMAGES_FOLDER, "file.png");
	public static final File IMAGE_SENSOR = new File(IMAGES_FOLDER, "sensors.png");
	public static final File IMAGE_CALENDAR = new File(IMAGES_FOLDER, "calendar.png");
	public static final File IMAGE_PODIUM = new File(IMAGES_FOLDER, "podium.png");
	public static final File IMAGE_COLD = new File(IMAGES_FOLDER, "cold.png");
	public static final File IMAGE_WARM = new File(IMAGES_FOLDER, "warm.png");
	public static final File IMAGE_AVATAR = new File(IMAGES_FOLDER, "avatar.png");
	public static final File IMAGE_HISTO = new File(IMAGES_FOLDER, "histo.png");
	public static final File IMAGE_ROUTINE = new File(IMAGES_FOLDER, "routine.png");
	public static final File IMAGE_VERA = new File(IMAGES_FOLDER, "vera.png");

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
