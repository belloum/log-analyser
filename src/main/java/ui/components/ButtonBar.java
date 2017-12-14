package ui.components;

import java.util.LinkedHashMap;

import javax.swing.JButton;

import utils.Configuration;

public class ButtonBar extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private int mButtonCount;
	private LinkedHashMap<Integer, JButton> mButtons;

	public ButtonBar() {
		this.mButtonCount = 0;
		this.mButtons = new LinkedHashMap<>();
	}

	public int getCount() {
		return this.mButtonCount;
	}

	public void setButtonCount(int pButtonCount) {
		this.mButtonCount = pButtonCount;
	}

	public JButton getButton(Integer pIndex) {
		return mButtons.containsKey(pIndex) ? mButtons.get(pIndex) : null;
	}

	public void setEnabled(boolean pEnabled) {
		mButtons.forEach((key, button) -> button.setEnabled(pEnabled));
	}

	public void addButton(JButton pButton) {
		addButton(pButton, true);
	}

	public void addButton(JButton pButton, boolean pEnable) {
		this.mButtonCount++;
		pButton.setEnabled(pEnable);
		this.mButtons.put(this.mButtonCount, pButton);
		build();
	}

	protected void build() {
		super.build();
		this.mButtons.forEach((key, button) -> {
			addComponentHorizontally(button, (getWidth() - Configuration.PADDING) / getCount(), false);
		});
		return;
	}

}
