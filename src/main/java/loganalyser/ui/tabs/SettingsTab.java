package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.old.ui.CustomComponent;
import loganalyser.operators.FileExtractor;
import loganalyser.ui.components.FileChooser;
import loganalyser.ui.components.SettingEntry;
import loganalyser.utils.Configuration;
import loganalyser.utils.LogToolSettings;

public class SettingsTab extends MyCustomTab {

	private static final String SAVED_HISTOGRAMS_FOLDER = "saved histograms folder";
	private static final String SAVED_CSV_FOLDER = "saved CSV folder";
	private static final String SAVED_CLEANED_LOGS_FOLDER = "saved cleaned logs folder";
	private static final String PARTICIPANT_LOGS_FOLDER = "Participant-logs folder";
	private static final String LOG_TOOL_LOG_FOLDER = "LogTool-log folder";
	// Kind of file which can be updated
	private static final String ROUTINE_FILE = "routine_file";
	private static final String PARTICIPANT = "participant";
	private static final String ERROR_LOG = "error-log";
	private static final String LOG = "log";
	private static final String CONFIG = "config";

	private static final Logger log = LoggerFactory.getLogger(SettingsTab.class);
	private static final long serialVersionUID = 1L;
	private SettingEntry mLogFolder, mLogFile, mLogErrorFile, mParticipantFile, mParticipantLogFolder, mHistogramFolder,
			mCSVFolder, mCleanLogFileFolder;

	public SettingsTab() {
		super();
		add(content(), BorderLayout.CENTER);
	}

	@Override
	protected Component content() {

		final JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Generic", null, genericSettings(), "Generic settings");
		tabbedPane.addTab("Participant", null, participantSettings(), "Handle participant settings");
		tabbedPane.addTab("Scripts", null, new JLabel("Not implemented yet"), "Not implemented yet");
		tabbedPane.addTab("LogTool logs", null, logToolLogsSettings(), "Handle LogTool log settings");

		return tabbedPane;
	}

	private JPanel genericSettings() {
		final JPanel genericSettings = new JPanel(new GridLayout(5, 1));
		genericSettings.add(new JLabel(configuration().getJSONObject("settings_description").getString("generic")));

		mHistogramFolder = new SettingEntry("Saved histograms folder", "The default folder for saved histograms.",
				LogToolSettings.getSavedHistogramsFolder(), e -> updateFolder(SAVED_HISTOGRAMS_FOLDER), true);
		genericSettings.add(mHistogramFolder);

		mCSVFolder = new SettingEntry("Saved CSV folder", "The default folder for saved CSV.",
				LogToolSettings.getSavedCSVFolder(), e -> updateFolder(SAVED_CSV_FOLDER), true);
		genericSettings.add(mCSVFolder);

		mCleanLogFileFolder = new SettingEntry("Saved cleaned log file folder",
				"The default folder for saved cleaned log file.", LogToolSettings.getCleanedLogsFolder(),
				e -> updateFolder(SAVED_CLEANED_LOGS_FOLDER), true);
		genericSettings.add(mCleanLogFileFolder);

		return CustomComponent.addEmptyBorder(genericSettings, 5);
	}

	private JPanel logToolLogsSettings() {
		final JPanel logSettings = new JPanel(new GridLayout(5, 1));

		logSettings.add(new JLabel(configuration().getJSONObject("settings_description").getString("logtool")));

		mLogFolder = new SettingEntry("Log folder", "The folder with log files.", LogToolSettings.getLogToolLogFolder(),
				e -> updateFolder(LOG_TOOL_LOG_FOLDER), true);
		logSettings.add(mLogFolder);

		mLogFile = new SettingEntry("Generic logs file", "The file where logs are gathered.",
				LogToolSettings.getGenericLogFilename(), e -> updateFile(LOG), true);
		logSettings.add(mLogFile);

		mLogErrorFile = new SettingEntry("Error logs file", "The file where error logs are gathered.",
				LogToolSettings.getErrorLogFilename(), e -> updateFile(ERROR_LOG), true);
		logSettings.add(mLogErrorFile);

		return CustomComponent.addEmptyBorder(logSettings, 5);
	}

	private JPanel participantSettings() {

		final JPanel participantSettings = new JPanel(new GridLayout(5, 1));

		participantSettings
				.add(new JLabel(configuration().getJSONObject("settings_description").getString("participant")));

		mParticipantLogFolder = new SettingEntry("Participant logs folder",
				"The folder that contains participant logs.", LogToolSettings.getParticipantLogsFolder(),
				e -> updateFolder(PARTICIPANT_LOGS_FOLDER), true);
		participantSettings.add(mParticipantLogFolder);

		mParticipantFile = new SettingEntry("Participant routine file",
				"The file that contains participant's routine data.", LogToolSettings.getParticipantRoutineFile(),
				e -> updateFile(ROUTINE_FILE), true);
		participantSettings.add(mParticipantFile);

		return CustomComponent.addEmptyBorder(participantSettings, 5);
	}

	private void updateFile(final String pFileToUpdate) {
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

			// TODO Check the file extension
			if (!selectedFile.getParentFile().exists()) {
				selectedFile.mkdirs();
				log.info("Making directories {}", selectedFile.getParentFile().getAbsolutePath());
			}

			if (!selectedFile.exists()) {
				FileExtractor.saveFile(new JSONObject().toString(), selectedFile);
				log.info("Create a new Participant routine file {}", selectedFile);
			}

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

	private FileChooser updatePicker(final String pCurrentFolderpath, final String pDialogTitle) {
		return new FileChooser(new File(pCurrentFolderpath), pDialogTitle);
	}

	private File chooseFile(final String pCurrentFolderpath, final String pDialogTitle) {
		final FileChooser picker = updatePicker(pCurrentFolderpath, pDialogTitle);
		picker.addFilter(new FileNameExtensionFilter("Log file", "log"));
		picker.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (picker.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			return picker.getSelectedFile();
		} else {
			return null;
		}
	}

	private void updateFolder(String pFolder) {
		String currentDirectoryPath, dialogTitle;
		switch (pFolder) {
		case LOG_TOOL_LOG_FOLDER:
			currentDirectoryPath = LogToolSettings.getLogToolLogFolder();
			dialogTitle = "Select the LogTool log folder";
			break;
		case PARTICIPANT_LOGS_FOLDER:
			currentDirectoryPath = LogToolSettings.getParticipantLogsFolder();
			dialogTitle = "Select the participant logs folder";
			break;
		case SAVED_HISTOGRAMS_FOLDER:
			currentDirectoryPath = LogToolSettings.getSavedHistogramsFolder();
			dialogTitle = "Select the saved histograms folder";
			break;
		case SAVED_CSV_FOLDER:
			currentDirectoryPath = LogToolSettings.getSavedCSVFolder();
			dialogTitle = "Select the saved CSV folder";
			break;
		case SAVED_CLEANED_LOGS_FOLDER:
			currentDirectoryPath = LogToolSettings.getCleanedLogsFolder();
			dialogTitle = "Select the saved cleand logs folder";
			break;
		default:
			log.warn("{} can not be configure", pFolder);
			return;
		}

		FileChooser folderPicker = new FileChooser(new File(currentDirectoryPath), dialogTitle);
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (folderPicker.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = folderPicker.getSelectedFile();

			if (!selectedFile.exists()) {
				selectedFile.mkdirs();
				try {
					selectedFile.createNewFile();
				} catch (IOException e) {
					log.error("{} could not have been created because of: {}", selectedFile.getPath(), e.getMessage(),
							e);
					return;
				}
			}

			switch (pFolder) {
			case LOG_TOOL_LOG_FOLDER:
				LogToolSettings.setLogToolLogFolder(selectedFile);
				mLogFolder.updateProperty(selectedFile.getPath());
				break;
			case PARTICIPANT_LOGS_FOLDER:
				LogToolSettings.setParticipantLogsFolder(selectedFile);
				mParticipantLogFolder.updateProperty(selectedFile.getPath());
				break;
			case SAVED_HISTOGRAMS_FOLDER:
				LogToolSettings.setSavedHistogramsFolder(selectedFile);
				mHistogramFolder.updateProperty(selectedFile.getPath());
				break;
			case SAVED_CSV_FOLDER:
				LogToolSettings.setSavedCSVFolder(selectedFile);
				mCSVFolder.updateProperty(selectedFile.getPath());
				break;
			case SAVED_CLEANED_LOGS_FOLDER:
				LogToolSettings.setSavedCleanedLogsFolder(selectedFile);
				mCleanLogFileFolder.updateProperty(selectedFile.getPath());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String configurationSection() {
		return "settings";
	}
}
