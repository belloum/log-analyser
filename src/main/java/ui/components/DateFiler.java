package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;

import utils.Configuration;

public class DateFiler extends CustomComponent implements ItemListener {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> mDatePicker = new JComboBox<String>();
	private JCheckBox mJCBAllDay = new JCheckBox("All day long");
	// TODO isItUseful ?
	private JFormattedTextField mJFTFStartHour = new JFormattedTextField("##:##") {

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
	}, mJFTFEndHour = new JFormattedTextField("##:##") {

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

	private List<String> mDates;

	public DateFiler() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.mDates = new ArrayList<>();
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
		return mJFTFStartHour.getText();
	}

	public String getEndHour() {
		return mJFTFEndHour.getText();
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

		int maxWidth = getParent().getPreferredSize().width;

		this.mDatePicker.setMaximumSize(new Dimension(30 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		add(this.mDatePicker);

		this.mJCBAllDay.setSelected(true);
		this.mJCBAllDay.addItemListener(this);
		this.mJCBAllDay.setMaximumSize(new Dimension(20 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		add(this.mJCBAllDay);

		this.mJFTFStartHour.setEditable(false);
		this.mJFTFStartHour.setValue("00:00");
		this.mJFTFStartHour.setMaximumSize(new Dimension(15 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		this.mJFTFStartHour.setHorizontalAlignment(SwingConstants.CENTER);
		add(this.mJFTFStartHour);

		this.mJFTFEndHour.setEditable(false);
		this.mJFTFEndHour.setValue("23:59");
		this.mJFTFEndHour.setHorizontalAlignment(SwingConstants.CENTER);
		this.mJFTFEndHour.setMaximumSize(new Dimension(15 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		add(this.mJFTFEndHour);

		return;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.DESELECTED == e.getStateChange()) {
			this.mJFTFStartHour.setEditable(true);
			this.mJFTFEndHour.setEditable(true);
		} else {
			this.mJFTFStartHour.setValue("00:00");
			this.mJFTFStartHour.setEditable(false);
			this.mJFTFEndHour.setValue("23:59");
			this.mJFTFEndHour.setEditable(false);
		}
	}
}
