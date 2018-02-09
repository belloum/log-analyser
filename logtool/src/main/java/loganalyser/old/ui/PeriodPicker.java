package loganalyser.old.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXDatePicker;

import beans.Period;
import loganalyser.exceptions.PeriodException;
import loganalyser.utils.Configuration;

public class PeriodPicker extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private static final String SELECT_PERIOD = "Select a period";

	private JXDatePicker mStartDate = new JXDatePicker(new Date()), mEndDate = new JXDatePicker(new Date());
	private PeriodPickerListener mListener;

	private String mLabel;

	public PeriodPicker(Dimension pDimension) {
		this(pDimension, SELECT_PERIOD, LEFT_ALIGNMENT, null);
	}

	public PeriodPicker(Dimension pDimension, String pLabel) {
		this(pDimension, pLabel, LEFT_ALIGNMENT, null);
	}

	public PeriodPicker(Dimension pDimension, String pLabel, float pAlignement, PeriodPickerListener pListener) {
		super(pDimension, null);
		this.mLabel = pLabel;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPreferredSize(pDimension);
		setAlignmentX(pAlignement);
		if (pListener != null) {
			this.mListener = pListener;
		}
		build();
	}

	protected void build() {
		super.build();

		if (StringUtils.isNotEmpty(mLabel)) {
			JLabel jLabel = new JLabel(this.mLabel);
			super.setJLabelFontStyle(jLabel, Font.BOLD);
			add(jLabel);
			jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width + Configuration.PADDING,
					jLabel.getPreferredSize().height));
			jLabel.setMaximumSize(jLabel.getPreferredSize());
		}

		add(mStartDate);
		mStartDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand())) {
					if (mListener != null) {
						if (isPeriodValid()) {
							mListener.validPeriod();
						} else {
							mListener.invalidPeriod();
						}
					}
				}
			}
		});
		mStartDate.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));

		add(mEndDate);
		mEndDate.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));
		mEndDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand())) {
					if (mListener != null) {
						if (isPeriodValid()) {
							mListener.validPeriod();
						} else {
							mListener.invalidPeriod();
						}
					}
				}
			}
		});

		validate();
	};

	public Period getPeriod() throws PeriodException {
		return new Period(mStartDate.getDate(), mEndDate.getDate());
	}

	private boolean isPeriodValid() {
		return mStartDate.getDate().before(mEndDate.getDate()) || mEndDate.getDate().equals(mStartDate.getDate());
	}

	public interface PeriodPickerListener {
		void validPeriod();

		void invalidPeriod();
	}

}
