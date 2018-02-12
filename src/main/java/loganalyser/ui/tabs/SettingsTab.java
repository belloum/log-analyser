package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loganalyser.ui.components.FileChooser;
import loganalyser.ui.components.SettingEntry;
import loganalyser.utils.Configuration;

public class SettingsTab extends MyCustomTab {
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
				Configuration.CONFIG_FOLDER.getPath());
		genericSettings.add(mConfigFileSetting);

		mParticipantFile = new SettingEntry("Participant file", "The file where participant data are gathered.",
				Configuration.CONFIG_FOLDER.getPath());
		genericSettings.add(mParticipantFile);

		mDefaultLogFolder = new SettingEntry("Default log folder", "The folder with log files",
				Configuration.RESOURCES.getPath());
		genericSettings.add(mDefaultLogFolder);

		mLogFile = new SettingEntry("Log file", "The file where logs are gathered.", Configuration.LOG_FILE.getPath());
		genericSettings.add(mLogFile);

		mLogErrorFile = new SettingEntry("Log error file", "The file where error logs are gathered.",
				Configuration.LOG_ERROR_FILE.getPath());
		mLogErrorFile.addActionListener(e -> updateLogErrorFile());
		genericSettings.add(mLogErrorFile);

		return genericSettings;
	}

	@Override
	public String configurationSection() {
		return "settings";
	}

	private File chooseDirectory(String pDialogTitle) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(pDialogTitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		//
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	private void updateLogErrorFile() {
		File errorFile = chooseLogFile();
		if (errorFile != null) {
			log.debug("Try to update errorFile with {}", errorFile.getName());
			// TODO: update log error file
		} else {
			error("No log error file selected");
		}
	}

	private File chooseLogFile() {
		final FileChooser fileChooser = new FileChooser(new File(""), "Select a log file",
				Arrays.asList(new FileNameExtensionFilter("Log file", "json")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}
}
