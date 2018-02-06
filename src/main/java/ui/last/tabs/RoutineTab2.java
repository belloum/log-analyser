package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import ui.components.MyButton;
import ui.last.FileSelector;
import ui.last.components.activityresults.MealResultPanel;
import ui.last.components.activityresults.WakeAndGoBedResultPanel;
import ui.last.results.ActivityResult;
import ui.last.routines.Routine;
import ui.last.routines.settings.panels.RoutineParameterPanel;
import ui.last.routines.settings.panels.newe.MealParametersPanel;
import ui.last.routines.settings.panels.newe.WakeAndBedParametersPanel;
import utils.Configuration;

public class RoutineTab2 extends LogTab {

	// TODO Invalid hour > error message
	// FIXME Clean file is ugly
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
	private final File mCleanFile;

	// TODO _cleaned
	public RoutineTab2(FileSelector pFileSelector) {
		super(pFileSelector);
		mCleanFile = new File(getLogFile().getParent(), getLogFile().getName().replaceAll(".json", "_cleaned.json"));
		updateRoutineFrame(Routine.WakeUp);
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

	private void updateRoutineFrame(Routine pRoutine) {
		mRoutineDesc.setText(mRoutinesList.get(pRoutine));
		mRoutineDesc.validate();
		mRightPanel.removeAll();

		JPanel settings = new JPanel(new BorderLayout());
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
		} catch (Exception e) {
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
		JPanel content = new JPanel(new BorderLayout());

		int histoWidth = 1 * (Configuration.MAX_WIDTH - Configuration.LEFT_MENU_WIDTH) / 4;
		int histoHeight = Configuration.MAX_HEIGHT;
		Dimension dim = new Dimension(histoWidth, histoHeight);

		/*
		 * Routine panel
		 */
		JPanel leftPanel = new JPanel(new BorderLayout());
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
				Routine routine = Routine.valueOf(event.getItem().toString());
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