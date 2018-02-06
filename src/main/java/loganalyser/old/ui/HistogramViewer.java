package loganalyser.old.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartPanel;

import beans.Histogram;
import loganalyser.beans.SoftLog;
import loganalyser.old.ui.ResultLabel.ResultType;
import loganalyser.utils.Configuration;

public class HistogramViewer extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private HistogramListener mListener;
	private JButton mJBDrawHisto, mJBSaveHisto, mJBExportCSV;
	private JCheckBox mJCBSplitting = new JCheckBox("Split by device");
	private ResultLabel mRLHistogram = new ResultLabel();

	private InputValue mInputThreshold = new InputValue("##");

	public HistogramViewer(HistogramListener pHistogramListener, Integer pWidth, Integer pHeight) {
		super(new Dimension(pWidth, pHeight), new BorderLayout());

		if (pHistogramListener instanceof HistogramListener) {
			this.mListener = pHistogramListener;
		} else {
			throw new IllegalArgumentException(
					String.format("%s must implement HistogramListener.", pHistogramListener.getClass().getName()));
		}
		build();
	}

	@Override
	public void build() {
		super.build();

		// Left panel
		JPanel left = new JPanel(new BorderLayout());
		left.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		left.setMaximumSize(new Dimension(getMaximumSize().width * 80 / 100, getMaximumSize().height));
		left.setPreferredSize(left.getMaximumSize());

		// Right Panel
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		JLabel jLabel = new JLabel("Interval (minutes)");
		CustomComponent.setJLabelFontStyle(jLabel, Font.BOLD);
		right.add(jLabel);

		right.add(mInputThreshold);
		mInputThreshold.setValue(60);
		mInputThreshold.setMaximumSize(
				new Dimension(mInputThreshold.getParent().getMaximumSize().width / 2, Configuration.ITEM_HEIGHT));

		right.add(mJCBSplitting);

		mJBDrawHisto = new MyButton("Draw histogram", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ChartPanel chP = new ChartPanel(Histogram.draw(
							new Histogram(Integer.parseInt(mInputThreshold.getText()), mListener.fillHistogram()),
							mJCBSplitting.isSelected()));
					left.add(chP, BorderLayout.CENTER);
					left.validate();
				} catch (Exception exception) {
					System.out.println("Unable to save file: " + exception);
					mRLHistogram.printResult(exception.getMessage(), ResultType.ERROR);
				}
			}
		});
		right.add(mJBDrawHisto);

		mJBSaveHisto = new MyButton("Save histogram", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(Configuration.RESOURCES_FOLDER, "Save histogram");

				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File output = fc.getSelectedFile();
					try {
						if (!output.getName().contains(".jpg")) {
							output = new File(output.getParent(), String.format("%s.jpg", output.getName()));
						}
						Histogram.saveChart(
								new Histogram(Integer.parseInt(mInputThreshold.getText()), mListener.fillHistogram()),
								output);
						mRLHistogram.printResult(
								new StringBuffer(output.getPath()).append(" has been saved.").toString(),
								ResultType.SUCCESS);
					} catch (Exception exception) {
						System.out.println("Unable to save file: " + exception);
						mRLHistogram.printResult(exception.getMessage(), ResultType.ERROR);
					}
				}
			}
		});
		right.add(mJBSaveHisto);

		mJBExportCSV = new MyButton("Export as CSV", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(Configuration.RESOURCES_FOLDER, "Save CSV file");

				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File output = fc.getSelectedFile();
					try {
						if (!output.getName().contains(".csv")) {
							output = new File(output.getParent(), String.format("%s.csv", output.getName()));
						}
						Histogram.saveCSVFile(
								new Histogram(Integer.parseInt(mInputThreshold.getText()), mListener.fillHistogram()),
								output);
						mRLHistogram.printResult(
								new StringBuffer(output.getPath()).append(" has been saved.").toString(),
								ResultType.SUCCESS);
					} catch (Exception exception) {
						System.out.println("Unable to save file: " + exception);
						mRLHistogram.printResult(exception.getMessage(), ResultType.ERROR);
					}
				}
			}
		});
		right.add(mJBExportCSV);

		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST);
		add(mRLHistogram, BorderLayout.PAGE_END);

		validate();
		return;
	}

	@Override
	public void setEnabled(boolean pEnabled) {
		mJBDrawHisto.setEnabled(pEnabled);
		mJBSaveHisto.setEnabled(pEnabled);
		mJBExportCSV.setEnabled(pEnabled);
		mJCBSplitting.setEnabled(pEnabled);
		mInputThreshold.setEnabled(pEnabled);
	}

	public interface HistogramListener {
		List<SoftLog> fillHistogram();

		void saveHistogram();
	}

}
