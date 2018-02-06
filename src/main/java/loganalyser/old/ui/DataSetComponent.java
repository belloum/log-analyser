package loganalyser.old.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import loganalyser.utils.Configuration;

public class DataSetComponent extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private LinkedHashMap<String, Object> mProperties = new LinkedHashMap<>();

	public DataSetComponent(Integer pMaxWidth) {
		super(pMaxWidth);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	public DataSetComponent(Integer pMaxWidth, List<String> pLabels) {
		this(pMaxWidth);
		setLabels(pLabels);
		build();
	}

	public void setLabels(List<String> pPropertyLabels) {
		pPropertyLabels.forEach(property -> {
			mProperties.put(property, "-");
		});
	}

	public List<String> getLabels() {
		return new ArrayList<>(this.mProperties.keySet());
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
		removeAll();

		this.mProperties.forEach((key, value) -> {

			JPanel jPanel = new JPanel(new BorderLayout());
			add(jPanel);
			jPanel.setPreferredSize(new Dimension(getMaximumSize().width, Configuration.ITEM_HEIGHT));

			JLabel jLabel = new JLabel(key);
			jLabel.setPreferredSize(new Dimension(getMaximumSize().width / 2, Configuration.ITEM_HEIGHT));
			setJLabelFontStyle(jLabel, Font.BOLD);
			jPanel.add(jLabel, BorderLayout.LINE_START);

			jLabel = new JLabel(value.toString());
			jLabel.setMaximumSize(new Dimension(getMaximumSize().width / 2, Configuration.ITEM_HEIGHT));
			jPanel.add(jLabel, BorderLayout.LINE_END);
			jPanel.add(jLabel);
		});

		validate();

		return;
	}

}
