package ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Configuration;

public class DataSetComponent extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private Integer mMaxWidth;

	private LinkedHashMap<String, Object> mProperties;

	public DataSetComponent(Integer pMaxWidth) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.mProperties = new LinkedHashMap<>();
		this.mMaxWidth = pMaxWidth;
	}

	public void setLabels(List<String> pPropertyLabels) {
		pPropertyLabels.forEach(property -> {
			mProperties.put(property, "-");
		});
		build();
	}

	public DataSetComponent(LinkedHashMap<String, Object> pProperties, Integer pMaxWidth) {
		this(pMaxWidth);
		this.mProperties = pProperties;
		build();
	}

	public List<String> getLabels() {
		if (this.mProperties != null) {
			return new ArrayList<>(this.mProperties.keySet());
		} else {
			throw new IllegalArgumentException("Null properties");
		}
	}

	public void setValue(String pKey, Object pValue) {
		if (this.mProperties.containsKey(pKey)) {
			this.mProperties.put(pKey, pValue.toString());
			build();
		} else {
			throw new IllegalArgumentException(
					new StringBuffer("No property").append(" `").append(pKey).append("`").toString());
		}
	}

	public void setProperties(LinkedHashMap<String, Object> pProperties) {
		this.mProperties = pProperties;
		build();
	}

	public void resetViews() {
		mProperties.forEach((key, value) -> {
			mProperties.put(key, "-");
		});
		build();
	}

	protected void build() {
		super.build();
		// TODO update here
		int a = 20 * mMaxWidth / 100;
		setMaximumSize(new Dimension(a, mProperties.size() * Configuration.ITEM_HEIGHT));

		this.mProperties.forEach((key, value) -> {

			JPanel jPanel = new JPanel(new BorderLayout());
			add(jPanel);
			jPanel.setPreferredSize(new Dimension(a, Configuration.ITEM_HEIGHT));

			JLabel jLabel = new JLabel(key);
			jLabel.setPreferredSize(new Dimension(a / 2, Configuration.ITEM_HEIGHT));
			setJLabelFontStyle(jLabel, Font.BOLD);
			jPanel.add(jLabel, BorderLayout.LINE_START);

			jLabel = new JLabel(value.toString());
			jLabel.setMaximumSize(new Dimension(a / 2, Configuration.ITEM_HEIGHT));
			jPanel.add(jLabel, BorderLayout.LINE_END);
			jPanel.add(jLabel);
		});

		return;
	}

}
