package loganalyser.operators;

import java.io.File;

import loganalyser.beans.LogFile;

public interface FileSelector {

	public void validFile(LogFile pValidFile);

	public void invalidFile(File pInvalidFile, String pCause);

	public void checkingFile();
	
	public LogFile getLogFile();
}
