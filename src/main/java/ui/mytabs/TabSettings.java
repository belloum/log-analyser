package ui.mytabs;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import ui.components.ResultLabel;
import utils.Configuration;

public class TabSettings extends AbstractTab {

	private static final long serialVersionUID = 1L;
	private JFileChooser mJFileChooser;
	private JLabel mJLabelLogFile;

	public TabSettings(String title) {
		super(title);

		if (!Configuration.RESOURCES_FOLDER.exists())
			Configuration.RESOURCES_FOLDER.mkdirs();

		int x = Configuration.MARGINS;
		int y = Configuration.MARGINS / 2;

		JLabel text = new JLabel("Userboxes file");
		text.setFont(new Font(text.getFont().getName(), Font.BOLD, text.getFont().getSize()));
		text.setBounds(x, y, 100, Configuration.ITEM_HEIGHT);
		add(text);

		x += 100 + Configuration.PADDING;
		mJLabelLogFile = new JLabel(Configuration.PARTICIPANT_FILE.getName());
		mJLabelLogFile.setBounds(x, y, Configuration.LABEL_WIDTH_LONG, Configuration.ITEM_HEIGHT);
		add(mJLabelLogFile);

		x = Configuration.MAX_WIDTH / 2 - Configuration.MARGINS;
		mJFileChooser = new JFileChooser(Configuration.RESOURCES_FOLDER);

		JButton pickFile = new JButton("Browse");
		pickFile.setBounds(x, y, Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT);
		add(pickFile);

		x += Configuration.BUTTON_WIDTH + Configuration.PADDING;
		text = new ResultLabel();
		text.setBounds(x, y, Configuration.LABEL_WIDTH_LONG, Configuration.ITEM_HEIGHT);
		add(text);

		add(pickFile);

		super.fillBlank(x, y);
	}
}
