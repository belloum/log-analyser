package loganalyser.ui.panels.results;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.beans.activityresults.MealResult;
import loganalyser.old.ui.CustomComponent;
import loganalyser.ui.components.MyTable;

//TODO Use GridLayout
public class MealResultPanel extends ActivityResultPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MealResultPanel(List<ActivityResult> pActivityResults) {
		super(pActivityResults);
	}

	@Override
	protected JPanel genericResult(List<ActivityResult> pActivityResults) {
		float successRate = (float) (pActivityResults.stream().filter(meal -> meal.isSuccess()).count() * 100)
				/ (float) pActivityResults.size();
		float mean = (float) pActivityResults.stream().mapToDouble(o -> o.getScore() / pActivityResults.size()).sum();

		/*
		 * Show global results
		 */
		JPanel headResult = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;

		/*
		 * Failure 0% label
		 */
		gbc.gridx = 0;
		gbc.gridy = 0;
		headResult.add(CustomComponent.boldLabel(new JLabel("Failure 0%")), gbc);

		/*
		 * Failure 0% value
		 */
		gbc.gridx = 1;
		headResult.add(
				new JLabel(
						String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() < 0.2f).count())),
				gbc);

		/*
		 * Failure 20% label
		 */
		gbc.gridx = 0;
		gbc.gridy = 1;
		headResult.add(CustomComponent.boldLabel(new JLabel("Failure 20%")), gbc);

		/*
		 * Failure 20% value
		 */
		gbc.gridx = 1;
		headResult.add(new JLabel(String.format("%d",
				(int) pActivityResults.stream().filter(m -> m.getScore() < 0.8f && m.getScore() >= 0.2f).count())),
				gbc);

		/*
		 * Success 80% label
		 */
		gbc.gridx = 0;
		gbc.gridy = 2;
		headResult.add(CustomComponent.boldLabel(new JLabel("Success 80%")), gbc);

		/*
		 * Success 80% value
		 */
		gbc.gridx = 1;
		headResult.add(
				new JLabel(
						String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() == 0.8f).count())),
				gbc);

		/*
		 * Success 100% label
		 */
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 3;
		headResult.add(CustomComponent.boldLabel(new JLabel("Success 100%")), gbc);

		/*
		 * Success 100% value
		 */
		gbc.gridx = 1;
		headResult.add(
				new JLabel(
						String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() == 1f).count())),
				gbc);

		/*
		 * Mean label
		 */
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weighty = 2;
		headResult.add(CustomComponent.boldLabel(new JLabel("Mean")), gbc);

		/*
		 * Mean value
		 */
		gbc.gridx = 3;
		headResult.add(new JLabel(String.format("%.2f %%", 100 * mean)), gbc);

		/*
		 * Success rate label
		 */
		gbc.gridx = 2;
		gbc.gridy = 2;
		headResult.add(CustomComponent.boldLabel(new JLabel("Success rate")), gbc);

		/*
		 * Success rate value
		 */
		gbc.gridx = 3;
		headResult.add(new JLabel(String.format("%.2f %%", successRate)), gbc);

		return headResult;
	}

	@Override
	protected JScrollPane detilledResults(List<ActivityResult> pActivityResults) {
		String[] headers = new String[] { "Date", "Score", "Success", "MarkerI", "MarkerII", "Publish hour" };
		String[][] data = new String[pActivityResults.size()][headers.length];
		for (int i = 0; i < pActivityResults.size(); i++) {

			MealResult mr = (MealResult) pActivityResults.get(i);
			data[i] = new String[] { new SimpleDateFormat("yyyy/MM/dd, E", Locale.ENGLISH).format(mr.getDate()),
					String.format("%.2f", mr.getScore()), String.valueOf(mr.isSuccess()),
					String.format("%d", mr.getMarkerIActivations()), String.format("%d", mr.getMarkerIIActivations()),
					new SimpleDateFormat("HH:mm:ss").format(mr.getPublishHour()) };
		}

		MyTable table = new MyTable(data, headers);

		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(table);
		return scroll;
	}

}
