package loganalyser.old.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import loganalyser.utils.Configuration;

public class FileChooserWithResult extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private static final String SELECT_A_FILE = "Please select a file ...";
	private static final String DEFAULT_BUTTON_TEXT = "Browse";

	private File mSelectedFile;
	private JTextArea mJTAPathFile = new JTextArea(SELECT_A_FILE);
	private JFileChooser mJFileChooser = new JFileChooser();
	private ResultLabel mJLResults = new ResultLabel("");
	private JButton mJButton = new JButton(DEFAULT_BUTTON_TEXT);
	private ActionListener mPickFileListener;
	private ChoiceListener mChoiceListener = new ChoiceListener() {

		@Override
		public void fileDoesNotExist(File pFile) {

		}

		@Override
		public void fileExists(File pFile) {

		}
	};

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ChoiceListener pFCListener,
			Integer pMaxWidth) {
		this(pCurrentDirectory, pButtonLabel, pFCListener, new Dimension(pMaxWidth, Configuration.ITEM_HEIGHT));
	}

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ChoiceListener pFCListener,
			Integer pMaxWidth, Predicate<?> pPredicate) {
		this(pCurrentDirectory, pButtonLabel, pFCListener, new Dimension(pMaxWidth, Configuration.ITEM_HEIGHT));
	}

	public FileChooserWithResult(File pCurrentDirectory, String pButtonLabel, ChoiceListener pFCListener,
			Dimension pDimension) {
		super(pDimension, new BorderLayout());

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
	}

	public FileChooserWithResult(File pCurrentDirectory, ChoiceListener pFCListener, Integer pMaxWidth) {
		this(pCurrentDirectory, DEFAULT_BUTTON_TEXT, pFCListener, pMaxWidth);
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

	protected void build() {
		JPanel jPan = new JPanel(new BorderLayout());
		add(jPan, BorderLayout.PAGE_START);
		jPan.setPreferredSize(getMaximumSize());

		jPan.add(mJTAPathFile, BorderLayout.LINE_START);
		mJTAPathFile.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		jPan.add(mJButton, BorderLayout.LINE_END);
		add(mJLResults, BorderLayout.PAGE_END);

		mJTAPathFile.setPreferredSize(new Dimension(80 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		mJTAPathFile.setMinimumSize(mJTAPathFile.getPreferredSize());

		mJButton.setPreferredSize(new Dimension(15 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		mJButton.setMinimumSize(mJButton.getPreferredSize());

		mJLResults.setPreferredSize(new Dimension(getMaximumSize().width, Configuration.ITEM_HEIGHT));
		mJLResults.setMinimumSize(mJLResults.getPreferredSize());

		setPreferredSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
	}

	public interface ChoiceListener {
		void fileDoesNotExist(File pFile);

		void fileExists(File pFile);
	}

}
