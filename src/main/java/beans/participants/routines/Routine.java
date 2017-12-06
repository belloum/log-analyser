package beans.participants.routines;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Routine {

	public enum RoutinizedActivities {
		WAKE_UP, BREAKFAST, LUNCH, DINNER, GO_TO_BED
	}

	private static final String JSON_START = "start";
	private static final String JSON_END = "end";
	private static final String JSON_USED_NAME = "activity";

	protected RoutinizedActivities name;
	protected String usedName;
	protected String start;
	protected String end;

	public Routine() {
	}

	public Routine(JSONObject infos) throws JSONException {
		this.start = infos.has(JSON_START) ? infos.getString(JSON_START) : null;
		this.end = infos.has(JSON_END) ? infos.getString(JSON_END) : null;
		this.usedName = infos.has(JSON_USED_NAME) ? infos.getString(JSON_USED_NAME) : null;
	}

	public abstract String getServerRequest(String scriptFolderPath, File logfile, File scoreFile);

}
