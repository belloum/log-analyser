package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartPanel;

import beans.Histogram;
import beans.SoftLog;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class HistogramViewer extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private HistogramListener mListener;
	private JButton mJBDrawHisto = new JButton("Draw histogram");
	private JButton mJBSaveHisto = new JButton("Save histogram");
	private JButton mJBExportCSV = new JButton("Export as CSV");
	private JCheckBox mJCBSplitting = new JCheckBox("Split by device");
	private ResultLabel mRLHistogram = new ResultLabel();

	private JFormattedTextField mJFTFSlotInterval = new JFormattedTextField("##") {

		private static final long serialVersionUID = 1L;

		@Override
		public void setEditable(boolean pEditabled) {
			super.setEditable(pEditabled);
			if (!pEditabled) {
				setBackground(Color.LIGHT_GRAY);
			} else {
				setBackground(Color.WHITE);
			}
		}
	};

	public HistogramViewer(HistogramListener pHistogramListener, Integer pWidth, Integer pHeight) {
		setLayout(new BorderLayout());
		setMaximumSize(new Dimension(pWidth, pHeight));
		setMinimumSize(getMaximumSize());
		setPreferredSize(getMaximumSize());

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
		removeAll();

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

		right.add(mJFTFSlotInterval);
		mJFTFSlotInterval.setHorizontalAlignment(SwingConstants.CENTER);
		mJFTFSlotInterval.setValue(60);
		mJFTFSlotInterval.setMaximumSize(
				new Dimension(mJFTFSlotInterval.getParent().getMaximumSize().width / 2, Configuration.ITEM_HEIGHT));

		right.add(mJCBSplitting);

		right.add(mJBDrawHisto);
		mJBDrawHisto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ChartPanel chP = new ChartPanel(Histogram.draw(
							new Histogram(Integer.parseInt(mJFTFSlotInterval.getText()), mListener.fillHistogram()),
							mJCBSplitting.isSelected()));
					left.add(chP, BorderLayout.CENTER);
					left.validate();
				} catch (Exception exception) {
					System.out.println(exception);
					exception.printStackTrace();
				}
			}
		});
		right.add(mJBSaveHisto);
		mJBSaveHisto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Deal with saving");
			}
		});

		right.add(mJBExportCSV);
		mJBExportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(Configuration.RESOURCES_FOLDER) {
					private static final long serialVersionUID = 1L;

					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						if (f.exists() && getDialogType() == SAVE_DIALOG) {
							int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?",
									"Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
							switch (result) {
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
							}
						}
						super.approveSelection();
					}
				};
				jfc.setDialogTitle("Save CSV file");

				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File output = jfc.getSelectedFile();
					try {
						if (!output.getName().contains(".csv")) {
							output = new File(output.getParent(), String.format("%s.csv", output.getName()));
						}
						Histogram.saveCSVFile(
								new Histogram(Integer.parseInt(mJFTFSlotInterval.getText()), mListener.fillHistogram()),
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

		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST);
		add(mRLHistogram, BorderLayout.PAGE_END);

		validate();

		return;
	}

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		mJBDrawHisto.setEnabled(pEnabled);
		mJBSaveHisto.setEnabled(pEnabled);
		mJBExportCSV.setEnabled(pEnabled);
		mJCBSplitting.setEnabled(pEnabled);
		mJFTFSlotInterval.setEnabled(pEnabled);
	}

	public interface HistogramListener {
		List<SoftLog> fillHistogram();

		void saveHistogram();
	}

}
