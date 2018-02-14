package logtool.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.json.JSONObject;

import logtool.exceptions.RequestException;
import logtool.operators.RequestExtractor;
import logtool.ui.components.ComponentUtils;
import logtool.ui.components.InputValue;
import logtool.ui.components.MyButton;
import logtool.utils.Configuration;
import logtool.utils.Utils;

public class RequestTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	// "45109548"
	// TODO Request report
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	private String mVeraId;
	private String mSDate = DAY_FORMAT.format(new Date());
	private String mEDate = mSDate;
	private String mOutputFilename = Configuration.DEFAULT_OUTPUT_FILENAME;

	private enum RequestType {
		LogRequest, ReportRequest
	}

	private JTextArea mRequestArea;

	public RequestTab() {
		super();
		add(content(), BorderLayout.CENTER);
	}

	@Override
	protected Component content() {

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
			validParameters();
			hideError();
			switch (pRequestType) {
			case LogRequest:
				mRequestArea.setText(RequestExtractor.logRequests(mOutputFilename, mVeraId, mSDate, mEDate));
				break;
			case ReportRequest:
				mRequestArea.setText(RequestExtractor.reportRequests(mOutputFilename, mVeraId, mSDate, mEDate));
				break;
			}

		} catch (final Exception e) {
			error(e.getMessage());
		}
	}

	private void validParameters() throws Exception {
		if (StringUtils.isEmpty(mVeraId)) {
			throw new RequestException(RequestException.VERA_DOES_NOT_MATCH_PATERN);
		} else if (StringUtils.isEmpty(mOutputFilename)) {
			throw new RequestException(RequestException.NO_OUTPUT_FILE);
		} else if (StringUtils.isEmpty(mSDate)) {
			throw new RequestException(RequestException.INVALID_START_DAY_FORMAT);
		} else if (StringUtils.isEmpty(mEDate)) {
			throw new RequestException(RequestException.INVALID_END_DAY_FORMAT);
		} else if (DAY_FORMAT.parse(mSDate).after(DAY_FORMAT.parse(mEDate))) {
			throw new RequestException(RequestException.INVALID_PERIOD);
		}
	}

	private JPanel settingsPanel() {

		final JPanel neoSettings = new JPanel(new BorderLayout(5, 5));

		final JPanel settingsLabel = new JPanel(new GridLayout(4, 1));
		settingsLabel.add(ComponentUtils.boldLabel("Start date"));
		settingsLabel.add(ComponentUtils.boldLabel("End date"));
		settingsLabel.add(ComponentUtils.boldLabel("Vera id"));
		settingsLabel.add(ComponentUtils.boldLabel("Output file"));

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

		final InputValue mVera = new InputValue();
		mVera.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				mVeraId = validVera(mVera.getText()) ? mVera.getText() : null;
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				mVeraId = validVera(mVera.getText()) ? mVera.getText() : null;
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				mVeraId = validVera(mVera.getText()) ? mVera.getText() : null;
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

		// TODO Select a report type (daily, weekly)

		final JPanel settingsValue = new JPanel(new GridLayout(4, 1));
		settingsValue.add(mStartDate);
		settingsValue.add(mEndDate);
		settingsValue.add(mVera);
		settingsValue.add(mOutput);

		neoSettings.add(settingsLabel, BorderLayout.WEST);
		neoSettings.add(settingsValue, BorderLayout.CENTER);

		final JPanel btns = new JPanel(new GridLayout(1, 2, 5, 0));
		btns.add(new MyButton("Log extraction request", event -> extractRequest(RequestType.LogRequest)));
		btns.add(new MyButton("Report extraction request", event -> extractRequest(RequestType.ReportRequest)));

		neoSettings.add(btns, BorderLayout.PAGE_END);

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

	private boolean validVera(final String pVeraId) {
		return Configuration.VERA_PATTERN.matcher(pVeraId).matches();
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
}
