package loganalyser.old.ui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import loganalyser.utils.Configuration;

public class DateFiler extends CustomComponent implements ItemListener {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> mDatePicker = new JComboBox<String>();
	private JCheckBox mJCBAllDay = new JCheckBox("All day long");
	private InputValue mInputStartHour = new InputValue("##:##");
	private InputValue mInputEndHour = new InputValue("##:##");

	private List<String> mDates = new ArrayList<>();

	public DateFiler() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public DateFiler(Integer pMaxWidth) {
		super(pMaxWidth);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		build();
	}

	public DateFiler(Dimension pDimension) {
		super(pDimension);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		build();
	}

	public List<String> getDates() {
		return mDates;
	}

	public void setDates(List<String> pDates) {
		this.mDates = pDates;
		build();
	}

	public int getSelectedPosition() {
		return this.mDatePicker.getSelectedIndex();
	}

	public int getCount() {
		return this.mDates.size();
	}

	public String getSelectedItem() {
		int position = (getCount() == 1) ? this.mDatePicker.getSelectedIndex()
				: this.mDatePicker.getSelectedIndex() - 1;
		return this.mDates.get(position);
	}

	public void reset() {
		mDates.clear();
		build();
	}

	public boolean isFilteringByTimestamp() {
		return !mJCBAllDay.isSelected();
	}

	public String getStartHour() {
		return mInputStartHour.getText();
	}

	public String getEndHour() {
		return mInputEndHour.getText();
	}

	public void setEnabled(boolean pEnabled) {
		this.mDatePicker.setEnabled(pEnabled);
		this.mJCBAllDay.setEnabled(pEnabled);
	}

	public void build() {
		removeAll();
		mDatePicker.removeAllItems();
		setEnabled(!this.mDates.isEmpty());

		if (this.mDates.isEmpty()) {
			mDatePicker.addItem("None");
		} else if (this.mDates.size() > 1) {
			mDatePicker.addItem("All days");
		}
		this.mDates.forEach(date -> {
			mDatePicker.addItem(date);
		});

		this.mDatePicker.setMaximumSize(new Dimension(30 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		add(this.mDatePicker);

		this.mJCBAllDay.setSelected(true);
		this.mJCBAllDay.addItemListener(this);
		this.mJCBAllDay.setMaximumSize(new Dimension(20 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		add(this.mJCBAllDay);

		this.mInputStartHour.setEditable(false);
		this.mInputStartHour.setValue("00:00");
		this.mInputStartHour
				.setMaximumSize(new Dimension(15 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		add(this.mInputStartHour);

		this.mInputEndHour.setEditable(false);
		this.mInputEndHour.setValue("23:59");
		this.mInputEndHour.setMaximumSize(new Dimension(15 * getMaximumSize().width / 100, Configuration.ITEM_HEIGHT));
		add(this.mInputEndHour);

		validate();

		return;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.DESELECTED == e.getStateChange()) {
			this.mInputStartHour.setEditable(true);
			this.mInputEndHour.setEditable(true);
		} else {
			this.mInputStartHour.setValue("00:00");
			this.mInputStartHour.setEditable(false);
			this.mInputEndHour.setValue("23:59");
			this.mInputEndHour.setEditable(false);
		}
	}
}
