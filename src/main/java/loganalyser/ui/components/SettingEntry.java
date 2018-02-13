package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import loganalyser.old.ui.CustomComponent;

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
		setLayout(new BorderLayout(5, 5));
		mButton = new JButton();
		mTextField = new JTextField();
		build(pName, pDescription, pValue, pAction);
	}

	private void build(final String pName, final String pDescription, final Object pValue, ActionListener pAction) {
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel container = CustomComponent.addLineBorder(new JPanel(), Color.GRAY, 1);
		JPanel content = new JPanel(new BorderLayout());

		final JPanel pan = new JPanel(new BorderLayout());
		pan.add(CustomComponent.boldLabel(pName), BorderLayout.PAGE_START);

		mTextField.setText(pValue.toString());
		mTextField.setEditable(false);
		pan.add(mTextField, BorderLayout.CENTER);

		mButton.setText("Update");
		mButton.addActionListener(pAction != null ? pAction : DEFAULT_ACTION);
		pan.add(mButton, BorderLayout.EAST);
		content.add(pan, BorderLayout.PAGE_START);
		content.add(new JLabel(pDescription), BorderLayout.CENTER);

		container.add(CustomComponent.addEmptyBorder(content, 3));
		add(container, BorderLayout.CENTER);
	}

	public void addActionListener(final ActionListener pAction) {
		Arrays.stream(mButton.getActionListeners()).forEach(action -> mButton.removeActionListener(action));
		mButton.addActionListener(pAction);
	}

	public void updateProperty(String pValue) {
		mTextField.setText(pValue);
	}

}
