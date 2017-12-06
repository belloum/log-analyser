package beans.participants.routines.meals;

import org.json.JSONException;
import org.json.JSONObject;

public class BreakfastRoutine extends MealRoutine {

	public BreakfastRoutine(JSONObject json) throws JSONException {
		super(json);
		this.name = RoutinizedActivities.BREAKFAST;
	}

	@Override
	public String toString() {
		return "\nBreakfastRoutine: " + super.toString();
	}

}
