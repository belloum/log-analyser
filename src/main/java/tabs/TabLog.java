package tabs;

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
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
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
import ui.components.DateFiler;
import ui.components.DeviceSelector;
import ui.components.DeviceSelector.DeviceSelectorListener;
import ui.components.FileChooserWithResult;
import ui.components.FileChooserWithResult.FileChooserListener;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;
import utils.DateFormater;

public class TabLog extends AbstractTab implements DeviceSelectorListener, FileChooserListener {

	private static final long serialVersionUID = 1L;

	private static final int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;
	private static final String LOG_SUCCESSFULY_EXTRACTED = "Logs have been extracted.";
	private static final String LOG_SUCCESSFULY_CLEANED = "Logs have been cleaned.";
	private static final String FILE_DOES_NOT_EXIST = "The selected file `%s` does not exist.";
	private static final int DEFAULT_THRESHOLD = 0;

	private FileChooserWithResult mFCLog;
	private DateFiler mDateFilter;
	private DeviceSelector mDeviceSelector;
	private DataSetComponent mDSCDeviceInfo;
	private ButtonBar mButtonBar;
	private JFormattedTextField mFTFElectricThreshold, mFTFRemainingLogs;
	private ResultLabel mRLCleaning;

	private Participant mParticipant;
	private File mSelectedFile;
	private List<SoftLog> mCleanedLogs = new ArrayList<>();
	private List<Device> mCurrentDeviceSelected = new ArrayList<>();

	public TabLog(String title) throws Exception {
		super(title);

		mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE, "4020");
		System.out.println(LogExtractor.getRequests("2017.12.*", mParticipant.getVera(), true));

		mFCLog = new FileChooserWithResult("Log file:", Configuration.RAW_LOG_FILE, this);

		addComponent(mFCLog, new Dimension(MAX_WIDTH, mFCLog.getPreferredSize().height));

		xOffset = Configuration.MARGINS;
		yOffset += 2 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);

		mDSCDeviceInfo = new DataSetComponent();
		mDSCDeviceInfo.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		addComponent(mDSCDeviceInfo, new Dimension((int) (0.2 * MAX_WIDTH), 5 * Configuration.ITEM_HEIGHT));
		mDSCDeviceInfo.setLabels(Arrays.asList("Logs", "Devices", "Types", "Days", "Months"));

		mDeviceSelector = new DeviceSelector();
		mDeviceSelector.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		addComponent(mDeviceSelector, new Dimension((int) (0.8 * MAX_WIDTH), 3 * Configuration.ITEM_HEIGHT));

		xOffset = mDeviceSelector.getX();
		yOffset += 3 * Configuration.ITEM_HEIGHT;

		mDateFilter = new DateFiler();
		mDateFilter.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		addComponent(mDateFilter, new Dimension((int) (0.8 * MAX_WIDTH), 2 * Configuration.ITEM_HEIGHT));

		xOffset = Configuration.MARGINS;
		yOffset += 2 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);

		JLabel watt = new JLabel("Electric threshold");
		CustomComponent.setJLabelFontStyle(watt, Font.BOLD);
		addComponent(watt, new Dimension((int) (0.15 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		mFTFElectricThreshold = new JFormattedTextField(new DecimalFormat("#.#"));
		mFTFElectricThreshold.setValue(DEFAULT_THRESHOLD);
		mFTFElectricThreshold.setHorizontalAlignment(SwingConstants.CENTER);
		addComponent(mFTFElectricThreshold, new Dimension((int) (0.1 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		mButtonBar = new ButtonBar();
		addComponent(mButtonBar, new Dimension((int) (0.6 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		JButton jB = new JButton("Clean logs");
		jB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mCleanedLogs = extractCleanedSoftLogList();
					mRLCleaning.printResult(LOG_SUCCESSFULY_CLEANED, ResultType.SUCCESS);
					mFTFRemainingLogs.setText(mCleanedLogs.size() + " logs");
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
				JFileChooser jfc = new JFileChooser(Configuration.RESOURCES_FOLDER) {
					private static final long serialVersionUID = 1L;

					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						if (f.exists() && getDialogType() == SAVE_DIALOG) {
							int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?",
									"Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
							switch (result) {
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
							}
						}
						super.approveSelection();
					}
				};
				jfc.setDialogTitle("Save SoftLog file");

				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File output = jfc.getSelectedFile();
					try {
						if (!output.getName().contains(".json")) {
							output = new File(output.getParent(), String.format("%s.json", output.getName()));
						}
						SoftLogExtractor.saveLogList(extractCleanedSoftLogList(), output);
						mRLCleaning.printResult(
								new StringBuffer(output.getPath()).append(" has been saved.").toString(),
								ResultType.SUCCESS);
					} catch (Exception exception) {
						System.out.println("Unable to save file: " + exception);
						mRLCleaning.printResult(exception.getMessage(), ResultType.ERROR);
					}
				}
			}
		});
		mButtonBar.addButton(jB, false);

		mFTFRemainingLogs = new JFormattedTextField();
		mFTFRemainingLogs.setEditable(false);
		mFTFRemainingLogs.setHorizontalAlignment(SwingConstants.CENTER);
		addComponent(mFTFRemainingLogs, new Dimension((int) (0.15 * MAX_WIDTH), Configuration.ITEM_HEIGHT));

		xOffset = Configuration.MARGINS;
		yOffset += 1 * (Configuration.ITEM_HEIGHT + Configuration.PADDING);

		mRLCleaning = new ResultLabel();
		addComponent(mRLCleaning, new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));
		resetLogData();
		super.fillBlank(xOffset, yOffset + Configuration.ITEM_HEIGHT);
	}

	private void fillLogInfos(File pSelectedFile) throws Exception {
		mCleanedLogs = RawLogFormater.extractLogs(pSelectedFile);

		LinkedHashMap<String, Object> infos = new LinkedHashMap<String, Object>();
		infos.put("Logs", mCleanedLogs.size());
		infos.put("Devices", SoftLogExtractor.getDeviceCount(mCleanedLogs));
		infos.put("Types", SoftLogExtractor.getDeviceTypeCount(mCleanedLogs));
		infos.put("Days", SoftLogExtractor.getDays(mCleanedLogs).size());
		infos.put("Months", SoftLogExtractor.getMonths(mCleanedLogs).size());

		mFCLog.getResultLabel().printResult(LOG_SUCCESSFULY_EXTRACTED, ResultType.SUCCESS);
		mDSCDeviceInfo.setProperties(infos);
		mFTFRemainingLogs.setText(mCleanedLogs.size() + " logs");

		mDateFilter.setVisible(true);
		mDateFilter.setDates(SoftLogExtractor.getDays(mCleanedLogs));

		List<Device> pDevices = SoftLogExtractor.getDeviceIds(mCleanedLogs);
		mDeviceSelector.setCheckBoxDevices(pDevices);
		mCurrentDeviceSelected.addAll(pDevices);
		notifyDataChanged();
		mDeviceSelector.setVisible(true);
		mDeviceSelector.setListener(this);
		mDeviceSelector.setSize(new Dimension(mDeviceSelector.getWidth(), 3 * Configuration.ITEM_HEIGHT));

		mButtonBar.setEnabled(true);
	}

	private List<SoftLog> extractCleanedSoftLogList() throws Exception {
		List<SoftLog> logs = RawLogFormater.extractLogs(this.mSelectedFile);
		List<String> types = mCurrentDeviceSelected.stream().map(Device::getId).collect(Collectors.toList());
		if (mDateFilter.getSelectedPosition() > 0) {
			return SoftLogExtractor.cleanUpLogs(logs, types, Float.parseFloat(mFTFElectricThreshold.getText()),
					DateFormater.getTimestampLimitsOfDay(mDateFilter.getSelectedItem()));
		} else {
			return SoftLogExtractor.cleanUpLogs(logs, types, Float.parseFloat(mFTFElectricThreshold.getText()));
		}
	}

	private void resetLogData() {
		mCleanedLogs.clear();
		mFTFRemainingLogs.setText(mCleanedLogs.size() + " logs");
		mCurrentDeviceSelected.clear();
		mDSCDeviceInfo.resetViews();
		mFTFElectricThreshold.setEditable(false);
		mDeviceSelector.clearDevicesList();
		mRLCleaning.setVisible(false);
		mButtonBar.setEnabled(false);
		mDateFilter.setVisible(false);
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

	@Override
	public void fileDoesNotExist(File pFile) {
		resetLogData();
		String msg = String.format(FILE_DOES_NOT_EXIST, pFile.getPath());
		mFCLog.getResultLabel().printResult(msg, ResultType.ERROR);
	}

	@Override
	public void fileExists(File pFile) {
		resetLogData();
		try {
			RawLogFormater.extractLogs(pFile);
			mSelectedFile = pFile;
			fillLogInfos(pFile);
		} catch (Exception exception) {
			System.out.println("Invalid file: " + exception.getMessage());
			mFCLog.getResultLabel().printResult(exception.getMessage(), ResultType.ERROR);
		}
	}
}
