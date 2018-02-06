package loganalyser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.StringUtils;

import loganalyser.beans.LogFile;
import loganalyser.operators.FileSelector;
import loganalyser.ui.components.LogFrame;
import loganalyser.ui.components.Menu;
import loganalyser.ui.components.Menu.MenuSelector;
import loganalyser.ui.tabs.DeviceTab;
import loganalyser.ui.tabs.FileDetailsTab;
import loganalyser.ui.tabs.HistogramTab;
import loganalyser.ui.tabs.OverviewTab;
import loganalyser.ui.tabs.RequestTab;
import loganalyser.ui.tabs.RoutineTab;
import loganalyser.utils.Configuration;
import loganalyser.utils.Utils;

//TODO: use log file
public class LogHandler2 extends JFrame implements MenuSelector, FileSelector {

	private static final long serialVersionUID = 1L;

	private JPanel mRightContent;
	private String mErrorMsg;
	private Menu mMenu;

	private LogFile mLogFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LogHandler2();
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
	public LogHandler2() throws Exception {
		super("Log Handler");
		Utils.log("Start application", this.getClass());
		setLayout(new BorderLayout());
		setResizable(false);
		setVisible(true);
		setBounds(0, 50, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
	}

	private void init() {
		Utils.log("init parameters", this.getClass());
		JPanel leftFrame = new JPanel(new BorderLayout());
		leftFrame.setBounds(0, 0, Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT);
		leftFrame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		leftFrame.setPreferredSize(new Dimension(Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));

		LogFrame mFilePanel = new LogFrame();
		mFilePanel.addFileSelectorListener(this);
		leftFrame.add(mFilePanel, BorderLayout.PAGE_START);

		mMenu = new Menu();
		mMenu.addNavigationListener(this);
		leftFrame.add(mMenu, BorderLayout.CENTER);

		add(leftFrame, BorderLayout.WEST);

		mRightContent = new JPanel(new BorderLayout());
		mRightContent.setBounds(Configuration.LEFT_MENU_WIDTH, 0,
				Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT);
		mRightContent.setPreferredSize(
				new Dimension(Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));
		add(mRightContent, BorderLayout.EAST);

		mMenu.disableMenu();
		goTo(OverviewTab.class.getSimpleName());
	}

	@Override
	public void validFile(LogFile pLogFile) {
		this.mLogFile = pLogFile;
		mMenu.setEnabled(true);
		mErrorMsg = null;
		goTo(FileDetailsTab.class.getSimpleName());
	}

	@Override
	public void invalidFile(File pInvalidFile, String pCause) {
		mLogFile = null;
		mErrorMsg = String.format("INVALID FILE: %s, %s", pInvalidFile.getName(), pCause);
		mMenu.disableMenu();
		goTo(OverviewTab.class.getSimpleName());
	}

	@Override
	public LogFile getLogFile() {
		return this.mLogFile;
	}

	@Override
	public void checkingFile() {
		mMenu.disableMenu();
	}

	@Override
	public void goTo(String pSection) {
		JPanel jPanel = new JPanel(new BorderLayout());
		if (pSection.equalsIgnoreCase(OverviewTab.class.getSimpleName())) {
			jPanel = StringUtils.isEmpty(mErrorMsg) ? new OverviewTab() : new OverviewTab(mErrorMsg);
		} else if (pSection.equalsIgnoreCase(FileDetailsTab.class.getSimpleName())) {
			jPanel = new FileDetailsTab(this);
		} else if (pSection.equalsIgnoreCase(DeviceTab.class.getSimpleName())) {
			jPanel = new DeviceTab(this);
		} else if (pSection.equalsIgnoreCase(HistogramTab.class.getSimpleName())) {
			jPanel = new HistogramTab(this);
		} else if (pSection.equalsIgnoreCase(RoutineTab.class.getSimpleName())) {
			jPanel = new RoutineTab(this);
		} else if (pSection.equalsIgnoreCase(RequestTab.class.getSimpleName())) {
			jPanel = new RequestTab();
		} else {
			jPanel.add(new JLabel("Not implemented but who cares ?"));
		}

		mRightContent.removeAll();
		mRightContent.add(jPanel, BorderLayout.CENTER);
		mRightContent.validate();
	}

}
