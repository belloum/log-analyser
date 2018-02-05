package ui.last.components;

import java.awt.Color;

import javax.swing.JLabel;

import ui.components.CustomComponent;
import utils.Configuration;

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
