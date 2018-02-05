package ui.menus.tabs;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import beans.SoftLog;
import exceptions.RawLogException;
import operators.extractors.RawLogFormater;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.components.HistogramViewer;
import ui.components.InputValue;
import ui.components.MyButton;

public class HistogramTab extends JPanel implements ItemListener {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HistogramViewer
	 */
	private JPanel mJPanelChart;
	private JPanel mJPSettings;
	private JLabel mJLTotalLogs;

	private List<SoftLog> mLogs;

	private List<String> mSelectedDevice = new ArrayList<>();
	private Map<String, Checkbox> mCheckBoxes = new HashMap<>();

	public HistogramTab(File pLogFile) {
		setLayout(new GridBagLayout());

		try {
			this.mLogs = RawLogFormater.extractLogs(pLogFile);
		} catch (RawLogException ignored) {
		}

		init();
	}

	private void init() {
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 75;
		gc.weighty = 100;

		mJPanelChart = new JPanel();
		mJPanelChart.setBorder(BorderFactory.createTitledBorder("Histogramme"));
		add(mJPanelChart, gc);

		gc.gridy = 1;
		gc.weighty = 1;
		JLabel a = new JLabel("ght");
		a.setBorder(BorderFactory.createTitledBorder("ght"));
		a.setBackground(Color.RED);
		add(a, gc);

		mJPSettings = rightSettings();
		gc.gridx = 1;
		gc.gridy = 0;
		gc.weightx = 25;
		add(mJPSettings, gc);

		gc.gridy = 1;
		gc.gridx = 1;
		gc.weightx = 25;
		add(new MyButton("draw".toUpperCase(), event -> {
			draws();
		}), gc);
		validate();
		System.out.println("At constructor: " + this.getSize());
	}

	private JPanel rightSettings() {
		mJLTotalLogs = CustomComponent.boldLabel(new JLabel(String.format("%d", this.mLogs.size())));
		mJLTotalLogs.setBorder(BorderFactory.createTitledBorder("Logs"));

		JPanel jpan = new JPanel(new GridBagLayout());
		GridBagConstraints gc2 = new GridBagConstraints();

		gc2.fill = GridBagConstraints.BOTH;
		gc2.gridx = 0;
		gc2.weighty = 1;
		gc2.weightx = 1;

		gc2.gridy = 0;
		jpan.add(mJLTotalLogs, gc2);

		gc2.gridy = 1;
		jpan.add(new MyButton("Raz filters".toUpperCase(), event -> {
			resetFilters();
		}), gc2);

		gc2.gridy = 2;
		InputValue input = new InputValue();
		input.setBorder(BorderFactory.createTitledBorder("Threshold"));
		jpan.add(input, gc2);

		gc2.gridy = 3;
		JComboBox<String> datePicker = new JComboBox<String>();
		datePicker.setBorder(BorderFactory.createTitledBorder("Period"));
		jpan.add(datePicker, gc2);

		gc2.gridy = 4;
		JComboBox<String> splitPicker = new JComboBox<String>();
		splitPicker.setBorder(BorderFactory.createTitledBorder("Splitting"));
		jpan.add(splitPicker, gc2);

		gc2.gridy = 5;
		gc2.weighty = 75;
		JScrollPane scroll = new JScrollPane();
		JPanel jpanel = new JPanel(new GridBagLayout());
		GridBagConstraints gg = new GridBagConstraints();
		gg.fill = GridBagConstraints.BOTH;
		gg.gridx = 0;
		gg.weighty = 1;
		gg.weightx = 1;
		gg.gridy = 0;
		jpanel.setBackground(Color.WHITE);
		for (String deviceId : SoftLogExtractor.getDevices(mLogs).stream().map(device -> device.getId())
				.collect(Collectors.toList())) {
			Checkbox chBx = new Checkbox(deviceId, true);
			this.mSelectedDevice.add(deviceId);
			chBx.addItemListener(this);
			mCheckBoxes.put(deviceId, chBx);
			jpanel.add(chBx, gg);
			gg.gridy++;
		}
		scroll.getViewport().add(jpanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(BorderFactory.createTitledBorder("Devices"));
		jpan.add(scroll, gc2);
		return jpan;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			mSelectedDevice.add(e.getItem().toString());
			System.out.println("Select: " + e.getItem().toString());
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			mSelectedDevice.remove(e.getItem().toString());
			System.out.println("Unselect: " + e.getItem().toString());
		}
	}

	private void resetFilters() {
		this.mSelectedDevice.forEach(deviceId -> this.mCheckBoxes.get(deviceId).setState(false));
		this.mSelectedDevice.clear();
	}

	private void draws() {
		mJLTotalLogs.setText(this.mSelectedDevice.size() + " devices");
	}

}
