package ui;

import java.awt.event.ActionListener;

import javax.swing.JLabel;

public interface PickLogFile extends ActionListener {
	
	enum ResultType {
		SUCCESS, ERROR
	}

	void setJLabelResult(JLabel pJLabelResult);
	
	void printResult(String pMessage, ResultType pResultType);
}
