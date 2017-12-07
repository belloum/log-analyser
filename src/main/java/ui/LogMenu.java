package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import ui.mytabs.TabParticipants;
import ui.mytabs.TabSettings;
import utils.Configuration;

public class LogMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// UI Components
	private JFrame mJFrameTabParticipant, mJFrameTabSettings;

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
		setBounds(100, 100, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane mJTabbedPane = new JTabbedPane();
		mJFrameTabParticipant = new TabParticipants("Participants");
		mJTabbedPane.addTab("Userbox", mJFrameTabParticipant.getContentPane());
		mJFrameTabSettings = new TabSettings("Settings");
		mJTabbedPane.addTab("Settings", mJFrameTabSettings.getContentPane());

		add(mJTabbedPane);
	}

}
