package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.old.ui.CustomComponent;
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
		tabbedPane.addTab("LogTool logs", null, logSettings(), "Handle LogTool log settings");

		return tabbedPane;
	}

	private JPanel genericSettings() {
		final JPanel genericSettings = new JPanel(new GridLayout(5, 1));
		genericSettings.add(new JLabel(configuration().getJSONObject("settings_description").getString("generic")));

		mHistogramFolder = new SettingEntry("Saved histograms folder", "The default folder for saved histograms.",
				LogToolSettings.getSavedHistogramsFolder(), e -> updateSavedHistogramsFolder(), true);
		genericSettings.add(mHistogramFolder);

		mCSVFolder = new SettingEntry("Saved CSV folder", "The default folder for saved CSV.",
				LogToolSettings.getSavedHistogramsFolder(), e -> System.out.println("Not implemented"), true);
		genericSettings.add(mCSVFolder);

		mCleanLogFileFolder = new SettingEntry("Saved cleaned log file folder",
				"The default folder for saved cleaned log file.", LogToolSettings.getSavedHistogramsFolder(),
				e -> System.out.println("Not implemented"), true);
		genericSettings.add(mCleanLogFileFolder);

		return CustomComponent.addEmptyBorder(genericSettings, 5);
	}

	private JPanel logSettings() {
		final JPanel logSettings = new JPanel(new GridLayout(5, 1));

		logSettings.add(new JLabel(configuration().getJSONObject("settings_description").getString("logtool")));

		mLogFolder = new SettingEntry("Log folder", "The folder with log files.", LogToolSettings.getLogToolLogFolder(),
				e -> updateLogFolder(), true);
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
				"The folder that contains participant logs.", LogToolSettings.getParticipantLogFolder(),
				e -> updateParticipantLogsFolder(), true);
		participantSettings.add(mParticipantLogFolder);

		mParticipantFile = new SettingEntry("Participant routine file",
				"The file that contains participant's routine data.", LogToolSettings.getParticipantRoutineFile(),
				e -> updateFile(ROUTINE_FILE), true);
		participantSettings.add(mParticipantFile);

		return CustomComponent.addEmptyBorder(participantSettings, 5);
	}

	private void updateParticipantLogsFolder() {
		final FileChooser folderPicker = updatePicker(LogToolSettings.getParticipantLogFolder(),
				"Select the participant logs folder");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update participant logs folder with {}", selectedFile.getName());
			LogToolSettings.setParticipantLogsFolder(selectedFile);
			mParticipantLogFolder.updateProperty(selectedFile.getPath());
		}
		return;
	}

	private void updateLogFolder() {
		final FileChooser folderPicker = updatePicker(LogToolSettings.getLogToolLogFolder(), "Select a log folder");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update logFolder with {}", selectedFile.getName());
			LogToolSettings.setLogToolLogFolder(selectedFile);
			mLogFolder.updateProperty(selectedFile.getPath());
		}
		return;
	}

	private void updateSavedHistogramsFolder() {
		final FileChooser folderPicker = updatePicker(LogToolSettings.getSavedHistogramsFolder(),
				"Select a folder for saved histograms");
		folderPicker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (folderPicker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = folderPicker.getSelectedFile();
			log.debug("Try to update saved histograms folder with {}", selectedFile.getName());
			LogToolSettings.setSavedHistogramsFolder(selectedFile);
			mHistogramFolder.updateProperty(selectedFile.getPath());
		}
		return;
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

	@Override
	public String configurationSection() {
		return "settings";
	}
}
