package ui.last.components;

import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import ui.components.MyButton;
import ui.last.FileSelector;

public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;

	private LinkedList<JButton> mMenuItems = new LinkedList<>();

	int mCurrentPosition;
	private MenuSelector mListener;
	private LogFrame mFilePanel;

	public Menu(List<String> pLabels) {
		setLayout(new GridLayout(pLabels.size(), 1));
		pLabels.forEach(label -> {
			this.mMenuItems.add(new MyButton(label, event -> this.mListener.goTo(label)));
		});

		build();
	}

	private void build() {
		removeAll();

		this.mMenuItems.forEach(button -> add(button));

		validate();
	};

	public void addNavigationListener(MenuSelector pNavigationListener) {
		this.mListener = pNavigationListener;
	}

	public void addFileSelector(FileSelector pFileSelector) {
		this.mFilePanel.addFileSelectorListener(pFileSelector);
	}

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		mMenuItems.forEach(button -> button.setEnabled(pEnabled));
	}

	public void enablePosition(boolean pEnabled, int pPosition) {
		super.setEnabled(pEnabled);
		mMenuItems.get(pPosition).setEnabled(pEnabled);
	}

	public void disableMenu() {
		setEnabled(false);
	}

	public interface MenuSelector {
		void goTo(String pSection);
	}

}
