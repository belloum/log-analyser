package ui.components;

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

	// UI
	private JComboBox<String> mDatePicker = new JComboBox<String>();
	private JCheckBox mJCheckBox = new JCheckBox("All day long");
	private JFormattedTextField mJFTFStartHour = new JFormattedTextField("##:##"),
			mJFTFEndHour = new JFormattedTextField("##:##");

	private List<String> mDates = new ArrayList<>();

	public DateFiler() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public JComboBox<String> getDatePicker() {
		return mDatePicker;
	}

	public void setDatePicker(JComboBox<String> pDatePicker) {
		this.mDatePicker = pDatePicker;
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

	protected void build() {
		mDatePicker.removeAllItems();
		if (this.mDates.size() > 1) {
			mDatePicker.addItem("All days");
		}
		this.mDates.forEach(date -> {
			mDatePicker.addItem(date);
		});

		int maxWidth = getParent().getPreferredSize().width;

		this.mDatePicker.setMaximumSize(new Dimension(30 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		add(this.mDatePicker);

		this.mJCheckBox.setSelected(true);
		this.mJCheckBox.addItemListener(this);
		this.mJCheckBox.setMaximumSize(new Dimension(20 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		add(this.mJCheckBox);

		this.mJFTFStartHour.setEditable(false);
		this.mJFTFStartHour.setValue("00:00");
		this.mJFTFStartHour.setMaximumSize(new Dimension(20 * maxWidth / 100, Configuration.ITEM_HEIGHT));
		this.mJFTFStartHour.setHorizontalAlignment(SwingConstants.CENTER);
		add(this.mJFTFStartHour);

		this.mJFTFEndHour.setEditable(false);
		this.mJFTFEndHour.setValue("23:59");
		this.mJFTFEndHour.setHorizontalAlignment(SwingConstants.CENTER);
		this.mJFTFEndHour.setMaximumSize(new Dimension(20 * maxWidth / 100, Configuration.ITEM_HEIGHT));
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
