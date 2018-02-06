package loganalyser.ui.panels.results;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.ui.components.ComponentWithImage;
import loganalyser.utils.Configuration;

public abstract class ActivityResultPanel extends JPanel {

	List<ActivityResult> mActivityResults;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivityResultPanel(List<ActivityResult> pActivityResults) {
		this.mActivityResults = pActivityResults;
		setLayout(new BorderLayout());

		ComponentWithImage genericResults = new ComponentWithImage(Configuration.IMAGE_PODIUM,
				genericResult(pActivityResults));
		genericResults.setBorder(BorderFactory.createTitledBorder("Global Results"));
		add(genericResults, BorderLayout.NORTH);

		add(detilledResults(pActivityResults), BorderLayout.CENTER);
	}

	protected abstract JPanel genericResult(List<ActivityResult> pActivityResults);

	protected abstract JScrollPane detilledResults(List<ActivityResult> pActivityResults);

}
