package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import tabs.TabLog;
import tabs.TabRequest;
import utils.Configuration;

public class LogHandler extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LogHandler();
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
	public LogHandler() throws Exception {
		super("Log Handler");
		setResizable(false);
		setVisible(true);
		setBounds(100, 100, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Requests", new TabRequest("Requests").getContentPane());
		tabbedPane.addTab("Logs", new TabLog("Logs").getContentPane());
		add(tabbedPane);
	}

}
