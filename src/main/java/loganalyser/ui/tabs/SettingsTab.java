package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.ui.components.FileChooser;
import loganalyser.ui.components.SettingEntry;
import loganalyser.utils.Configuration;

public class SettingsTab extends MyCustomTab {

	// Kind of file which can be updated
	private static final String PARTICIPANT = "participant";
	private static final String ERROR_LOG = "error-log";
	private static final String LOG = "log";
	private static final String CONFIG = "config";

	private static final Logger log = LoggerFactory.getLogger(SettingsTab.class);
	private static final long serialVersionUID = 1L;
	private SettingEntry mConfigFileSetting, mDefaultLogFolder, mLogFile, mLogErrorFile, mParticipantFile;

	public SettingsTab() {
		super();
		add(content(), BorderLayout.CENTER);
	}

	@Override
	protected Component content() {

		final JPanel content = new JPanel(new BorderLayout());

		content.add(settingsPanel(), BorderLayout.PAGE_START);

		return content;
	}

	private JPanel settingsPanel() {
		final JPanel genericSettings = new JPanel(new GridLayout(5, 1));

		mConfigFileSetting = new SettingEntry("Configuration file", "The file of configuration",
				Configuration.CONFIG_FOLDER.getPath(), e -> updateFile(CONFIG));
		genericSettings.add(mConfigFileSetting);

		mParticipantFile = new SettingEntry("Participant file", "The file where participant data are gathered.",
				Configuration.CONFIG_FOLDER.getPath(), e -> updateFile(PARTICIPANT));
		genericSettings.add(mParticipantFile);

		mDefaultLogFolder = new SettingEntry("Default log folder", "The folder with log files",
				Configuration.RESOURCES.getPath(), e -> updateFolder());
		genericSettings.add(mDefaultLogFolder);

		mLogFile = new SettingEntry("Log file", "The file where logs are gathered.", Configuration.LOG_FILE.getPath(),
				e -> updateFile(LOG));
		genericSettings.add(mLogFile);

		mLogErrorFile = new SettingEntry("Log error file", "The file where error logs are gathered.",
				Configuration.LOG_ERROR_FILE.getPath(), e -> updateFile(ERROR_LOG));
		genericSettings.add(mLogErrorFile);

		return genericSettings;
	}

	@Override
	public String configurationSection() {
		return "settings";
	}

	private void updateFolder() {
		File errorFile = chooseDirectory("Select a folder");
		if (errorFile != null) {
			log.debug("Try to update errorFile with {}", errorFile.getName());
			// TODO: update log error file
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
			log.debug("try to update {} with {}", pFileToUpdate, selectedFile);
		}
	}

	private FileChooser updatePicker(String pDialogTitle) {
		return new FileChooser(new File(""), pDialogTitle);
	}

	private File chooseFile(String pDialogTitle) {
		FileChooser picker = updatePicker(pDialogTitle);
		picker.addFilter(new FileNameExtensionFilter("Log file", "json"));
		picker.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return picker.getSelectedFile();
		} else {
			return null;
		}
	}

	private File chooseDirectory(String pDialogTitle) {
		FileChooser picker = updatePicker(pDialogTitle);
		picker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return picker.getSelectedFile();
		} else {
			return null;
		}
	}
}
