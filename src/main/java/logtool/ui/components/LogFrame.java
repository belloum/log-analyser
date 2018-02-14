
package logtool.ui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONException;

import logtool.beans.LogFile;
import logtool.beans.SoftLog;
import logtool.beans.devices.Device;
import logtool.beans.devices.Device.DeviceType;
import logtool.exceptions.LogExtractorException;
import logtool.operators.FileSelector;
import logtool.operators.LogExtractorListener;
import logtool.operators.SoftLogExtractor;
import logtool.settings.LogToolSettingsHandler;
import logtool.settings.ParticipantSettingsListener;
import logtool.utils.Configuration;
import logtool.utils.Utils;

public class LogFrame extends JPanel implements LogExtractorListener, ParticipantSettingsListener {

	private static final long serialVersionUID = 1L;

	private FileSelector mFileListener;

	private static final String NO_FILE_SELECTED = "No file selected";
	private static final String SELECT_A_FILE = "Select a file";
	private static final int IMG_DIMENSION = 40;

	private final LegendValueLabel mFileName = new LegendValueLabel("Name", NO_FILE_SELECTED);
	private final LegendValueLabel mLogCount = new LegendValueLabel("Logs", "0");
	private final ProgressBarWithLabel mProgressBar = new ProgressBarWithLabel();
	private FileChooser mFileChooser;

	public LogFrame() {
		setLayout(new BorderLayout());
		init();
	}

	private void init() {
		LogToolSettingsHandler.addParticipantSettingsListener(this);
		setBorder(BorderFactory.createTitledBorder("Current file"));

		try {
			add(new JLabel(new ImageIcon(Utils.scaleImg(Configuration.IMAGE_LOG_FILE, IMG_DIMENSION, IMG_DIMENSION))),
					BorderLayout.PAGE_START);
		} catch (final IOException ignored) {
			System.err.println("Hay una problema");
		}

		final JPanel info = new JPanel(new GridLayout(3, 1));
		info.add(mFileName);
		info.add(mLogCount);

		mFileChooser = new FileChooser(new File(LogToolSettingsHandler.getParticipantLogsFolder()), "Select a log file",
				Arrays.asList(new FileNameExtensionFilter("Log file", "json")));

		// button
		info.add(new MyButton(SELECT_A_FILE, event -> {
			if (mFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				checkLogFile(mFileChooser.getSelectedFile());
			}
		}));

		add(info, BorderLayout.CENTER);
		add(mProgressBar, BorderLayout.PAGE_END);

		mProgressBar.setVisible(false);
	}

	public void addFileSelectorListener(final FileSelector pSelector) {
		this.mFileListener = pSelector;
	}

	public void checkLogFile(final File pSelectedFile) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				LogFrame.this.mProgressBar.showProgress();
				try {
					validFile(new LogFile(pSelectedFile, LogFrame.this));
				} catch (final LogExtractorException e) {
					System.err.println(e.getMessage());
					invalidFile(pSelectedFile, e.getMessage());
				} finally {
					LogFrame.this.mProgressBar.hideProgress();
				}
			}
		}).start();
	}

	public void validFile(final LogFile pLogFile) throws LogExtractorException {
		mFileName.setValue(pLogFile.getName());
		mLogCount.setValue(String.format("%d", pLogFile.getLogCount()));

		try {
			if (saveLogFile(pLogFile)) {
				if (mFileListener != null) {
					mFileListener.validFile(pLogFile);
				}
			} else {
				throw new LogExtractorException("Unable to save cleaned list to tempory file");
			}
		} catch (JSONException | IOException | LogExtractorException e) {
			e.printStackTrace();
		}
	}

	public void invalidFile(final File pInvalidFile, final String pCause) {
		mLogCount.setValue("0");
		mFileName.setValue(NO_FILE_SELECTED);
		if (mFileListener != null) {
			mFileListener.invalidFile(pInvalidFile, pCause);
		}
	}

	private boolean saveLogFile(final LogFile pLogFile) throws JSONException, IOException {

		final float pThreshold = 20f;
		ignoreLowConsumptionLogs();
		final List<SoftLog> cleanList = SoftLogExtractor.ignoreLowConsumptionLogs(pLogFile.getSoftLogs(), pThreshold);

		cleanLogsResult(pLogFile.getSoftLogs().size(), cleanList.size());

		saveCleanFile();
		return Utils.saveTempLogFile(cleanList);
	}

	@Override
	public void startExtraction() {
		this.mProgressBar.setProgressText("Getting logs");
		if (mFileListener != null) {
			mFileListener.checkingFile();
		}
	}

	@Override
	public void userExtracted(final String pUser) {
		this.mProgressBar.setProgressText("Extract user");
	}

	@Override
	public void veraExtracted(final String pVera) {
	}

	@Override
	public void startLogExtraction() {
		this.mProgressBar.setProgressText("Extract logs");
	}

	@Override
	public void logExtractionProgress(final int pProgress) {
		this.mProgressBar.setProgressValue(pProgress);
	}

	@Override
	public void deviceExtracted(final List<Device> pDevices) {
		this.mProgressBar.setProgressText("Extract devices");
	}

	@Override
	public void devieTypeExtracted(final List<DeviceType> pDeviceType) {
		this.mProgressBar.setProgressText("Extract device types");
	}

	@Override
	public void dayExtracted(final int dayCount) {
		this.mProgressBar.setProgressText("Extract days");
	}

	@Override
	public void formatLogs() {
		this.mProgressBar.setProgressText("Format logs");
	}

	@Override
	public void ignoreLowConsumptionLogs() {
		this.mProgressBar.setProgressText("Ignore low consumption logs");
	}

	@Override
	public void cleanLogsResult(final int pOldCount, final int pNewCount) {
		this.mProgressBar.setProgressText(String.format("%d logs removed", (pOldCount - pNewCount)));
	}

	@Override
	public void saveCleanFile() {
		this.mProgressBar.setProgressText("Save file");
	}

	@Override
	public void participantLogsFolderUpdated(final File participantFolder) {
		mFileChooser.setCurrentDirectory(participantFolder);
	}

	@Override
	public void participantRoutineFileUpdated(File participantRoutineFile) {
	}

	@Override
	public void validateRawLogFile() {
		this.mProgressBar.setProgressText("Validating log file");
	}
}
