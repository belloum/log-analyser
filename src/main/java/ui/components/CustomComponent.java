package ui.components;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class CustomComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	public CustomComponent() {
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
