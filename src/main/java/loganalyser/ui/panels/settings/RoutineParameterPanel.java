package loganalyser.ui.panels.settings;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import beans.devices.Device;
import loganalyser.beans.Routine;
import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.InputValue;
import loganalyser.operators.settings.Executable;
import loganalyser.operators.settings.RoutineSettings;

public abstract class RoutineParameterPanel extends JPanel implements Executable {

	private static final String DEFAULT_HOUR = "00:00";
	private static final Pattern HOUR_PATTERN = Pattern.compile("\\d{2}[:]\\d{2}$");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected RoutineSettings mSettings;
	private Routine mRoutine;

	private List<Device> mDevices;

	public RoutineParameterPanel(List<Device> pDevices, Routine pRoutine) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.mRoutine = pRoutine;
		this.mDevices = pDevices;
		this.mSettings = instantiateSettings();

		InputValue mStartHour = new InputValue(DEFAULT_HOUR);
		mStartHour.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (validHour(mStartHour.getText())) {
					mSettings.setStartHour(mStartHour.getText());
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (validHour(mStartHour.getText())) {
					mSettings.setStartHour(mStartHour.getText());
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (validHour(mStartHour.getText())) {
					mSettings.setStartHour(mStartHour.getText());
				}
			}
		});

		InputValue mEndHour = new InputValue(DEFAULT_HOUR);
		mEndHour.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (validHour(mEndHour.getText())) {
					mSettings.setEndHour(mEndHour.getText());
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (validHour(mEndHour.getText())) {
					mSettings.setEndHour(mEndHour.getText());
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (validHour(mEndHour.getText())) {
					mSettings.setEndHour(mEndHour.getText());
				}
			}
		});

		JPanel start = new JPanel(new BorderLayout());
		start.add(CustomComponent.boldLabel("Start hour"), BorderLayout.WEST);
		start.add(mStartHour, BorderLayout.CENTER);

		JPanel end = new JPanel(new BorderLayout());
		end.add(CustomComponent.boldLabel("End hour"), BorderLayout.WEST);
		end.add(mEndHour, BorderLayout.CENTER);

		JPanel hour = new JPanel(new GridLayout(1, 2));
		hour.add(start);
		hour.add(end);

		add(hour);
		add(customParameters());
	}

	private boolean validHour(String pHour) {
		return HOUR_PATTERN.matcher(pHour).find();
	}

	protected abstract JPanel customParameters();

	protected RoutineSettings getRoutineSettings() {
		return this.mSettings;
	};

	protected abstract RoutineSettings instantiateSettings();

	protected List<Device> getRoutineDevices() {
		return this.mDevices;
	}

	protected Routine getRoutine() {
		return this.mRoutine;
	}

	@Override
	public void validParameters() throws Exception {
		getRoutineSettings().validParameters();
	}
}
