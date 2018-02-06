package ui.last;

import java.io.File;

public interface FileSelector {

	public void validFile(LogFile pValidFile);

	public void invalidFile(File pInvalidFile, String pCause);

	public void checkingFile();
	
	public LogFile getLogFile();
}
