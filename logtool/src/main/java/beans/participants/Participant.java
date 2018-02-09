package beans.participants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import beans.participants.routines.GoBedRoutine;
import beans.participants.routines.Routine;
import beans.participants.routines.WakeUpRoutine;
import beans.participants.routines.meals.BreakfastRoutine;
import beans.participants.routines.meals.DinnerRoutine;
import beans.participants.routines.meals.LunchRoutine;

public class Participant {

	public static final String JSON_VERA = "vera";
	public static final String JSON_TCAPSA = "tcapsa";
	public static final String JSON_INSTALL = "install";
	public static final String JSON_ACTIVITIES = "activities";
	public static final String ROUTINE_BREAKFAST = "breakfast";
	public static final String ROUTINE_LUNCH = "lunch";
	public static final String ROUTINE_DINNER = "dinner";
	public static final String ROUTINE_WAKEUP = "wakeup";
	public static final String ROUTINE_GOBED = "gobed";

	private String name;
	private String vera;
	private String tcapsa;
	private Date install;
	private Map<String, Routine> routines;

	public Participant(String name) {
		this.name = name;
		this.routines = new HashMap<String, Routine>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTcapsa() {
		return tcapsa;
	}

	public void setTcapsa(String tcapsa) {
		this.tcapsa = tcapsa;
	}

	public Date getInstall() {
		return install;
	}

	public void setInstall(Date install) {
		this.install = install;
	}

	public Map<String, Routine> getRoutines() {
		return routines;
	}

	public void setRoutines(Map<String, Routine> routines) {
		this.routines = routines;
	}

	public void fillWithJSON(JSONObject infos) throws JSONException {
		this.tcapsa = infos.has(JSON_TCAPSA) ? infos.getString(JSON_TCAPSA) : null;
		this.vera = infos.has(JSON_VERA) ? infos.getString(JSON_VERA) : null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		try {
			this.install = infos.has(JSON_INSTALL) ? sdf.parse(infos.getString(JSON_INSTALL)) : null;
		} catch (Exception e) {
			throw new IllegalArgumentException("Malformed date: " + e.getMessage());
		}

		if (infos.has(JSON_ACTIVITIES)) {
			infos = infos.getJSONObject(JSON_ACTIVITIES);
			if (infos.has(ROUTINE_BREAKFAST)) {
				this.routines.put(ROUTINE_BREAKFAST, new BreakfastRoutine(infos.getJSONObject(ROUTINE_BREAKFAST)));
			}

			if (infos.has(ROUTINE_LUNCH)) {
				this.routines.put(ROUTINE_LUNCH, new LunchRoutine(infos.getJSONObject(ROUTINE_LUNCH)));
			}

			if (infos.has(ROUTINE_DINNER)) {
				this.routines.put(ROUTINE_DINNER, new DinnerRoutine(infos.getJSONObject(ROUTINE_DINNER)));
			}

			if (infos.has(ROUTINE_WAKEUP)) {
				this.routines.put(ROUTINE_WAKEUP, new WakeUpRoutine(infos.getJSONObject(ROUTINE_WAKEUP)));
			}

			if (infos.has(ROUTINE_GOBED)) {
				this.routines.put(ROUTINE_GOBED, new GoBedRoutine(infos.getJSONObject(ROUTINE_GOBED)));
			}
		}

	}

	public String getVera() {
		return vera;
	}

	public void setVera(String vera) {
		this.vera = vera;
	}

	public Routine getRoutine(String activity) {
		return this.routines.containsKey(activity) ? this.routines.get(activity) : null;
	}

	@Override
	public String toString() {
		return "Participant [\nname=" + name + ", \nvera=" + vera + ", \ntcapsa=" + tcapsa + ", \ninstall=" + install
				+ ", \nroutines=" + routines + "]";
	}

	public List<String> extractRoutineRequests(String scriptPath, File logFile, File scoreFile) {

		List<String> requests = new ArrayList<>();

		String request;
		if (this.routines.containsKey(ROUTINE_WAKEUP)) {
			request = getRoutine(Participant.ROUTINE_WAKEUP).getServerRequest(scriptPath, logFile, scoreFile);
			requests.add(request);
			System.out.println(request);
		}

		if (this.routines.containsKey(ROUTINE_BREAKFAST)) {
			request = getRoutine(Participant.ROUTINE_BREAKFAST).getServerRequest(scriptPath, logFile, scoreFile);
			requests.add(request);
			System.out.println(request);
		}

		if (this.routines.containsKey(ROUTINE_LUNCH)) {
			request = getRoutine(Participant.ROUTINE_LUNCH).getServerRequest(scriptPath, logFile, scoreFile);
			requests.add(request);
			System.out.println(request);
		}

		if (this.routines.containsKey(ROUTINE_DINNER)) {
			request = getRoutine(Participant.ROUTINE_DINNER).getServerRequest(scriptPath, logFile, scoreFile);
			requests.add(request);
			System.out.println(request);
		}

		if (this.routines.containsKey(ROUTINE_GOBED)) {
			request = getRoutine(Participant.ROUTINE_GOBED).getServerRequest(scriptPath, logFile, scoreFile);
			requests.add(request);
			System.out.println(request);
		}

		return requests;
	}
}
