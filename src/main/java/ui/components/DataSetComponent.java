package ui.components;

import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JLabel;

import utils.Configuration;

public class DataSetComponent extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private LinkedHashMap<String, Object> mProperties;

	public DataSetComponent() {
		super();
		mProperties = new LinkedHashMap<>();
	}

	public void setLabels(List<String> pPropertyLabels) {
		pPropertyLabels.forEach(property -> {
			mProperties.put(property, "-");
		});
		build();
	}

	public DataSetComponent(LinkedHashMap<String, Object> pProperties) {
		this();
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
		xOffset = 5;
		this.mProperties.forEach((key, value) -> {

			JLabel jlabel = new JLabel(key);
			addComponentHorizontally(jlabel, getWidth() / 2, false);
			setJLabelFontStyle(jlabel, Font.BOLD);

			jlabel = new JLabel(value.toString());// , SwingConstants.RIGHT);
			addComponentHorizontally(jlabel, getWidth() / 2, false);
			yOffset += Configuration.ITEM_HEIGHT;
			xOffset = 5;
		});
		return;
	}

}
