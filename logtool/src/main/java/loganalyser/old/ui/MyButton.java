package loganalyser.old.ui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MyButton extends JButton {

	private static final long serialVersionUID = 1L;

	public MyButton(String pText, ActionListener pAction) {
		super(pText);
		addActionListener(pAction);
	}

}
