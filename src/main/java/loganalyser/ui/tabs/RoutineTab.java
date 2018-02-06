package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.json.JSONException;

import loganalyser.beans.Routine;
import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.old.ui.MyButton;
import loganalyser.operators.FileSelector;
import loganalyser.ui.panels.results.MealResultPanel;
import loganalyser.ui.panels.results.WakeAndGoBedResultPanel;
import loganalyser.ui.panels.settings.MealParametersPanel;
import loganalyser.ui.panels.settings.RoutineParameterPanel;
import loganalyser.ui.panels.settings.WakeAndBedParametersPanel;
import loganalyser.utils.Configuration;
import loganalyser.utils.Utils;

public class RoutineTab extends LogTab {

	// TODO Invalid hour > error message
	// TODO helper for decision, show the biggest sender
	// TODO Progress

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea mRoutineDesc;
	private JPanel mResultsPanel;
	private JPanel mRightPanel;

	private LinkedHashMap<Routine, String> mRoutinesList;

	private Routine mCurrentRoutine;
	private RoutineParameterPanel mRoutineSetting;
	private File mCleanFile;

	public RoutineTab(final FileSelector pFileSelector) {
		super(pFileSelector);
		try {
			mCleanFile = Utils.tempLogFile();
			updateRoutineFrame(Routine.WakeUp);
		} catch (JSONException | IOException e) {
			System.out.println("Unable to find temp file");
		}
	}

	@Override
	protected void init() {
		mResultsPanel = new JPanel(new BorderLayout());
		mRightPanel = new JPanel(new BorderLayout());
		mRoutineDesc = new JTextArea() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEditable() {
				return false;
			}

			@Override
			public boolean getLineWrap() {
				return true;
			}

			@Override
			public boolean getWrapStyleWord() {
				return true;
			}
		};

		mCurrentRoutine = Routine.WakeUp;
		mRoutinesList = new LinkedHashMap<>();
		Arrays.asList(Routine.values())
				.forEach(routine -> mRoutinesList.put(routine, String.format("No description for %s", routine.name())));
	}

	private void updateRoutineFrame(final Routine pRoutine) {
		mRoutineDesc.setText(mRoutinesList.get(pRoutine));
		mRoutineDesc.validate();
		mRightPanel.removeAll();

		final JPanel settings = new JPanel(new BorderLayout());
		settings.setBorder(BorderFactory.createTitledBorder("Settings"));

		mResultsPanel = new JPanel(new BorderLayout());

		switch (pRoutine) {
		case Meal:
			mRoutineSetting = new MealParametersPanel(getLogFile().getDevices(), pRoutine);
			break;
		case WakeUp:
			mRoutineSetting = new WakeAndBedParametersPanel(getLogFile().getDevices(), pRoutine,
					WakeAndBedParametersPanel.WAKE_UP);
			break;
		case GoToBed:
			mRoutineSetting = new WakeAndBedParametersPanel(getLogFile().getDevices(), pRoutine,
					WakeAndBedParametersPanel.GO_BED);
			break;
		}

		settings.add(mRoutineSetting, BorderLayout.CENTER);
		settings.add(new MyButton("Compute", event -> displayResult()), BorderLayout.PAGE_END);

		mResultsPanel.validate();

		mRightPanel.add(settings, BorderLayout.PAGE_START);
		mRightPanel.add(mResultsPanel, BorderLayout.CENTER);
		mRightPanel.validate();
	}

	private void displayResult() {
		List<ActivityResult> results = new ArrayList<>();
		try {
			results = mRoutineSetting.getResults(mCleanFile);
		} catch (final Exception e) {
			System.err.println(e);
			error(e.getMessage());
		}

		mResultsPanel.removeAll();

		switch (mCurrentRoutine) {
		case Meal:
			mResultsPanel.add(new MealResultPanel(results), BorderLayout.CENTER);
			break;
		case WakeUp:
		case GoToBed:
			mResultsPanel.add(new WakeAndGoBedResultPanel(results), BorderLayout.CENTER);
			break;
		}

		mResultsPanel.validate();
	}

	@Override
	protected Component content() {
		final JPanel content = new JPanel(new BorderLayout());

		final int histoWidth = 1 * (Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 4;
		final int histoHeight = Configuration.MAX_HEIGHT;
		final Dimension dim = new Dimension(histoWidth, histoHeight);

		/*
		 * Routine panel
		 */
		final JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBounds(0, 0, dim.width, dim.height);
		leftPanel.setPreferredSize(dim);
		leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		/*
		 * Routine selection
		 */
		final JComboBox<Routine> routines = new JComboBox<>();
		mRoutinesList.forEach((routine, routineDesc) -> routines.addItem(routine));
		routines.addItemListener(event -> {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				final Routine routine = Routine.valueOf(event.getItem().toString());
				mCurrentRoutine = routine;
				updateRoutineFrame(routine);
			}
		});
		routines.setBorder(BorderFactory.createTitledBorder("Please, select a routine"));

		leftPanel.add(routines, BorderLayout.PAGE_START);
		leftPanel.add(mRoutineDesc, BorderLayout.CENTER);
		content.add(leftPanel, BorderLayout.WEST);

		mRightPanel = new JPanel(new BorderLayout());
		content.add(mRightPanel, BorderLayout.CENTER);

		return content;
	}

	@Override
	public String configurationSection() {
		return "routine";
	}
}
// 245 - 4:44
// 294 - 4:52
// 232 - 5:04