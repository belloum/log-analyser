package loganalyser.old.ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JLabelWithLegend extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JLabelWithLegend(String legend, String value) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel a = CustomComponent.boldLabel(new JLabel(legend));
		a.setBorder(new EmptyBorder(0, 0, 0, 10));
		add(a);
		add(new JLabel(value));
	}

}
