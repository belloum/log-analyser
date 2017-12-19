package operators.extractors;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import beans.Period;
import beans.SoftLog;
import exceptions.PeriodException;

public class HExtract {

	private static LinkedList<Period> getHistogramSlots(Integer pIntervalInMinutes) throws PeriodException {
		if (pIntervalInMinutes > 0 && pIntervalInMinutes <= 24 * 60) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			int cDay = calendar.get(Calendar.DAY_OF_YEAR);
			long start;
			LinkedList<Period> histogramItems = new LinkedList<>();
			while (calendar.get(Calendar.DAY_OF_YEAR) == cDay) {
				start = calendar.getTimeInMillis();
				calendar.add(Calendar.MINUTE, pIntervalInMinutes);
				calendar.add(Calendar.SECOND, -1);
				histogramItems.add(new Period(start, calendar.getTimeInMillis()));
				calendar.add(Calendar.SECOND, 1);
			}

			String lastLabel = Period.getFriendlyLabel(histogramItems.getLast());
			if (!lastLabel.contains("23:59:59")) {
				histogramItems.removeLast();
				histogramItems.add(new Period(lastLabel.split(" - ")[0], "23:59:59"));
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

	public static LinkedHashMap<Period, Long> extractHistogram(int pIntervalInMinutes, List<SoftLog> pLogs)
			throws PeriodException {
		LinkedHashMap<Period, Long> map = new LinkedHashMap<>();
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
				.forEach((tsLimit, value) -> dataset.setValue(value, "occurence", Period.getFriendlyLabel(tsLimit)));

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
				.forEach((tsLimit, value) -> dataset.setValue(value, "occurence", Period.getFriendlyLabel(tsLimit)));

		return ChartFactory.createBarChart("Logs", "timestamp", "occurrence", dataset, PlotOrientation.VERTICAL, false,
				true, false);
	}
}
