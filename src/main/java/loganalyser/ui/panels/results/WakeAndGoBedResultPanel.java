package loganalyser.ui.panels.results;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.old.ui.CustomComponent;
import loganalyser.ui.components.MyTable;

public class WakeAndGoBedResultPanel extends ActivityResultPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WakeAndGoBedResultPanel(List<ActivityResult> pActivityResults) {
		super(pActivityResults);
	}

	@Override
	protected JPanel genericResult(List<ActivityResult> pActivityResults) {

		JPanel head = new JPanel(new GridLayout(2, 4));

		head.add(CustomComponent.boldLabel("Success"));
		head.add(new JLabel(
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() >= 0.8f).count())));

		head.add(CustomComponent.boldLabel("Failure"));
		head.add(new JLabel(
				String.format("%d", (int) pActivityResults.stream().filter(m -> m.getScore() < 0.8f).count())));

		head.add(CustomComponent.boldLabel("Mean"));
		head.add(new JLabel(String.format("%.2f %%",
				100 * pActivityResults.stream().mapToDouble(o -> o.getScore() / pActivityResults.size()).sum())));

		// Fill offset with blank panels
		head.add(new JPanel());
		head.add(new JPanel());

		return head;
	}

	@Override
	protected JScrollPane detilledResults(List<ActivityResult> pActivityResults) {
		String[] headers = new String[] { "Date", "Score", "Success", "Publish hour" };
		String[][] data = new String[pActivityResults.size()][headers.length];
		for (int i = 0; i < pActivityResults.size(); i++) {

			ActivityResult ar = pActivityResults.get(i);
			data[i] = new String[] { new SimpleDateFormat("yyyy/MM/dd, E", Locale.ENGLISH).format(ar.getDate()),
					String.format("%.2f", ar.getScore()), String.valueOf(ar.isSuccess()),
					new SimpleDateFormat("HH:mm:ss").format(ar.getPublishHour()) };
		}

		MyTable table = new MyTable(data, headers);

		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(table);
		return scroll;
	}

}
