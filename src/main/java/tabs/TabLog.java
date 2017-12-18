package tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import beans.SoftLog;
import beans.TSLimits;
import beans.devices.Device;
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
import ui.components.FileChooser;
import ui.components.FileChooserWithResult;
import ui.components.FileChooserWithResult.ChoiceListener;
import ui.components.HistogramViewer;
import ui.components.HistogramViewer.HistogramListener;
import ui.components.InputValue;
import ui.components.MyButton;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class TabLog extends AbstractTab implements DeviceSelectorListener, ChoiceListener, HistogramListener {

	private static final long serialVersionUID = 1L;

	private static final int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;
	private static final String LOG_SUCCESSFULY_EXTRACTED = "Logs have been extracted.";
	private static final String LOG_SUCCESSFULY_CLEANED = "Logs have been cleaned.";
	private static final String FILE_DOES_NOT_EXIST = "The selected file `%s` does not exist.";
	private static final int DEFAULT_THRESHOLD = 0;

	private FileChooserWithResult mFCLog = new FileChooserWithResult(Configuration.RAW_LOG_FILE, this, MAX_WIDTH);
	private DateFiler mDateFilter;
	private DataSetComponent mDSCDeviceInfo = new DataSetComponent(20 * MAX_WIDTH / 100,
			Arrays.asList("Logs", "Devices", "Types", "Days", "Months"));
	private ButtonBar mButtonBar;
	private JFormattedTextField mFTFRemainingLogs;
	private InputValue mFTFElectricThreshold = new InputValue("##");
	private JScrollPane mScroll;
	private ResultLabel mRLCleaning;
	private HistogramViewer mHistogramPanel;

	private FileChooser mFChooser = new FileChooser(Configuration.RESOURCES_FOLDER);

	private Participant mParticipant;
	private File mSelectedFile;
	private List<Device> mCurrentDeviceSelected = new ArrayList<>();

	public TabLog(String title) throws Exception {
		super(title);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE, "4010");
		System.out.println(LogExtractor.getRequests("2017.12.*", mParticipant.getVera(), true));

		// Browse RawLog file
		add(mFCLog);

		// Extracted Log Info
		JPanel jPan = new JPanel(new BorderLayout());
		mDSCDeviceInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jPan.add(mDSCDeviceInfo, BorderLayout.WEST);
		add(jPan);
		jPan.setMaximumSize(new Dimension(MAX_WIDTH, 7 * Configuration.ITEM_HEIGHT));

		// Right Panel
		JPanel pright = new JPanel(new BorderLayout());
		jPan.add(pright, BorderLayout.EAST);

		mDateFilter = new DateFiler(new Dimension(MAX_WIDTH * 75 / 100, pright.getPreferredSize().height));
		mDateFilter.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		pright.add(mDateFilter, BorderLayout.PAGE_START);

		mScroll = new JScrollPane(new JPanel());
		pright.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		pright.add(mScroll, BorderLayout.CENTER);

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

		mFTFElectricThreshold.setValue(DEFAULT_THRESHOLD);
		mFTFElectricThreshold.setMaximumSize(new Dimension(MAX_WIDTH * 10 / 100, Configuration.ITEM_HEIGHT));
		pBarButton.add(mFTFElectricThreshold);

		mButtonBar = new ButtonBar(new Dimension(MAX_WIDTH * 60 / 100, Configuration.ITEM_HEIGHT),
				Arrays.asList(new MyButton("Clean logs", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							List<SoftLog> logs = extractCleanedSoftLogList();
							mRLCleaning.printResult(LOG_SUCCESSFULY_CLEANED, ResultType.SUCCESS);
							mFTFRemainingLogs.setText(logs.size() + " logs");
						} catch (Exception exception) {
							mRLCleaning.printResult(exception.getMessage(), ResultType.ERROR);
							System.out.println("Exception while cleaning: " + exception);
						}
					}
				}), new MyButton("Save SoftLog file", new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						mFChooser.setDialogTitle("Save SoftLog file");

						if (mFChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
							File output = mFChooser.getSelectedFile();
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
				})));

		pBarButton.add(mButtonBar);

		mFTFRemainingLogs = new JFormattedTextField();
		mFTFRemainingLogs.setEditable(false);
		mFTFRemainingLogs.setHorizontalAlignment(SwingConstants.CENTER);
		mFTFRemainingLogs.setMaximumSize(new Dimension(MAX_WIDTH * 15 / 100, Configuration.ITEM_HEIGHT));

		pBarButton.add(mFTFRemainingLogs);

		pBarButton.setMaximumSize(new Dimension(MAX_WIDTH, pBarButton.getPreferredSize().height));
		pBarButton.setPreferredSize(pBarButton.getMaximumSize());

		JPanel jP = new JPanel(new BorderLayout());
		mRLCleaning = new ResultLabel();
		jP.add(mRLCleaning, BorderLayout.LINE_START);
		add(jP);
		jP.setPreferredSize(new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));
		jP.setMaximumSize(jP.getPreferredSize());

		// HISTO
		mHistogramPanel = new HistogramViewer(this, MAX_WIDTH, 12 * Configuration.ITEM_HEIGHT);
		add(mHistogramPanel);

		resetLogData();
	}

	private void fillLogInfos(File pSelectedFile) throws Exception {
		List<SoftLog> cleanLogs = RawLogFormater.extractLogs(pSelectedFile);

		LinkedHashMap<String, Object> infos = new LinkedHashMap<String, Object>();
		infos.put("Logs", cleanLogs.size());
		infos.put("Devices", SoftLogExtractor.getDeviceCount(cleanLogs));
		infos.put("Types", SoftLogExtractor.getDeviceTypeCount(cleanLogs));
		infos.put("Days", SoftLogExtractor.getDays(cleanLogs).size());
		infos.put("Months", SoftLogExtractor.getMonths(cleanLogs).size());

		mFCLog.getResultLabel().printResult(LOG_SUCCESSFULY_EXTRACTED, ResultType.SUCCESS);
		mDSCDeviceInfo.setProperties(infos);
		mFTFRemainingLogs.setText(cleanLogs.size() + " logs");

		mDateFilter.setDates(SoftLogExtractor.getDays(cleanLogs));

		mCurrentDeviceSelected = SoftLogExtractor.getDevices(cleanLogs);

		mButtonBar.setEnabled(true);
		mHistogramPanel.setEnabled(true);

		mScroll.setViewportView(new DeviceSelector(new Dimension(MAX_WIDTH * 75 / 100, Configuration.ITEM_HEIGHT),
				mCurrentDeviceSelected, this));
	}

	private List<SoftLog> extractCleanedSoftLogList() throws Exception {
		List<SoftLog> logs = RawLogFormater.extractLogs(this.mSelectedFile);
		List<String> ids = mCurrentDeviceSelected.stream().map(Device::getId).collect(Collectors.toList());

		// Filter Id
		logs = SoftLogExtractor.filterByIds(logs, ids);

		// Filter Day
		if (mDateFilter.getSelectedPosition() > 0) {
			logs = SoftLogExtractor.filterByDay(logs, mDateFilter.getSelectedItem());
		}

		// Filter time stamp
		if (!mCurrentDeviceSelected.isEmpty()) {
			logs = SoftLogExtractor.filterByIds(logs, ids);
		}

		// Filter by hour
		if (mDateFilter.isFilteringByTimestamp()) {
			logs = SoftLogExtractor.filterByHour(logs,
					new TSLimits(mDateFilter.getStartHour(), mDateFilter.getEndHour()));
		}

		// Ignore Consumption
		logs = SoftLogExtractor.ignoreLowConsumptionLogs(logs, Float.parseFloat(mFTFElectricThreshold.getText()));

		return logs;
	}

	private void resetLogData() {
		mFTFRemainingLogs.setText("0 logs");
		mCurrentDeviceSelected.clear();
		mDSCDeviceInfo.resetViews();
		// mDeviceSelector.resetDevicesList();
		mRLCleaning.setText("");
		mButtonBar.setEnabled(false);
		mDateFilter.setEnabled(false);
		mHistogramPanel.setEnabled(false);
		mDateFilter.reset();
	}

	@Override
	public void select(Device pDevice) {
		if (!mCurrentDeviceSelected.contains(pDevice)) {
			mCurrentDeviceSelected.add(pDevice);
		}
	}

	@Override
	public void unselect(Device pDevice) {
		if (mCurrentDeviceSelected.contains(pDevice)) {
			mCurrentDeviceSelected.remove(pDevice);
		}
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

	@Override
	public void saveHistogram() {

	}

	@Override
	public List<SoftLog> fillHistogram() {
		try {
			return extractCleanedSoftLogList();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.toString());
		}
	}
}
