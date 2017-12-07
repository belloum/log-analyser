package ui.mytabs;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import operators.extractors.ParticipantExtractor;
import ui.PickLogFile;
import utils.Configuration;

public class TabSettings extends AbstractTab {

	private static final long serialVersionUID = 1L;
	private JFileChooser mJFileChooser;
	private JLabel mJLabelLogFile;
	private File mLogFile;

	public TabSettings(String title) {
		super(title);

		// TODO change place
		if (!Configuration.RESOURCES_FOLDER.exists())
			Configuration.RESOURCES_FOLDER.mkdirs();

		int x = Configuration.MARGINS;
		int y = Configuration.MARGINS / 2;

		JLabel text = new JLabel("Userboxes file");
		text.setFont(new Font(text.getFont().getName(), Font.BOLD, text.getFont().getSize()));
		text.setBounds(x, y, 100, Configuration.ITEM_HEIGHT);
		add(text);

		x += 100 + Configuration.PADDING_RIGHT;
		mJLabelLogFile = new JLabel(Configuration.PARTICIPANT_FILE.getName());
		mJLabelLogFile.setBounds(x, y, Configuration.LABEL_WIDTH, Configuration.ITEM_HEIGHT);
		add(mJLabelLogFile);

		x = Configuration.MAX_WIDTH / 2 - Configuration.MARGINS;
		mJFileChooser = new JFileChooser(Configuration.RESOURCES_FOLDER);
		JButton pickFile = new JButton("Browse");
		pickFile.setBounds(x, y, Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT);
		add(pickFile);

		x += Configuration.BUTTON_WIDTH + Configuration.PADDING_RIGHT;
		text = new JLabel("Hey");
		text.setBounds(x, y, Configuration.LABEL_WIDTH, Configuration.ITEM_HEIGHT);
		add(text);
		add(text);

		pickFile.addActionListener(mPickLogFile);
		mPickLogFile.setJLabelResult(text);
		add(pickFile);

		super.fillBlank(x, y);
	}

	private PickLogFile mPickLogFile = new PickLogFile() {

		private JLabel mJLabelResult;

		@Override
		public void actionPerformed(ActionEvent e) {
			int retour = mJFileChooser.showOpenDialog(null);
			if (retour == JFileChooser.APPROVE_OPTION) {

				File selectedFile = mJFileChooser.getSelectedFile();
				System.out.println("Selected file: " + selectedFile.getName());

				try {
					ParticipantExtractor.validateFile(selectedFile);
					printResult(ParticipantExtractor.extractParticipants(selectedFile).size() + " userboxes found",
							ResultType.SUCCESS);
					mJLabelLogFile.setText(selectedFile.getName());
					mLogFile = selectedFile;
				} catch (Exception exception) {
					System.out.println("Invalid file: " + exception.getMessage());
					printResult("Invalid file: " + selectedFile.getName(), ResultType.ERROR);
				}
			}
		}

		@Override
		public void setJLabelResult(JLabel pJLabelResult) {
			this.mJLabelResult = pJLabelResult;
			this.mJLabelResult.setVisible(false);
		}

		@Override
		public void printResult(String pMessage, ResultType pResultType) {
			if (this.mJLabelResult != null) {
				this.mJLabelResult.setVisible(true);
				this.mJLabelResult.setText(pMessage);
				this.mJLabelResult.setFont(new Font(this.mJLabelResult.getFont().getName(), Font.BOLD,
						this.mJLabelResult.getFont().getSize()));
				switch (pResultType) {
				case ERROR:
					this.mJLabelResult.setForeground(Color.RED);
					break;
				case SUCCESS:
					this.mJLabelResult.setForeground(Color.BLACK);
					break;
				}
			}
		}
	};
}
