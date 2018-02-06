package loganalyser.old.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import loganalyser.utils.Configuration;

public abstract class CustomComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	public CustomComponent() {
	}

	public CustomComponent(int pMaxWidth) {
		this(new Dimension(pMaxWidth, Configuration.ITEM_HEIGHT));
	}

	public CustomComponent(Dimension pDimension) {
		this(pDimension, null);
	}

	public CustomComponent(Dimension pDimension, LayoutManager pLayoutManager) {
		setLayout(pLayoutManager);
		setMaximumSize(pDimension);
	}

	public CustomComponent(LayoutManager pLayoutManager) {
		setLayout(pLayoutManager);
	}

	public static void setJLabelFontStyle(JLabel pJlLabel, int pFontStyle) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), pFontStyle, pJlLabel.getFont().getSize()));
	}

	public static JLabel boldLabel(JLabel pJlLabel) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public static JLabel boldLabel(String pText) {
		JLabel pJlLabel = new JLabel(pText);
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public static JLabel boldLabel() {
		JLabel pJlLabel = new JLabel();
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public void addMultiLineText(List<String> pItems, int pItemByLine, JPanel pContainer) {
		List<String> line = new ArrayList<>();
		for (int i = 0; i < pItems.size(); i++) {
			if (line.size() == pItemByLine || i == pItems.size() - 1) {
				pContainer.add(new JLabel(StringUtils.join(line, ", ")));
				line.clear();
			} else {
				line.add(pItems.get(i));
			}
		}
	}

	protected void build() {
		removeAll();
	};
}
