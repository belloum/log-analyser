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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import loganalyser.ui.tabs.SettingsTab;
import loganalyser.utils.Configuration;

//FIXME use log file
//TODO add ParticipantTab implementation
//TODO add a loading for configuration
public class LogTool extends JFrame implements MenuSelector, FileSelector {

	private static final Logger log = LoggerFactory.getLogger(LogTool.class);
	private static final long serialVersionUID = 1L;

	private JPanel mRightContent;
	private String mErrorMsg;
	private Menu mMenu;

	private LogFile mLogFile;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new LogTool();
				} catch (final Exception e) {
					log.error("Unable to start application: {}", e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Create the form
	 * 
	 * @throws Exception
	 */
	public LogTool() throws Exception {
		super("LogTool");
		log.info("Start application-info");
		init();
	}

	private void init() {
		log.info("init parameters");
		setLayout(new BorderLayout());
		setResizable(false);
		setVisible(true);
		setBounds(0, 50, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel leftFrame = new JPanel(new BorderLayout());
		leftFrame.setBounds(0, 0, Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT);
		leftFrame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		leftFrame.setPreferredSize(new Dimension(Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));

		final LogFrame mFilePanel = new LogFrame();
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
	public void validFile(final LogFile pLogFile) {
		this.mLogFile = pLogFile;
		mMenu.setEnabled(true);
		mErrorMsg = null;
		goTo(FileDetailsTab.class.getSimpleName());
	}

	@Override
	public void invalidFile(final File pInvalidFile, final String pCause) {
		mLogFile = null;
		mErrorMsg = String.format("INVALID FILE: %s, %s", pInvalidFile.getName(), pCause);
		mMenu.disableMenu();
		log.error("Invalid file: {}, {}", pInvalidFile.getName(), pCause);
		goTo(OverviewTab.class.getSimpleName());
	}

	@Override
	public LogFile getLogFile() {
		return this.mLogFile;
	}

	@Override
	public void checkingFile() {
		goTo(OverviewTab.class.getSimpleName());
		mMenu.disableMenu();
	}

	@Override
	public void goTo(final String pSection) {
		log.debug("Go to {}", pSection);
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
		} else if (pSection.equalsIgnoreCase(SettingsTab.class.getSimpleName())) {
			jPanel = new SettingsTab();
		} else {
			jPanel.add(new JLabel("Not implemented but who cares ?"));
		}

		mRightContent.removeAll();
		mRightContent.add(jPanel, BorderLayout.CENTER);
		mRightContent.validate();
	}

}
