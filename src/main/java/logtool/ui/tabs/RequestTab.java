package logtool.ui.tabs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logtool.beans.RequestType;
import logtool.exceptions.RequestException;
import logtool.operators.RequestExtractor;
import logtool.ui.components.ComponentUtils;
import logtool.ui.components.InputValue;
import logtool.ui.components.MyButton;
import logtool.utils.Configuration;
import logtool.utils.Utils;

public class RequestTab extends MyCustomTab {

	// FIXME Check vera validity as changing request type

	private static final Logger log = LoggerFactory.getLogger(RequestTab.class);

	private static final long serialVersionUID = 1L;

	// "45109548"
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	private String mRequestId;
	private RequestType mRequestType;
	private String mSDate;
	private String mEDate;
	private String mOutputFilename;

	private JTextArea mRequestArea;

	public RequestTab() {
		super();
	}

	@Override
	protected JPanel content() {

		final JPanel content = new JPanel(new BorderLayout());

		// Center
		final JPanel center = new JPanel(new BorderLayout());
		center.setLayout(new GridLayout(1, 2));
		center.add(settingsPanel());
		center.add(requestPanel());

		content.add(instructionsPanel(), BorderLayout.PAGE_START);
		content.add(center, BorderLayout.CENTER);

		return content;
	}

	private void extractRequest(final RequestType pRequestType) {
		try {
			hideError();
			validParameters();
			switch (pRequestType) {
			case LogRequest:
				mRequestArea.setText(RequestExtractor.logRequests(mOutputFilename, mRequestId, mSDate, mEDate));
				break;
			case DailyReportRequest:
				mRequestArea.setText(RequestExtractor.dailyReportRequests(mOutputFilename, mRequestId, mSDate, mEDate));
				break;
			case WeeklyReportRequest:
				mRequestArea.setText(RequestExtractor.weeklyReportRequests(mOutputFilename, mRequestId, mSDate, mEDate));
				break;
			}

		} catch (final Exception e) {
			log.error("Exctracting request exception: {}", e.getMessage(), e);
			error(e.getMessage());
			mRequestArea.setText("");
		}
	}

	private void validParameters() throws RequestException {
		if (!validRequestIdentifier(mRequestId)) {
			final String pMsg = this.mRequestType == RequestType.LogRequest
					? RequestException.VERA_DOES_NOT_MATCH_PATERN
					: RequestException.USER_DOES_NOT_MATCH_PATERN;
			throw new RequestException(pMsg);
		} else if (StringUtils.isEmpty(mOutputFilename)) {
			throw new RequestException(RequestException.NO_OUTPUT_FILE);
		} else if (StringUtils.isEmpty(mSDate)) {
			throw new RequestException(RequestException.INVALID_START_DAY_FORMAT);
		} else if (StringUtils.isEmpty(mEDate)) {
			throw new RequestException(RequestException.INVALID_END_DAY_FORMAT);
		} else {
			Date start = new Date();
			Date end = start;
			try {
				start = DAY_FORMAT.parse(mSDate);
			} catch (final ParseException e) {
				throw new RequestException(RequestException.INVALID_START_DAY_FORMAT);
			}

			try {
				end = DAY_FORMAT.parse(mEDate);
			} catch (final ParseException e) {
				throw new RequestException(RequestException.INVALID_END_DAY_FORMAT);
			}

			if (start.after(end)) {
				throw new RequestException(RequestException.INVALID_PERIOD);
			}
		}

	}

	private JPanel settingsPanel() {

		final JPanel neoSettings = new JPanel(new BorderLayout(5, 5));

		final GridLayout layoutParams = new GridLayout(5, 1);

		final JPanel settingsLabel = new JPanel(layoutParams);
		settingsLabel.add(ComponentUtils.boldLabel("Request type"));
		settingsLabel.add(ComponentUtils.boldLabel("Start date"));
		settingsLabel.add(ComponentUtils.boldLabel("End date"));
		settingsLabel.add(ComponentUtils.boldLabel("Vera/User id"));
		settingsLabel.add(ComponentUtils.boldLabel("Output file"));

		final JComboBox<RequestType> requestTypeSelector = new JComboBox<>(new RequestType[] { RequestType.LogRequest,
				RequestType.DailyReportRequest, RequestType.WeeklyReportRequest });
		requestTypeSelector.setSelectedItem(mRequestType);
		requestTypeSelector.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mRequestType = RequestType.valueOf(e.getItem().toString());
				log.debug("Switch to {} request", mRequestType);
				mRequestId = validRequestIdentifier(mRequestId) ? mRequestId : null;
				hideError();
			}
		});

		final InputValue mStartDate = new InputValue(mSDate);
		mStartDate.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				mSDate = validDate(mStartDate.getText()) ? mStartDate.getText() : null;
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				mSDate = validDate(mStartDate.getText()) ? mStartDate.getText() : null;
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				mSDate = validDate(mStartDate.getText()) ? mStartDate.getText() : null;
			}
		});

		final InputValue mEndDate = new InputValue(mEDate);
		mEndDate.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				mEDate = validDate(mEndDate.getText()) ? mEndDate.getText() : null;
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				mEDate = validDate(mEndDate.getText()) ? mEndDate.getText() : null;
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				mEDate = validDate(mEndDate.getText()) ? mEndDate.getText() : null;
			}
		});

		final InputValue mVera = new InputValue(mRequestId);
		mVera.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				mRequestId = validRequestIdentifier(mVera.getText()) ? mVera.getText() : null;
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				mRequestId = validRequestIdentifier(mVera.getText()) ? mVera.getText() : null;
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				mRequestId = validRequestIdentifier(mVera.getText()) ? mVera.getText() : null;
			}
		});

		final InputValue mOutput = new InputValue(mOutputFilename);
		mOutput.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				mOutputFilename = StringUtils.isNotEmpty(mOutput.getText()) ? mOutput.getText() : null;
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				mOutputFilename = StringUtils.isNotEmpty(mOutput.getText()) ? mOutput.getText() : null;
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				mOutputFilename = StringUtils.isNotEmpty(mOutput.getText()) ? mOutput.getText() : null;
			}
		});

		final JPanel settingsValue = new JPanel(layoutParams);
		settingsValue.add(requestTypeSelector);
		settingsValue.add(mStartDate);
		settingsValue.add(mEndDate);
		settingsValue.add(mVera);
		settingsValue.add(mOutput);

		neoSettings.add(settingsLabel, BorderLayout.WEST);
		neoSettings.add(settingsValue, BorderLayout.CENTER);
		neoSettings.add(new MyButton("Get request", event -> extractRequest(mRequestType)), BorderLayout.PAGE_END);

		neoSettings.setBorder(BorderFactory.createTitledBorder("Settings"));
		return neoSettings;
	}

	private JPanel instructionsPanel() {

		final JPanel instructions = new JPanel(new BorderLayout());
		instructions.setBorder(BorderFactory.createTitledBorder("Instructions"));

		final JSONObject steps = settings().getJSONObject("steps");

		final JPanel indexes = new JPanel(new GridLayout(steps.length(), 1));
		final JPanel values = new JPanel(new GridLayout(steps.length(), 1));

		for (int i = 1; i <= steps.length(); i++) {
			indexes.add(ComponentUtils.boldLabel(String.format("%d\t\t", i)));
			final JXLabel instruct = new JXLabel(steps.getString(String.valueOf(i)));
			instruct.setLineWrap(true);
			values.add(instruct);
		}

		instructions.add(indexes, BorderLayout.WEST);
		instructions.add(values, BorderLayout.CENTER);

		return instructions;
	}

	private JPanel requestPanel() {

		final JPanel requestPanel = new JPanel(new BorderLayout());
		requestPanel.setBorder(BorderFactory.createTitledBorder("Request"));

		requestPanel.add(
				new MyButton("Copy to clipboard",
						event -> Utils.copyToClipboard(mRequestArea.getText().replaceAll("\n", ""))),
				BorderLayout.PAGE_END);

		final JScrollPane scroll = new JScrollPane();
		mRequestArea = new JTextArea();
		mRequestArea.setEditable(false);
		mRequestArea.setLineWrap(true);
		scroll.getViewport().add(mRequestArea);
		requestPanel.add(scroll, BorderLayout.CENTER);

		return requestPanel;
	}

	private boolean validRequestIdentifier(final String pVeraId) {
		boolean validId = false;
		if (StringUtils.isEmpty(pVeraId)) {
			validId = false;
		} else if (mRequestType == RequestType.LogRequest) {
			validId = Configuration.VERA_PATTERN.matcher(pVeraId).matches();
		} else {
			validId = Configuration.USER_PATTERN.matcher(pVeraId).matches();
		}
		return validId;
	}

	private boolean validDate(final String pDate) {
		try {
			DAY_FORMAT.parse(pDate);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	@Override
	public String configurationSection() {
		return "request";
	}

	@Override
	protected void init(final Object... params) {
		mSDate = mEDate = DAY_FORMAT.format(new Date());
		mOutputFilename = Configuration.DEFAULT_OUTPUT_FILENAME;
		mRequestType = RequestType.LogRequest;
	}
}
