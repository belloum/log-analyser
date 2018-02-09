package loganalyser.ui.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import loganalyser.utils.Configuration;

public interface Configurable {

	default JSONObject configuration() {
		FileInputStream configJson;
		try {
			configJson = new FileInputStream(new File(Configuration.CONFIG_FOLDER, configurationFilename()));
			return new JSONObject(IOUtils.toString(configJson)).getJSONObject(configurationSection());
		} catch (JSONException | IOException e) {
			return new JSONObject();
		}
	};

	String configurationFilename();

	String configurationSection();
}
