package loganalyser.ui.components;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import loganalyser.old.ui.CustomComponent;

public class LegendValueLabel extends JPanel {

	private static Integer DEFAULT_PADDING = 5;

	private JLabel mLegend, mValue;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LegendValueLabel(String pLabel, String pValue) {
		setLayout(new BorderLayout(DEFAULT_PADDING, 0));
		mLegend = CustomComponent.boldLabel();
		mValue = new JLabel();
		setLegend(pLabel);
		setValue(pValue);
		build();
	}

	private void build() {
		add(mLegend, BorderLayout.WEST);
		add(mValue, BorderLayout.CENTER);
		validate();
	}

	public String getLegend() {
		return mLegend.getText();
	}

	public String getValue() {
		return mValue.getText();
	}

	public void setLegend(String pLegend) {
		this.mLegend.setText(pLegend);
	}

	public void setValue(String pValue) {
		this.mValue.setText(pValue);
	}

	public void setPadding(int pPadding) {
		setLayout(new BorderLayout(pPadding, 0));
		build();
	}

}
