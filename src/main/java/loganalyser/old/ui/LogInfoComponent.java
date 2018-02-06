package loganalyser.old.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import loganalyser.beans.SoftLog;
import loganalyser.exceptions.RawLogException;
import loganalyser.operators.RawLogFormater;
import loganalyser.operators.SoftLogExtractor;
import loganalyser.utils.Configuration;

public class LogInfoComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel mJLTitle;
	private LinkedHashMap<String, Object> mProperties = new LinkedHashMap<>();
	private List<JPanel> mPanels = new ArrayList<>();

	public LogInfoComponent(List<String> pPropertyLabels) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		pPropertyLabels.forEach(property -> mProperties.put(property, "-"));
		build();
	}

	public LogInfoComponent(File pLogFile) throws RawLogException {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		try {
			List<SoftLog> logs = RawLogFormater.extractLogs(pLogFile);
			mProperties.put("Name", pLogFile.getName());
			mProperties.put("Logs", logs.size());
			mProperties.put("Devices", SoftLogExtractor.getDeviceCount(logs));
			mProperties.put("Types", SoftLogExtractor.getDeviceTypeCount(logs));
			mProperties.put("Days", SoftLogExtractor.getDayCount(logs));
			mProperties.put("Months", SoftLogExtractor.getMonthCount(logs));
		} catch (RawLogException e) {
			System.out.println("RawLogException: " + e.getMessage());
			throw e;
		}
		build();
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		setMaximumSize(preferredSize);
		setMinimumSize(preferredSize);

		mJLTitle.getParent().setPreferredSize(new Dimension(preferredSize.width, 2 * Configuration.ITEM_HEIGHT));
		mJLTitle.getParent().setMinimumSize(mJLTitle.getParent().getPreferredSize());
		mJLTitle.getParent().setMaximumSize(mJLTitle.getParent().getPreferredSize());

		mPanels.forEach(panel -> {
			panel.setPreferredSize(new Dimension(preferredSize.width, Configuration.ITEM_HEIGHT));
			panel.setMinimumSize(panel.getPreferredSize());
			panel.setMaximumSize(panel.getPreferredSize());
		});

		validate();
	}

	private void build() {
		JPanel panel = new JPanel(new BorderLayout());
		mJLTitle = new JLabel("Log infos");
		CustomComponent.setJLabelFontStyle(mJLTitle, Font.BOLD);
		panel.add(mJLTitle, BorderLayout.CENTER);
		add(panel);
		// add(mJLTitle);
		mProperties.forEach((property, value) -> {
			JPanel p = makeRow(property, value);
			add(p);
			mPanels.add(p);
		});
		validate();
	}

	private JPanel makeRow(String pProperty, Object pValue) {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel title = new JLabel(pProperty);
		CustomComponent.setJLabelFontStyle(title, Font.BOLD);
		panel.add(title, BorderLayout.LINE_START);
		JLabel value = new JLabel(pValue.toString());
		panel.add(value, BorderLayout.LINE_END);
		return panel;
	}

}
