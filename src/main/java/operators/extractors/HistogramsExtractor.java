package operators.extractors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import beans.Day;
import beans.Day.LabelFormat;
import beans.HistogramItem;
import beans.MyLog;
import beans.devices.ElectricMeter;
import beans.devices.Sensor;

/**
 * Histogram Extractor to export logs gathered by sensor and hour slot
 * 
 * @param logs
 *            The logs to be treated
 * @param interval
 *            The interval of hour slots for histogram (in minutes)
 * @param histogramItems
 *            The list of histogram items
 * @param elecTreshold
 *            The electric consumption threshold (by default 20 Watts).
 * 
 * @see HistogramItem
 * 
 * @author Antoine Riché
 * @since 06/09/17
 * 
 */

public class HistogramsExtractor {

	List<MyLog> logs;
	List<HistogramItem> histogramItems;
	int interval;
	int elecThreshold;
	String[] period;

	/**
	 * 
	 * @param logs
	 *            The logs to be treated
	 * @param interval
	 *            The interval of hour slots for histogram (in minutes)
	 * @param elecTreshold
	 *            The electric consumption threshold (in Watts)
	 */
	public HistogramsExtractor(List<MyLog> logs, int interval) {
		if (interval <= 0 || interval >= 24 * 60 || 24 * 60 % interval != 0)
			throw new IllegalArgumentException("Interval is not valid!");
		else {
			this.elecThreshold = 20;
			this.logs = cleanTimestamps(logs);
			this.interval = interval;
			this.histogramItems = extractHistogram();
			this.period = extractPeriod(logs);
		}
	}

	/**
	 * 
	 * @return The logs to be treated
	 */
	public List<MyLog> getLogs() {
		return logs;
	}

	/**
	 * 
	 * @param logs
	 *            The logs to be treated
	 */
	public void setLogs(List<MyLog> logs) {
		this.logs = logs;
	}

	/**
	 * 
	 * @return The list of histogram items
	 */
	public List<HistogramItem> getHistogramItems() {
		return histogramItems;
	}

	/**
	 * 
	 * @param histogramItems
	 *            The list of histogram items
	 */
	public void setHistogramItems(List<HistogramItem> histogramItems) {
		this.histogramItems = histogramItems;
	}

	/**
	 * 
	 * @return The interval of hour slots for histogram (in minutes)
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * 
	 * @param interval
	 *            The interval of hour slots for histogram (in minutes)
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * 
	 * @return The electric consumption threshold (in Watts)
	 */
	public int getElecThreshold() {
		return this.elecThreshold;
	}

	/**
	 * 
	 * @param elecTreshold
	 *            The electric consumption threshold (in Watts)
	 * 
	 */

	public void setElecThreshold(int elecThreshold) {
		this.elecThreshold = elecThreshold;
	}

	/**
	 * 
	 * @return A list containing void histogram slots
	 */
	private List<HistogramItem> getHistogramSlots() {
		List<HistogramItem> histogramItems = new ArrayList<>();
		int nbSlots = 24 * 60 / this.interval;
		for (int i = 0; i < nbSlots; i++) {
			int startSlot = i * this.interval * 60 * 1000;
			int endSlot = (i + 1) * this.interval * 60 * 1000;
			endSlot -= 1;
			histogramItems.add(new HistogramItem(0, startSlot, endSlot));
		}

		return histogramItems;
	}

	/**
	 * 
	 * @return A list containing the logs gathered by hour slot
	 * @see Sensor
	 */
	public List<HistogramItem> extractHistogram() {

		List<HistogramItem> histogramItems = getHistogramSlots();

		Collections.sort(this.logs, new Comparator<MyLog>() {
			@Override
			public int compare(MyLog o1, MyLog o2) {
				return Double.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});

		for (MyLog log : this.logs) {
			int index = (int) (log.getTimestamp() / (this.interval * 60 * 1000));
			histogramItems.get(index).setValue(histogramItems.get(index).getValue() + 1);
		}

		return histogramItems;
	}

	/**
	 * 
	 * @param sensorIds
	 *            The ids of the sensors which have sent the logs to be
	 *            extracted
	 * @return A list containing the logs sent by the sensors, gathered by hour
	 *         slot
	 * @see Sensor
	 */
	public List<HistogramItem> extractFilteredHistogram(String[] sensorIds) {

		List<HistogramItem> histogramItems = getHistogramSlots();
		Collections.sort(this.logs, new Comparator<MyLog>() {
			@Override
			public int compare(MyLog o1, MyLog o2) {
				return Double.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});

		for (MyLog log : this.logs) {
			int index = (int) (log.getTimestamp() / (this.interval * 60 * 1000));
			if (Arrays.asList(sensorIds).contains(log.getDevice().getId()))
				histogramItems.get(index).setValue(histogramItems.get(index).getValue() + 1);
		}

		return histogramItems;
	}

	/**
	 * Cleans the initial log list by computing the timestamp and removing
	 * electric meter log with too low consumption.
	 * 
	 * @param logs
	 *            The logs to be cleaned
	 * @return The list containing logs which correct timestamp (from 00:00)
	 * @see ElectricMeter
	 */
	private List<MyLog> cleanTimestamps(List<MyLog> logs) {
		Calendar calendar = Calendar.getInstance();
		List<MyLog> myLogs = new ArrayList<>();

		for (MyLog log : logs) {
			if (!log.getDevice().getId().contains("EMeter") || log.getValue() >= this.elecThreshold) {
				calendar.setTimeInMillis(log.getTimestamp());
				long timestamp = 0;
				timestamp += calendar.get(Calendar.HOUR_OF_DAY) * 3600 * 1000;
				timestamp += calendar.get(Calendar.MINUTE) * 60 * 1000;
				timestamp += calendar.get(Calendar.SECOND) * 1000;
				timestamp += calendar.get(Calendar.MILLISECOND);

				myLogs.add(new MyLog(timestamp, log.getValue(), log.getDevice()));
			}
		}

		return myLogs;
	}

	/**
	 * Exports the list of logs sent by device which fit id specified in
	 * parameters as a CSV-file that contains logs gathered by hour slot.<br>
	 * </br>
	 * 
	 * @param filename
	 *            The output CSV-file
	 * @param sensors
	 *            The list of sensors id to extract
	 * @return
	 *         <ul>
	 *         <li><b>true: </b><i>if the CSV-file is correctly saved/i></li>
	 *         <li><b>false: </b><i>otherwise</i></li>
	 *         </ul>
	 * @since 06/15/17
	 */
	public boolean exportCsv(File csvFile, String[] sensorIds) {

		StringBuilder stringBuilder = new StringBuilder("Slots;");
		for (HistogramItem item : this.histogramItems)
			stringBuilder.append(item.getLabel() + ";");

		stringBuilder.append("\n");
		stringBuilder.append("Logs;");
		for (HistogramItem item : this.histogramItems)
			stringBuilder.append(item.getValue() + ";");

		for (String sensor : sensorIds) {
			stringBuilder.append("\n");
			stringBuilder.append(sensor + ";");
			for (HistogramItem item : extractFilteredHistogram(new String[] { sensor }))
				stringBuilder.append(item.getValue() + ";");
		}

		return saveCSV(stringBuilder.toString(), csvFile);
	}

	/**
	 * Exports the list of logs as a CSV-file that contains logs gathered by
	 * hour slot.<br>
	 * </br>
	 * 
	 * @param csvFile
	 *            The output CSV-file
	 * @return
	 *         <ul>
	 *         <li><b>true: </b><i>if the CSV-file is correctly saved/i></li>
	 *         <li><b>false: </b><i>otherwise</i></li>
	 *         </ul>
	 * 
	 */
	public boolean exportCsv(File csvFile) {

		StringBuilder stringBuilder = new StringBuilder("Slots;");
		for (HistogramItem item : this.histogramItems)
			stringBuilder.append(item.getLabel() + ";");

		stringBuilder.append("\n");
		stringBuilder.append("Logs;");
		for (HistogramItem item : this.histogramItems)
			stringBuilder.append(item.getValue() + ";");

		for (Sensor sensor : getAllSensors()) {
			stringBuilder.append("\n");
			stringBuilder.append(sensor.getId() + ";");
			for (HistogramItem item : extractFilteredHistogram(new String[] { sensor.getId() }))
				stringBuilder.append(item.getValue() + ";");
		}

		return saveCSV(stringBuilder.toString(), csvFile);
	}

	/**
	 * Draw and save an histogram that gathering the sensors specified in
	 * parameters. The x-axis represents the hour of the days and the y-axis,
	 * the occurrences of logs along the period of the list. <br>
	 * </br>
	 * If the list of sensor ids is composed by more than one element, a mixed
	 * histogram and a gethering histogram are also drawn.
	 * 
	 * @param parentFolder
	 *            The destination folder of graphics
	 * @param xLabel
	 *            Label for the x-axis
	 * @param yLabel
	 *            Label for the y-axis
	 * @param sensorIds
	 *            Specified sensor ids
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if graphics are correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @throws Exception
	 *             Exception are thrown if
	 *             <ul>
	 *             <li>Destination folder is unreachable</li>
	 *             <li>One chart has can not created</li>
	 *             </ul>
	 * @since 06/15/17
	 */
	public boolean saveHistogram(File parentFolder, String xLabel, String yLabel, String[] sensorIds, String period)
			throws Exception {

		if (!parentFolder.exists() && !parentFolder.mkdirs())
			throw new Exception("Unable to reach: " + parentFolder.getAbsolutePath());

		DefaultCategoryDataset dataset;
		JFreeChart chart;

		List<HistogramItem> items = getHistogramSlots();

		String chartTitle = "";
		for (String string : sensorIds)
			chartTitle += string + "-";
		chartTitle = chartTitle.substring(0, chartTitle.lastIndexOf("-"));

		// Get datas
		for (int i = 0; i < items.size(); i++) {
			HistogramItem hItem = items.get(i);
			for (String sensorId : sensorIds) {
				int value = extractFilteredHistogram(new String[] { sensorId }).get(i).getValue();
				hItem.setValue(hItem.getValue() + value);
			}
		}

		dataset = new DefaultCategoryDataset();
		for (HistogramItem hItem : items)
			dataset.setValue(hItem.getValue(), yLabel, hItem.getLabel());

		StringBuilder chartTitle2 = new StringBuilder(chartTitle);
		if (this.period != null) {
			chartTitle2.append("\n");
			chartTitle2.append(this.period[0]);
			chartTitle2.append(" - ");
			chartTitle2.append(this.period[1]);
		}

		chart = ChartFactory.createBarChart(chartTitle2.toString(), xLabel, yLabel, dataset, PlotOrientation.VERTICAL,
				false, true, false);

		if (!drawChart(chart, new File(parentFolder, period + "_" + chartTitle + ".jpg")))
			return false;

		if (sensorIds.length > 1)
			if (!saveMixedHistogram(parentFolder, period + "_" + chartTitle + ".jpg", xLabel, yLabel,
					Arrays.asList(sensorIds)))
				return false;

		return true;
	}

	/**
	 * Draw and save histograms of all sensors that appear in the log list. The
	 * x-axis represents the hour of the days and the y-axis, the occurrences of
	 * logs along the period of the list. <br>
	 * </br>
	 * The drawn graphics are:
	 * <ul>
	 * <li>One histogram by device</li>
	 * <li>One histogram by device type</li>
	 * <li>One histogram with all sensors</li>
	 * <li>One histogram of logs</li>
	 * </ul>
	 * 
	 * @param parentFolder
	 *            The destination folder of graphics
	 * @param xLabel
	 *            Label for the x-axis
	 * @param yLabel
	 *            Label for the y-axis
	 * @param period
	 *            The period of the logs to be drawn
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if graphics are correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @throws Exception
	 *             Exception are thrown if
	 *             <ul>
	 *             <li>Destination folder is unreachable</li>
	 *             <li>One chart has can not created</li>
	 *             </ul>
	 * @since 06/16/17
	 */
	public boolean saveHistograms(File parentFolder, String xLabel, String yLabel, String period) throws Exception {

		if (!parentFolder.exists() && !parentFolder.mkdirs())
			throw new Exception("Unable to reach: " + parentFolder.getAbsolutePath());

		List<String> sensorIds = new ArrayList<>();
		// Device charts
		for (Sensor device : getAllSensors()) {
			sensorIds.add(device.getId());
			if (!saveHistogram(parentFolder, xLabel, yLabel, new String[] { device.getId() }, period))
				return false;
		}

		// Type charts
		for (Sensor.Type type : getAllSensorTypes())
			if (!saveDeviceTypeHistogram(parentFolder, xLabel, yLabel, type, period))
				return false;

		// Mixed-sensor chart
		if (!saveMixedHistogram(parentFolder, period + "_Sensors.jpg", xLabel, yLabel, sensorIds))
			return false;

		// Log chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (HistogramItem hItem : this.histogramItems)
			dataset.setValue(hItem.getValue(), yLabel, hItem.getLabel());

		StringBuilder chartTitlte = new StringBuilder("Logs");
		if (this.period != null) {
			chartTitlte.append("\n");
			chartTitlte.append(this.period[0]);
			chartTitlte.append(" - ");
			chartTitlte.append(this.period[1]);
		}
		JFreeChart chart = ChartFactory.createBarChart(chartTitlte.toString(), xLabel, yLabel, dataset,
				PlotOrientation.VERTICAL, false, true, false);

		if (!drawChart(chart, new File(parentFolder, period + "_Logs.jpg")))
			return false;

		return true;
	}

	/**
	 * Draw and save an histogram gathering logs of all sensors The x-axis
	 * represents the hour of the days and the y-axis, the occurrences of logs
	 * along the period of the list. <br>
	 * </br>
	 * 
	 * @param parentFolder
	 *            The destination folder of graphics
	 * @param xLabel
	 *            Label for the x-axis
	 * @param yLabel
	 *            Label for the y-axis
	 * @param filename
	 *            The output file name
	 * @param sensorIds
	 *            The list of sensors' id to represent on the chart
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if graphics are correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @throws Exception
	 *             Exception are thrown if
	 *             <ul>
	 *             <li>Destination folder is unreachable</li>
	 *             <li>One chart has can not created</li>
	 *             </ul>
	 * @since 06/15/17
	 */
	public boolean saveMixedHistogram(File parentFolder, String filename, String xLabel, String yLabel,
			List<String> sensorIds) throws Exception {

		if (!parentFolder.exists() && !parentFolder.mkdirs())
			throw new Exception("Unable to reach: " + parentFolder.getAbsolutePath());

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart;

		for (String sensor : sensorIds)
			for (HistogramItem hItem : extractFilteredHistogram(new String[] { sensor }))
				dataset.setValue(hItem.getValue(), sensor, hItem.getLabel());

		StringBuilder chartTitlte = new StringBuilder("Sensors");
		if (this.period != null) {
			chartTitlte.append("\n");
			chartTitlte.append(this.period[0]);
			chartTitlte.append(" - ");
			chartTitlte.append(this.period[1]);
		}
		chart = ChartFactory.createBarChart(chartTitlte.toString(), xLabel, yLabel, dataset, PlotOrientation.VERTICAL,
				true, true, false);

		return drawChart(chart, new File(parentFolder, filename));
	}

	/**
	 * Draw and save an histogram gathering logs of sensors that fit type
	 * specified in the parameters. The x-axis represents the hour of the days
	 * and the y-axis, the occurrences of logs along the period of the list.
	 * <br>
	 * </br>
	 * 
	 * @param parentFolder
	 *            The destination folder of graphics
	 * @param xLabel
	 *            Label for the x-axis
	 * @param yLabel
	 *            Label for the y-axis
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if graphics are correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @throws Exception
	 *             Exception are thrown if
	 *             <ul>
	 *             <li>Destination folder is unreachable</li>
	 *             <li>One chart has can not created</li>
	 *             </ul>
	 * @since 06/15/17
	 */
	public boolean saveDeviceTypeHistogram(File parentFolder, String xLabel, String yLabel, Sensor.Type deviceType,
			String period) throws Exception {

		if (!parentFolder.exists() && !parentFolder.mkdirs())
			throw new Exception("Unable to reach: " + parentFolder.getAbsolutePath());

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart;

		for (Sensor sensor : getAllSensors()) {
			if (sensor.getType().equals(deviceType))
				for (HistogramItem hItem : extractFilteredHistogram(new String[] { sensor.getId() }))
					dataset.setValue(hItem.getValue(), sensor.getId(), hItem.getLabel());
		}

		StringBuilder chartTitlte = new StringBuilder(deviceType.name());
		if (this.period != null) {
			chartTitlte.append("\n");
			chartTitlte.append(this.period[0]);
			chartTitlte.append(" - ");
			chartTitlte.append(this.period[1]);
		}
		chart = ChartFactory.createBarChart(chartTitlte.toString(), xLabel, yLabel, dataset, PlotOrientation.VERTICAL,
				true, true, false);

		return drawChart(chart, new File(parentFolder, period + "_" + deviceType.name() + ".jpg"));
	}

	/**
	 * Gets all sensors which appear in the log list
	 * 
	 * @return The list of sensors
	 * @since 06/15/17
	 */
	public List<Sensor> getAllSensors() {
		List<Sensor> sensors = new ArrayList<>();
		String alreadyIn = "";
		for (MyLog log : this.logs)
			if (!sensors.contains(log.getDevice()) && !alreadyIn.contains(log.getDevice().getId())) {
				sensors.add(log.getDevice());
				alreadyIn += log.getDevice().getId();
			}
		return sensors;
	}

	/**
	 * Gets all sensor types which appear in the log list
	 * 
	 * @return The list of sensor types
	 * @since 06/16/17
	 */
	public List<Sensor.Type> getAllSensorTypes() {
		List<Sensor.Type> sensorTypes = new ArrayList<>();
		for (MyLog log : this.logs)
			if (!sensorTypes.contains(log.getDevice().getType()))
				sensorTypes.add(log.getDevice().getType());
		return sensorTypes;
	}

	/**
	 * Save the content passed in parameter as a CSV-file named by the
	 * parameters
	 * 
	 * @param content
	 *            The content to save as CSV
	 * @param outputfile
	 *            The output file
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if CSV-file is correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @since 06/15/17
	 */
	private boolean saveCSV(String content, File outputfile) {

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "utf-8"));
			writer.write(content);
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			System.out.println("Exception while writing content: " + e);
			return false;
		}
	}

	/**
	 * Draws the specified chart into the output file in parameters
	 * 
	 * @param chart
	 *            The JFreeChart to draw
	 * @param output
	 *            The output file
	 * @return
	 *         <ul>
	 *         <li><b>true </b> if graphics are correctly saved</li>
	 *         <li>otherwise</li>
	 *         <ul>
	 * @since 06/15/17
	 */
	private boolean drawChart(JFreeChart chart, File output) {
		System.out.println("Saving " + output.getName() + " in " + output.getParent());
		try {
			ChartUtils.saveChartAsJPEG(output, chart, 1500, 600);
			return true;
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
			return false;
		}
	}

	/**
	 * Extracts the time period of the logs passed in parameters.
	 * 
	 * @param logs
	 *            The list of logs
	 * @return A 2-length array which contains labels
	 *         <ul>
	 *         <li><b>[0] </b> the first day</li>
	 *         <li><b>[1] </b> the last days</li>
	 *         </ul>
	 * @since 06/16/17
	 */
	private String[] extractPeriod(List<MyLog> logs) {

		Collections.sort(logs, new Comparator<MyLog>() {
			@Override
			public int compare(MyLog o1, MyLog o2) {
				return Double.compare(o1.getTimestamp(), o2.getTimestamp());
			}
		});

		if (!logs.isEmpty()) {
			String begin = Day.formatLabelDay(LabelFormat.DD_MM_YYYY, logs.get(0).getTimestamp());
			String end = Day.formatLabelDay(LabelFormat.DD_MM_YYYY, logs.get(logs.size() - 1).getTimestamp());
			return new String[] { begin, end };
		}

		return null;
	}
}
