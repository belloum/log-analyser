package beans.devices;

/**
 * Simple representation of contact sensor
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */
public class ContactSensor extends Sensor {

	public ContactSensor(String pId, String pLocation) {
		super(pId, pLocation);
		this.type = Type.ContactSensor;
	}

}
