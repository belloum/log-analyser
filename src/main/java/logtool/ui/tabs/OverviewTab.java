package logtool.ui.tabs;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.json.JSONObject;

public class OverviewTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	public OverviewTab() {
		super();
	}

	public OverviewTab(final String pErrorMsg) {
		super(pErrorMsg);
	}

	@Override
	protected JPanel content() {
		final JPanel content = new JPanel(new GridLayout(4, 1));

		JSONObject json = settings().getJSONObject("sections").getJSONObject("log");

		final JXLabel logs = new JXLabel(json.getString("description"));
		logs.setLineWrap(true);
		logs.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(logs);

		json = settings().getJSONObject("sections").getJSONObject("routine");
		final JXLabel routine = new JXLabel(json.getString("description"));
		routine.setLineWrap(true);
		routine.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(routine);

		json = settings().getJSONObject("sections").getJSONObject("device");
		final JXLabel device = new JXLabel(json.getString("description"));
		device.setLineWrap(true);
		device.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(device);

		json = settings().getJSONObject("sections").getJSONObject("histogram");
		final JXLabel histograms = new JXLabel(json.getString("description"));
		histograms.setLineWrap(true);
		histograms.setBorder(BorderFactory.createTitledBorder(json.getString("title")));
		content.add(histograms);

		return content;
	}

	@Override
	public String configurationSection() {
		return "overview";
	}

	@Override
	protected void init(final Object... params) {
		if (params.length > 0 && StringUtils.isNotEmpty(params[0].toString())) {
			error(params[0].toString());
		}
	}

}
