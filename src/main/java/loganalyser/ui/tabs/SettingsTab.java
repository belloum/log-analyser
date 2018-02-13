package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
	private static final String ROUTINE_FILE = "routine_file";
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
		left.add(logSetttings(), BorderLayout.CENTER);

		JPanel right = new JPanel(new BorderLayout());
		right.add(participantSettings(), BorderLayout.CENTER);

		content.add(left);
		content.add(right);

		return content;
	}

	private JPanel logSetttings() {
		final JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createTitledBorder("LogTool settings"));

		final JPanel logSetttings = new JPanel(new GridLayout(4, 1));

		container.add(new JLabel(configuration().getJSONObject("settings_description").getString("logtool")),
				BorderLayout.PAGE_START);

		mLogFolder = new SettingEntry("Log folder", "The folder with log files.", LogToolSettings.getLogToolLogFolder(),
				e -> updateLogFolder());
		logSetttings.add(mLogFolder);

		mLogFile = new SettingEntry("Generic logs file", "The file where logs are gathered.",
				LogToolSettings.getGenericLogFilename(), e -> updateFile(LOG));
		logSetttings.add(mLogFile);

		mLogErrorFile = new SettingEntry("Error logs file", "The file where error logs are gathered.",
				LogToolSettings.getErrorLogFilename(), e -> updateFile(ERROR_LOG));
		logSetttings.add(mLogErrorFile);

		container.add(logSetttings, BorderLayout.CENTER);

		return container;
	}

	private JPanel participantSettings() {
		final JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createTitledBorder("Participant settings"));

		final JPanel participantSettings = new JPanel(new GridLayout(4, 1));

		container.add(new JLabel(configuration().getJSONObject("settings_description").getString("participant")),
				BorderLayout.PAGE_START);

		mParticipantLogFolder = new SettingEntry("Participant logs folder",
				"The folder that contains participant logs.", LogToolSettings.getParticipantLogFolder(),
				e -> updateParticipantLogsFolder());
		participantSettings.add(mParticipantLogFolder);

		mParticipantFile = new SettingEntry("Participant file", "The file that contains participant's routine data.",
				LogToolSettings.getParticipantRoutineFile(), e -> updateFile(ROUTINE_FILE));
		participantSettings.add(mParticipantFile);

		container.add(participantSettings, BorderLayout.CENTER);

		return container;
	}

	private void updateParticipantLogsFolder() {
		FileChooser folderPicker = updatePicker(LogToolSettings.getParticipantLogFolder(),
				"Select the participant logs folder");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update participant logs folder with {}", selectedFile.getName());
			LogToolSettings.setParticipantLogsFolder(selectedFile);
			mParticipantLogFolder.updateProperty(selectedFile.getPath());
		}
		return;
	}

	private void updateLogFolder() {
		FileChooser folderPicker = updatePicker(LogToolSettings.getLogToolLogFolder(), "Select a log folder");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update logFolder with {}", selectedFile.getName());
			LogToolSettings.setLogToolLogFolder(selectedFile);
			mLogFolder.updateProperty(selectedFile.getPath());
		}
		return;
	}

	private void updateFile(String pFileToUpdate) {
		File selectedFile = null;
		switch (pFileToUpdate) {
		case ERROR_LOG:
			selectedFile = chooseFile(LogToolSettings.getLogToolLogFolder(), "Pick a log error file");
			break;
		case LOG:
			selectedFile = chooseFile(LogToolSettings.getLogToolLogFolder(), "Pick a log file");
			break;
		case ROUTINE_FILE:
			selectedFile = chooseFile(Configuration.PARTICIPANT_FOLDER.getPath(), "Pick the routine file");
			break;
		case CONFIG:
			selectedFile = chooseFile(Configuration.ROOT_FOLDER.getPath(), "Pick a configuration file");
			break;
		case PARTICIPANT:
			selectedFile = chooseFile(Configuration.ROOT_FOLDER.getPath(), "Pick a participant file");
			break;
		}
		if (selectedFile != null) {
			switch (pFileToUpdate) {
			case LOG:
				LogToolSettings.setGenericLogFile(selectedFile.getName());
				mLogFile.updateProperty(selectedFile.getName());
				break;
			case ERROR_LOG:
				LogToolSettings.setErrorLogFile(selectedFile.getName());
				mLogErrorFile.updateProperty(selectedFile.getName());
				break;
			case ROUTINE_FILE:
				LogToolSettings.setParticipantRoutineFile(selectedFile);
				mParticipantFile.updateProperty(selectedFile.getName());
				break;
			case PARTICIPANT:
				break;
			default:
				log.debug("try to update {} with {}", pFileToUpdate, selectedFile);
				break;
			}
		}
	}

	private FileChooser updatePicker(String pCurrentFolderpath, String pDialogTitle) {
		return new FileChooser(new File(pCurrentFolderpath), pDialogTitle);
	}

	private File chooseFile(String pCurrentFolderpath, String pDialogTitle) {
		FileChooser picker = updatePicker(pCurrentFolderpath, pDialogTitle);
		picker.addFilter(new FileNameExtensionFilter("Log file", "log"));
		picker.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (picker.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			return picker.getSelectedFile();
		} else {
			return null;
		}
	}

	@Override
	public String configurationSection() {
		return "settings";
	}
}
