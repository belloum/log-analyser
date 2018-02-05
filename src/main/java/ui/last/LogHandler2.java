package ui.last;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.StringUtils;

import ui.last.components.LogFrame;
import ui.last.components.Menu;
import ui.last.components.Menu.MenuSelector;
import ui.last.tabs.DeviceTab2;
import ui.last.tabs.FileDetailsTab;
import ui.last.tabs.OverviewTab;
import ui.last.tabs.RequestTab;
import ui.last.tabs.HistogramTab;
import ui.last.tabs.RoutineTab2;
import utils.Configuration;

public class LogHandler2 extends JFrame implements MenuSelector, FileSelector {

	private static final long serialVersionUID = 1L;
	private static final String OVERVIEW = "Overview";
	private static final String FILE_DETAILS = "File details";
	private static final String DEVICE = "Devices";
	private static final String HISTOGRAM = "Histograms";
	private static final String ROUTINES = "Routines";
	private static final String SETTINGS = "Settings";
	private static final String REQUEST_MAKER = "Request maker";

	private static final List<String> SECTIONS = Arrays.asList(OVERVIEW, REQUEST_MAKER, FILE_DETAILS, DEVICE, HISTOGRAM,
			ROUTINES, SETTINGS);

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
		setLayout(new BorderLayout());
		setResizable(false);
		setVisible(true);
		setBounds(0, 50, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();
	}

	private void init() {

		JPanel leftFrame = new JPanel(new BorderLayout());
		leftFrame.setBounds(0, 0, Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT);
		leftFrame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		leftFrame.setPreferredSize(new Dimension(Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));

		LogFrame mFilePanel = new LogFrame();
		mFilePanel.addFileSelectorListener(this);
		leftFrame.add(mFilePanel, BorderLayout.PAGE_START);

		mMenu = new Menu(SECTIONS);
		mMenu.addNavigationListener(this);
		leftFrame.add(mMenu, BorderLayout.CENTER);

		add(leftFrame, BorderLayout.WEST);

		mRightContent = new JPanel(new BorderLayout());
		mRightContent.setBounds(Configuration.LEFT_MENU_WIDTH, 0,
				Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT);
		mRightContent.setPreferredSize(
				new Dimension(Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));
		add(mRightContent, BorderLayout.EAST);

		resetMenu();
		goTo(OVERVIEW);
	}

	private void resetMenu() {
		mMenu.disableMenu();
		mMenu.enablePosition(true, SECTIONS.indexOf(OVERVIEW));
		mMenu.enablePosition(true, SECTIONS.indexOf(REQUEST_MAKER));
	}

	@Override
	public void validFile(LogFile pLogFile) {
		this.mLogFile = pLogFile;
		mMenu.setEnabled(true);
		mErrorMsg = null;
		goTo(FILE_DETAILS);
	}

	@Override
	public void invalidFile(File pInvalidFile, String pCause) {
		mLogFile = null;
		mErrorMsg = String.format("INVALID FILE: %s, %s", pInvalidFile.getName(), pCause);
		System.err.println(mErrorMsg);
		mMenu.disableMenu();
		goTo(OVERVIEW);
	}

	@Override
	public void goTo(String pSection) {
		JPanel jPanel = new JPanel(new BorderLayout());
		switch (pSection) {
		case OVERVIEW:
			jPanel = StringUtils.isEmpty(mErrorMsg) ? new OverviewTab() : new OverviewTab(mErrorMsg);
			mErrorMsg = null;
			break;
		case FILE_DETAILS:
			jPanel = new FileDetailsTab(this);
			break;
		case DEVICE:
			jPanel = new DeviceTab2(this);
			break;
		case HISTOGRAM:
			jPanel = new HistogramTab(this);
			break;
		case ROUTINES:
			jPanel = new RoutineTab2(this);
			break;
		case REQUEST_MAKER:
			jPanel = new RequestTab();
			break;
		default:
			jPanel.add(new JLabel("Not implemented but who cares ?"));
			break;
		}
		mRightContent.removeAll();
		mRightContent.add(jPanel, BorderLayout.CENTER);
		mRightContent.validate();
	}

	@Override
	public LogFile getLogFile() {
		return this.mLogFile;
	}

}
