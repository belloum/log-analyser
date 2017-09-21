package beans.participants.routines.meals;

import org.json.JSONException;
import org.json.JSONObject;

public class DinnerRoutine extends MealRoutine {

	public DinnerRoutine(JSONObject json) throws JSONException {
		super(json);
		this.name = RoutinizedActivities.DINNER;
	}

	@Override
	public String toString() {
		return "\nDinnerRoutine: " + super.toString();
	}

}
