package loganalyser.old.ui;

import java.awt.Color;

import javax.swing.JLabel;

public class ResultLabel extends JLabel {

	private Color mSuccessColor = Color.BLACK;
	private Color mErrorColor = Color.RED;

	private static final long serialVersionUID = 1L;

	public enum ResultType {
		SUCCESS, ERROR
	}

	public ResultLabel() {
		super();
	}

	public ResultLabel(String pText) {
		super(pText);
	}

	public ResultLabel(Color pSuccessColor, Color pErrorColor) {
		this();
		this.mErrorColor = pErrorColor;
		this.mSuccessColor = pSuccessColor;
	}

	public void printResult(String pMessage, ResultType pResultType) {
		setVisible(true);
		setText(pMessage);
		switch (pResultType) {
		case ERROR:
			setForeground(this.mErrorColor);
			break;
		case SUCCESS:
			setForeground(this.mSuccessColor);
			break;
		}
	}

	public Color getSuccessColor() {
		return mSuccessColor;
	}

	public void setSuccessColor(Color pSuccessColor) {
		this.mSuccessColor = pSuccessColor;
	}

	public Color getErrorColor() {
		return mErrorColor;
	}

	public void setErrorColor(Color pErrorColor) {
		this.mErrorColor = pErrorColor;
	}

}
