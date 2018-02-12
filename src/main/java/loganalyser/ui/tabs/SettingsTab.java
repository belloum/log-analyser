package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.ui.components.FileChooser;
import loganalyser.ui.components.SettingEntry;
import loganalyser.utils.Configuration;
import loganalyser.utils.LogToolSettings;

public class SettingsTab extends MyCustomTab {

	// Kind of file which can be updated
	private static final String PARTICIPANT = "participant";
	private static final String ERROR_LOG = "error-log";
	private static final String LOG = "log";
	private static final String CONFIG = "config";

	private static final Logger log = LoggerFactory.getLogger(SettingsTab.class);
	private static final long serialVersionUID = 1L;
	private SettingEntry mLogFolder, mLogFile, mLogErrorFile, mParticipantFile, mParticipantLogFolder;

	public SettingsTab() {
		super();
		add(content(), BorderLayout.CENTER);
	}

	@Override
	protected Component content() {

		final JPanel content = new JPanel(new GridLayout(1, 2));

		JPanel left = new JPanel(new BorderLayout());

		left.add(logSetttings(), BorderLayout.PAGE_START);
		content.add(left);
		content.add(genericSettings());

		return content;
	}

	private JPanel logSetttings() {
		final JPanel logSetttings = new JPanel(new GridLayout(3, 1));
		logSetttings.setBorder(BorderFactory.createTitledBorder("Log settings"));

		mLogFolder = new SettingEntry("Log folder", "The folder with log files.", LogToolSettings.getLogFolder(),
				e -> updateLogFolder());
		logSetttings.add(mLogFolder);

		mLogFile = new SettingEntry("Log file", "The file where logs are gathered.",
				LogToolSettings.getGenericLogFilepath(), e -> updateFile(LOG));
		logSetttings.add(mLogFile);

		mLogErrorFile = new SettingEntry("Log error file", "The file where error logs are gathered.",
				LogToolSettings.getErrorLogFilepath(), e -> updateFile(ERROR_LOG));
		logSetttings.add(mLogErrorFile);

		return logSetttings;
	}

	private JPanel genericSettings() {
		final JPanel genericSettings = new JPanel(new GridLayout(2, 1));
		genericSettings.setBorder(BorderFactory.createTitledBorder("Generic settings"));

		mParticipantLogFolder = new SettingEntry("Participant logs folder",
				"The folder that contains participant logs.", Configuration.CONFIG_FOLDER.getPath(),
				e -> updateFile(CONFIG));
		genericSettings.add(mParticipantLogFolder);

		mParticipantFile = new SettingEntry("Participant file", "The file where participant data are gathered.",
				Configuration.CONFIG_FOLDER.getPath(), e -> updateFile(PARTICIPANT));
		genericSettings.add(mParticipantFile);

		return genericSettings;
	}

	@Override
	public String configurationSection() {
		return "settings";
	}

	private void updateLogFolder() {
		FileChooser folderPicker = new FileChooser(new File(LogToolSettings.getLogFolder()), "Select a folder");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update logFolder with {}", selectedFile.getName());
			LogToolSettings.setLogFolder(selectedFile);
			mLogFolder.updateProperty(selectedFile.getPath());
		}
	}

	private void updateFile(String pFileToUpdate) {
		File selectedFile = null;
		switch (pFileToUpdate) {
		case ERROR_LOG:
			selectedFile = chooseFile("Pick a log error file");
			break;
		case LOG:
			selectedFile = chooseFile("Pick a log file");
			break;
		case CONFIG:
			selectedFile = chooseFile("Pick a configuration file");
			break;
		case PARTICIPANT:
			selectedFile = chooseFile("Pick a participant file");
			break;
		}
		if (selectedFile != null) {
			// TODO
			switch (pFileToUpdate) {
			case LOG:
				LogToolSettings.setGenericLogFile(selectedFile.getName());
				mLogFile.updateProperty(selectedFile.getName());
				break;
			case ERROR_LOG:
				LogToolSettings.setErrorLogFile(selectedFile.getName());
				mLogErrorFile.updateProperty(selectedFile.getName());
				break;
			default:
				log.debug("try to update {} with {}", pFileToUpdate, selectedFile);
				break;
			}
		}
	}

	private FileChooser updatePicker(String pDialogTitle) {
		return new FileChooser(new File(""), pDialogTitle);
	}

	private File chooseFile(String pDialogTitle) {
		FileChooser picker = updatePicker(pDialogTitle);
		picker.addFilter(new FileNameExtensionFilter("Log file", "log"));
		picker.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (picker.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			return picker.getSelectedFile();
		} else {
			return null;
		}
	}

	// private File chooseDirectory(String pDialogTitle) {
	// FileChooser picker = updatePicker(pDialogTitle);
	// picker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	// if (picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	// return picker.getSelectedFile();
	// } else {
	// return null;
	// }
	// }
}
