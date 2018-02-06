package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import beans.Histogram;
import beans.Histogram.HistogramProgressListener;
import beans.devices.Device;
import loganalyser.beans.SoftLog;
import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.FileChooser;
import loganalyser.old.ui.HistogramViewer;
import loganalyser.old.ui.InputValue;
import loganalyser.old.ui.MyButton;
import loganalyser.operators.FileSelector;
import loganalyser.operators.SoftLogExtractor;
import loganalyser.ui.components.ErrorLabel;
import loganalyser.ui.components.TabHeaderWithProgress;
import loganalyser.utils.Configuration;

//TODO: see the best way to handle progress, top bar or top histogram
public class HistogramTab extends LogTab implements ItemListener, HistogramProgressListener {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_SLOT = "60";
	private static final String DEFAULT_THRESHOLD = "20";
	/**
	 * @see HistogramViewer
	 */
	private JPanel mJPanelChart;
	private JLabel mJLTotalLogs;
	private InputValue mInputThreshold, mInputSlotHour;
	private JComboBox<String> mDatePicker, mSplitPicker;
	private MyButton mBtnSave, mBtnDraw, mBtnExportCSV;

	private JProgressBar mJPB;

	private JFreeChart mChart;

	private List<String> mSelectedDevice;
	private Map<String, Checkbox> mCheckBoxes;

	public HistogramTab(FileSelector pFileSelector) {
		super(pFileSelector);
		draws();
	}

	@Override
	protected void init() {
		super.init();
		mSelectedDevice = new ArrayList<>();
		mCheckBoxes = new HashMap<>();
		mJPB = new JProgressBar(0, 100);
		mBtnSave = new MyButton("Save histogram".toUpperCase(), event -> save());
		mBtnDraw = new MyButton("Draw".toUpperCase(), event -> draws());
		mBtnExportCSV = new MyButton("Export as CSV".toUpperCase(),
				event -> System.out.println("CSV export is not yet implemented"));
	}

	private void save() {

		FileChooser fc = new FileChooser(Configuration.RESOURCES_FOLDER, "Save histogram");

		if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File output = fc.getSelectedFile();
			try {
				if (!output.getName().contains(".jpg")) {
					output = new File(output.getParent(), String.format("%s.jpg", output.getName()));
				}
				if (mChart != null) {
					ChartUtils.saveChartAsJPEG(output, mChart, 1500, 600);
					System.out.println("Chart has been saved");
				} else {
					error("Nothing to save");
					System.err.println("Nothing to save");
				}
			} catch (Exception exception) {
				System.err.println(String.format("Unable to save file, %s", exception));
				error(String.format("Unable to save file, %s", exception));
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			mSelectedDevice.add(e.getItem().toString());
		} else if (e.getStateChange() == ItemEvent.DESELECTED) {
			mSelectedDevice.remove(e.getItem().toString());
		}
	}

	private void toggleSelection() {
		if (this.mSelectedDevice.size() < getLogFile().getDeviceCount()) {
			getLogFile().getDevices().forEach(device -> {
				this.mCheckBoxes.get(device.getId()).setState(true);
				this.mSelectedDevice.add(device.getId());
			});
		} else {
			this.mSelectedDevice.forEach(deviceId -> {
				this.mCheckBoxes.get(deviceId).setState(false);
			});
			this.mSelectedDevice.clear();
		}
	}

	@SuppressWarnings("unused")
	private void resetFilters() {
		mInputSlotHour.setValue(DEFAULT_SLOT);
		mInputThreshold.setValue(DEFAULT_THRESHOLD);
		mDatePicker.setSelectedIndex(0);
		mSplitPicker.setSelectedIndex(0);
		this.mSelectedDevice.clear();
		toggleSelection();
	}

	// FIXME check Thread usage
	private void draws() {
		new Thread(new HistogramDrawer(this)).start();
	}

	private List<SoftLog> filterLogsBeforeDrawing() throws Exception {

		List<SoftLog> logs = new ArrayList<>();

		// Filter Devices
		if (mSelectedDevice.isEmpty()) {
			throw new Exception("No device selected");
		} else {
			logs = getLogFile().getLogsByDevice(mSelectedDevice);
		}

		// Filter Days
		if (mDatePicker.getSelectedIndex() > 0) {
			logs = SoftLogExtractor.filterByDay(logs, mDatePicker.getSelectedItem().toString());
		}

		// Ignore Consumption
		try {
			int threshold = Integer.parseInt(mInputThreshold.getText());
			if (threshold <= 0) {
				throw new Exception("Electrical threshold must be a positive integer");
			} else {
				logs = SoftLogExtractor.ignoreLowConsumptionLogs(logs, threshold);
			}
		} catch (Exception e) {
			throw new Exception("Electrical threshold must be a positive integer");
		}

		// END
		if (logs.isEmpty()) {
			throw new Exception("No log found with the specific filters");
		}

		return logs;
	}

	private void enableBtns(boolean pEnabled) {
		mBtnDraw.setEnabled(pEnabled);
		mBtnExportCSV.setEnabled(pEnabled);
		mBtnSave.setEnabled(pEnabled);
	}

	@Override
	protected void error(String pError) {
		mJPanelChart.add(new ErrorLabel(pError), BorderLayout.PAGE_START);
		mJPanelChart.validate();
	}

	@Override
	protected Component content() {
		JPanel content = new JPanel(new BorderLayout());

		/*
		 * Histogram Panel
		 */
		mJPanelChart = new JPanel(new BorderLayout());
		mJPanelChart.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		content.add(mJPanelChart, BorderLayout.CENTER);

		/*
		 * Bottom
		 */
		JPanel btm = new JPanel(new BorderLayout());
		btm.add(mBtnSave, BorderLayout.WEST);
		btm.add(mBtnDraw, BorderLayout.CENTER);
		btm.add(mBtnExportCSV, BorderLayout.EAST);
		content.add(btm, BorderLayout.PAGE_END);

		/*
		 * Right Settings
		 */

		JPanel settings = new JPanel(new GridLayout(2, 1));

		// Top Settings
		JPanel topSettings = new JPanel(new GridLayout(5, 1));

		mJLTotalLogs = CustomComponent.boldLabel(new JLabel(String.format("%d", getLogFile().getSoftLogs().size())));
		mJLTotalLogs.setBorder(BorderFactory.createTitledBorder("Logs"));
		topSettings.add(mJLTotalLogs);

		mInputSlotHour = new InputValue(DEFAULT_SLOT);
		mInputSlotHour.setBorder(BorderFactory.createTitledBorder("Delay (in minutes)"));
		topSettings.add(mInputSlotHour);

		mInputThreshold = new InputValue(DEFAULT_THRESHOLD);
		mInputThreshold.setBorder(BorderFactory.createTitledBorder("Electrical threshold"));
		topSettings.add(mInputThreshold);

		mDatePicker = new JComboBox<String>();
		mDatePicker.addItem("All period");
		getLogFile().getDays().forEach(day -> mDatePicker.addItem(day));
		topSettings.add(mDatePicker);

		mSplitPicker = new JComboBox<String>(new String[] { "No split", "Split by device" });
		topSettings.add(mSplitPicker);

		settings.add(topSettings);

		// Bottom Settings
		JPanel btmSettings = new JPanel(new BorderLayout());

		JScrollPane scroll = new JScrollPane();
		JPanel jpanel = new JPanel(new GridLayout(getLogFile().getDeviceCount(), 1));
		getLogFile().getDevices().stream().map(Device::getId).forEach(deviceId -> {
			Checkbox chBx = new Checkbox(deviceId, true);
			this.mSelectedDevice.add(deviceId);
			chBx.addItemListener(this);
			mCheckBoxes.put(deviceId, chBx);
			jpanel.add(chBx);
		});

		scroll.getViewport().add(jpanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		btmSettings.add(scroll, BorderLayout.CENTER);
		btmSettings.add(new MyButton("Select/Unselect all".toUpperCase(), event -> toggleSelection()),
				BorderLayout.PAGE_END);

		settings.add(btmSettings);

		content.add(settings, BorderLayout.EAST);

		return content;
	}

	@Override
	protected TabHeaderWithProgress header() {
		return new TabHeaderWithProgress(settings().getString("title"), settings().getString("description"),
				new File(Configuration.IMAGES_FOLDER, settings().getString("img")));
	}

	@Override
	protected TabHeaderWithProgress getHeader() {
		return (TabHeaderWithProgress) super.getHeader();
	}

	@Override
	public void startFillingSlots() {
		getHeader().setProgressText("Filling slots");
	}

	@Override
	public void finishFillingSlots() {
		getHeader().hideProgress();
	}

	@Override
	public void progressBulding(float pProgress) {
		getHeader().updateProgress(pProgress);
		mJPB.setValue((int) pProgress);
	}

	private class HistogramDrawer implements Runnable {

		private HistogramProgressListener mListener;

		public HistogramDrawer(HistogramProgressListener pListener) {
			this.mListener = pListener;
		}

		public void run() {
			enableBtns(false);
			mJPanelChart.removeAll();
			mJPanelChart.add(mJPB);

			try {

				int hourSlot = Integer.parseInt(mInputSlotHour.getText());

				List<SoftLog> logs = filterLogsBeforeDrawing();
				boolean splitted = mSplitPicker.getSelectedIndex() > 0;

				mChart = Histogram.draw(new Histogram(hourSlot, logs, mListener), splitted);
				mJLTotalLogs.setText(String.format("%d", logs.size()));
			} catch (Exception exception) {
				mChart = null;
				System.out.println("it is in the thread");
				mJPanelChart.removeAll();
				error(exception.getMessage());
			} finally {
				mJPanelChart.add(new ChartPanel(mChart), BorderLayout.CENTER);
				mJPanelChart.validate();
				enableBtns(true);
			}
		}
	}

	@Override
	public String configurationSection() {
		return "histogram";
	}

}
