package ui.components;

import java.awt.Color;
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
	private static final String NO_FILE_PROVIDED = "Please select a file ...";

	private File mSelectedFile;
	private JTextArea mJLabelRawLog;
	private JFileChooser mJFileChooser;
	private ResultLabel mJLabelResults;
	private JButton mJButton;
	private ActionListener mPickFileListener;

	public FileChooserWithResult(String pLegend, File pCurrentDirectory, String pButtonLabel) {
		super();

		this.mJFileChooser = new JFileChooser(pCurrentDirectory);
		this.mJLabelRawLog = new JTextArea(NO_FILE_PROVIDED);
		this.mJLabelRawLog.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		addComponentHorizontally(this.mJLabelRawLog, (int) (0.85 * MAX_WIDTH), false);

		this.mJButton = new JButton(pButtonLabel);
		addComponentHorizontally(this.mJButton, (int) (MAX_WIDTH - xOffset), false);

		this.mJLabelRawLog.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (StringUtils.isNotEmpty(mJLabelRawLog.getText())) {
					System.out.println(
							"File (" + mJLabelRawLog.getText() + ")? " + new File(mJLabelRawLog.getText()).exists());
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

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel) {
		this(null, pCurrentDirectory, pButtonLabel);
	}

	public FileChooserWithResult(String pLegend, File pCurrentDirectory) {
		this(pLegend, pCurrentDirectory, "Browse");
	}

	public FileChooserWithResult(File pCurrentDirectory) {
		this(null, pCurrentDirectory);
	}

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ActionListener pPickFileListener) {
		this(pCurrentDirectory, pButtonLabel);
		this.mPickFileListener = pPickFileListener;
		this.mJButton.addActionListener(this.mPickFileListener);
	}

	public JButton getJButton() {
		return mJButton;
	}

	public void setActionListener(ActionListener pPickFileListener) {
		this.mPickFileListener = pPickFileListener;
		this.mJButton.addActionListener(pPickFileListener);
	}

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
			this.mJLabelRawLog.setText(NO_FILE_PROVIDED);
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

}
