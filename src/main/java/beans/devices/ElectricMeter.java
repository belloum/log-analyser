package beans.devices;

/**
 * Simple representation of electric meter
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */

public class ElectricMeter extends Sensor {

	public ElectricMeter(String pId, String pLocation) {
		super(pId, pLocation);
		this.type = Type.ElectricMeter;
	}

}
