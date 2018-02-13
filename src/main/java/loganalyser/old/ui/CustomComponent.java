package loganalyser.old.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import loganalyser.utils.Configuration;

public abstract class CustomComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	public CustomComponent() {
	}

	public CustomComponent(final int pMaxWidth) {
		this(new Dimension(pMaxWidth, Configuration.ITEM_HEIGHT));
	}

	public CustomComponent(final Dimension pDimension) {
		this(pDimension, null);
	}

	public CustomComponent(final Dimension pDimension, final LayoutManager pLayoutManager) {
		setLayout(pLayoutManager);
		setMaximumSize(pDimension);
	}

	public CustomComponent(final LayoutManager pLayoutManager) {
		setLayout(pLayoutManager);
	}

	public static void setJLabelFontStyle(final JLabel pJlLabel, final int pFontStyle) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), pFontStyle, pJlLabel.getFont().getSize()));
	}

	public static JLabel boldLabel(final JLabel pJlLabel) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public static JLabel boldLabel(final String pText) {
		final JLabel pJlLabel = new JLabel(pText);
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public static JLabel boldLabel() {
		final JLabel pJlLabel = new JLabel();
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), Font.BOLD, pJlLabel.getFont().getSize()));
		return pJlLabel;
	}

	public static JPanel addEmptyBorder(JComponent pComponent, int pTop, int pLeft, int pBottom, int pRight) {
		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createEmptyBorder(pTop, pLeft, pBottom, pRight));
		container.add(pComponent, BorderLayout.CENTER);
		return container;
	}

	public static JPanel addEmptyBorder(JComponent pComponent, int pThickness) {
		return addEmptyBorder(pComponent, pThickness, pThickness, pThickness, pThickness);
	}

	public static JPanel addLineBorder(JComponent pComponent, Color pColor, int pThickness) {
		JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createLineBorder(pColor, pThickness, false));
		container.add(pComponent, BorderLayout.CENTER);
		return container;
	}

	public void addMultiLineText(final List<String> pItems, final int pItemByLine, final JPanel pContainer) {
		final List<String> line = new ArrayList<>();
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
