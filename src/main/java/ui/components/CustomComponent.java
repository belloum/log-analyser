package ui.components;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Configuration;

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

	protected void build() {
		removeAll();
	};
}
