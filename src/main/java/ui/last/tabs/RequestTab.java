package ui.last.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXLabel;

import exceptions.RequestException;
import ui.components.CustomComponent;
import ui.components.InputValue;
import ui.components.MyButton;
import ui.last.components.TabHeader;
import utils.Configuration;

public class RequestTab extends MyCustomTab {

	private static final long serialVersionUID = 1L;

	// "45109548"
	private static final Pattern VERA_PATTERN = Pattern.compile("^\\d{8}$");

	private String mVeraId;
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

	private void copyToClipboard() {
		final StringSelection stringSelection = new StringSelection(mRequestArea.getText());
		final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	private String reuqest(final RequestType pRequestType) {
		String request = null;
		String outputFile = mOutputFilename.endsWith(".json") ? mOutputFilename
				: String.format("%s.json", mOutputFilename);
		switch (pRequestType) {
		case LogRequest:
			// elasticdump --input=http://localhost:9200/logstash-$d.*/event --sourceOnly
			// --output=$d.json --searchBody
			// "{\"query\":{\"term\":{\"vera_serial\":\"$v\"}}}"
			request = String.format(
					"elasticdump --input=http://localhost:9200/logstash-%s/event --sourceOnly --output=%s --searchBody \"{\\\"query\\\":{\\\"term\\\":{\\\"vera_serial\\\":\\\"%s\\\"}}}\"",
					"2018.01.*", outputFile, mVeraId);
			break;
		case ReportRequest:
			request = "Not implemented yet";
			break;
		}
		System.out.println(request);
		return request;
	}

	private void extractRequest(final RequestType pRequestType) {
		try {
			validParameters();
			hideError();
			mRequestArea.setText(reuqest(pRequestType));
		} catch (Exception e) {
			error(e.getMessage());
		}
	}

	private void validParameters() throws Exception {
		if (StringUtils.isEmpty(mVeraId)) {
			throw new RequestException(RequestException.VERA_DOES_NOT_MATCH_PATERN);
		} else if (StringUtils.isEmpty(mOutputFilename)) {
			throw new RequestException(RequestException.NO_OUTPUT_FILE);
		}
	}

	@Override
	protected TabHeader header() {
		return new TabHeader("Request Maker", "Please, enter a description for this tab ...",
				Configuration.IMAGE_HISTO);
	}

	private JPanel settingsPanel() {
		final JPanel settings = new JPanel(new BorderLayout());

		final InputValue mStartHour = new InputValue("00");
		mStartHour.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				// if (validHour(mStartHour.getText())) {
				// mSettings.setStartHour(mStartHour.getText());
				// }
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				// if (validHour(mStartHour.getText())) {
				// mSettings.setStartHour(mStartHour.getText());
				// }
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				// if (validHour(mStartHour.getText())) {
				// mSettings.setStartHour(mStartHour.getText());
				// }
			}
		});

		final InputValue mEndHour = new InputValue("00");
		mEndHour.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				// if (validHour(mEndHour.getText())) {
				// mSettings.setEndHour(mEndHour.getText());
				// }
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				// if (validHour(mEndHour.getText())) {
				// mSettings.setEndHour(mEndHour.getText());
				// }
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				// if (validHour(mEndHour.getText())) {
				// mSettings.setEndHour(mEndHour.getText());
				// }
			}
		});

		final InputValue mVera = new InputValue("00");
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
		start.add(mStartHour, BorderLayout.CENTER);

		final JPanel end = new JPanel(new BorderLayout());
		end.add(CustomComponent.boldLabel(String.format("%s\t\t", "End date")), BorderLayout.WEST);
		end.add(mEndHour, BorderLayout.CENTER);

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

		final JPanel indexes = new JPanel(new GridLayout(Configuration.INSTRUCTIONS.size(), 1));
		final JPanel values = new JPanel(new GridLayout(Configuration.INSTRUCTIONS.size(), 1));

		for (int i = 0; i < Configuration.INSTRUCTIONS.size(); i++) {
			indexes.add(CustomComponent.boldLabel(String.format("%d\t\t", (i + 1))));
			final JXLabel instruct = new JXLabel(Configuration.INSTRUCTIONS.get(i));
			instruct.setLineWrap(true);
			values.add(instruct);
		}

		instructions.add(indexes, BorderLayout.WEST);
		instructions.add(values, BorderLayout.CENTER);

		return instructions;
	}

	private JPanel requestPanel() {

		final JPanel requestPanel = new JPanel(new BorderLayout());

		requestPanel.add(new MyButton("Copy to clipboard", event -> copyToClipboard()), BorderLayout.PAGE_START);

		mRequestArea = new JTextArea(2, 3);
		mRequestArea.setBorder(BorderFactory.createTitledBorder("Request"));
		mRequestArea.setEditable(false);
		mRequestArea.setLineWrap(true);
		requestPanel.add(mRequestArea, BorderLayout.CENTER);

		return requestPanel;
	}

	private boolean validVera(String pVeraId) {
		return VERA_PATTERN.matcher(pVeraId).find();
	}
}
