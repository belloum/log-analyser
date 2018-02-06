package loganalyser.ui.panels.settings;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import beans.devices.Device;
import beans.devices.Device.DeviceType;
import loganalyser.beans.Routine;
import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.InputValue;
import loganalyser.operators.settings.RoutineSettings;
import loganalyser.operators.settings.WakeUpAndGoBedSettings;

public class WakeAndBedParametersPanel extends RoutineParameterPanel {

	private static final long serialVersionUID = 1L;

	public static final Integer WAKE_UP = 0;
	public static final Integer GO_BED = 1;

	/**
	 * Parameters:
	 * <ul>
	 * <li>T the time between markers</li>
	 * <li>1 the bathroom sensor</li>
	 * <li>2 the bedroom sensor</li>
	 * </ul>
	 */
	private static final String DEFAULT_DELAY = "10";
	private final Integer mMode;

	public WakeAndBedParametersPanel(List<Device> pDevices, Routine pRoutine, int pMode) {
		super(pDevices, pRoutine);
		this.mMode = pMode == GO_BED ? GO_BED : WAKE_UP;
	}

	@Override
	public ProcessBuilder script(Routine pRoutine, File pFileToUse) throws Exception {
		validParameters();
		WakeUpAndGoBedSettings ws = getRoutineSettings();

		String markerI = this.mMode == GO_BED ? String.format("-1%s", ws.getRoutineMarker())
				: String.format("-1%s", ws.getBedroomMarker());

		String markerII = this.mMode == GO_BED ? String.format("-2%s", ws.getBedroomMarker())
				: String.format("-2%s", ws.getRoutineMarker());

		return new ProcessBuilder(getScriptName(), String.format("-b%s", ws.getStartHour()),
				String.format("-e%s", ws.getEndHour()), markerI, markerII,
				String.format("-T%d", ws.getDelayBetweenMarker()), pFileToUse.getAbsolutePath());
	}

	@Override
	protected WakeUpAndGoBedSettings getRoutineSettings() {
		return (WakeUpAndGoBedSettings) super.mSettings;
	}

	@Override
	public List<ActivityResult> getResults(File pFileToUse) throws Exception {
		return Arrays.asList(executeScript(script(getRoutine(), pFileToUse))).stream()
				.map(line -> new ActivityResult(line)).collect(Collectors.toList());
	}

	@Override
	public String getScriptName() {
		return this.mMode == GO_BED ? "./gotobed.pl" : "./wakeup.pl";
	}

	@Override
	protected JPanel customParameters() {

		JPanel bedroom = new JPanel(new BorderLayout());
		JComboBox<String> bed = new JComboBox<>();
		getWakeUpDevices().forEach(device -> bed.addItem(device.getId()));
		bed.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				getRoutineSettings().setBedroomMarker(e.getItem().toString());
			}
		});
		bedroom.add(CustomComponent.boldLabel("MotionD Bedroom"), BorderLayout.WEST);
		bedroom.add(bed, BorderLayout.CENTER);

		JPanel routine = new JPanel(new BorderLayout());
		JComboBox<String> rout = new JComboBox<>();
		getRoutineDevices().forEach(device -> rout.addItem(device.getId()));
		routine.add(CustomComponent.boldLabel("MotionD Routine"), BorderLayout.WEST);
		routine.add(rout, BorderLayout.CENTER);

		JPanel time = new JPanel(new BorderLayout());
		InputValue delay = new InputValue(DEFAULT_DELAY);
		delay.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (validDelay(delay.getText())) {
					getRoutineSettings().setDelayBetweenMarker(Integer.parseInt(delay.getText()));
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (validDelay(delay.getText())) {
					getRoutineSettings().setDelayBetweenMarker(Integer.parseInt(delay.getText()));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (validDelay(delay.getText())) {
					getRoutineSettings().setDelayBetweenMarker(Integer.parseInt(delay.getText()));
				}
			}
		});
		time.add(CustomComponent.boldLabel("Time between markers"), BorderLayout.WEST);
		time.add(delay, BorderLayout.CENTER);

		JPanel mark = new JPanel(new GridLayout(1, 3));
		mark.add(bedroom);
		mark.add(routine);
		mark.add(time);

		getRoutineSettings().setDelayBetweenMarker(Integer.parseInt(delay.getText()));

		if (!getWakeUpDevices().isEmpty()) {
			getRoutineSettings().setBedroomMarker(getWakeUpDevices().get(0).getId());
		}

		if (!getRoutineDevices().isEmpty()) {
			getRoutineSettings().setRoutineMarker(getRoutineDevices().get(0).getId());
		}

		return mark;
	}

	@Override
	protected RoutineSettings instantiateSettings() {
		return new WakeUpAndGoBedSettings();
	}

	private boolean validDelay(String pDelay) {
		try {
			int delay = Integer.parseInt(pDelay);
			return delay > 0 && delay < 60 * 24;
		} catch (Exception e) {
			return false;
		}
	}

	protected List<Device> getWakeUpDevices() {
		return super.getRoutineDevices().stream().filter(device -> device.getType().equals(DeviceType.MotionDetector)
				&& device.getLocation().equalsIgnoreCase("Bedroom")).collect(Collectors.toList());
	}

	@Override
	protected List<Device> getRoutineDevices() {
		return super.getRoutineDevices().stream().filter(
				device -> device.getType().equals(DeviceType.MotionDetector) && !getWakeUpDevices().contains(device))
				.collect(Collectors.toList());
	}

}
