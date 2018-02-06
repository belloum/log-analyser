package loganalyser.old.ui;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	public FileChooser(File pCurrentDirectory) {
		super(pCurrentDirectory);
	}

	public FileChooser(File pCurrentDirectory, String pTitle) {
		this(pCurrentDirectory);
		setDialogTitle(pTitle);
	}

	public FileChooser(File pCurrentDirectory, String pTitle, List<FileNameExtensionFilter> filters) {
		this(pCurrentDirectory, pTitle);
		filters.forEach(filter -> setFileFilter(filter));
		setAcceptAllFileFilterUsed(false);
	}

	@Override
	public void approveSelection() {
		File f = getSelectedFile();
		if (f.exists() && getDialogType() == SAVE_DIALOG) {
			int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite ?", "Existing file",
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (result) {
			case JOptionPane.YES_OPTION:
				super.approveSelection();
				return;
			case JOptionPane.NO_OPTION:
				return;
			case JOptionPane.CLOSED_OPTION:
				return;
			case JOptionPane.CANCEL_OPTION:
				cancelSelection();
				return;
			}
		}
		super.approveSelection();
	}

}
