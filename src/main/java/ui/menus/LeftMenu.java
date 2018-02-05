package ui.menus;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ui.components.LabelButton;
import ui.components.LabelButton.LabelButtonListener;
import utils.Configuration;

public class LeftMenu extends JPanel implements LabelButtonListener {

	private static final long serialVersionUID = 1L;

	private List<LabelButton> mMenuItems = new LinkedList<>();
	private LeftMenuSelectorListener mListener;

	private JPanel mNavigationPanel;
	private JPanel mContentPanel;

	public LeftMenu(LeftMenuSelectorListener pLeftMenuListener) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.mListener = pLeftMenuListener;
		build();
	}

	public LeftMenu(List<String> pLabels, LeftMenuSelectorListener pLeftMenuListener) {
		this(pLeftMenuListener);
		pLabels.forEach(label -> {
			this.mMenuItems.add(new LabelButton(
					new Dimension(Configuration.LEFT_MENU_WIDTH, 2 * Configuration.ITEM_HEIGHT), label, true, this));
		});
		build();
	}

	public LeftMenu(LayoutManager layout) {
		super(layout);
		build();
	}

	private void build() {
		removeAll();

		mNavigationPanel = new JPanel();
		mNavigationPanel.setLayout(new BoxLayout(mNavigationPanel, BoxLayout.Y_AXIS));

		this.mMenuItems.forEach(button -> {
			mNavigationPanel.add(button);
		});
		mNavigationPanel.validate();
		validate();
	};

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		mMenuItems.forEach(button -> button.setEnabled(pEnabled));
	}

	public void setEnabled(boolean pEnabled, int position) {
		super.setEnabled(pEnabled);
		mMenuItems.get(position).setEnabled(pEnabled);
	}

	public void setSelectection(int pPosition) {
		mMenuItems.get(pPosition).setSelected(true);
	}

	public interface LeftMenuSelectorListener {
		void selectItem(int position);
	}

	@Override
	public void click(String pLabel) {
		LabelButton button = mMenuItems.stream().filter(btn -> btn.getText().equals(pLabel)).findFirst().get();
		mMenuItems.stream().forEach(btn -> btn.setSelected(btn.equals(button)));
		this.mListener.selectItem(this.mMenuItems.indexOf(button));
	}

}
