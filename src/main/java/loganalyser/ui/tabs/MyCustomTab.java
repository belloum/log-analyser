package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.ui.components.ErrorLabel;
import loganalyser.ui.components.TabHeader;
import loganalyser.utils.Utils;

/**
 * Force tabs to have a header
 */
public abstract class MyCustomTab extends JPanel implements Configurable {

	private static final Logger log = LoggerFactory.getLogger(MyCustomTab.class);
	private static final long serialVersionUID = 1L;

	private final TabHeader mHeader;
	private final ErrorLabel mError;
	private JSONObject mSettings;

	public MyCustomTab() {
		setLayout(new BorderLayout());

		try {
			mSettings = configuration();
		} catch (final Exception e) {
			final String error = String.format("Unable to find configuration file (%s): %s", this.getClass().getName(),
					e.getMessage());
			// Utils.errorLog(error, this.getClass());
			System.err.println(error);
		}

		mHeader = header();

		add(mHeader, BorderLayout.PAGE_START);
		mError = new ErrorLabel();
		add(getError(), BorderLayout.PAGE_END);
	}

	protected abstract Component content();

	protected TabHeader header() {
		return new TabHeader(settings().getString("title"), settings().getString("description"),
				Utils.getImg(settings().getString("img")));
	}

	protected void error(final String pErrorMsg) {
		if (!StringUtils.isEmpty(pErrorMsg)) {
			log.error(pErrorMsg);
			System.err.println("Error: " + pErrorMsg);
			getError().setVisible(true);
			getError().setText(pErrorMsg);
			validate();
		}
	}

	protected void hideError() {
		getError().setVisible(false);
		validate();
	}

	protected TabHeader getHeader() {
		return this.mHeader;
	}

	protected JSONObject settings() {
		return this.mSettings;
	}

	public ErrorLabel getError() {
		return this.mError;
	}

	@Override
	public String configurationFilename() {
		return "tabs.json";
	}

}
