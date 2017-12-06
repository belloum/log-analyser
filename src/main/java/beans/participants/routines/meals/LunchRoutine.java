package beans.participants.routines.meals;

import org.json.JSONException;
import org.json.JSONObject;

public class LunchRoutine extends MealRoutine {

	public LunchRoutine(JSONObject json) throws JSONException {
		super(json);
		this.name = RoutinizedActivities.LUNCH;
	}

	@Override
	public String toString() {
		return "\nLunchRoutine: " + super.toString();
	}

}
