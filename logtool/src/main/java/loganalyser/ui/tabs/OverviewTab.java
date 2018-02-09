package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXLabel;
import org.json.JSONObject;

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

		JSONObject json = settings().getJSONObject("sections").getJSONObject("log");

		JXLabel logs = new JXLabel(json.getString("description"));
		logs.setLineWrap(true);
		logs.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(logs);

		json = settings().getJSONObject("sections").getJSONObject("routine");
		JXLabel routine = new JXLabel(json.getString("description"));
		routine.setLineWrap(true);
		routine.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(routine);

		json = settings().getJSONObject("sections").getJSONObject("device");
		JXLabel device = new JXLabel(json.getString("description"));
		device.setLineWrap(true);
		device.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(device);

		json = settings().getJSONObject("sections").getJSONObject("histogram");
		JXLabel histograms = new JXLabel(json.getString("description"));
		histograms.setLineWrap(true);
		histograms.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(histograms);

		return content;
	}

	@Override
	public String configurationSection() {
		return "overview";
	}

}
