package utils;

import java.io.File;

public class Configuration {

	public static final File RESOURCES_FOLDER = new File("src/main/resources/");
	public static final File PARTICIPANT_FILE = new File(RESOURCES_FOLDER, "userboxes.json");
	public static final File RAW_LOG_FILE = new File(RESOURCES_FOLDER, "raw-logs.json");

	// UI DIMENSIONS
	public static final Integer MARGINS = 40;

	public static final Integer MAX_WIDTH = 900;
	public static final Integer MAX_HEIGHT = 500;
	public static final Integer BUTTON_WIDTH = 100;
	public static final Integer LABEL_WIDTH_LONG = 200;
	public static final Integer LABEL_WIDTH_LITTLE = 100;
	public static final Integer LABEL_WIDTH_MEDIUM = 150;
	public static final Integer PADDING = 5;
	public static final Integer ITEM_HEIGHT = 20;
}
