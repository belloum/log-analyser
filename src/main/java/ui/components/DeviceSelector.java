package ui.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import beans.devices.Device;
import utils.Configuration;

public class DeviceSelector extends CustomComponent implements ItemListener {

	private static final long serialVersionUID = 1L;
	private int ITEM_BY_LINE = 4;

	private Map<Device, JCheckBox> mCheckBoxDevices;
	private DeviceSelectorListener mListener;

	public DeviceSelector() {
		super();
	}

	public DeviceSelector(List<Device> pDevices) {
		super();
		setCheckBoxDevices(pDevices);
	}

	public void setCheckBoxDevices(List<Device> pDevices) {
		resetDevicesList();
		mCheckBoxDevices = new HashMap<>();
		setVisible(true);
		pDevices.forEach(device -> {
			JCheckBox jCheckDevice = new JCheckBox(device.getId());
			jCheckDevice.setSelected(true);
			jCheckDevice.addItemListener(this);

			mCheckBoxDevices.put(device, jCheckDevice);
			if (mCheckBoxDevices.size() % (this.ITEM_BY_LINE + 1) == 0) {
				xOffset = 0;
				yOffset += Configuration.ITEM_HEIGHT;
			}
			addComponentHorizontally(jCheckDevice, (getSize().width - Configuration.PADDING) / this.ITEM_BY_LINE,
					false);
		});
	}

	public void adaptSizeToList() {
		if (this.mCheckBoxDevices != null) {
			setSize(getWidth(), (1 + (int) (mCheckBoxDevices.size() / this.ITEM_BY_LINE)) * Configuration.ITEM_HEIGHT);
		} else {
			throw new IllegalArgumentException("List of device is null");
		}
	}

	public void clearDevicesList() {
		if (mCheckBoxDevices != null) {
			mCheckBoxDevices.clear();
			resetDevicesList();
		}
	}

	private void resetDevicesList() {
		removeAll();
		xOffset = 0;
		yOffset = 0;
	}

	public void setListener(DeviceSelectorListener pListener) {
		if (pListener instanceof DeviceSelectorListener) {
			this.mListener = pListener;
		} else {
			throw new IllegalArgumentException(
					String.format("%s  must implemet DeviceSelectorListener", pListener.getClass().getName()));
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		String deviceId = ((JCheckBox) e.getItem()).getText();

		if (ItemEvent.SELECTED == e.getStateChange()) {
			mListener.select(mCheckBoxDevices.keySet().stream().filter(device -> device.getId().equals(deviceId))
					.findFirst().get());
		}

		else if (ItemEvent.DESELECTED == e.getStateChange()) {
			mListener.unselect(mCheckBoxDevices.keySet().stream().filter(device -> device.getId().equals(deviceId))
					.findFirst().get());
		}
	}

	public interface DeviceSelectorListener {
		void select(Device device);

		void unselect(Device device);

		void notifyDataChanged();
	}

}
