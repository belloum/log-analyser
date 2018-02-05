package tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import beans.devices.Device;
import beans.devices.Device.DeviceType;
import operators.extractors.RawLogFormater;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.components.DeviceSelector;
import ui.components.DeviceSelector.DeviceSelectorListener;
import ui.components.FileChooserWithResult;
import ui.components.FileChooserWithResult.ChoiceListener;
import ui.components.InputValue;
import ui.components.MyButton;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class TabRoutine extends AbstractTab implements ChoiceListener {

	private static final long serialVersionUID = 1L;
	private static final int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;

	private ResultLabel mRLabel;
	private JPanel mPanelActivity;
	private File mLogFile;
	private FileChooserWithResult mFCLog;
	private List<Device> mDevices;
	private DeviceSelector mDeviceSelectorI, mDeviceSelectorII;
	private List<Device> mMarkerI = new ArrayList<>(), mMarkerII = new ArrayList<>();
	private InputValue mIVStartHour, mIVEndHour;
	private String mCurrentActivity;
	private DeviceSelectorListener mMarkerIListener = new DeviceSelectorListener() {

		@Override
		public void unselect(Device pDevice) {
			mDeviceSelectorII.setEnabled(pDevice.getId(), true);
			mMarkerI.remove(pDevice);
		}

		@Override
		public void select(Device pDevice) {
			mDeviceSelectorII.setEnabled(pDevice.getId(), false);
			mMarkerI.add(pDevice);
		}
	}, mMarkerIIListener = new DeviceSelectorListener() {

		@Override
		public void unselect(Device pDevice) {
			mDeviceSelectorI.setEnabled(pDevice.getId(), true);
			mMarkerII.remove(pDevice);
		}

		@Override
		public void select(Device pDevice) {
			mDeviceSelectorI.setEnabled(pDevice.getId(), false);
			mMarkerII.add(pDevice);
		}
	};

	public TabRoutine(String title) throws Exception {
		super(title);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JPanel selection = new JPanel();
		selection.setLayout(new BoxLayout(selection, BoxLayout.X_AXIS));

		mFCLog = new FileChooserWithResult(Configuration.RAW_LOG_FILE, this, MAX_WIDTH / 2);
		mFCLog.setPreferredSize(new Dimension(MAX_WIDTH / 2, Configuration.ITEM_HEIGHT));
		mFCLog.setMaximumSize(mFCLog.getPreferredSize());
		selection.add(mFCLog);

		JComboBox<String> mActivityPicker = new JComboBox<String>();
		selection.setAlignmentX(LEFT_ALIGNMENT);
		mActivityPicker.setPreferredSize(new Dimension(MAX_WIDTH / 2, Configuration.ITEM_HEIGHT));
		mActivityPicker.setMaximumSize(mActivityPicker.getPreferredSize());

		mActivityPicker.addItem("Select a daily activity");
		mActivityPicker.addItem("Breakfast");
		mActivityPicker.addItem("Lunch");
		mActivityPicker.addItem("Dinner");

		mActivityPicker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					displayActivityPanel(e.getItem().toString());
				}
			}
		});

		selection.add(mActivityPicker);
		add(selection);

		mPanelActivity = new JPanel();

		mPanelActivity.setMaximumSize(new Dimension(MAX_WIDTH, 12 * Configuration.ITEM_HEIGHT));
		mPanelActivity.setPreferredSize(mPanelActivity.getMaximumSize());
		mPanelActivity.setAlignmentX(LEFT_ALIGNMENT);
		add(mPanelActivity);

		add(new MyButton("Check scripts", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					Process proc;

					if (mCurrentActivity.equals("Breakfast")) {
						StringBuffer strB = new StringBuffer("perl ");
						strB.append(new File(Configuration.SCRIPTS_FOLDER, "meal.pl ").getPath());
						strB.append("-mBreakfast");
						strB.append(" -b").append(mIVStartHour.getText());
						strB.append(" -e").append(mIVEndHour.getText());
						strB.append(" -1").append(String.format("'%s'", StringUtils
								.join(mMarkerI.stream().map(Device::getId).collect(Collectors.toList()), "|")));
						strB.append(" -2").append(String.format("'%s'", StringUtils
								.join(mMarkerII.stream().map(Device::getId).collect(Collectors.toList()), "|")));
						strB.append(" " + new File(Configuration.RESOURCES_FOLDER, mLogFile.getName()));
						strB.append(new File(Configuration.RESOURCES_FOLDER, mLogFile.getName()));
						String cmd = strB.toString();
						System.out.println(cmd);

						ProcessBuilder pb = new ProcessBuilder("./meal.pl",
								String.format("-b%s", mIVStartHour.getText()),
								String.format("-e%s", mIVEndHour.getText()), String.format("-m%s", mCurrentActivity),
								String.format("-1%s", StringUtils
										.join(mMarkerI.stream().map(Device::getId).collect(Collectors.toList()), "|")),
								String.format("-2%s", StringUtils
										.join(mMarkerII.stream().map(Device::getId).collect(Collectors.toList()), "|")),
								"../justatest.json");

						pb.directory(Configuration.SCRIPTS_FOLDER);
						System.out.println(pb.command());
						Process process = pb.start();
						int errCode = process.waitFor();
						String result = output(process.getInputStream());
						System.out.println("Scores:\n" + result);
						process.destroy();

						mRLabel.printResult(result, ResultType.SUCCESS);

					} else {
						mRLabel.printResult("Not allowed !", ResultType.ERROR);
					}

				} catch (IOException | InterruptedException e1) {
					mRLabel.printResult("Exception: " + e1, ResultType.ERROR);
					System.out.println(e1);
				}
			}
		}));
		mRLabel = new ResultLabel("");
		mRLabel.setAlignmentX(LEFT_ALIGNMENT);
		add(mRLabel);
	}

	private void displayActivityPanel(String pActivity) {
		mCurrentActivity = pActivity;
		mPanelActivity.removeAll();
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		List<DeviceType> types;
		if (pActivity.equals("Breakfast")) {
			types = Arrays.asList(DeviceType.ContactSensor, DeviceType.ElectricMeter);
			List<Device> devices = new ArrayList<>();

			devices.addAll(this.mDevices.stream().filter(device -> types.contains(device.getType()))
					.collect(Collectors.toList()));

			JPanel hours = new JPanel();
			hours.setLayout(new BoxLayout(hours, BoxLayout.X_AXIS));
			hours.setAlignmentX(LEFT_ALIGNMENT);

			mIVStartHour = new InputValue("00:00");
			mIVStartHour.setMaximumSize(new Dimension(Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT));
			mIVStartHour.setPreferredSize(mIVStartHour.getMaximumSize());
			hours.add(mIVStartHour);

			mIVEndHour = new InputValue("00:00");
			mIVEndHour.setMaximumSize(new Dimension(Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT));
			mIVEndHour.setPreferredSize(mIVEndHour.getMaximumSize());
			hours.add(mIVEndHour);
			hours.setMaximumSize(new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));
			hours.setPreferredSize(hours.getMaximumSize());
			p.add(hours);

			JPanel primaryPanel = new JPanel();
			primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.X_AXIS));
			primaryPanel.setAlignmentX(LEFT_ALIGNMENT);
			JLabel primary = new JLabel("Primary markers");
			CustomComponent.setJLabelFontStyle(primary, Font.BOLD);
			primary.setMaximumSize(new Dimension(Configuration.LABEL_WIDTH_MEDIUM, primary.getPreferredSize().height));
			primary.setPreferredSize(primary.getMaximumSize());
			primary.setMinimumSize(primary.getMaximumSize());
			primary.setAlignmentX(LEFT_ALIGNMENT);
			primaryPanel.add(primary);
			mDeviceSelectorI = new DeviceSelector(new Dimension(mPanelActivity.getMaximumSize().width, 0), devices,
					mMarkerIListener);
			mDeviceSelectorI.setMaxItemByLine(3);
			primaryPanel.add(mDeviceSelectorI);
			p.add(primaryPanel);

			JPanel secondaryPanel = new JPanel();
			secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.X_AXIS));
			secondaryPanel.setAlignmentX(LEFT_ALIGNMENT);
			JLabel secondary = new JLabel("Secondary markers");
			CustomComponent.setJLabelFontStyle(secondary, Font.BOLD);
			secondary.setMaximumSize(
					new Dimension(Configuration.LABEL_WIDTH_MEDIUM, secondary.getPreferredSize().height));
			secondary.setPreferredSize(secondary.getMaximumSize());
			secondary.setMinimumSize(secondary.getMaximumSize());
			secondary.setAlignmentX(LEFT_ALIGNMENT);
			secondaryPanel.add(secondary);
			mDeviceSelectorII = new DeviceSelector(new Dimension(mPanelActivity.getMaximumSize().width, 0), devices,
					mMarkerIIListener);
			mDeviceSelectorII.setMaxItemByLine(3);
			secondaryPanel.add(mDeviceSelectorII);
			p.add(secondaryPanel);

			mDeviceSelectorI.setSelected(false);
			mDeviceSelectorII.setSelected(false);

		} else {
			p.add(new JLabel("Not implemented yet !"), BorderLayout.PAGE_START);
		}
		mPanelActivity.add(p);
		p.setMaximumSize(p.getParent().getMaximumSize());
		p.setPreferredSize(p.getMaximumSize());
		mPanelActivity.setVisible(false);
		mPanelActivity.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		mPanelActivity.setVisible(true);
	}

	@Override
	public void fileDoesNotExist(File pFile) {

	}

	@Override
	public void fileExists(File pFile) {
		try {
			mDevices = SoftLogExtractor.getDevices(RawLogFormater.extractLogs(pFile));
			SoftLogExtractor.saveLogList(SoftLogExtractor.sortLogsByDate(RawLogFormater.extractLogs(pFile)),
					new File(Configuration.RESOURCES_FOLDER, "justatest.json"));
			mLogFile = new File(Configuration.RESOURCES_FOLDER, "justatest.json");
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

}
