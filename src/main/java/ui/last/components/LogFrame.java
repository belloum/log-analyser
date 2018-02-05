package ui.last.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import exceptions.RawLogException;
import ui.components.CustomComponent;
import ui.components.FileChooser;
import ui.components.MyButton;
import ui.last.FileSelector;
import ui.last.LogFile;
import utils.Configuration;
import utils.Utils;

public class LogFrame extends JPanel {

	// TODO Show progress while checking validity

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FileSelector mFileListener;

	private static final String NO_FILE_SELECTED = "No file selected";
	private static final int IMG_DIMENSION = 40;

	private JLabel mFileName = new JLabel(NO_FILE_SELECTED);
	private JLabel mLogCount = new JLabel("0");

	public LogFrame() {
		setLayout(new BorderLayout());
		init();
	}

	private void init() {
		setBorder(BorderFactory.createTitledBorder("Current file"));

		try {
			add(new JLabel(new ImageIcon(Utils.scaleImg(Configuration.IMAGE_LOG_FILE, IMG_DIMENSION, IMG_DIMENSION))),
					BorderLayout.PAGE_START);
		} catch (IOException ignored) {
			System.err.println("Hay una problema");
		}

		JPanel logInfo = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;

		// Name
		gbc.gridx = 0;
		gbc.gridy = 0;
		logInfo.add(CustomComponent.boldLabel("Name"), gbc);
		gbc.gridy++;
		logInfo.add(mFileName, gbc);

		// Log
		gbc.gridx = 0;
		gbc.gridy++;
		logInfo.add(CustomComponent.boldLabel("Logs"), gbc);
		gbc.gridy++;
		logInfo.add(mLogCount, gbc);

		add(logInfo, BorderLayout.CENTER);

		final FileChooser fileChooser = new FileChooser(Configuration.RESOURCES_FOLDER, "Select a log file",
				Arrays.asList(new FileNameExtensionFilter("Log file", "json")));

		add(new MyButton("Select a file", event -> {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				checkLogFile(fileChooser.getSelectedFile());
			}
		}), BorderLayout.PAGE_END);

	}

	public void addFileSelectorListener(FileSelector pSelector) {
		this.mFileListener = pSelector;
	}

	public void checkLogFile(File pSelectedFile) {
		// FIXME start progress
		try {
			validFile(new LogFile(pSelectedFile));
		} catch (RawLogException e) {
			System.err.println(e.getMessage());
			invalidFile(pSelectedFile, e.getMessage());
		}
		// FIXME stop progress
	}

	public void validFile(LogFile pLogFile) throws RawLogException {
		mFileName.setText(pLogFile.getName());
		mLogCount.setText(String.format("%d", pLogFile.getLogCount()));
		if (mFileListener != null) {
			mFileListener.validFile(pLogFile);
		}
	}

	public void invalidFile(File pInvalidFile, String pCause) {
		mLogCount.setText("0");
		mFileName.setText(NO_FILE_SELECTED);
		if (mFileListener != null) {
			mFileListener.invalidFile(pInvalidFile, pCause);
		}
	}

}
