package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import exceptions.RawLogException;
import ui.components.LogInfoComponent;
import ui.menus.Menu;
import ui.menus.Menu.MenuSelector;
import ui.menus.tabs.DeviceTab;
import ui.menus.tabs.HistogramTab;
import ui.menus.tabs.SelectionFileTab;
import ui.menus.tabs.SelectionFileTab.SelectionListener;
import utils.Configuration;

public class LogHandler extends JFrame implements MenuSelector, SelectionListener {

	private static final long serialVersionUID = 1L;
	private static final String SELECT_A_FILE = "Select a file";
	private static final String DEVICE = "Devices";
	private static final String PERIOD = "Periode";
	private static final String HISTOGRAM = "Histograms";
	private static final String ROUTINES = "Routines";
	private static final String CSV = "CSV";

	private static final List<String> SECTIONS = Arrays.asList(SELECT_A_FILE, DEVICE, PERIOD, HISTOGRAM, ROUTINES, CSV);

	private JPanel mRightContent;
	private Menu mLeftMenu;
	private JPanel mLogFileInfo;

	private File mLogFile;

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
		setBounds(0, 0, Configuration.MAX_WIDTH, Configuration.MAX_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		mLeftMenu = new Menu(SECTIONS, this);
		JPanel jPanelLeft = new JPanel();
		jPanelLeft.setLayout(new BoxLayout(jPanelLeft, BoxLayout.Y_AXIS));
		jPanelLeft.add(mLeftMenu);
		jPanelLeft.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		mLogFileInfo = new JPanel(new BorderLayout());
		jPanelLeft.add(mLogFileInfo);

		add(jPanelLeft, BorderLayout.LINE_START);

		mRightContent = new JPanel(new BorderLayout());
		mRightContent.setPreferredSize(
				new Dimension(Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));
		add(mRightContent, BorderLayout.CENTER);

		mLeftMenu.resetMenu();

		selectItem(0);
	}

	@Override
	public void selectItem(int pPosition) {
		String section = SECTIONS.get(pPosition);
		JPanel jPanel = new JPanel(new BorderLayout());
		switch (section) {
		case SELECT_A_FILE:
			jPanel = new SelectionFileTab(this);
			break;
		case DEVICE:
			jPanel = new DeviceTab(this.mLogFile);
			break;
		case HISTOGRAM:
			jPanel = new HistogramTab(this.mLogFile);
			break;
		default:
			jPanel.add(new JLabel("Not implemented yet"));
			break;
		}
		mRightContent.removeAll();
		mRightContent.add(jPanel, BorderLayout.CENTER);
		mRightContent.validate();
		validate();
		System.out.println(this.mRightContent.getSize() + " - ho");
	}

	@Override
	public void invalidFile(File pFile, String pCause) {
		System.out.println(pFile.getName() + " is not valid: " + pCause);
		mLogFile = null;
		mLeftMenu.resetMenu();
	}

	@Override
	public void validFile(File pFile) {
		System.out.println(pFile.getName() + " is valid");
		mLeftMenu.setEnabled(true);
		mLogFile = pFile;

		try {
			LogInfoComponent logInfo = new LogInfoComponent(pFile);
			mLogFileInfo.add(logInfo, BorderLayout.PAGE_END);
		} catch (RawLogException e) {
			System.out.println(e);
		}
	}
}
