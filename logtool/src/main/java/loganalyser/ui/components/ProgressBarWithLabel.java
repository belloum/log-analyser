package loganalyser.ui.components;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarWithLabel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JLabel mProgressText;
	private final JProgressBar mProgressBar;

	public ProgressBarWithLabel() {
		setLayout(new BorderLayout());
		mProgressBar = new JProgressBar(0, 100);
		mProgressText = new JLabel();
		build();
	}

	private void build() {
		add(mProgressText, BorderLayout.PAGE_START);
		add(mProgressBar, BorderLayout.CENTER);
	}

	public void setProgressText(String pProgresstext) {
		setVisible(true);
		this.mProgressText.setText(pProgresstext);
	}

	public void showProgress() {
		setVisible(true);
		mProgressBar.setValue(mProgressBar.getMinimum());
	}

	public void setProgressValue(int pProgress) {
		mProgressBar.setValue(pProgress);
	}

	public void hideProgress() {
		setVisible(false);
	}

}
