package ui.menus.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import beans.SoftLog;
import beans.devices.Device;
import exceptions.RawLogException;
import operators.extractors.RawLogFormater;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.components.MyButton;
import utils.Configuration;

public class DeviceTab extends JPanel {

	// FIXME: Bug with all sensors

	private static final long serialVersionUID = 1L;

	private enum SplitMod {
		HOUR, DAY
	}

	private List<SoftLog> mLogs;
	private JScrollPane mDevicesView = new JScrollPane();
	private JScrollPane mDetailsView = new JScrollPane();
	private JPanel mDetails = new JPanel();

	private Device mSelectedDevice;
	private SplitMod mCurrentSplittingMod = SplitMod.DAY;

	public DeviceTab(File pLogFile) {
		setLayout(new BorderLayout());
		try {
			this.mLogs = RawLogFormater.extractLogs(pLogFile);
		} catch (RawLogException ignored) {
		}

		add(mDevicesView, BorderLayout.LINE_START);
		mDevicesView.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 2,
				mDevicesView.getPreferredSize().height));

		setUpDevicePanel(this.mLogs);

		add(mDetailsView, BorderLayout.LINE_END);
		mDetailsView.setPreferredSize(
				new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 2, Configuration.MAX_HEIGHT));

		validate();
	}

	private void setUpDevicePanel(List<SoftLog> pLogs) {
		mDevicesView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mDevicesView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mDevicesView.getViewport().removeAll();

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		/*
		 * Device info
		 */
		center.add(CustomComponent.boldLabel(new JLabel("Device infos")));
		List<Device> devices = SoftLogExtractor.getDevices(pLogs);
		String[] headers = new String[] { "Device", "Type", "Logs" };
		String[][] data = new String[devices.size() + 1][headers.length];
		int index = 0;
		for (Device device : devices) {
			data[index] = new String[] { device.getId(), device.getType().name(),
					SoftLogExtractor.filterByIds(pLogs, Collections.singletonList(device.getId())).size() + "" };
			index++;
		}
		data[devices.size()] = new String[] { "All", "", pLogs.size() + "" };

		JTable table = new JTable(data, headers) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		center.add(table);

		table.setMaximumSize(new Dimension(mDevicesView.getPreferredSize().width, table.getPreferredSize().height));
		table.getSelectionModel().addListSelectionListener(event -> {
			String id = table.getValueAt(table.getSelectedRow(), 0).toString();
			setUpDetailsView(id);
			System.out.println("Selected device: " + id);
		});

		mDevicesView.getViewport().add(center);
	}

	private void setUpDetailsView(String pDeviceId) {
		mDetailsView.getViewport().removeAll();

		mDetails = new JPanel(new BorderLayout());

		List<SoftLog> deviceLogs;

		if (!"All".equals(pDeviceId)) {
			deviceLogs = SoftLogExtractor.filterByIds(this.mLogs, Collections.singletonList(pDeviceId));
			mSelectedDevice = SoftLogExtractor.getDeviceWithId(deviceLogs, pDeviceId);
		} else {
			deviceLogs = this.mLogs;
			mSelectedDevice = null;
		}

		if (!deviceLogs.isEmpty()) {
			JPanel head = new JPanel();
			head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));

			head.add(CustomComponent.boldLabel(new JLabel("Generic infos")));
			head.add(getDeviceGenericDetails(mSelectedDevice));

			head.add(getSplittingButtons());

			mDetails.add(head, BorderLayout.PAGE_START);

		} else {
			mDetails.add(new JLabel(String.format("No log for %s", pDeviceId)));
			mSelectedDevice = null;
		}

		getRepartitionView(pDeviceId);
		mDetailsView.getViewport().add(mDetails);
	}

	private JTable getDeviceGenericDetails(Device pDevice) {
		String[][] data = { { "Id", pDevice != null ? pDevice.getId() : "-" },
				{ "Type", pDevice != null ? pDevice.getType().name() : "-" },
				{ "Location", pDevice != null ? pDevice.getLocation() : "-" } };

		JTable fileTab = new JTable(data, new String[] { "key", "value" });
		fileTab.setEnabled(false);
		fileTab.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 3,
				fileTab.getPreferredSize().height));

		return fileTab;
	}

	private JPanel getSplittingButtons() {
		JPanel btns = new JPanel();
		btns.setLayout(new BorderLayout());
		MyButton mButDay = new MyButton("Day Repartition", e -> {
			System.out.println("Click on day repartition");
			mCurrentSplittingMod = SplitMod.DAY;
			getRepartitionView(mSelectedDevice != null ? mSelectedDevice.getId() : null);
		});

		MyButton mButHour = new MyButton("Hour Repartition", e -> {
			System.out.println("Click on hour repartition");
			mCurrentSplittingMod = SplitMod.HOUR;
			getRepartitionView(mSelectedDevice != null ? mSelectedDevice.getId() : null);
		});

		btns.add(mButDay, BorderLayout.LINE_START);
		btns.add(mButHour, BorderLayout.LINE_END);
		btns.setMaximumSize(new Dimension(btns.getPreferredSize().width, Configuration.ITEM_HEIGHT));
		btns.setPreferredSize(btns.getMaximumSize());
		return btns;
	}

	private JPanel getRepartitionView(String pDeviceId) {
		JPanel mRepartitionView = new JPanel();
		mRepartitionView.setLayout(new BoxLayout(mRepartitionView, BoxLayout.Y_AXIS));

		if (mCurrentSplittingMod.equals(SplitMod.DAY)) {
			mRepartitionView.add(new JLabel("Sorted by day."));
			mRepartitionView.add(split(pDeviceId, SplitMod.DAY));
		}

		else {
			mRepartitionView.add(new JLabel("Sorted by hour."));
			mRepartitionView.add(split(pDeviceId, SplitMod.HOUR));
		}

		mDetails.add(mRepartitionView, BorderLayout.CENTER);
		mDetails.validate();
		return mRepartitionView;
	}

	private JTable split(String pDeviceId, SplitMod pSplitMod) {
		Map<String, List<SoftLog>> splitted;

		List<SoftLog> toSplit;
		if (pDeviceId != null && !pDeviceId.equals("All")) {
			toSplit = SoftLogExtractor.filterByIds(mLogs, Collections.singletonList(pDeviceId));
		} else {
			toSplit = this.mLogs;
		}

		if (pSplitMod.equals(SplitMod.DAY)) {
			splitted = SoftLogExtractor.gatherLogsByDay(toSplit);
		} else {
			splitted = SoftLogExtractor.gatherLogsByHour(toSplit);
		}
		String[][] data = new String[splitted.size()][2];

		List<String> keyList = new ArrayList<String>(splitted.keySet());
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			data[i] = new String[] { key, String.format("%d", splitted.get(key).size()) };
		}

		JTable fileTab = new JTable(data, new String[] { "key", "value" });
		fileTab.setEnabled(false);
		fileTab.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 3,
				fileTab.getPreferredSize().height));

		return fileTab;
	}

}
