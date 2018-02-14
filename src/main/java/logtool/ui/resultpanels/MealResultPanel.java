package logtool.ui.resultpanels;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JTable;

import logtool.beans.activityresults.ActivityResult;
import logtool.beans.activityresults.MealResult;
import logtool.ui.components.ComponentUtils;
import logtool.ui.components.LegendValueLabel;
import logtool.ui.components.MyTable;

public class MealResultPanel extends ActivityResultPanel {

	private static final long serialVersionUID = 1L;

	public MealResultPanel(List<ActivityResult> pActivityResults) {
		super(pActivityResults);
	}

	@Override
	protected JPanel genericResult(List<ActivityResult> pActivityResults) {
		float successRate = (float) (pActivityResults.stream().filter(meal -> meal.isSuccess()).count() * 100)
				/ (float) pActivityResults.size();
		double mean = 100 * pActivityResults.stream().mapToDouble(o -> o.getScore() / pActivityResults.size()).sum();

		JPanel headResult = new JPanel(new GridLayout(2, 5, 0, 0));

		// Line 1
		headResult.add(ComponentUtils.boldLabel("Scores"));
		headResult.add(new LegendValueLabel("0 %",
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() < 0.2f).count())));

		headResult.add(new LegendValueLabel("20 %", String.format("%d",
				(int) pActivityResults.stream().filter(m -> m.getScore() < 0.8f && m.getScore() >= 0.2f).count())));

		headResult.add(new LegendValueLabel("80 %",
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() == 0.8f).count())));

		headResult.add(new LegendValueLabel("100 %",
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() == 1f).count())));

		// Line 2
		headResult.add(new LegendValueLabel("Mean", String.format("%.2f %%", mean)));
		headResult.add(new LegendValueLabel("Success rate", String.format("%.2f %%", successRate)));
		headResult.add(new JPanel());
		headResult.add(new JPanel());
		headResult.add(new JPanel());

		return headResult;
	}

	@Override
	protected JTable formatResults(List<ActivityResult> pActivityResults) {
		String[] headers = new String[] { "Date", "Score", "Success", "MarkerI", "MarkerII", "Publish hour" };
		String[][] data = new String[pActivityResults.size()][headers.length];
		for (int i = 0; i < pActivityResults.size(); i++) {

			MealResult mr = (MealResult) pActivityResults.get(i);
			data[i] = new String[] { new SimpleDateFormat("yyyy/MM/dd, E", Locale.ENGLISH).format(mr.getDate()),
					String.format("%.2f", mr.getScore()), String.valueOf(mr.isSuccess()),
					String.format("%d", mr.getMarkerIActivations()), String.format("%d", mr.getMarkerIIActivations()),
					new SimpleDateFormat("HH:mm:ss").format(mr.getPublishHour()) };
		}

		return new MyTable(data, headers);
	}

}
