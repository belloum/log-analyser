package loganalyser.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.json.JSONObject;

import loganalyser.exceptions.RequestException;
import loganalyser.old.ui.CustomComponent;
import loganalyser.old.ui.InputValue;
import loganalyser.old.ui.MyButton;
import loganalyser.operators.RequestExtractor;
import loganalyser.utils.Configuration;
import loganalyser.utils.Utils;

public class RequestTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	// "45109548"
	// TODO Request report
	private static final Pattern VERA_PATTERN = Pattern.compile("^\\d{8}$");
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

		// Settings

		// Center
		final JPanel center = new JPanel(new BorderLayout());
		center.add(settingsPanel(), BorderLayout.PAGE_START);
		center.add(requestPanel(), BorderLayout.CENTER);

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
		final JPanel settings = new JPanel(new BorderLayout());

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

		final JPanel start = new JPanel(new BorderLayout());
		start.add(CustomComponent.boldLabel(String.format("%s\t\t", "Start date")), BorderLayout.WEST);
		start.add(mStartDate, BorderLayout.CENTER);

		final JPanel end = new JPanel(new BorderLayout());
		end.add(CustomComponent.boldLabel(String.format("%s\t\t", "End date")), BorderLayout.WEST);
		end.add(mEndDate, BorderLayout.CENTER);

		final JPanel vera = new JPanel(new BorderLayout());
		vera.add(CustomComponent.boldLabel(String.format("%s\t\t", "Vera id")), BorderLayout.WEST);
		vera.add(mVera, BorderLayout.CENTER);

		final JPanel output = new JPanel(new BorderLayout());
		output.add(CustomComponent.boldLabel(String.format("%s\t\t", "Output file")), BorderLayout.WEST);
		output.add(mOutput, BorderLayout.CENTER);

		final JPanel hour = new JPanel(new GridLayout(1, 4, 10, 0));
		hour.add(start);
		hour.add(end);
		hour.add(vera);
		hour.add(output);

		settings.add(hour, BorderLayout.PAGE_START);

		final JPanel btns = new JPanel(new GridLayout(1, 2, 5, 0));
		btns.add(new MyButton("Log extraction request", event -> extractRequest(RequestType.LogRequest)));
		btns.add(new MyButton("Report extraction request", event -> extractRequest(RequestType.ReportRequest)));

		settings.add(btns, BorderLayout.PAGE_END);
		settings.setBorder(BorderFactory.createTitledBorder("Settings"));
		return settings;
	}

	private JPanel instructionsPanel() {

		final JPanel instructions = new JPanel(new BorderLayout());
		instructions.setBorder(BorderFactory.createTitledBorder("Instructions"));

		JSONObject steps = settings().getJSONObject("steps");

		final JPanel indexes = new JPanel(new GridLayout(steps.length(), 1));
		final JPanel values = new JPanel(new GridLayout(steps.length(), 1));

		for (int i = 1; i <= steps.length(); i++) {
			indexes.add(CustomComponent.boldLabel(String.format("%d\t\t", i)));
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

		requestPanel.add(
				new MyButton("Copy to clipboard",
						event -> Utils.copyToClipboard(mRequestArea.getText().replaceAll("\n", ""))),
				BorderLayout.PAGE_START);

		final JScrollPane scroll = new JScrollPane();
		mRequestArea = new JTextArea();
		mRequestArea.setBorder(BorderFactory.createTitledBorder("Request"));
		mRequestArea.setEditable(false);
		mRequestArea.setLineWrap(true);
		scroll.getViewport().add(mRequestArea);
		requestPanel.add(scroll, BorderLayout.CENTER);

		return requestPanel;
	}

	private boolean validVera(final String pVeraId) {
		return VERA_PATTERN.matcher(pVeraId).find();
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
