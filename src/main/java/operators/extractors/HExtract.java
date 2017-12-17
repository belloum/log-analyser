package operators.extractors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import beans.SoftLog;
import beans.TSLimits;

public class HExtract {

	private static LinkedList<TSLimits> getHistogramSlots(Integer pIntervalInMinutes) {
		if (pIntervalInMinutes > 0 && pIntervalInMinutes <= 24 * 60) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			int cDay = calendar.get(Calendar.DAY_OF_YEAR);
			long start;
			LinkedList<TSLimits> histogramItems = new LinkedList<>();
			while (calendar.get(Calendar.DAY_OF_YEAR) == cDay) {
				start = calendar.getTimeInMillis();
				calendar.add(Calendar.MINUTE, pIntervalInMinutes);
				calendar.add(Calendar.SECOND, -1);
				histogramItems.add(new TSLimits(start, calendar.getTimeInMillis()));
				calendar.add(Calendar.SECOND, 1);
			}

			String lastLabel = TSLimits.getFriendlyLabel(histogramItems.getLast());
			if (!lastLabel.contains("23:59:59")) {
				histogramItems.removeLast();
				try {
					histogramItems.add(new TSLimits(lastLabel.split(" - ")[0], "23:59:59"));
				} catch (ParseException e) {
					System.out.println("parseException: " + e);
				}
			}

			return histogramItems;
		} else if (pIntervalInMinutes < 0) {
			System.out.println("Null interval");
			throw new IllegalArgumentException("Null interval");
		} else {
			System.out.println("Too long interval");
			throw new IllegalArgumentException("Too long interval");
		}
	}

	public static LinkedHashMap<TSLimits, Long> extractHistogram(int pIntervalInMinutes, List<SoftLog> pLogs) {
		LinkedHashMap<TSLimits, Long> map = new LinkedHashMap<>();
		getHistogramSlots(pIntervalInMinutes).stream().forEachOrdered(
				tsLimits -> map.put(tsLimits, pLogs.stream().filter(log -> log.isBetweenHours(tsLimits)).count()));
		return map;
	}

	private static boolean drawChart(JFreeChart chart, File output) {
		System.out.println("Saving " + output.getName() + " in " + output.getParent());
		try {
			ChartUtils.saveChartAsJPEG(output, chart, 1500, 600);
			return true;
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart: " + e);
			return false;
		}
	}

	public static boolean saveHistograms(File pOutput, int pInterval, List<SoftLog> pLogs) throws Exception {

		// Log chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		extractHistogram(pInterval, pLogs)
				.forEach((tsLimit, value) -> dataset.setValue(value, "occurence", TSLimits.getFriendlyLabel(tsLimit)));

		JFreeChart chart = ChartFactory.createBarChart("Logs", "timestamp", "occurrence", dataset,
				PlotOrientation.VERTICAL, false, true, false);

		if (!drawChart(chart, pOutput))
			return false;

		return true;
	}

	public static JFreeChart getHistograms(int pInterval, List<SoftLog> pLogs) throws Exception {

		// Log chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		extractHistogram(pInterval, pLogs)
				.forEach((tsLimit, value) -> dataset.setValue(value, "occurence", TSLimits.getFriendlyLabel(tsLimit)));

		return ChartFactory.createBarChart("Logs", "timestamp", "occurrence", dataset, PlotOrientation.VERTICAL, false,
				true, false);
	}
}
