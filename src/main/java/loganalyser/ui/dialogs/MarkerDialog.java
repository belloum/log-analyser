package loganalyser.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import beans.devices.Device;
import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.MyButton;

public class MarkerDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PRIMARY_MARKER = "primary";
	private static final String SECONDARY_MARKER = "secondary";
	private static final String IGNORED_MARKER = "ignored";

	private Map<Device, ButtonGroup> mSelection = new HashMap<>();
	private MarkerSelectionListener mListener;

	@SuppressWarnings("unchecked")
	public MarkerDialog(List<Device> pDevices, MarkerSelectionListener pListener) {
		this(pDevices, new ArrayList[] { new ArrayList<>(), new ArrayList<>() }, pListener);
	}

	public MarkerDialog(List<Device> pDevices, List<Device>[] pCurrentSelection, MarkerSelectionListener pListener) {
		super(new JFrame(), "Select markers");
		setLocation(400, 400);
		setResizable(false);
		this.mListener = pListener;

		JPanel content = new JPanel(new BorderLayout());

		JPanel devList = new JPanel(new GridLayout(pDevices.size() + 1, 1));
		devList.add(CustomComponent.boldLabel("Device"));
		pDevices.forEach(device -> devList.add(new JLabel(device.getId())));

		JPanel selectors = new JPanel(new GridLayout(pDevices.size() + 1, 1));
		selectors.add(CustomComponent.boldLabel("Marker I\t"));
		selectors.add(CustomComponent.boldLabel("Marker II\t"));
		selectors.add(CustomComponent.boldLabel("Ignored\t"));

		pDevices.forEach(d -> {

			JRadioButton markIBtn = new JRadioButton();
			markIBtn.setActionCommand(PRIMARY_MARKER);
			markIBtn.setSelected(pCurrentSelection[0].contains(d));
			selectors.add(markIBtn);

			JRadioButton markIIBtn = new JRadioButton();
			markIIBtn.setActionCommand(SECONDARY_MARKER);
			markIIBtn.setSelected(pCurrentSelection[1].contains(d));
			selectors.add(markIIBtn);

			JRadioButton ignBtn = new JRadioButton();
			ignBtn.setActionCommand(IGNORED_MARKER);
			ignBtn.setSelected(!pCurrentSelection[0].contains(d) && !pCurrentSelection[1].contains(d));
			ignBtn.setSelected(true);
			selectors.add(ignBtn);

			ButtonGroup group = new ButtonGroup();
			group.add(markIBtn);
			group.add(markIIBtn);
			group.add(ignBtn);

			mSelection.put(d, group);
		});

		content.setLayout(new BorderLayout());
		content.add(devList, BorderLayout.WEST);
		content.add(selectors, BorderLayout.CENTER);
		content.add(new MyButton("Valid markers", event -> {
			applySelection();
			close();
		}), BorderLayout.PAGE_END);

		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(content, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	// override the createRootPane inherited by the JDialog, to create the rootPane.
	// create functionality to close the window when "Escape" button is pressed
	public JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action action = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", action);
		return rootPane;
	}

	private void applySelection() {

		if (this.mListener != null) {
			mListener
					.setMarkers(
							this.mSelection.entrySet().stream()
									.filter(map -> map.getValue().getSelection().getActionCommand()
											.equals(PRIMARY_MARKER))
									.map(m -> m.getKey()).collect(Collectors.toList()),
							this.mSelection.entrySet().stream().filter(
									map -> map.getValue().getSelection().getActionCommand().equals(SECONDARY_MARKER))
									.map(m -> m.getKey()).collect(Collectors.toList()));
		} else {
			System.err.println("MarkerSelectionListener is null");
		}
	}

	public void build() {
		setVisible(true);
	}

	public void close() {
		dispose();
		setVisible(false);
	}

	public interface MarkerSelectionListener {
		void setMarkers(List<Device> pPrimaryMarkers, List<Device> pSecondaryMarkers);
	}

}