package ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import operators.extractors.ParticipantExtractor;
import utils.Configuration;

public class LogMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// UI Components
	private JFrame mJFrameTabParticipant, mJFrameTabSettings;
	private JFileChooser mJFileChooser;
	private JComboBox<String> mParticipantsPicker;

	private static final Integer MARGINS = 40;
	private static final Integer BUTTON_WIDTH = 100;
	private static final Integer LABEL_WIDTH = 200;
	private static final Integer PADDING_RIGHT = 10;
	private static final Integer ITEM_HEIGHT = 20;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LogMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the form
	 * 
	 * @throws Exception
	 */
	public LogMenu() throws Exception {
		super("Log Handler");
		setVisible(true);
		setBounds(100, 100, 900, 489);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane mJTabbedPane = new JTabbedPane();
		mJFrameTabParticipant = initTabParticipant();
		mJTabbedPane.addTab("Participants", mJFrameTabParticipant.getContentPane());
		mJFrameTabSettings = initTabSettings();
		mJTabbedPane.addTab("Settings", mJFrameTabSettings.getContentPane());

		add(mJTabbedPane);
	}

	private JFrame initTabParticipant() throws Exception {
		JFrame frame = new JFrame();

		/////////////////////////////
		// Select a file
		int x = MARGINS;
		int y = MARGINS / 2;

		if (!Configuration.RESOURCES_FOLDER.exists())
			Configuration.RESOURCES_FOLDER.mkdirs();

		mJFileChooser = new JFileChooser(Configuration.RESOURCES_FOLDER);
		JButton pickFile = new JButton("Select a file");
		pickFile.setBounds(x, y, BUTTON_WIDTH, ITEM_HEIGHT);

		x += BUTTON_WIDTH + PADDING_RIGHT;
		JLabel label = new JLabel("No file provided");
		label.setBounds(x, y, LABEL_WIDTH, ITEM_HEIGHT);

		pickFile.addActionListener(mPickFile);
		mPickFile.setJLabelResult(label);
		frame.getContentPane().add(pickFile);
		frame.getContentPane().add(label);

		/////////////////////////////
		// Select a participant
		x = MARGINS;
		y += MARGINS;
		mParticipantsPicker = new JComboBox<String>();
		mParticipantsPicker.addItem("Select a userbox");
		ParticipantExtractor.extractParticipants(Configuration.PARTICIPANT_FILE)
				.forEach((participant) -> mParticipantsPicker.addItem(participant.getName()));
		mParticipantsPicker.setBounds(x, y, LABEL_WIDTH, ITEM_HEIGHT);
		mParticipantsPicker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					System.out.println("Selected userbox: " + e.getItem().toString());
				}
			}
		});
		frame.getContentPane().add(mParticipantsPicker);

		/////////////////////////////
		// Blank
		x = MARGINS;
		y += MARGINS;
		JFrame blank = new JFrame();
		blank.setBounds(x, y, BUTTON_WIDTH, ITEM_HEIGHT);
		frame.getContentPane().add(blank.getContentPane());

		return frame;
	}

	private JFrame initTabSettings() throws Exception {
		JFrame frame = new JFrame();
		/////////////////////////////
		// Not implemented yet
		int x = MARGINS;
		int y = MARGINS / 2;

		JLabel label = new JLabel("Not implemeted yet");
		label.setBounds(x, y, LABEL_WIDTH, ITEM_HEIGHT);
		frame.getContentPane().add(label);

		/////////////////////////////
		// Blank
		x = MARGINS;
		y += MARGINS;
		JFrame blank = new JFrame();
		blank.setBounds(x, y, BUTTON_WIDTH, ITEM_HEIGHT);
		frame.getContentPane().add(blank.getContentPane());

		return frame;
	}

	public interface PickLogFile extends ActionListener {
		void setLogFile(File pLogFile);

		void setJLabelResult(JLabel pJLabelResult);
	}

	private PickLogFile mPickFile = new PickLogFile() {

		private File mLogFile;
		private JLabel mJLabelResult;

		@Override
		public void actionPerformed(ActionEvent e) {
			int retour = mJFileChooser.showOpenDialog(null);
			if (retour == JFileChooser.APPROVE_OPTION) {
				System.out.println("Selected file: " + mJFileChooser.getSelectedFile().getName());

				if (mJLabelResult != null && mJLabelResult.isVisible()) {
					this.mJLabelResult.setText(mJFileChooser.getSelectedFile().getName());
				}
				setLogFile(mJFileChooser.getSelectedFile());
			}
		}

		@Override
		public void setLogFile(File pLogFile) {
			this.mLogFile = pLogFile;
		}

		@Override
		public void setJLabelResult(JLabel pJLabelResult) {
			this.mJLabelResult = pJLabelResult;
		}

		public File getLogFile() {
			return this.mLogFile;
		}

	};

}
