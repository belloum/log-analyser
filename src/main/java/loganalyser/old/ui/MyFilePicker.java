package loganalyser.old.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

public class MyFilePicker extends JComponent {

	private static final long serialVersionUID = 1L;

	private JTextArea mJTAFilePath = new JTextArea();
	private JButton mJBBrowse = new JButton("Browse");

	public MyFilePicker() {
		this(null, null);
	}

	public MyFilePicker(FilePickerListener pListener, FileChooser pFileChooser) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.mJBBrowse.addActionListener((ActionEvent event) -> {
			if (pFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File output = pFileChooser.getSelectedFile();
				mJTAFilePath.setText(output.getPath());
				pListener.fileSelected(output);
			}
		});
		build();
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		mJTAFilePath.setPreferredSize(new Dimension(preferredSize.width * 80 / 100, preferredSize.height));
		mJTAFilePath.setMaximumSize(mJTAFilePath.getPreferredSize());
		mJBBrowse.setPreferredSize(new Dimension(preferredSize.width * 20 / 100, preferredSize.height));
		mJBBrowse.setMaximumSize(mJBBrowse.getPreferredSize());
		validate();
	}

	private void build() {
		add(mJTAFilePath);
		add(mJBBrowse);
		validate();
	}

	public interface FilePickerListener {
		void fileSelected(File pSelectedFile);
	}

}
