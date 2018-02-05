package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import ui.last.components.ErrorLabel;
import ui.last.components.TabHeader;

/**
 * Force tabs to have a header
 */
public abstract class MyCustomTab extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TabHeader mHeader;
	private ErrorLabel mError;

	public MyCustomTab() {
		setLayout(new BorderLayout());

		mHeader = header();
		add(mHeader, BorderLayout.PAGE_START);
		mError = new ErrorLabel();
		add(mError, BorderLayout.PAGE_END);

	}

	protected abstract Component content();

	protected abstract TabHeader header();

	protected void error(String pErrorMsg) {
		if (!StringUtils.isEmpty(pErrorMsg)) {
			System.err.println("Error: " + pErrorMsg);
			mError.setVisible(true);
			mError.setText(pErrorMsg);
			validate();
		}
	}

	protected void hideError() {
		mError.setVisible(false);
		validate();
	}

	protected TabHeader getHeader() {
		return this.mHeader;
	};

}
