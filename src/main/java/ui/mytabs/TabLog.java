package ui.mytabs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import beans.SoftLog;
import beans.devices.Device;
import beans.devices.Device.DeviceType;
import beans.participants.Participant;
import operators.extractors.LogExtractor;
import operators.extractors.ParticipantExtractor;
import operators.extractors.RawLogFormater;
import operators.extractors.SoftLogExtractor;
import ui.components.ButtonBar;
import ui.components.CustomComponent;
import ui.components.DataSetComponent;
import ui.components.DeviceSelector;
import ui.components.DeviceSelector.DeviceSelectorListener;
import ui.components.FileChooserWithResult;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class TabLog extends AbstractTab implements DeviceSelectorListener {

	private static final long serialVersionUID = 1L;

	private static final int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;
	private static final String LOG_SUCCESSFULY_EXTRACTED = "Logs have been extracted.";

	private FileChooserWithResult mFCLog;
	private DeviceSelector mDeviceSelector;
	private DataSetComponent mDSCLogInfo;
	private ButtonBar mButtonBar;
	private JFormattedTextField mFTFElectricThreshold;
	private ResultLabel mRLCleaning;

	private Participant mParticipant;
	private File mSelectedFile;
	private List<SoftLog> mCleanedLogs = new ArrayList<>();
	private List<Device> mCurrentDeviceSelected = new ArrayList<>();
	// TODO ask for name
	private static final File mOutputFile = new File(Configuration.RESOURCES_FOLDER, "testo.json");

	public TabLog(String title) throws Exception {
		super(title);

		mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE, "4020");
		System.out.println(LogExtractor.getRequests("2017.12.*", mParticipant.getVera(), true));

		mFCLog = new FileChooserWithResult("Log file:", Configuration.RAW_LOG_FILE);
		addComponent(mFCLog, new Dimension(MAX_WIDTH, mFCLog.getPreferredSize().height));

		mFCLog.setActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mFCLog.getJFileChooser().showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					File selectedFile = mFCLog.getJFileChooser().getSelectedFile();
					mFCLog.setSelected(selectedFile);
					System.out.println("Selected file: " + selectedFile.getName());
					resetLogData();

					try {
						mSelectedFile = selectedFile;
						RawLogFormater.extractLogs(selectedFile);
						fillLogInfos(selectedFile);
					} catch (Exception exception) {
						System.out.println("Invalid file: " + exception.getMessage());
						mFCLog.getResultLabel().printResult(exception.getMessage(), ResultType.ERROR);
					}
				}
			}
		});

		xOffset = Configuration.MARGINS;
		yOffset += 2 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);

		mDSCLogInfo = new DataSetComponent();
		mDSCLogInfo.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		addComponent(mDSCLogInfo, new Dimension((int) (0.2 * MAX_WIDTH), 3 * Configuration.ITEM_HEIGHT));
		mDSCLogInfo.setLabels(Arrays.asList("Logs", "Days", "Devices"));

		mDeviceSelector = new DeviceSelector();
		mDeviceSelector.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		addComponent(mDeviceSelector, new Dimension((int) (0.8 * MAX_WIDTH), 3 * Configuration.ITEM_HEIGHT));

		xOffset = Configuration.MARGINS;
		yOffset += 3 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);

		JLabel watt = new JLabel("Electrical threshold");
		CustomComponent.setJLabelFontStyle(watt, Font.BOLD);
		addComponent(watt, new Dimension((int) (0.2 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		mFTFElectricThreshold = new JFormattedTextField(new DecimalFormat("#.#"));
		// TODO static value
		mFTFElectricThreshold.setValue(10);
		mFTFElectricThreshold.setEditable(false);
		addComponent(mFTFElectricThreshold, new Dimension((int) (0.1 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		mButtonBar = new ButtonBar();
		addComponent(mButtonBar, new Dimension((int) (0.7 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		JButton jB = new JButton("Clean logs");
		jB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mCleanedLogs = extractCleanedSoftLogList();
					int removedLogs = RawLogFormater.extractLogs(mSelectedFile).size() - mCleanedLogs.size();
					mRLCleaning.printResult(String.format("Logs have been cleaned (%d logs removed).", removedLogs),
							ResultType.SUCCESS);
					System.out.println(mCleanedLogs.size());
				} catch (Exception exception) {
					mRLCleaning.printResult(exception.getMessage(), ResultType.ERROR);
					System.out.println("Exception while cleaning: " + exception);
				}
			}
		});
		mButtonBar.addButton(jB, false);

		jB = new JButton("Save SoftLog file");
		jB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SoftLogExtractor.saveLogList(extractCleanedSoftLogList(), mOutputFile);
					mRLCleaning.printResult(
							new StringBuffer(mOutputFile.getPath()).append(" has been saved.").toString(),
							ResultType.SUCCESS);
					System.out.println("File has been saved");
				} catch (Exception exception) {
					System.out.println("Unable to save file: " + exception);
					mRLCleaning.printResult(exception.getMessage(), ResultType.ERROR);
				}
			}
		});
		mButtonBar.addButton(jB, false);

		yOffset += 1 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);
		xOffset = Configuration.MARGINS;

		mRLCleaning = new ResultLabel();
		addComponent(mRLCleaning, new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));

		super.fillBlank(xOffset, yOffset + Configuration.ITEM_HEIGHT);
	}

	private void fillLogInfos(File pSelectedFile) throws Exception {
		mCleanedLogs = RawLogFormater.extractLogs(pSelectedFile);
		LinkedHashMap<String, Object> infos = new LinkedHashMap<String, Object>();
		infos.put("Logs", mCleanedLogs.size());
		infos.put("Days", SoftLogExtractor.getDays(mCleanedLogs).size());
		infos.put("Devices", SoftLogExtractor.getDeviceCount(mCleanedLogs));
		mFCLog.getResultLabel().printResult(LOG_SUCCESSFULY_EXTRACTED, ResultType.SUCCESS);
		mDSCLogInfo.setProperties(infos);

		List<Device> pDevices = SoftLogExtractor.getDeviceIds(mCleanedLogs);
		mDeviceSelector.setCheckBoxDevices(pDevices);
		mCurrentDeviceSelected.addAll(pDevices);
		notifyDataChanged();
		mDeviceSelector.setVisible(true);
		mDeviceSelector.setListener(this);
		mDeviceSelector.setSize(new Dimension(mDeviceSelector.getWidth(), 3 * Configuration.ITEM_HEIGHT));

		mButtonBar.getButton(1).setEnabled(true);
		mButtonBar.getButton(2).setEnabled(true);
	}

	private List<SoftLog> extractCleanedSoftLogList() throws Exception {
		List<SoftLog> logs = RawLogFormater.extractLogs(this.mSelectedFile);
		List<String> types = mCurrentDeviceSelected.stream().map(Device::getId).collect(Collectors.toList());
		return SoftLogExtractor.cleanUpLogs(logs, types, Float.parseFloat(mFTFElectricThreshold.getText()));
	}

	private void resetLogData() {
		mCleanedLogs.clear();
		mCurrentDeviceSelected.clear();
		mDSCLogInfo.resetViews();
		mFTFElectricThreshold.setEditable(false);
		mDeviceSelector.clearDevicesList();
		mRLCleaning.setVisible(false);
		notifyDataChanged();
	}

	/*
	 * DeviceSelectorListener
	 */
	@Override
	public void select(Device pDevice) {
		if (!mCurrentDeviceSelected.contains(pDevice)) {
			mCurrentDeviceSelected.add(pDevice);
			notifyDataChanged();
		}
	}

	@Override
	public void unselect(final Device pDevice) {
		if (mCurrentDeviceSelected.contains(pDevice)) {
			mCurrentDeviceSelected.remove(pDevice);
			notifyDataChanged();
		}
	}

	@Override
	public void notifyDataChanged() {
		mFTFElectricThreshold.setEditable(
				mCurrentDeviceSelected.stream().anyMatch(device -> device.getType().equals(DeviceType.ElectricMeter)));
	}

}
