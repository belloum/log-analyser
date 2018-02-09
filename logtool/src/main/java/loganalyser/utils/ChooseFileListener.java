package loganalyser.utils;

import java.io.File;

public interface ChooseFileListener {

	void validFile(File pFile);

	void invalidFile(File pFile, String pCause);

	void unfoundFile(File pFile);
}
