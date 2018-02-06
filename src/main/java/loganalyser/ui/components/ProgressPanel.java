package loganalyser.ui.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class ProgressPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JProgressBar mProgressBar;
	private Runnable mRunnable;

	public ProgressPanel(JComponent pContent, Runnable pRunnable) {
		setLayout(new BorderLayout());
		replaceBorder(pContent);

		this.mProgressBar = new JProgressBar(0, 100);
		this.mRunnable = pRunnable;
		add(this.mProgressBar, BorderLayout.CENTER);
	}

	public void setProgress(int pProgress) {
		getProgressBar().setValue(pProgress);
	}

	public void start() {
		new Thread(this.mRunnable).start();
	}

	private void replaceBorder(JComponent pComponent) {
		setBorder(pComponent.getBorder());
		pComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public JProgressBar getProgressBar() {
		return this.mProgressBar;
	}

	public void updateContent(JComponent pComponent) {
		removeAll();
		if (pComponent.getBorder() != null) {
			replaceBorder(pComponent);
		}
		add(pComponent, BorderLayout.CENTER);
		getProgressBar().setVisible(false);
		validate();
	}
}
