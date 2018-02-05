package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.StringUtils;

import beans.SoftLog;
import beans.devices.Device;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.last.FileSelector;
import ui.last.components.ComponentWithImage;
import ui.last.components.MyTable;
import ui.last.components.TabHeader;
import utils.Configuration;

//TODO implements Runnable to fill view
public class DeviceTab2 extends LogTab {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private enum SplitMod {
		HOUR, DAY
	}

	private JScrollPane mRepartitionDetails;
	private ComponentWithImage mRepartitionTop;

	private String mDevice;
	private SplitMod mCurrentSplittingMod = SplitMod.DAY;

	public DeviceTab2(FileSelector pFileSelector) {
		super(pFileSelector);
		mDevice = "All";
		updateSplittedView();
	}

	private JPanel createRepartitionTops(String[] pHoursTop, String[] pDaysTop) {
		JPanel repartitionTops = new JPanel(new GridLayout(4, 2));
		repartitionTops.add(CustomComponent.boldLabel("Hourly top"));
		repartitionTops.add(CustomComponent.boldLabel("Daily top"));
		for (int i = 0; i < pHoursTop.length; i++) {
			repartitionTops.add(new JLabel(pHoursTop[i]));
			repartitionTops.add(new JLabel(pDaysTop[i]));
		}
		return repartitionTops;
	}

	private void updateSplittedView() {
		mRepartitionDetails.getViewport().removeAll();

		Map<String, List<SoftLog>> splitted;
		String[] headers;
		String[][] data;

		List<SoftLog> toSplit = (!StringUtils.isEmpty(mDevice) && !mDevice.equals("All"))
				? getLogFile().getLogByDevice(mDevice)
				: getLogFile().getSoftLogs();

		if (mCurrentSplittingMod.equals(SplitMod.DAY)) {
			splitted = SoftLogExtractor.gatherLogsByDay(toSplit);
			headers = new String[] { "Days", "Logs" };
		} else {
			splitted = SoftLogExtractor.gatherLogsByHour(toSplit);
			headers = new String[] { "Hours", "Logs" };
		}

		data = new String[splitted.size()][2];

		List<String> keyList = new ArrayList<String>(splitted.keySet());
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			data[i] = new String[] { key, String.format("%d", splitted.get(key).size()) };
		}

		mRepartitionDetails.getViewport().add(new MyTable(data, headers));

		// Get top day
		List<Entry<String, List<SoftLog>>> topDays = SoftLogExtractor.sortDaysByLog(toSplit);
		Collections.reverse(topDays);
		String[] topDay = new String[] {
				String.format("%s: %d logs", topDays.get(0).getKey(),
						SoftLogExtractor.filterByDay(toSplit, topDays.get(0).getKey()).size()),
				String.format("%s: %d logs", topDays.get(1).getKey(),
						SoftLogExtractor.filterByDay(toSplit, topDays.get(1).getKey()).size()),
				String.format("%s: %d logs", topDays.get(2).getKey(),
						SoftLogExtractor.filterByDay(toSplit, topDays.get(2).getKey()).size()) };

		// Get top hour
		List<Entry<String, List<SoftLog>>> topHours = SoftLogExtractor.sortHoursByLog(toSplit);
		Collections.reverse(topHours);
		String[] topHour = new String[] {
				String.format("%s: %d logs", topHours.get(0).getKey(),
						SoftLogExtractor.filterByHour(toSplit, topHours.get(0).getKey()).size()),
				String.format("%s: %d logs", topHours.get(1).getKey(),
						SoftLogExtractor.filterByHour(toSplit, topHours.get(1).getKey()).size()),
				String.format("%s: %d logs", topHours.get(2).getKey(),
						SoftLogExtractor.filterByHour(toSplit, topHours.get(2).getKey()).size()) };

		mRepartitionTop.updateComponent(createRepartitionTops(topHour, topDay));
		mRepartitionDetails.validate();
	}

	@Override
	protected Component content() {

		JPanel content = new JPanel(new GridLayout(1, 2));

		JScrollPane leftScroll = new JScrollPane();
		List<Device> devices = getLogFile().getDevices();
		String[] headers = new String[] { "Device", "Type", "Location", "Logs" };
		String[][] data = new String[devices.size() + 1][headers.length];
		data[0] = new String[] { "All", "", "", String.format("%d", getLogFile().getSoftLogs().size()) };
		for (int i = 1; i < devices.size() + 1; i++) {
			Device device = devices.get(i - 1);
			data[i] = new String[] { device.getId(), device.getType().name(), device.getLocation(),
					String.format("%d", getLogFile().getLogByDevice(device.getId()).size()) };
		}

		MyTable tab = new MyTable(data, headers);
		tab.setEnabled(true);
		tab.getSelectionModel().addListSelectionListener(event -> {
			mDevice = tab.getValueAt(tab.getSelectedRow(), 0).toString();
			updateSplittedView();
		});

		leftScroll.getViewport().add(tab);
		content.add(leftScroll);

		// RIGHT TOP
		JPanel right = new JPanel(new BorderLayout());
		JComboBox<String> jCBRepartition = new JComboBox<>(new String[] { "Daily repartition", "Hourly repartition" });
		jCBRepartition.addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				mCurrentSplittingMod = event.getItem().toString().toLowerCase().contains("hour") ? SplitMod.HOUR
						: SplitMod.DAY;
				updateSplittedView();
			}
		});
		right.add(jCBRepartition, BorderLayout.PAGE_START);

		// RIGHT CENTER
		mRepartitionDetails = new JScrollPane();
		right.add(mRepartitionDetails, BorderLayout.CENTER);

		// RIGHT BOTTOM
		JPanel rightBottom = createRepartitionTops(new String[] { "-", "-", "-" }, new String[] { "-", "-", "-" });
		mRepartitionTop = new ComponentWithImage(Configuration.IMAGE_PODIUM, rightBottom);
		mRepartitionTop.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		right.add(mRepartitionTop, BorderLayout.PAGE_END);

		content.add(right);

		return content;
	}

	@Override
	protected TabHeader header() {
		return new TabHeader("Devices", "Please, enter a description for this tab ...", Configuration.IMAGE_SENSOR);
	}

}
