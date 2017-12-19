package tabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import beans.Period;
import beans.participants.Participant;
import operators.extractors.ParticipantExtractor;
import operators.extractors.RequestExtractor;
import ui.components.CustomComponent;
import ui.components.InputValue;
import ui.components.MyButton;
import ui.components.PeriodPicker;
import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.Configuration;

public class TabRequest extends AbstractTab {

	private static final long serialVersionUID = 1L;

	private static final String NO_PARTICIPANT = "Please, chose a participant or pick a vera number";

	private Participant mParticipant = null;
	private JComboBox<String> mParticipantsPicker;
	private MyButton mBMakeRequest;
	private ResultLabel mRLabel;
	private InputValue mIVera;
	private JTextArea mJRequest;
	private static final int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;

	public TabRequest(String title) throws Exception {
		super(title);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		PeriodPicker periodPicker = new PeriodPicker(new Dimension(MAX_WIDTH, Configuration.ITEM_HEIGHT));
		periodPicker.setAlignmentX(LEFT_ALIGNMENT);

		mParticipantsPicker = new JComboBox<String>();
		mParticipantsPicker.setAlignmentX(LEFT_ALIGNMENT);
		mParticipantsPicker.setPreferredSize(new Dimension(250, Configuration.ITEM_HEIGHT));
		mParticipantsPicker.setMaximumSize(mParticipantsPicker.getPreferredSize());
		System.out.println(mParticipantsPicker.getPreferredSize());

		mParticipantsPicker.addItem("Select a userbox");
		ParticipantExtractor.extractParticipants(Configuration.PARTICIPANT_FILE)
				.forEach((participant) -> mParticipantsPicker.addItem(participant.getName()));

		mParticipantsPicker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					try {
						mParticipant = ParticipantExtractor.extractParticipant(Configuration.PARTICIPANT_FILE,
								e.getItem().toString());
						mIVera.setText("");
					} catch (Exception e1) {
						mParticipant = null;
						System.out.println("Unable to extract participant: " + e.getItem().toString());
					}
				}
			}
		});

		mBMakeRequest = new MyButton("Get request", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (mParticipant == null && StringUtils.isEmpty(mIVera.getText())) {
					mRLabel.printResult(NO_PARTICIPANT, ResultType.ERROR);
				} else {
					try {
						String vera = (mParticipant != null) ? mParticipant.getVera() : mIVera.getText();
						mJRequest.setText(RequestExtractor
								.extractLogsAmongPeriod(formatPeriod(periodPicker.getPeriod()), vera, true));
						mRLabel.printResult("", ResultType.SUCCESS);
					} catch (Exception e1) {
						System.out.println(e1);
						mRLabel.printResult(e1.getMessage(), ResultType.ERROR);
					}
				}
			}
		});

		add(periodPicker);

		JPanel participantSelect = new JPanel();
		participantSelect.setLayout(new BoxLayout(participantSelect, BoxLayout.X_AXIS));
		JLabel jL = new JLabel(" Pick a vera number");
		CustomComponent.setJLabelFontStyle(jL, Font.BOLD);
		jL.setMaximumSize(jL.getPreferredSize());
		mIVera = new InputValue();
		mIVera.setMaximumSize(new Dimension(Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT));
		mIVera.setPreferredSize(mIVera.getMaximumSize());
		participantSelect.add(mParticipantsPicker);
		participantSelect.add(new JLabel("OR"));
		participantSelect.add(jL);
		participantSelect.add(mIVera);
		participantSelect
				.setMaximumSize(new Dimension(participantSelect.getPreferredSize().width, Configuration.ITEM_HEIGHT));
		participantSelect.setAlignmentX(LEFT_ALIGNMENT);

		mJRequest = new JTextArea();
		mJRequest.setMaximumSize(new Dimension(MAX_WIDTH, 3 * Configuration.ITEM_HEIGHT));
		mJRequest.setMinimumSize(mJRequest.getMaximumSize());
		mJRequest.setEditable(false);
		mJRequest.setLineWrap(true);
		mJRequest.setAlignmentX(LEFT_ALIGNMENT);
		mJRequest.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		mRLabel = new ResultLabel("");

		add(participantSelect);
		add(mBMakeRequest);
		add(mJRequest);
		add(mRLabel);
	}

	private String formatPeriod(Period pPeriod) {
		// TODO enable extract period from different months
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(pPeriod.getStartDate());
		int startMonth = calendar.get(Calendar.MONTH);
		calendar.setTime(pPeriod.getEndDate());
		if (startMonth != calendar.get(Calendar.MONTH)) {
			throw new IllegalArgumentException("Invalid period: Start date and end date must be among the same month");
		} else {
			int endDay = calendar.get(Calendar.DAY_OF_MONTH);
			calendar.setTime(pPeriod.getStartDate());
			int month = (calendar.get(Calendar.MONTH) + 1);
			if (calendar.get(Calendar.DAY_OF_MONTH) != endDay) {
				return String.format("%d.%02d.*", calendar.get(Calendar.YEAR), month);
			} else {
				return String.format("%d.%02d.%02d", calendar.get(Calendar.YEAR), month, endDay);
			}
		}
	}
}
