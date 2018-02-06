package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

//TODO implement circular progress bar in the east of the layout
public class TabHeaderWithProgress extends TabHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// FIXME must be replace by a circular indeterminate progress
	private JLabel mJLabel;
	private JProgressBar mJProgress;

	public TabHeaderWithProgress(String pTitle, String pDescription, File pImage) {
		super(pTitle, pDescription, pImage);
		mJLabel = new JLabel("Progress");
		mJProgress = new JProgressBar(0, 100);
		mJProgress.setVisible(false);
		add(mJLabel, BorderLayout.EAST);
		add(mJProgress, BorderLayout.EAST);
	}

	public void showProgress() {
		mJLabel.setText("Progress");
		mJProgress.setVisible(true);
		mJProgress.setValue(0);
		validate();
	}

	public void hideProgress() {
		mJProgress.setVisible(false);
		mJLabel.setVisible(false);
		validate();
	}

	public void setProgressText(String pProgressTxt) {
		mJLabel.setVisible(true);
		mJLabel.setText(pProgressTxt);
		validate();
	}

	public void updateProgress(float pPropgress) {
		if (!mJProgress.isVisible()) {
			mJProgress.setVisible(true);
		}
		mJProgress.setValue((int) pPropgress);
	}

}
