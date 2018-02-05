package ui.menus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ui.components.LabelButton;
import ui.components.LabelButton.LabelButtonListener;
import utils.Configuration;

public class Menu extends JPanel implements LabelButtonListener {

	private static final long serialVersionUID = 1L;

	private LinkedList<LabelButton> mMenuItems = new LinkedList<>();

	private JPanel mNavigationPanel;
	int mCurrentPosition;
	private MenuSelector mListener;

	public Menu(List<String> pLabels, MenuSelector pListener) {
		setLayout(new BorderLayout());
		pLabels.forEach(label -> {
			this.mMenuItems.add(new LabelButton(
					new Dimension(Configuration.LEFT_MENU_WIDTH, 2 * Configuration.ITEM_HEIGHT), label, true, this));
		});
		this.mListener = pListener;
		build();
	}

	public Menu(LayoutManager layout) {
		super(layout);
		build();
	}

	private void build() {
		removeAll();

		mNavigationPanel = new JPanel();
		mNavigationPanel.setLayout(new BoxLayout(mNavigationPanel, BoxLayout.Y_AXIS));
		mNavigationPanel.setPreferredSize(new Dimension(Configuration.LEFT_MENU_WIDTH, Configuration.MAX_HEIGHT));

		this.mMenuItems.forEach(button -> {
			mNavigationPanel.add(button);
		});
		mNavigationPanel.validate();
		add(mNavigationPanel, BorderLayout.LINE_START);

		setMinimumSize(
				new Dimension(Configuration.LEFT_MENU_WIDTH, 2 * Configuration.ITEM_HEIGHT * this.mMenuItems.size()));
		setPreferredSize(getMinimumSize());
		setMaximumSize(getMinimumSize());
		validate();
	};

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		mMenuItems.forEach(button -> button.setEnabled(pEnabled));
	}

	public void setEnabled(boolean pEnabled, int pPosition) {
		super.setEnabled(pEnabled);
		mMenuItems.get(pPosition).setEnabled(pEnabled);
	}

	@Override
	public void click(String pLabel) {
		LabelButton button = mMenuItems.stream().filter(btn -> btn.getText().equals(pLabel)).findFirst().get();
		mListener.selectItem(mMenuItems.indexOf(button));
	}

	public void resetMenu() {
		setEnabled(false);
		setEnabled(true, 0);
	}

	public interface MenuSelector {
		void selectItem(int pPosition);
	}

}
