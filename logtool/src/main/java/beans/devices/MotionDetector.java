package beans.devices;

/**
 * Simple representation of motion detector
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */

public class MotionDetector extends Device {

	public enum MotionDetectorState {
		Presence, Absence
	}

	public MotionDetector(String pId, String pLocation) {
		super(pId, pLocation);
		this.mType = DeviceType.MotionDetector;
	}

}
