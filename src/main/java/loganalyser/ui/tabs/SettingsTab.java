package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.MyButton;

public class SettingsTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	public SettingsTab() {
		super();
		add(content(), BorderLayout.CENTER);
	}

	@Override
	protected Component content() {

		final JPanel content = new JPanel(new BorderLayout());

		content.add(settingsPanel(), BorderLayout.PAGE_START);

		return content;
	}

	private JPanel settingsPanel() {
		final JPanel genericSettings = new JPanel(new GridLayout(4, 1));

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(CustomComponent.boldLabel("Configuration file"), BorderLayout.PAGE_START);
		JTextField a = new JTextField("Default configuration file");
		a.setEditable(false);
		pan.add(a, BorderLayout.CENTER);
		pan.add(new MyButton("He says", event -> System.out.println("Eat your shorts !")), BorderLayout.EAST);
		genericSettings.add(pan);

		pan = new JPanel(new BorderLayout());
		pan.add(CustomComponent.boldLabel("Default folder"), BorderLayout.PAGE_START);
		a = new JTextField("Default log folder");
		a.setEditable(false);
		pan.add(a, BorderLayout.CENTER);
		pan.add(new MyButton("He says", event -> System.out.println("Eat your shorts !")), BorderLayout.EAST);
		genericSettings.add(pan);

		pan = new JPanel(new BorderLayout());
		pan.add(CustomComponent.boldLabel("Log file"), BorderLayout.PAGE_START);
		a = new JTextField("Default log file");
		a.setEditable(false);
		pan.add(a, BorderLayout.CENTER);
		pan.add(new MyButton("He says", event -> System.out.println("Eat your log file !")), BorderLayout.EAST);
		genericSettings.add(pan);

		pan = new JPanel(new BorderLayout());
		pan.add(CustomComponent.boldLabel("Log error file"), BorderLayout.PAGE_START);
		a = new JTextField("Default log error file");
		a.setEditable(false);
		pan.add(a, BorderLayout.CENTER);
		pan.add(new MyButton("He says", event -> System.out.println("Eat your log error sile !")), BorderLayout.EAST);
		genericSettings.add(pan);

		return genericSettings;
	}

	@Override
	public String configurationSection() {
		return "settings";
	}
}
