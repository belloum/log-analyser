package beans.participants.routines;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

public class GoBedRoutine extends Routine {

	private static final String JSON_MOTION_I = "motionI";
	private static final String JSON_MOTION_II = "motionII";
	private static final String JSON_DELAY_RULES = "delay_rules";
	private static final String JSON_DELAY_WITHOUT_MOTION = "delay_without_motion";

	private String primaryMarkerId;
	private String secondaryMarkerId;
	private int delayRules;
	private int delayWithoutMotion;

	public GoBedRoutine(JSONObject infos) throws JSONException {
		super(infos);
		this.name = RoutinizedActivities.GO_TO_BED;
		this.delayRules = infos.has(JSON_DELAY_RULES) ? infos.getInt(JSON_DELAY_RULES) : -1;
		this.delayWithoutMotion = infos.has(JSON_DELAY_WITHOUT_MOTION) ? infos.getInt(JSON_DELAY_WITHOUT_MOTION) : -1;
		this.primaryMarkerId = infos.has(JSON_MOTION_I) ? infos.getString(JSON_MOTION_I) : "";
		this.secondaryMarkerId = infos.has(JSON_MOTION_II) ? infos.getString(JSON_MOTION_II) : "";
	}

	@Override
	public String toString() {
		return "\nGoBedRoutine ['\n" + "\tname=" + name + ", \n" + "\tusedName=" + usedName + ", \n" + "\tstart="
				+ start + ", \n" + "\tend=" + end + ", \n" + "\tdelayRules=" + delayRules + ", \n"
				+ "\tdelayWithoutMotion=" + delayWithoutMotion + ", \n" + "\tprimaryMarkerId=" + primaryMarkerId
				+ ", \n" + "\tsecondaryMarkerId=" + secondaryMarkerId + "]";
	}

	@Override
	public String getServerRequest(String scriptFolderPath, File logfile, File scoreFile) {
		return String.format("%sgotobed.pl -b%s -e%s -1'%s' -2'%s' -T%d %s >> %s", scriptFolderPath, this.start,
				this.end, this.secondaryMarkerId, this.primaryMarkerId, this.delayRules, logfile.getAbsolutePath(),
				scoreFile.getAbsolutePath());
	}

}