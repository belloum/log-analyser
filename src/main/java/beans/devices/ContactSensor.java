package beans.devices;

/**
 * Simple representation of contact sensor
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */
public class ContactSensor extends Device {

	public enum ContactSensorState {
		Open, Close
	}

	public ContactSensor(String pId, String pLocation) {
		super(pId, pLocation);
		this.mType = DeviceType.ContactSensor;
	}

}
