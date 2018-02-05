package ui.menus.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
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
import ui.components.MyFilePicker;
import ui.components.MyFilePicker.FilePickerListener;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class SelectionFileTab extends JPanel implements FilePickerListener {

	// TODO: add work in progress view

	private static final long serialVersionUID = 1L;

	private SelectionListener mListener;
	private JPanel mHeadPanel = new JPanel(new BorderLayout());
	private JScrollPane mCenterScrollPanel = new JScrollPane();

	public SelectionFileTab(SelectionListener pListener) {
		setLayout(new BorderLayout());
		this.mListener = pListener;

		// HEAD PANEL
		setUpHeadPanel();
		add(mHeadPanel, BorderLayout.PAGE_START);

		// CENTER PANEL
		add(mCenterScrollPanel, BorderLayout.LINE_START);
		mCenterScrollPanel.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH),
				mCenterScrollPanel.getPreferredSize().height));

		validate();
	}

	private void setUpHeadPanel() {
		mHeadPanel.add(new JLabel("Please, select a log file"), BorderLayout.PAGE_START);

		FileChooser fileChooser = new FileChooser(Configuration.RESOURCES_FOLDER, "Select a log file",
				Arrays.asList(new FileNameExtensionFilter("Log file", "json")));
		MyFilePicker mFP = new MyFilePicker(this, fileChooser);
		mFP.setPreferredSize(
				new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH), Configuration.ITEM_HEIGHT));
		mHeadPanel.add(mFP, BorderLayout.PAGE_END);
	}

	private void setUpCenterPanel(File pFile) throws RawLogException {
		mCenterScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mCenterScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mCenterScrollPanel.getViewport().removeAll();

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

		List<SoftLog> logs = SoftLogExtractor.sortLogsByDate(RawLogFormater.extractLogs(pFile));

		/*
		 * File info
		 */
		center.add(CustomComponent.boldLabel(new JLabel("File infos")));
		String[][] data = { { "Name", pFile.getName() },
				{ "Size", String.format("%d octets", pFile.getTotalSpace()) } };
		JTable fileTab = new JTable(data, new String[] { "Name", "Size" });
		fileTab.setEnabled(false);
		fileTab.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH),
				fileTab.getPreferredSize().height));
		center.add(fileTab);

		/*
		 * Log info
		 */
		center.add(CustomComponent.boldLabel(new JLabel("Log infos")));
		data = new String[][] { { "Total", String.format("%d", logs.size()) },
				{ "Device types", StringUtils.join(SoftLogExtractor.getDeviceTypes(logs), ", ") }, { "Period", String
						.format("%s - %s", logs.get(0).getDayLabel(), logs.get(logs.size() - 1).getDayLabel()) } };
		fileTab = new JTable(data, new String[] { "Key", "Value" });
		fileTab.setEnabled(false);
		fileTab.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH),
				fileTab.getPreferredSize().height));
		center.add(fileTab);

		/*
		 * Device info
		 */
		center.add(CustomComponent.boldLabel(new JLabel("Device infos")));
		List<Device> devices = SoftLogExtractor.getDevices(logs);
		String[] headers = new String[] { "Device", "Type", "Logs" };
		data = new String[devices.size() + 1][headers.length];
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			data[i] = new String[] { device.getId(), device.getType().name(),
					SoftLogExtractor.filterByIds(logs, Collections.singletonList(device.getId())).size() + "" };
		}
		data[devices.size()] = new String[] { "Total", "", logs.size() + "" };

		JTable tableau = new JTable(data, headers);
		tableau.setEnabled(false);
		tableau.setPreferredSize(new Dimension((Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH),
				tableau.getPreferredSize().height));
		center.add(tableau);

		mCenterScrollPanel.getViewport().add(center);
	}

	public interface SelectionListener {
		void invalidFile(File pFile, String pCause);

		void validFile(File pFile);
	}

	private void displayError(File pFile, String error) {
		mCenterScrollPanel.getViewport().removeAll();
		ResultLabel resultLabel = new ResultLabel("");
		resultLabel.printResult(String.format("%s is not a valid log file - %s", pFile.getName(), error),
				ResultType.ERROR);
		mCenterScrollPanel.getViewport().add(resultLabel);
	}

	@Override
	public void fileSelected(File pSelectedFile) {
		try {
			setUpCenterPanel(pSelectedFile);
			mListener.validFile(pSelectedFile);
		} catch (RawLogException e) {
			displayError(pSelectedFile, e.getMessage());
			mListener.invalidFile(pSelectedFile, e.getMessage());
		}
	}

}
