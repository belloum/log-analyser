package logtool.ui.tabs;

import java.awt.BorderLayout;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logtool.beans.Routine;
import logtool.beans.activityresults.ActivityResult;
import logtool.operators.FileSelector;
import logtool.ui.components.MyButton;
import logtool.ui.resultpanels.MealResultPanel;
import logtool.ui.resultpanels.WakeAndGoBedResultPanel;
import logtool.ui.resultpanels.settings.MealParametersPanel;
import logtool.ui.resultpanels.settings.RoutineParameterPanel;
import logtool.ui.resultpanels.settings.WakeAndBedParametersPanel;
import logtool.utils.Configuration;
import logtool.utils.Utils;

public class RoutineTab extends LogTab {

	private static final Logger log = LoggerFactory.getLogger(RoutineTab.class);

	// TODO helper for decision, show the biggest sender
	// TODO Progress
	// TODO Save best configurations in participant routine file, which can be seen
	// in ParticipantTab

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
	}

	@Override
	protected void init(final Object... params) {
		super.init(params);

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
		hideError();

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

		mRightPanel.add(settings, BorderLayout.PAGE_START);
		mRightPanel.add(mResultsPanel, BorderLayout.CENTER);
		mRightPanel.add(getError(), BorderLayout.PAGE_END);
		mRightPanel.validate();
	}

	private void displayResult() {
		List<ActivityResult> results = new ArrayList<>();
		mResultsPanel.removeAll();
		hideError();

		try {
			results = mRoutineSetting.getResults(mCleanFile);

			switch (mCurrentRoutine) {
			case Meal:
				mResultsPanel.add(new MealResultPanel(results), BorderLayout.CENTER);
				break;
			case WakeUp:
			case GoToBed:
				mResultsPanel.add(new WakeAndGoBedResultPanel(results), BorderLayout.CENTER);
				break;
			}
		} catch (final Exception e) {
			System.err.println(e);
			error(e.getMessage());
		}
		mResultsPanel.validate();
	}

	@Override
	protected JPanel content() {
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

	@Override
	protected void postInit() {
		try {
			mCleanFile = Utils.tempLogFile();
		} catch (JSONException | IOException e) {
			log.error("Exception in postInit: {}", e.getMessage(), e);
			System.err.println("Unable to find temp file");
		}
		updateRoutineFrame(Routine.WakeUp);
	}
}