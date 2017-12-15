package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import utils.Configuration;

public class FileChooserWithResult extends CustomComponent {
	
	private static final long serialVersionUID = 1L;
	private static final String SELECT_A_FILE = "Please select a file ...";
	private static final String DEFAULT_BUTTON_TEXT = "Browse";

	private File mSelectedFile;
	private JTextArea mJTAPathFile = new JTextArea(SELECT_A_FILE);
	private JFileChooser mJFileChooser = new JFileChooser();
	private ResultLabel mJLResults = new ResultLabel();
	private JButton mJButton = new JButton(DEFAULT_BUTTON_TEXT);
	private ActionListener mPickFileListener;
	private ChoiceListener mChoiceListener = new ChoiceListener() {

		@Override
		public void fileDoesNotExist(File pFile) {
			String msg = new StringBuffer("The selected file `").append(pFile.getPath()).append("` does not exist.")
					.toString();
			System.out.println(msg);
		}

		@Override
		public void fileExists(File pFile) {

		}
	};

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ChoiceListener pFCListener) {
		setLayout(new BorderLayout());

		this.mJFileChooser.setCurrentDirectory(pCurrentDirectory);
		this.mJTAPathFile.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		if (pFCListener != null) {
			this.mChoiceListener = pFCListener;
		}

		this.mJButton.setText(pButtonLabel);
		this.mJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mJFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = mJFileChooser.getSelectedFile();
					mJTAPathFile.setText(file.getPath());
					System.out.println("File (" + file.getPath() + ")? " + file.exists());
					if (!file.exists()) {
						mChoiceListener.fileDoesNotExist(file);
					} else {
						mChoiceListener.fileExists(file);
					}
				}
			}
		});

		this.mJTAPathFile.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (StringUtils.isNotEmpty(mJTAPathFile.getText()) && !SELECT_A_FILE.equals(mJTAPathFile.getText())) {
					String path = mJTAPathFile.getText();
					File file = new File(path);
					System.out.println("File (" + path + ")? " + file.exists());
					if (!file.exists()) {
						mChoiceListener.fileDoesNotExist(file);
					} else {
						mChoiceListener.fileExists(file);
					}
				}
			}
		});

		build();
		System.out.println(getPreferredSize());
	}

	public FileChooserWithResult(File pCurrentDirectory, ChoiceListener pFCListener) {
		this(pCurrentDirectory, DEFAULT_BUTTON_TEXT, pFCListener);
	}

	public void setFileChooserListener(ChoiceListener pListener) {
		this.mChoiceListener = pListener;
	}

	public JFileChooser getJFileChooser() {
		return mJFileChooser;
	}

	public ResultLabel getResultLabel() {
		return mJLResults;
	}

	public ActionListener getPickFileListener() {
		return mPickFileListener;
	}

	public void setSelected(File pSelectedFile) {
		this.mSelectedFile = pSelectedFile;
		if (pSelectedFile == null) {
			this.mJTAPathFile.setText(SELECT_A_FILE);
		} else {
			this.mJTAPathFile.setText(this.mSelectedFile.getPath());
		}
	}

	public File getSelectedFile() {
		return this.mSelectedFile;
	}

	public void setMaxWidth(int pMaxWidth) {
		this.MAX_WIDTH = pMaxWidth - 2 * Configuration.MARGINS;
	}

	protected void build() {
		//TODO update here
		setMaximumSize(new Dimension(MAX_WIDTH, 2 * Configuration.ITEM_HEIGHT));
		setPreferredSize(new Dimension(MAX_WIDTH, 2 * Configuration.ITEM_HEIGHT));
		setMinimumSize(new Dimension(MAX_WIDTH, 2 * Configuration.ITEM_HEIGHT));

		JPanel jPan = new JPanel(new BorderLayout());
		add(jPan, BorderLayout.PAGE_START);
		jPan.setPreferredSize(new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));

		jPan.add(mJTAPathFile, BorderLayout.LINE_START);
		mJTAPathFile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		int a = MAX_WIDTH * 80 / 100;
		mJTAPathFile.setPreferredSize(new Dimension(a, Configuration.ITEM_HEIGHT));
		mJTAPathFile.setMinimumSize(new Dimension(a, Configuration.ITEM_HEIGHT));

		jPan.add(mJButton, BorderLayout.LINE_END);
		mJButton.setPreferredSize(new Dimension(100, Configuration.ITEM_HEIGHT));

		add(mJLResults, BorderLayout.PAGE_END);
		mJLResults.setPreferredSize(new Dimension(a, Configuration.ITEM_HEIGHT));
		mJLResults.setBackground(Color.GREEN);
	}

	public interface ChoiceListener {
		void fileDoesNotExist(File pFile);

		void fileExists(File pFile);
	}

}
