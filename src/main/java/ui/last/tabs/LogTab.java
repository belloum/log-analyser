package ui.last.tabs;

import java.awt.BorderLayout;

import ui.last.FileSelector;
import ui.last.LogFile;

public abstract class LogTab extends MyCustomTab {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final LogFile mLogFile;

	public LogTab(final FileSelector pFileSelector) {
		this.mLogFile = pFileSelector.getLogFile();
		init();
		add(content(), BorderLayout.CENTER);
	}

	protected LogFile getLogFile() {
		return mLogFile;
	}

	protected void init() {
	}

}
