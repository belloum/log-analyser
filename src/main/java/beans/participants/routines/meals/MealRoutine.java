package beans.participants.routines.meals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beans.participants.routines.Routine;

public abstract class MealRoutine extends Routine {

	private static final String JSON_MARKER_I = "markerI";
	private static final String JSON_MARKER_II = "markerII";
	private static final String JSON_ELEC_THRESHOLD = "elec_threshold";

	private List<String> primaryMarkerId;
	private List<String> secondaryMarkerId;
	private int elecThreshold;

	public MealRoutine(JSONObject infos) throws JSONException {
		super(infos);

		this.elecThreshold = infos.has(JSON_ELEC_THRESHOLD) ? infos.getInt(JSON_ELEC_THRESHOLD) : -1;
		JSONArray array;

		array = infos.has(JSON_MARKER_I) ? infos.getJSONArray(JSON_MARKER_I) : null;
		if (array != null) {
			this.primaryMarkerId = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				this.primaryMarkerId.add(array.getString(i));
			}
		}

		array = infos.has(JSON_MARKER_II) ? infos.getJSONArray(JSON_MARKER_II) : null;
		if (array != null) {
			this.secondaryMarkerId = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				this.secondaryMarkerId.add(array.getString(i));
			}
		}
	}

	@Override
	public String toString() {
		return "['\n" + "\tname=" + name + ", \n" + "\tusedName=" + usedName + ", \n" + "\tstart=" + start + ", \n"
				+ "\tend=" + end + ", \n" + "\tprimaryMarkerId=" + primaryMarkerId + ", \n" + "\tsecondaryMarkerId="
				+ secondaryMarkerId + ", \n" + "\telecThreshold=" + elecThreshold + "]";
	}

	@Override
	public String getServerRequest(String scriptFolderPath, File logfile, File scoreFile) {
		StringBuilder strB;

		strB = new StringBuilder("'");
		for (String marker : this.primaryMarkerId) {
			strB.append(marker);
			strB.append("|");
		}
		strB.append("'");
		String markerI = strB.toString().replace("|'", "'");

		strB = new StringBuilder("'");
		for (String marker : this.secondaryMarkerId) {
			strB.append(marker);
			strB.append("|");
		}
		strB.append("'");
		String markerII = strB.toString().replace("|'", "'");

		return String.format("%smeal.pl -m%s -b%s -e%s -1%s -2%s -r %s >> %s", scriptFolderPath, this.name, this.start,
				this.end, markerI, markerII, logfile.getAbsolutePath(), scoreFile.getAbsolutePath());
	}

}
