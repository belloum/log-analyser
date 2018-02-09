package loganalyser.old.ui;

import java.awt.Color;

import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;

public class InputValue extends JFormattedTextField {

	private static final long serialVersionUID = 1L;

	public InputValue(Object value) {
		super(value);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	public InputValue() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public void setEditable(boolean pEditabled) {
		super.setEditable(pEditabled);
		if (!pEditabled) {
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(Color.WHITE);
		}
	}

}
