package ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

public class DateFiler extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> mDatePicker = new JComboBox<String>();
	private List<String> mDates = new ArrayList<>();

	public DateFiler() {
		super();
		xOffset = 5;
		yOffset = 5;
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
		int position = getCount() == 1 ? this.mDatePicker.getSelectedIndex() : this.mDatePicker.getSelectedIndex() - 1;
		return this.mDates.get(position);
	}

	protected void build() {
		super.build();
		mDatePicker.removeAllItems();
		if (this.mDates.size() > 1) {
			mDatePicker.addItem("All days");
		}
		addComponentHorizontally(this.mDatePicker, (int) (0.85 * getWidth()), false);
		System.out.println(mDatePicker.getWidth());
		Collections.sort(this.mDates);
		this.mDates.forEach(date -> {
			mDatePicker.addItem(date);
		});
		return;
	}
}
