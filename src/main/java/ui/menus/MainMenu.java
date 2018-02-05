package ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import exceptions.RawLogException;
import operators.extractors.RawLogFormater;
import ui.components.FileChooser;
import utils.ChooseFileListener;
import utils.Configuration;

public class MainMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private ChooseFileListener mChooseFileListener;

	public MainMenu(ChooseFileListener pChooseFileListener) {
		super();
		this.mChooseFileListener = pChooseFileListener;
		initMenu();
	}

	private void initMenu() {
		JMenu file = new JMenu("File");

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});

		JMenuItem importItem = new JMenuItem("Select a file");
		importItem.addActionListener(mValidFileListener);

		file.add(importItem);
		file.add(exitItem);

		add(file);
	}

	ActionListener mValidFileListener = (ActionEvent event) -> {
		FileChooser fc = new FileChooser(Configuration.RESOURCES_FOLDER, "Select a log file");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Log file", "json");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File output = fc.getSelectedFile();
			try {
				RawLogFormater.validateRawLogFile(output);
				mChooseFileListener.validFile(output);
			} catch (RawLogException e) {
				mChooseFileListener.invalidFile(output, e.getMessage());
			}
		}
	};

}
