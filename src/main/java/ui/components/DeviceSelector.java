package ui.components;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import beans.devices.Device;
import utils.Configuration;

public class DeviceSelector extends CustomComponent implements ItemListener {

	private static final long serialVersionUID = 1L;
	private int ITEM_BY_LINE = 4;

	private Map<String, Device> mDevices = new LinkedHashMap<>();
	private List<JCheckBox> mLCheck = new LinkedList<>();
	private DeviceSelectorListener mListener;

	public DeviceSelector() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public DeviceSelector(List<Device> pDevices) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setCheckBoxDevices(pDevices);
	}

	public void setCheckBoxDevices(List<Device> pDevices) {
		resetDevicesList();
		setVisible(true);
		pDevices.forEach(device -> {
			JCheckBox jCheckDevice = new JCheckBox(device.getId());
			jCheckDevice.setSelected(true);
			jCheckDevice.addItemListener(this);
			mDevices.put(device.getId(), device);
			mLCheck.add(jCheckDevice);
		});
		build();
	}

	public void resetDevicesList() {
		removeAll();
		mDevices.clear();
		mLCheck.clear();
	}

	public void setListener(DeviceSelectorListener pListener) {
		if (pListener instanceof DeviceSelectorListener) {
			this.mListener = pListener;
		} else {
			throw new IllegalArgumentException(
					String.format("%s  must implemet DeviceSelectorListener", pListener.getClass().getName()));
		}
	}

	protected void build() {
		super.build();
		int a = getParent().getPreferredSize().width;

		JPanel jPanel = null;

		for (int i = 0; i < this.mLCheck.size(); i++) {
			if (i % this.ITEM_BY_LINE == 0) {
				jPanel = new JPanel();
				jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
				jPanel.setMaximumSize(new Dimension(a, Configuration.ITEM_HEIGHT));
				jPanel.setMinimumSize(new Dimension(a, Configuration.ITEM_HEIGHT));
				add(jPanel);
			}
			JCheckBox value = this.mLCheck.get(i);
			value.setPreferredSize(new Dimension(a / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			value.setMaximumSize(new Dimension(a / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			value.setMinimumSize(new Dimension(a / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			jPanel.add(value);
		}

		return;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		String deviceId = ((JCheckBox) e.getItem()).getText();

		if (ItemEvent.SELECTED == e.getStateChange()) {
			mListener.select(this.mDevices.get(deviceId));
		}

		else if (ItemEvent.DESELECTED == e.getStateChange()) {
			mListener.unselect(this.mDevices.get(deviceId));
		}
	}

	public interface DeviceSelectorListener {
		void select(Device pDevice);

		void unselect(Device pDevice);

		void notifyDataChanged();
	}

}
