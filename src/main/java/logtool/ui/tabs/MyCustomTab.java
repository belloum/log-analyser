package logtool.ui.tabs;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logtool.ui.components.ErrorLabel;
import logtool.ui.components.TabHeader;
import logtool.utils.Configuration;

/**
 * Force tabs to have a header
 */
public abstract class MyCustomTab extends JPanel implements Configurable {

	private static final Logger log = LoggerFactory.getLogger(MyCustomTab.class);
	private static final long serialVersionUID = 1L;

	private final TabHeader mHeader;
	private final ErrorLabel mError;
	private JSONObject mSettings;

	public MyCustomTab(final Object... params) {
		setLayout(new BorderLayout());
		log.debug("{} constructor", this.getClass().getSimpleName());
		try {
			mSettings = configuration();
		} catch (final Exception e) {
			final String error = String.format("Unable to find configuration file (%s): %s", this.getClass().getName(),
					e.getMessage());
			System.err.println(error);
		}

		mHeader = header();
		add(mHeader, BorderLayout.PAGE_START);
		mError = new ErrorLabel();
		add(getError(), BorderLayout.PAGE_END);
		log.debug("Before init in {}", this.getClass().getSimpleName());
		init(params);
		log.debug("After init in {}", this.getClass().getSimpleName());
		add(content(), BorderLayout.CENTER);
	}

	protected abstract JComponent content();

	protected abstract void init(final Object... params);

	protected TabHeader header() {
		return new TabHeader(settings().getString("title"), settings().getString("description"),
				new File(Configuration.IMG_FOLDER, settings().getString("img")));
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
