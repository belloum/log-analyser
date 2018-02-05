package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.last.components.TabHeader;
import utils.Configuration;

public class OverviewTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	public OverviewTab() {
		this(null);
	}

	public OverviewTab(String pErrorMsg) {
		super();
		add(content(), BorderLayout.CENTER);
		error(pErrorMsg);
	}

	@Override
	protected Component content() {
		JPanel content = new JPanel(new GridLayout(4, 1));

		JLabel logText = new JLabel("This is a useless description");
		logText.setBorder(BorderFactory.createTitledBorder("Log description"));
		content.add(logText);

		JLabel routineTxt = new JLabel("This is a routine description");
		routineTxt.setBorder(BorderFactory.createTitledBorder("Routine description"));
		content.add(routineTxt);

		JLabel device = new JLabel("This is a useless description");
		device.setBorder(BorderFactory.createTitledBorder("Device description"));
		content.add(device);

		JLabel histText = new JLabel("This is a histogram description");
		histText.setBorder(BorderFactory.createTitledBorder("Histo description"));
		content.add(histText);

		return content;
	}

	@Override
	protected TabHeader header() {
		return new TabHeader("Overview", "Please, enter a description for this tab ...", Configuration.IMAGE_HISTO);
	}

}
