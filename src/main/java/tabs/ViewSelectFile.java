package tabs;

import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.components.ResultLabel;
import ui.components.ResultLabel.ResultType;
import utils.ChooseFileListener;

public class ViewSelectFile extends JPanel implements ChooseFileListener {

	private static final long serialVersionUID = 1L;

	private ResultLabel mResultLabel = new ResultLabel();

	public ViewSelectFile() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(new JLabel("Veuillez sélectionner un fichier"));
		add(new JLabel("File > Select a file"));
		add(mResultLabel);
		validate();
	}

	@Override
	public void validFile(File pFile) {
		mResultLabel.printResult(String.format("%s is a valid log file.", pFile.getName()), ResultType.SUCCESS);
		validate();
	}

	@Override
	public void invalidFile(File pFile, String pCause) {
		mResultLabel.printResult(String.format("%s is not a valid file: %s", pFile.getName(), pCause),
				ResultType.ERROR);
		validate();
	}

	@Override
	public void unfoundFile(File pFile) {
		mResultLabel.printResult(String.format("%s can not be find.", pFile.getName()), ResultType.ERROR);
		validate();
	}
}
