package ui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import utils.Configuration;

public class FileChooserWithResult extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private static final String SELECT_A_FILE = "Please select a file ...";
	private static final String DEFAULT_LABEL = "Browse";

	private File mSelectedFile;
	private JTextArea mJLabelRawLog;
	private JFileChooser mJFileChooser;
	private ResultLabel mJLabelResults;
	private JButton mJButton;
	private ActionListener mPickFileListener;
	private FileChooserListener mFileChooserListener;

	public FileChooserWithResult(String pLegend, File pCurrentDirectory, String pButtonLabel,
			FileChooserListener pFCListener) {
		super();

		this.mJFileChooser = new JFileChooser(pCurrentDirectory);
		this.mJLabelRawLog = new JTextArea(SELECT_A_FILE);
		this.mJLabelRawLog.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		addComponentHorizontally(this.mJLabelRawLog, (int) (0.85 * MAX_WIDTH), false);

		this.mFileChooserListener = pFCListener != null ? pFCListener : new FileChooserListener() {

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

		this.mJButton = new JButton(pButtonLabel);
		this.mJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mJFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = mJFileChooser.getSelectedFile();
					mJLabelRawLog.setText(file.getPath());
					System.out.println("File (" + file.getPath() + ")? " + file.exists());
					if (!file.exists()) {
						mFileChooserListener.fileDoesNotExist(file);
					} else {
						mFileChooserListener.fileExists(file);
					}
				}
			}
		});
		addComponentHorizontally(this.mJButton, (int) (MAX_WIDTH - xOffset), false);

		this.mJLabelRawLog.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (StringUtils.isNotEmpty(mJLabelRawLog.getText()) && !SELECT_A_FILE.equals(mJLabelRawLog.getText())) {
					String path = mJLabelRawLog.getText();
					File file = new File(path);
					System.out.println("File (" + path + ")? " + file.exists());
					if (!file.exists()) {
						mFileChooserListener.fileDoesNotExist(file);
					} else {
						mFileChooserListener.fileExists(file);
					}
				}
			}
		});

		xOffset = 0;
		yOffset += Configuration.ITEM_HEIGHT + Configuration.PADDING;
		this.mJLabelResults = new ResultLabel();
		addComponentHorizontally(this.mJLabelResults, this.MAX_WIDTH, false);
		yOffset += Configuration.ITEM_HEIGHT + Configuration.PADDING;

		xOffset += Configuration.LABEL_WIDTH_LONG + Configuration.PADDING;
		setSize(Configuration.MAX_WIDTH / 2, yOffset);
	}

	public FileChooserWithResult(File pCurrentDirectory, FileChooserListener pFCListener) {
		this(null, pCurrentDirectory, null, pFCListener);
	}

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel) {
		this(null, pCurrentDirectory, pButtonLabel, null);
	}

	public FileChooserWithResult(String pLegend, File pCurrentDirectory) {
		this(pLegend, pCurrentDirectory, DEFAULT_LABEL, null);
	}

	public FileChooserWithResult(String pLegend, File pCurrentDirectory, FileChooserListener pFCListener) {
		this(pLegend, pCurrentDirectory, DEFAULT_LABEL, pFCListener);
	}

	public FileChooserWithResult(File pCurrentDirectory) {
		this(null, pCurrentDirectory);
	}

	public FileChooserListener getFileChooserListener() {
		return mFileChooserListener;
	}

	public void setFileChooserListener(FileChooserListener pListener) {
		this.mFileChooserListener = pListener;
	}

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ActionListener pPickFileListener) {
		this(pCurrentDirectory, pButtonLabel);
		this.mPickFileListener = pPickFileListener;
		this.mJButton.addActionListener(this.mPickFileListener);
	}

	public JButton getJButton() {
		return mJButton;
	}

	// public void setActionListener(ActionListener pPickFileListener) {
	// this.mPickFileListener = pPickFileListener;
	// this.mJButton.addActionListener(pPickFileListener);
	// }

	public JFileChooser getJFileChooser() {
		return mJFileChooser;
	}

	public ResultLabel getResultLabel() {
		return mJLabelResults;
	}

	public ActionListener getPickFileListener() {
		return mPickFileListener;
	}

	public void setSelected(File pSelectedFile) {
		this.mSelectedFile = pSelectedFile;
		if (pSelectedFile == null) {
			this.mJLabelRawLog.setText(SELECT_A_FILE);
		} else {
			this.mJLabelRawLog.setText(this.mSelectedFile.getPath());
		}
	}

	public File getSelectedFile() {
		return this.mSelectedFile;
	}

	public void setMaxWidth(int pMaxWidth) {
		this.MAX_WIDTH = pMaxWidth - 2 * Configuration.MARGINS;
	}

	public interface FileChooserListener {
		void fileDoesNotExist(File pFile);

		void fileExists(File pFile);
	}

}
