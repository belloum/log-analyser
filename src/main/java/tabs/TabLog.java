package tabs;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import ui.components.FileChooserWithResult.ChoiceListener;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;
import utils.DateFormater;

public class TabLog extends AbstractTab implements DeviceSelectorListener, ChoiceListener {

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
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE, "4020");
		System.out.println(LogExtractor.getRequests("2017.12.*", mParticipant.getVera(), true));

		// Browse RawLog file
		mFCLog = new FileChooserWithResult(Configuration.RAW_LOG_FILE, this, MAX_WIDTH);
		add(mFCLog);

		// Extracted Log Info
		JPanel jPan = new JPanel(new BorderLayout());
		mDSCDeviceInfo = new DataSetComponent(MAX_WIDTH);
		mDSCDeviceInfo.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		mDSCDeviceInfo.setLabels(Arrays.asList("Logs", "Devices", "Types", "Days", "Months"));
		jPan.add(mDSCDeviceInfo, BorderLayout.WEST);
		add(jPan);
		jPan.setMaximumSize(new Dimension(MAX_WIDTH, jPan.getPreferredSize().height));

		// Right Panel
		JPanel pright = new JPanel(new BorderLayout());
		pright.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		jPan.add(pright, BorderLayout.EAST);

		mDateFilter = new DateFiler();
		pright.add(mDateFilter, BorderLayout.PAGE_START);

		mDeviceSelector = new DeviceSelector();
		pright.add(mDeviceSelector, BorderLayout.CENTER);

		pright.setMaximumSize(new Dimension(MAX_WIDTH * 75 / 100, pright.getPreferredSize().height));
		pright.setPreferredSize(pright.getMaximumSize());
		pright.setMinimumSize(pright.getMaximumSize());

		JPanel pBarButton = new JPanel();
		pBarButton.setLayout(new BoxLayout(pBarButton, BoxLayout.X_AXIS));
		add(pBarButton);

		JLabel watt = new JLabel("Electric threshold");
		CustomComponent.setJLabelFontStyle(watt, Font.BOLD);
		pBarButton.add(watt);
		watt.setMaximumSize(new Dimension(MAX_WIDTH * 15 / 100, Configuration.ITEM_HEIGHT));

		mFTFElectricThreshold = new JFormattedTextField(new DecimalFormat("#.#"));
		mFTFElectricThreshold.setValue(DEFAULT_THRESHOLD);
		mFTFElectricThreshold.setHorizontalAlignment(SwingConstants.CENTER);
		mFTFElectricThreshold.setMaximumSize(new Dimension(MAX_WIDTH * 10 / 100, Configuration.ITEM_HEIGHT));
		pBarButton.add(mFTFElectricThreshold);

		mButtonBar = new ButtonBar();
		mButtonBar.setMaximumSize(new Dimension(MAX_WIDTH * 60 / 100, Configuration.ITEM_HEIGHT));

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

		pBarButton.add(mButtonBar);

		mFTFRemainingLogs = new JFormattedTextField();
		mFTFRemainingLogs.setEditable(false);
		mFTFRemainingLogs.setHorizontalAlignment(SwingConstants.CENTER);
		mFTFRemainingLogs.setMaximumSize(new Dimension(MAX_WIDTH * 15 / 100, Configuration.ITEM_HEIGHT));

		pBarButton.add(mFTFRemainingLogs);

		pBarButton.setMaximumSize(new Dimension(MAX_WIDTH, pBarButton.getPreferredSize().height));
		pBarButton.setPreferredSize(pBarButton.getMaximumSize());

		JPanel jP = new JPanel(new BorderLayout());
		jP.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		mRLCleaning = new ResultLabel();
		jP.add(mRLCleaning, BorderLayout.LINE_START);
		add(jP);
		jP.setPreferredSize(new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));
		jP.setMaximumSize(jP.getPreferredSize());

		resetLogData();
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

		List<Device> pDevices = SoftLogExtractor.getDevices(mCleanedLogs);
		mDeviceSelector.setCheckBoxDevices(pDevices);
		mCurrentDeviceSelected.addAll(pDevices);
		notifyDataChanged();
		mDeviceSelector.setVisible(true);
		mDeviceSelector.setListener(this);

		mButtonBar.setEnabled(true);
	}

	private List<SoftLog> extractCleanedSoftLogList() throws Exception {
		List<SoftLog> logs = RawLogFormater.extractLogs(this.mSelectedFile);
		List<String> ids = mCurrentDeviceSelected.stream().map(Device::getId).collect(Collectors.toList());
		if (mDateFilter.getSelectedPosition() > 0) {
			return SoftLogExtractor.cleanUpLogs(logs, ids, Float.parseFloat(mFTFElectricThreshold.getText()),
					DateFormater.getTimestampLimitsOfDay(mDateFilter.getSelectedItem()));
		} else {
			return SoftLogExtractor.cleanUpLogs(logs, ids, Float.parseFloat(mFTFElectricThreshold.getText()));
		}
	}

	private void resetLogData() {
		mCleanedLogs.clear();
		mFTFRemainingLogs.setText(mCleanedLogs.size() + " logs");
		mCurrentDeviceSelected.clear();
		mDSCDeviceInfo.resetViews();
		mFTFElectricThreshold.setEditable(false);
		mDeviceSelector.resetDevicesList();
		mRLCleaning.setVisible(false);
		mButtonBar.setEnabled(false);
		mDateFilter.setVisible(false);
		notifyDataChanged();
	}

	@Override
	public void select(Device pDevice) {
		if (!mCurrentDeviceSelected.contains(pDevice)) {
			mCurrentDeviceSelected.add(pDevice);
			notifyDataChanged();
		}
	}

	@Override
	public void unselect(Device pDevice) {
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
