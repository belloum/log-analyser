package logtool.ui.tabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logtool.beans.LogFile;
import logtool.operators.FileSelector;

public abstract class LogTab extends MyCustomTab {

	private static final Logger log = LoggerFactory.getLogger(LogTab.class);

	private static final long serialVersionUID = 1L;

	private LogFile mLogFile;

	public LogTab(final FileSelector pFileSelector) {
		super(pFileSelector);
		postInit();
	}

	protected LogFile getLogFile() {
		return mLogFile;
	}

	@Override
	protected void init(final Object... params) {
		log.debug("init LogTab");
		this.mLogFile = ((FileSelector) params[0]).getLogFile();
		log.debug("LogTab ready {}", this.mLogFile.getName());
	}
	
	protected abstract void postInit();
	
}
