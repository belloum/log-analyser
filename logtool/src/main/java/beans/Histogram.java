package beans;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import beans.devices.Device;
import loganalyser.beans.SoftLog;
import loganalyser.exceptions.HistogramException;
import loganalyser.exceptions.PeriodException;
import loganalyser.operators.FileExtractor;
import loganalyser.operators.SoftLogExtractor;

public class Histogram extends LinkedHashMap<Period, Integer> {

	private static final Integer MIN_DELAY = 1;
	private static final Integer MAX_DELAY = 24 * 60;

	private static final long serialVersionUID = 1L;
	private Integer mMinuteSlot;
	private List<?> mDataSet;

	private HistogramProgressListener mProgressListener;

	public void setBuildingListener(HistogramProgressListener pListener) {
		this.mProgressListener = pListener;
	}

	public Histogram(Integer pMinuteSlot) throws HistogramException, PeriodException {
		this(pMinuteSlot, new ArrayList<>(), null);
	}

	public Histogram(Integer pMinuteSlot, List<?> pDatas) throws HistogramException, PeriodException {
		this(pMinuteSlot, pDatas, null);
	}

	public Histogram(Integer pMinuteSlot, List<?> pDatas, HistogramProgressListener pListener)
			throws HistogramException, PeriodException {

		if (pMinuteSlot >= MIN_DELAY && pMinuteSlot <= MAX_DELAY) {
			this.mMinuteSlot = pMinuteSlot;
		} else {
			throw new HistogramException(HistogramException.INVALID_INTERVAL);
		}

		this.mDataSet = pDatas;
		this.mProgressListener = pListener;

		buildSlots();

		fillHistogram(pDatas);
	}

	private void buildSlots() throws PeriodException {
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
			calendar.add(Calendar.MINUTE, this.mMinuteSlot);
			calendar.add(Calendar.SECOND, -1);
			histogramItems.add(new Period(start, calendar.getTimeInMillis()));
			calendar.add(Calendar.SECOND, 1);
		}

		String lastLabel = Period.getFriendlyLabel(histogramItems.getLast());
		if (!lastLabel.contains("23:59")) {
			histogramItems.removeLast();
			histogramItems.add(new Period(lastLabel.split(" - ")[0], "23:59"));
		}

		histogramItems.forEach(tsLimits -> put(tsLimits, 0));

	}

	private void fillHistogram(List<?> pDatas) {
		if (mProgressListener != null) {
			mProgressListener.startFillingSlots();
		}

		int index = 0;
		for (Period tsLimits : keySet()) {
			put(tsLimits, (int) pDatas.stream().filter(log -> ((SoftLog) log).isBetweenHours(tsLimits)).count());
			index++;
			if (mProgressListener != null) {
				mProgressListener.progressBulding(((float) index * 100) / size());
			}
		}

		if (mProgressListener != null) {
			mProgressListener.finishFillingSlots();
		}

	}

	public static JFreeChart draw(Histogram pHistogram, boolean pSplitByDevice) throws Exception {
		return draw(pHistogram, pSplitByDevice, null, null, null);
	}

	public static JFreeChart draw(Histogram pHistogram, boolean pSplitByDevice, String pTitle, String xLabel,
			String yLabel) throws Exception {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (!pSplitByDevice) {
			pHistogram.forEach(
					(tsLimit, value) -> dataset.setValue(value, "occurence", Period.getFriendlyLabel(tsLimit)));
		} else {
			@SuppressWarnings("unchecked")
			List<Device> pDevices = SoftLogExtractor.getDevices((List<SoftLog>) pHistogram.mDataSet);
			pHistogram.forEach((tsLimit, value) -> {
				pDevices.forEach(device -> {
					dataset.setValue(
							pHistogram.mDataSet.stream()
									.filter(log -> ((SoftLog) log).getDevice().getId().equals(device.getId())
											&& ((SoftLog) log).isBetweenHours(tsLimit))
									.count(),
							device.getId(), Period.getFriendlyLabel(tsLimit));
				});
			});
		}

		((SoftLog) pHistogram.mDataSet.get(0)).getDayLabel();
		if (StringUtils.isNotEmpty(pTitle)) {
			pTitle = new StringBuffer(pTitle).append("\n").append(((SoftLog) pHistogram.mDataSet.get(0)).getDayLabel())
					.append(" - ")
					.append(((SoftLog) pHistogram.mDataSet.get(pHistogram.mDataSet.size() - 1)).getDayLabel())
					.toString();
		}

		JFreeChart chart = ChartFactory.createBarChart(pTitle, yLabel, xLabel, dataset, PlotOrientation.VERTICAL,
				pSplitByDevice, false, false);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		return chart;
	}

	@SuppressWarnings("unchecked")
	private static String formatAsCSV(Histogram pHistogram) {
		StringBuilder strB = new StringBuilder("Hours;");
		pHistogram.forEach((ts, count) -> strB.append(Period.getFriendlyLabel(ts)).append(";"));
		strB.append("\n").append("Logs;");
		strB.append(StringUtils.join(pHistogram.values(), ";"));

		strB.append("\n");

		SoftLogExtractor.getDevices((List<SoftLog>) pHistogram.mDataSet).forEach(device -> {
			strB.append(device.getId()).append(";");
			pHistogram.forEach((tsLimit, value) -> {
				strB.append(pHistogram.mDataSet.stream()
						.filter(log -> ((SoftLog) log).getDevice().getId().equals(device.getId())
								&& ((SoftLog) log).isBetweenHours(tsLimit))
						.count()).append(";");
			});
			strB.append("\n");
		});

		return strB.toString();
	}

	public static boolean saveChart(Histogram pHistogram, File output) {
		System.out.println("Saving " + output.getName() + " in " + output.getParent());
		try {
			ChartUtils.saveChartAsJPEG(
					new File(output.getParent(), output.getName().replaceAll(".jpg", "_splitted.jpg")),
					draw(pHistogram, true, "Sensors", "occurences", "time"), 1500, 600);
			ChartUtils.saveChartAsJPEG(output, draw(pHistogram, false, "Logs", "occurences", "time"), 1500, 600);
			return true;
		} catch (Exception e) {
			System.err.println("Problem occurred creating chart.");
			return false;
		}
	}

	public static boolean saveCSVFile(Histogram pHistogram, File pOutputFile) {
		return FileExtractor.saveFile(formatAsCSV(pHistogram), pOutputFile);
	}

	public interface HistogramProgressListener {
		public void startFillingSlots();

		public void finishFillingSlots();

		public void progressBulding(float pProgress);
	}

}
