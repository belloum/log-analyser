package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingEntry extends JPanel {

	private final JButton mButton;
	private final JTextField mTextField;
	private final ActionListener DEFAULT_ACTION = event -> System.out.println("Not implemented yet");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SettingEntry(final String pName, final String pDescription, final Object pValue) {
		this(pName, pDescription, pValue, null);
	}

	public SettingEntry(final String pName, final String pDescription, final Object pValue, ActionListener pAction) {
		setLayout(new BorderLayout());
		mButton = new JButton();
		mTextField = new JTextField();
		build(pName, pDescription, pValue, pAction);
	}

	private void build(final String pName, final String pDescription, final Object pValue, ActionListener pAction) {
		final JPanel pan = new JPanel(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(pName));

		mTextField.setText(pValue.toString());
		mTextField.setEditable(false);
		pan.add(mTextField, BorderLayout.CENTER);

		mButton.setText("Update");
		mButton.addActionListener(pAction != null ? pAction : DEFAULT_ACTION);
		pan.add(mButton, BorderLayout.EAST);
		add(pan, BorderLayout.PAGE_START);

		add(new JLabel(pDescription), BorderLayout.CENTER);
	}

	public void addActionListener(final ActionListener pAction) {
		Arrays.stream(mButton.getActionListeners()).forEach(action -> mButton.removeActionListener(action));
		mButton.addActionListener(pAction);
	}

}
