package loganalyser.ui.tabs;

import java.io.File;

import org.json.JSONObject;

import loganalyser.operators.FileExtractor;
import loganalyser.utils.Configuration;

public interface Configurable {

	default JSONObject configuration() throws Exception {
		return new JSONObject(FileExtractor.readFile(configurationFile())).getJSONObject(configurationSection());
	};

	static File configurationFile() {
		return new File(Configuration.CONFIG_FOLDER, "settings.json");
	}

	String configurationSection();
}
