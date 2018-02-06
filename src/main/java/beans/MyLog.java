//package beans;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.TimeZone;
//
//import org.json.JSONObject;
//
//import beans.devices.Sensor;
//
///**
// * Soft log representation
// * 
// * @param id
// *            The id of the log
// * @param timestamp
// *            The timestamp of the log (in milliseconds)
// * @param value
// *            The value sent by the device (depending on device)
// * @param device
// *            The device which sends the log
// * 
// * @see Sensor
// * @see Sensor.Type
// * 
// * @author Antoine Riché
// * @since 05/24/17
// *
// */
//
//public class MyLog {
//
//	long timestamp;
//	float value;
//	Sensor device;
//
//	/**
//	 * 
//	 * @param pId
//	 *            The id of the log
//	 * @param pTimestamp
//	 *            The timestamp of the log (in milliseconds)
//	 * @param pValue
//	 *            The value sent by the device (depending on device)
//	 * @param pDevice
//	 *            The device which sends the log
//	 * 
//	 * @see Sensor
//	 * @see Sensor.Type
//	 */
//	public MyLog(long pTimestamp, float pValue, Sensor pDevice) {
//		this.timestamp = pTimestamp;
//		this.value = pValue;
//		this.device = pDevice;
//	}
//
//	/**
//	 * 
//	 * @param json
//	 *            A JSON representation of the log
//	 * @throws Exception
//	 *             The exception is thrown if the JSON does not contain the
//	 *             right keys
//	 */
//	public MyLog(JSONObject json) throws Exception {
//
//		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//		this.timestamp = utcFormat.parse(json.getString("@timestamp")).getTime();
//		this.value = Float.parseFloat(json.getJSONObject("event").getString("value"));
//		this.device = Sensor.getSensorWithJSON(json.getJSONObject("device"));
//	}
//
//	/**
//	 * 
//	 * @return The timestamp of the log (in milliseconds)
//	 */
//	public long getTimestamp() {
//		return timestamp;
//	}
//
//	/**
//	 * 
//	 * @param timestamp
//	 *            The timestamp of the log (in milliseconds)
//	 */
//	public void setTimestamp(long timestamp) {
//		this.timestamp = timestamp;
//	}
//
//	/**
//	 * 
//	 * @return The value sent by the device (depending on type)<br>
//	 *         In case of:</br>
//	 *         <ul>
//	 *         <li><u>ContactSensor: </u>
//	 *         <ul>
//	 *         <li><b>0</b> contact</li>
//	 *         <li><b>1</b> no more contact</li>
//	 *         </ul>
//	 *         </li>
//	 *         <li><u>MotionDetector: </u>
//	 *         <ul>
//	 *         <li><b>0</b> no motion</li>
//	 *         <li><b>1</b> motion</li>
//	 *         </ul>
//	 *         </li>
//	 *         <li><u>ElectricMeter: </u><br>
//	 *         The consumption value (in Watts)</br>
//	 *         </li>
//	 *         </ul>
//	 * 
//	 * @see Sensor
//	 * @see Sensor.Type
//	 */
//	public float getValue() {
//		return value;
//	}
//
//	/**
//	 * 
//	 * @param value
//	 *            The value sent by the device (depending on type)<br>
//	 *            In case of:</br>
//	 *            <ul>
//	 *            <li><u>ContactSensor: </u>
//	 *            <ul>
//	 *            <li><b>0</b> contact</li>
//	 *            <li><b>1</b> no more contact</li>
//	 *            </ul>
//	 *            </li>
//	 *            <li><u>MotionDetector: </u>
//	 *            <ul>
//	 *            <li><b>0</b> no motion</li>
//	 *            <li><b>1</b> motion</li>
//	 *            </ul>
//	 *            </li>
//	 *            <li><u>ElectricMeter: </u><br>
//	 *            The consumption value (in Watts)</br>
//	 *            </li>
//	 *            </ul>
//	 * 
//	 * @see Sensor
//	 * @see Sensor.Type
//	 */
//	public void setValue(float value) {
//		this.value = value;
//	}
//
//	/**
//	 * 
//	 * @return The device which sent the log <br>
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
//	 * @see Sensor
//	 * @see Sensor.Type
//	 */
//	public Sensor getDevice() {
//		return device;
//	}
//
//	/**
//	 * 
//	 * @param device
//	 *            The device which sent the log <br>
//	 *            Can be :</br>
//	 *            <ul>
//	 *            <li><b>ContactSensor: </b><i>A contact sensor which notifies
//	 *            when contact state changes</i></li>
//	 *            <li><b>MotionDetector: </b><i>A motion detector which notifies
//	 *            when motion is detected or when someone disappears</i></li>
//	 *            <li><b>ElectricMeter: </b><i>An electric meter which notifies
//	 *            when electric consumption is changes</i></li>
//	 *            </ul>
//	 * 
//	 * @see Sensor
//	 * @see Sensor.Type
//	 */
//	public void setDevice(Sensor device) {
//		this.device = device;
//	}
//
//	@Override
//	public String toString() {
//		return "MyLog [timestamp=" + timestamp + ", value=" + value + ", device=" + device + "]";
//	}
//
//	/**
//	 * 
//	 * @param json
//	 *            The JSON representation of {@link MyLog}
//	 * @return
//	 *         <ul>
//	 *         <li><b>true </b> if the Json representation specified in
//	 *         parameters contains correct fields</li>
//	 *         <li><b>false </b> otherwise</li>
//	 *         </ul>
//	 * @throws Exception
//	 *             Exception is thrown if json specified in parameters is
//	 *             malformed
//	 */
//	public static boolean isAValidLog(JSONObject json) {
//
//		if (!json.has("@timestamp")) {
//			// System.out.println("Doesn't have '@timestamp' leaf");
//			return false;
//		}
//
//		if (!json.has("device") || !json.has("event")) {
//			// System.out.println("Doesn't have 'device' or 'event' leaf");
//			return false;
//		}
//
//		JSONObject device = json.getJSONObject("device");
//		JSONObject event = json.getJSONObject("event");
//
//		if (!event.has("value") || event.getString("value").equals("")) {
//			// System.out.println("Doesn't have 'value' leaf");
//			return false;
//		}
//
//		if (!device.has("name") || !device.has("room")) {
//			// System.out.println("Doesn't have 'name' or 'room' leaf");
//			return false;
//		}
//
//		String name = device.getString("name");
//
//		if (!name.contains("Motion") && !name.contains("Contact") && !name.contains("EMeter")) {
//			// System.out.println("Name doesn't contain 'Motion' nor 'Contact'
//			// nor 'EMeter' : " + name);
//			return false;
//		}
//
//		if (name.contains("Motion") || name.contains("Contact")) {
//			if (!device.has("tripped")) {
//				// System.out.println(name + " doesn't have 'tripped' leaf " +
//				// json);
//				return false;
//			}
//		}
//
//		else if (name.contains("EMeter")) {
//			if (!device.has("watts")) {
//				// System.out.println(name + " doesn't have 'watts' leaf " +
//				// json);
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//}
