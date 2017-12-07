package utils;

import java.io.File;

public class Configuration {

	public static final File RESOURCES_FOLDER = new File("src/main/resources/");
	public static final File PARTICIPANT_FILE = new File(RESOURCES_FOLDER, "userboxes.json");

	// UI DIMENSIONS
	public static final Integer MARGINS = 40;

	public static final Integer MAX_WIDTH = 900;
	public static final Integer MAX_HEIGHT = 500;
	public static final Integer BUTTON_WIDTH = 100;
	public static final Integer LABEL_WIDTH = 200;
	public static final Integer LABEL_WIDTH_LITTLE = 100;
	public static final Integer PADDING_RIGHT = 10;
	public static final Integer ITEM_HEIGHT = 20;
}
