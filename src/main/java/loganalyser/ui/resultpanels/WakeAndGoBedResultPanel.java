package loganalyser.ui.resultpanels;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JTable;

import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.ui.components.ComponentUtils;
import loganalyser.ui.components.LegendValueLabel;
import loganalyser.ui.components.MyTable;

public class WakeAndGoBedResultPanel extends ActivityResultPanel {

	private static final long serialVersionUID = 1L;

	public WakeAndGoBedResultPanel(List<ActivityResult> pActivityResults) {
		super(pActivityResults);
	}

	@Override
	protected JPanel genericResult(List<ActivityResult> pActivityResults) {
		double mean = 100 * pActivityResults.stream().mapToDouble(o -> o.getScore() / pActivityResults.size()).sum();

		JPanel headResult = new JPanel(new GridLayout(2, 3, 0, 0));
		// Line 1
		headResult.add(ComponentUtils.boldLabel("Scores"));
		headResult.add(new LegendValueLabel("0 %",
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() < 0.8f).count())));
		headResult.add(new LegendValueLabel("100 %",
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() >= 0.8f).count())));

		// Line 2
		headResult.add(new LegendValueLabel("Mean", String.format("%.2f %%", mean)));
		headResult.add(new JPanel());
		headResult.add(new JPanel());

		return headResult;
	}

	@Override
	protected JTable formatResults(List<ActivityResult> pActivityResults) {
		String[] headers = new String[] { "Date", "Score", "Success", "Publish hour" };
		String[][] data = new String[pActivityResults.size()][headers.length];
		for (int i = 0; i < pActivityResults.size(); i++) {

			ActivityResult ar = pActivityResults.get(i);
			data[i] = new String[] { new SimpleDateFormat("yyyy/MM/dd, E", Locale.ENGLISH).format(ar.getDate()),
					String.format("%.2f", ar.getScore()), String.valueOf(ar.isSuccess()),
					new SimpleDateFormat("HH:mm:ss").format(ar.getPublishHour()) };
		}

		return new MyTable(data, headers);
	}

}
