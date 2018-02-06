//package beans.devices;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Generic representation of device
// * 
// * @param id
// *            The id of the device
// * @param location
// *            The location of the device
// * @param type
// *            The type of the device
// * 
// * @see Sensor.Type
// * 
// * @author Antoine Riché
// * @since 05/24/17
// *
// */
//public class Sensor {
//
//	String id;
//	String location;
//	Type type;
//
//	/**
//	 * Sensor types:<br>
//	 * </br>
//	 * <ul>
//	 * <li><b>ContactSensor: </b><i>A contact sensor which notifies when contact
//	 * state changes</i></li>
//	 * <li><b>MotionDetector: </b><i>A motion detector which notifies when
//	 * motion is detected or when someone disappears</i></li>
//	 * <li><b>ElectricMeter: </b><i>An electric meter which notifies when
//	 * electric consumption changes</i></li>
//	 * </ul>
//	 * 
//	 * @author Antoine Riché
//	 *
//	 */
//	public enum Type {
//		ContactSensor, MotionDetector, ElectricMeter
//	}
//
//	/**
//	 * 
//	 * @param pId
//	 *            The id of the device
//	 * @param pLocation
//	 *            The location of the device in the house
//	 */
//	public Sensor(String pId, String pLocation) {
//		this.id = pId;
//		this.location = pLocation;
//	}
//
//	public Sensor() {
//	}
//
//	/**
//	 * 
//	 * @param json
//	 *            A JSON representation of the device
//	 * @throws JSONException
//	 *             The exception is thrown if the JSON does not contain the
//	 *             right keys
//	 */
//	public Sensor(JSONObject jsonObject) throws JSONException {
//		this.id = jsonObject.getString("id");
//		this.location = jsonObject.getString("location");
//		if (id.contains("Motion")) {
//			this.type = Type.MotionDetector;
//		} else if (id.contains("Contact"))
//			this.type = Type.ContactSensor;
//		else if (id.contains("EMeter"))
//			this.type = Type.ElectricMeter;
//	}
//
//	/**
//	 * 
//	 * @return The id of the device
//	 */
//	public String getId() {
//		return id;
//	}
//
//	/**
//	 * 
//	 * @param id
//	 *            The id of the device
//	 */
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	/**
//	 * 
//	 * @return The location of the device
//	 */
//	public String getLocation() {
//		return location;
//	}
//
//	/**
//	 * 
//	 * @param location
//	 *            The location of the device
//	 */
//	public void setLocation(String location) {
//		this.location = location;
//	}
//
//	/**
//	 * 
//	 * @return type The type of the device <br>
//	 *         Can be :</br>
//	 *         <ul>
//	 *         <li><b>ContactSensor: </b><i>A contact sensor which notifies when
//	 *         contact state changes</i></li>
//	 *         <li><b>MotionDetector: </b><i>A motion detector which notifies
//	 *         when motion is detected or when someone disappears</i></li>
//	 *         <li><b>ElectricMeter: </b><i>An electric meter which notifies
//	 *         when electric consumption changes</i></li>
//	 *         </ul>
//	 * 
//	 * @see Sensor.Type
//	 */
//	public Type getType() {
//		return type;
//	}
//
//	/**
//	 * 
//	 * @param type
//	 *            The type of the device <br>
//	 *            Can be :</br>
//	 *            <ul>
//	 *            <li><b>ContactSensor: </b><i>A contact sensor which notifies
//	 *            when contact state changes</i></li>
//	 *            <li><b>MotionDetector: </b><i>A motion detector which notifies
//	 *            when motion is detected or when someone disappears</i></li>
//	 *            <li><b>ElectricMeter: </b><i>An electric meter which notifies
//	 *            when electric consumption changes</i></li>
//	 *            </ul>
//	 * 
//	 * @see Sensor.Type
//	 */
//	public void setType(Type type) {
//		this.type = type;
//	}
//
//	/**
//	 * Map a JSON representation to a Device instance
//	 * 
//	 * @param jsonDevice
//	 *            The JSON representation of the device
//	 * @return The instance of device
//	 * @throws Exception
//	 *             The exception is thrown if the JSON does not contain the
//	 *             right keys
//	 */
//	public static Sensor getSensorWithJSON(JSONObject jsonDevice) throws Exception {
//
//		// String id = jsonDevice.getString("name");
//		// if (id.contains("Motion"))
//		// return new MotionDetector(id, jsonDevice.getString("room"));
//		// else if (id.contains("Contact"))
//		// return new ContactSensor(id, jsonDevice.getString("room"));
//		// else if (id.contains("EMeter"))
//		// return new ElectricMeter(id, jsonDevice.getString("room"));
//		// else
//		// return null;
//		return null;
//
//	}
//
//	@Override
//	public String toString() {
//		return "Device [location=" + location + ", type=" + type + ", id=" + id + "]";
//	}
//
//}
