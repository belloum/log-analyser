package loganalyser.old.ui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import beans.devices.Device;
import loganalyser.utils.Configuration;

public class DeviceSelector extends CustomComponent implements ItemListener {

	private static final long serialVersionUID = 1L;
	private int ITEM_BY_LINE = 4;

	private Map<String, Device> mDevices = new LinkedHashMap<>();
	private List<JCheckBox> mLCheck = new LinkedList<>();
	private DeviceSelectorListener mListener;

	public DeviceSelector() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public DeviceSelector(Dimension pDimension, List<Device> pDevices) {
		super(pDimension);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setCheckBoxDevices(pDevices);
		build();
	}

	public void setEnabled(String pLabel, boolean pEnabled) {
		this.mLCheck.stream().filter(check -> check.getText().equals(pLabel)).findFirst().get().setEnabled(pEnabled);
	}

	public void setSelected(boolean pSelected) {
		this.mLCheck.stream().forEach(box -> box.setSelected(pSelected));
	}

	public DeviceSelector(Dimension pDimension, List<Device> pDevices, DeviceSelectorListener pDeviceSelectorListener) {
		this(pDimension, pDevices);
		if (pDeviceSelectorListener instanceof DeviceSelectorListener) {
			this.mListener = pDeviceSelectorListener;
		} else {
			throw new IllegalArgumentException(String.format("%s  must implement DeviceSelectorListener",
					pDeviceSelectorListener.getClass().getName()));
		}
	}

	public DeviceSelector(List<Device> pDevices) {
		this();
		setCheckBoxDevices(pDevices);
	}

	public void setMaxItemByLine(int pMaxItem) {
		this.ITEM_BY_LINE = pMaxItem;
		build();
	}

	public void setCheckBoxDevices(List<Device> pDevices) {
		mDevices.clear();
		mLCheck.clear();
		pDevices.forEach(device -> {
			JCheckBox jCheckDevice = new JCheckBox(device.getId());
			jCheckDevice.setSelected(true);
			jCheckDevice.addItemListener(this);
			mDevices.put(device.getId(), device);
			mLCheck.add(jCheckDevice);
		});
	}

	public void updateDeviceList(List<Device> pDevices) {
		setCheckBoxDevices(pDevices);
		build();
	}

	public void resetDevicesList() {
		setCheckBoxDevices(new ArrayList<Device>());
		build();
	}

	private void adaptSize() {
		int nbLine = (int) Math.ceil((double) this.mDevices.size() / (double) this.ITEM_BY_LINE);
		setPreferredSize(new Dimension(getMaximumSize().width, nbLine * Configuration.ITEM_HEIGHT));
		setMaximumSize(getPreferredSize());
	}

	protected void build() {
		super.build();
		JPanel jPanel = null;

		for (int i = 0; i < this.mLCheck.size(); i++) {
			if (i % this.ITEM_BY_LINE == 0) {
				jPanel = new JPanel();
				jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
				jPanel.setMaximumSize(new Dimension(getMaximumSize().width, Configuration.ITEM_HEIGHT));
				jPanel.setMinimumSize(new Dimension(getMaximumSize().width, Configuration.ITEM_HEIGHT));
				add(jPanel);
			}
			JCheckBox value = this.mLCheck.get(i);
			value.setPreferredSize(
					new Dimension(getMaximumSize().width / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			value.setMaximumSize(new Dimension(getMaximumSize().width / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			value.setMinimumSize(new Dimension(getMaximumSize().width / this.ITEM_BY_LINE, Configuration.ITEM_HEIGHT));
			jPanel.add(value);
		}

		setVisible(false);
		setVisible(true);
		adaptSize();
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
	}

}
