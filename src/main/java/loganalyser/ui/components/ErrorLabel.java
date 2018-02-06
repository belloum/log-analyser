package loganalyser.ui.components;

import java.awt.Color;

import javax.swing.JLabel;

import loganalyser.old.ui.CustomComponent;
import loganalyser.utils.Configuration;

public class ErrorLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorLabel() {
		super();
		init();
		setVisible(false);
	}

	public ErrorLabel(String text) {
		super(text);
		init();
	}

	private void init() {
		setOpaque(true);
		setBackground(Configuration.RED_COLOR);
		setForeground(Color.WHITE);
		CustomComponent.boldLabel(this);
	}

}
