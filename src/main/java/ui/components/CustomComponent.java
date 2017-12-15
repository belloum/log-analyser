package ui.components;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Configuration;

public abstract class CustomComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;

	public CustomComponent() {
		setLayout(null);
	}

	public static void setJLabelFontStyle(JLabel pJlLabel, int pFontStyle) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), pFontStyle, pJlLabel.getFont().getSize()));
	}


	protected void build() {
		removeAll();
		redraw();
	};

	private void redraw() {
		setVisible(false);
		setVisible(true);
	}

}
