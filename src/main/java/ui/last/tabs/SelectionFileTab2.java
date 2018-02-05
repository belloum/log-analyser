package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;

import beans.SoftLog;
import beans.devices.Device;
import exceptions.RawLogException;
import operators.extractors.RawLogFormater;
import operators.extractors.SoftLogExtractor;
import ui.components.CustomComponent;
import ui.components.FileChooser;
import ui.components.MyButton;
import ui.components.MyFilePicker.FilePickerListener;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class SelectionFileTab2 extends JPanel implements FilePickerListener {

	// TODO: add work in progress view
	// TODO: hide error txt
	// TODO: custom btn view, automatize it

	private static final long serialVersionUID = 1L;

	private final SelectionListener mListener;
	private final JScrollPane mCenterScrollPanel = new JScrollPane();
	private final ResultLabel mResultPicker = new ResultLabel("error");
	private JPanel mHead;
	private JPanel mDetails;
	GridBagConstraints gc;

	public SelectionFileTab2(final SelectionListener pListener) {
		setLayout(new BorderLayout());
		this.mListener = pListener;
		init();
	}

	private void init() {
		Dimension dim = new Dimension(Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH,
				Configuration.MAX_HEIGHT / 8);

		/*
		 * HEAD
		 */
		mHead = new JPanel(new BorderLayout());
		mHead.setBounds(0, 0, dim.width, dim.height);
		mHead.setPreferredSize(dim);
		mHead.setBorder(BorderFactory.createTitledBorder("Please, select a log file"));

		final JLabel jta = new JLabel();
		jta.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		jta.setBackground(Color.WHITE);
		jta.setOpaque(true);

		mHead.add(jta, BorderLayout.CENTER);

		final FileChooser fileChooser = new FileChooser(Configuration.RESOURCES_FOLDER, "Select a log file",
				Arrays.asList(new FileNameExtensionFilter("Log file", "json")));
		final MyButton mBut = new MyButton("Click", event -> {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				final File output = fileChooser.getSelectedFile();
				jta.setText(output.getPath());
				fileSelected(output);
			}
		});

		mHead.add(mBut, BorderLayout.LINE_END);
		mHead.add(mResultPicker, BorderLayout.PAGE_END);

		add(mHead, BorderLayout.PAGE_START);

		/*
		 * CENTER
		 */
		dim = new Dimension(Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH,
				Configuration.MAX_HEIGHT - Configuration.MAX_HEIGHT / 8);

		mDetails = new JPanel(new BorderLayout());
		mDetails.setBounds(0, 0, dim.width, dim.height);
		mDetails.setPreferredSize(dim);
		mDetails.setBorder(BorderFactory.createTitledBorder("Details"));
		resetDetailsView();

		add(mDetails, BorderLayout.CENTER);
	}

	private void resetDetailsView() {
		mDetails.removeAll();
		mDetails.validate();
	}

	private void updateDetailsView(final File pSelectedFile) throws RawLogException {
		resetDetailsView();
		mDetails.setLayout(new BoxLayout(mDetails, BoxLayout.Y_AXIS));

		String[] headers;
		String[][] data;
		JTable table;

		mDetails.add(new MyButton("Save cleaned logs", event -> saveLogFile(pSelectedFile)));

		/*
		 * File info
		 */
		mDetails.add(CustomComponent.boldLabel(new JLabel("File info")));
		headers = new String[] { " ", " s" };
		data = new String[][] { { "Name", pSelectedFile.getName() },
				{ "Size", String.format("%d octets", pSelectedFile.getTotalSpace()) } };
		table = new JTable(data, headers);
		table.setEnabled(false);
		mDetails.add(table);

		/*
		 * Log info
		 */
		mDetails.add(CustomComponent.boldLabel(new JLabel("Logs info")));
		headers = new String[] { " ", " " };
		final List<SoftLog> logs = SoftLogExtractor.sortLogsByDate(RawLogFormater.extractLogs(pSelectedFile));
		data = new String[][] { { "Total", String.format("%d", logs.size()) },
				{ "Device types", StringUtils.join(SoftLogExtractor.getDeviceTypes(logs), ", ") }, { "Period", String
						.format("%s - %s", logs.get(0).getDayLabel(), logs.get(logs.size() - 1).getDayLabel()) } };
		table = new JTable(data, headers);
		table.setEnabled(false);
		mDetails.add(table);

		/*
		 * Device info
		 */
		mDetails.add(CustomComponent.boldLabel(new JLabel("Device info")));
		final JScrollPane scroll = new JScrollPane();
		final List<Device> devices = SoftLogExtractor.getDevices(logs);
		headers = new String[] { "Device", "Type", "Location", "Logs" };
		data = new String[devices.size()][headers.length];
		for (int i = 0; i < devices.size(); i++) {
			final Device device = devices.get(i);
			data[i] = new String[] { device.getId(), device.getType().name(), device.getLocation(),
					SoftLogExtractor.filterByIds(logs, Collections.singletonList(device.getId())).size() + "" };
		}

		table = new JTable(data, headers);
		table.setEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);
		scroll.getViewport().add(table);
		mDetails.add(scroll);

		mDetails.validate();
	}

	private boolean saveLogFile(File pSelectedFile) {

		try {
			if (pSelectedFile != null) {
				List<SoftLog> cleanList = SoftLogExtractor
						.ignoreLowConsumptionLogs(RawLogFormater.extractLogs(pSelectedFile), 20f);
				System.out.println(String.format("From %d to %d logs", RawLogFormater.extractLogs(pSelectedFile).size(),
						cleanList.size()));
				File output = new File(pSelectedFile.getParent(),
						pSelectedFile.getName().replace(".json", "_cleaned.json"));
				SoftLogExtractor.saveLogList(cleanList, output);
				System.out.println(String.format("Logs has been saved (%s)", output.getAbsolutePath()));
				return true;
			} else {
				System.err.println("Nothing to save");
			}
		} catch (Exception exception) {
			System.err.println("Unable to save file: " + exception);
		}

		return false;
	}

	private void displayError(final File pFile, final String error) {
		mCenterScrollPanel.getViewport().removeAll();
		mResultPicker.printResult(String.format("%s is not a valid log file - %s", pFile.getName(), error),
				ResultType.ERROR);
	}

	@Override
	public void fileSelected(final File pSelectedFile) {
		try {
			updateDetailsView(pSelectedFile);
			mListener.validFile(pSelectedFile);
		} catch (final RawLogException e) {
			displayError(pSelectedFile, e.getMessage());
			resetDetailsView();
			mListener.invalidFile(pSelectedFile, e.getMessage());
		}
	}

	public interface SelectionListener {
		void invalidFile(File pFile, String pCause);

		void validFile(File pFile);
	}

}
