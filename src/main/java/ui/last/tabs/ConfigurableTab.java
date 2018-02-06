package ui.last.tabs;

import java.io.File;

import org.json.JSONObject;

import operators.extractors.FileExtractor;
import utils.Configuration;

public interface ConfigurableTab {

	default JSONObject configuration() throws Exception {
		return new JSONObject(FileExtractor.readFile(configurationFile())).getJSONObject(configurationSection());
	};

	static File configurationFile() {
		return Configuration.SETTINGS_FILE;
	}

	String configurationSection();
}
