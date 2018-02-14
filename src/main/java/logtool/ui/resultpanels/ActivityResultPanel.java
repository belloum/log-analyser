package logtool.ui.resultpanels;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import logtool.beans.activityresults.ActivityResult;
import logtool.ui.components.ComponentWithImage;
import logtool.utils.Configuration;

public abstract class ActivityResultPanel extends JPanel {

	List<ActivityResult> mActivityResults;

	private static final long serialVersionUID = 1L;

	public ActivityResultPanel(List<ActivityResult> pActivityResults) {
		this.mActivityResults = pActivityResults;
		setLayout(new BorderLayout());

		ComponentWithImage genericResults = new ComponentWithImage(Configuration.IMAGE_PODIUM,
				genericResult(pActivityResults));
		genericResults.setBorder(BorderFactory.createTitledBorder("Global Results"));
		add(genericResults, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(formatResults(pActivityResults));
		add(scroll, BorderLayout.CENTER);
	}

	protected abstract JPanel genericResult(List<ActivityResult> pActivityResults);

	protected abstract JTable formatResults(List<ActivityResult> pActivityResults);
}
