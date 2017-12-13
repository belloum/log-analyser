package ui.mytabs;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import beans.participants.Participant;
import operators.extractors.ParticipantExtractor;
import utils.Configuration;

public class TabParticipants extends AbstractTab {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> mParticipantsPicker;
	private JPanel mParticipantInfos;
	private JLabel mParticipantVera, mParticipantInstalled, mParticipantRoutines, mParticipantSensors;

	private Participant mParticipant;

	public TabParticipants(String title) throws Exception {
		super(title);
		int x = Configuration.MARGINS;
		int y = Configuration.MARGINS / 2;

		/////////////////////////////
		// Select a participant
		mParticipantsPicker = new JComboBox<String>();
		mParticipantsPicker.addItem("Select a userbox");
		ParticipantExtractor.extractParticipants(Configuration.PARTICIPANT_FILE)
				.forEach((participant) -> mParticipantsPicker.addItem(participant.getName()));
		mParticipantsPicker.setBounds(x, y, Configuration.LABEL_WIDTH_LONG, Configuration.ITEM_HEIGHT);
		mParticipantsPicker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					try {
						System.out.println("Selected userbox: " + e.getItem().toString());
						mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE,
								e.getItem().toString());
						fillParticipantInfos(mParticipant);
					} catch (Exception e1) {
						System.out.println("Unable to extract participant: " + e.getItem().toString());
					}
				}
			}
		});
		add(mParticipantsPicker);

		y += Configuration.ITEM_HEIGHT + Configuration.PADDING;
		mParticipantInfos = initParticipantView();
		mParticipantInfos.setBounds(x, y, mParticipantInfos.getSize().width, mParticipantInfos.getSize().height);
		add(mParticipantInfos);

		super.fillBlank(x, y);
	}

	private JPanel initParticipantView() {
		JPanel view = new JPanel();
		view.setLayout(null);

		int x = 0;
		int y = 0;
		JLabel label = new JLabel("Vera: ");
		label.setBounds(x, y, Configuration.LABEL_WIDTH_LITTLE, Configuration.ITEM_HEIGHT);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		view.add(label);

		mParticipantVera = new JLabel();
		x += Configuration.LABEL_WIDTH_LITTLE;
		mParticipantVera.setBounds(x, y, Configuration.MAX_WIDTH / 2 - x, Configuration.ITEM_HEIGHT);
		view.add(mParticipantVera);

		x = 0;
		y += Configuration.ITEM_HEIGHT + Configuration.PADDING;
		label = new JLabel("Installed by: ");
		label.setBounds(x, y, Configuration.LABEL_WIDTH_LITTLE, Configuration.ITEM_HEIGHT);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		view.add(label);

		mParticipantInstalled = new JLabel();
		x += Configuration.LABEL_WIDTH_LITTLE;
		mParticipantInstalled.setBounds(x, y, Configuration.MAX_WIDTH / 2 - x, Configuration.ITEM_HEIGHT);
		view.add(mParticipantInstalled);

		x = 0;
		y += Configuration.ITEM_HEIGHT + Configuration.PADDING;
		label = new JLabel("Routines: ");
		label.setBounds(x, y, Configuration.LABEL_WIDTH_LITTLE, Configuration.ITEM_HEIGHT);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		view.add(label);

		mParticipantRoutines = new JLabel();
		x += Configuration.LABEL_WIDTH_LITTLE;
		mParticipantRoutines.setBounds(x, y, Configuration.MAX_WIDTH / 2 - x, Configuration.ITEM_HEIGHT);
		view.add(mParticipantRoutines);

		x = 0;
		y += Configuration.ITEM_HEIGHT + Configuration.PADDING;
		label = new JLabel("Sensors: ");
		label.setBounds(x, y, Configuration.LABEL_WIDTH_LITTLE, Configuration.ITEM_HEIGHT);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		view.add(label);

		mParticipantSensors = new JLabel();
		x += Configuration.LABEL_WIDTH_LITTLE;
		mParticipantSensors.setBounds(x, y, Configuration.MAX_WIDTH / 2 - x, Configuration.ITEM_HEIGHT);
		view.add(mParticipantSensors);

		y += Configuration.ITEM_HEIGHT + Configuration.PADDING;

		view.setSize(Configuration.MAX_WIDTH / 2, y);
		view.setVisible(false);
		return view;
	}

	private void fillParticipantInfos(Participant pParticipant) throws Exception {
		if (mParticipantInfos != null) {
			mParticipantInfos.setVisible(true);
		}
		if (mParticipantInstalled != null) {
			mParticipantInstalled.setText(pParticipant.getInstall().toString());
		}
		if (mParticipantRoutines != null) {
			mParticipantRoutines.setText(StringUtils.join(pParticipant.getRoutines().keySet(), ", "));
		}
		if (mParticipantVera != null) {
			mParticipantVera.setText(pParticipant.getVera());
		}
		if (mParticipantSensors != null) {
			// TODO extract sensors
			JSONObject j = ParticipantExtractor.extractJSON(Configuration.PARTICIPANT_FILE)
					.getJSONObject(pParticipant.getName());
			if (j.has("sensors")) {
				j = j.getJSONObject("sensors");
				List<String> sensors = new ArrayList<>();
				if (j.has("contact")) {
					j.getJSONArray("contact").forEach(con -> sensors.add(con.toString()));
				}
				if (j.has("emeter")) {
					j.getJSONArray("emeter").forEach(con -> sensors.add(con.toString()));
				}
				if (j.has("motion")) {
					j.getJSONArray("motion").forEach(con -> sensors.add(con.toString()));
				}
				mParticipantSensors.setText(StringUtils.join(sensors, ", "));
			}

		}
	}

}
