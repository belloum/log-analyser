package beans.devices;

/**
 * Simple representation of electric meter
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */

public class ElectricMeter extends Device {

	public ElectricMeter(String pId, String pLocation) {
		super(pId, pLocation);
		this.mType = DeviceType.ElectricMeter;
	}

}
