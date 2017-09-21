package beans.participants.routines;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WakeUpRoutine extends Routine {

	private static final String JSON_MOTION_I = "motionI";
	private static final String JSON_MOTION_II = "motionII";
	private static final String JSON_MOTION_III = "motionIII";
	private static final String JSON_DELAY = "delay";

	private String primaryMarkerId;
	private String secondaryMarkerId;
	private List<String> aletrnativeMarkersId;
	private int delay;

	public WakeUpRoutine() {
	}

	public WakeUpRoutine(JSONObject infos) throws JSONException {
		super(infos);
		this.name = RoutinizedActivities.WAKE_UP;
		this.delay = infos.has(JSON_DELAY) ? infos.getInt(JSON_DELAY) : -1;
		this.primaryMarkerId = infos.has(JSON_MOTION_I) ? infos.getString(JSON_MOTION_I) : "";
		this.secondaryMarkerId = infos.has(JSON_MOTION_II) ? infos.getString(JSON_MOTION_II) : "";
		JSONArray array = infos.has(JSON_MOTION_III) ? infos.getJSONArray(JSON_MOTION_III) : null;
		if (array != null) {
			this.aletrnativeMarkersId = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				this.aletrnativeMarkersId.add(array.getString(i));
			}
		}
	}

	@Override
	public String toString() {
		return "\nWakeUpRoutine ['\n" + "\tname=" + name + ", \n" + "\tusedName=" + usedName + ", \n" + "\tstart="
				+ start + ", \n" + "\tend=" + end + ", \n" + "\tdelay=" + delay + ", \n" + "\tprimaryMarkerId="
				+ primaryMarkerId + ", \n" + "\tsecondaryMarkerId=" + secondaryMarkerId + ", \n"
				+ "\taletrnativeMarkersId=" + aletrnativeMarkersId + "]";
	}

	@Override
	public String getServerRequest(String scriptFolderPath, File logfile, File scoreFile) {
		return String.format("%swakeup.pl -b%s -e%s -1'%s' -2'%s' -T%d %s >> %s", scriptFolderPath, this.start,
				this.end, this.primaryMarkerId, this.secondaryMarkerId, this.delay, logfile.getAbsolutePath(),
				scoreFile.getAbsolutePath());
	}

}
