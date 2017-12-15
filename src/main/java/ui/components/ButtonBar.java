package ui.components;

import java.awt.Dimension;
import java.util.LinkedHashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;

public class ButtonBar extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private int mButtonCount;
	private LinkedHashMap<Integer, JButton> mButtons;

	public ButtonBar() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
			button.setMaximumSize(new Dimension(getMaximumSize().width / getCount(), getMaximumSize().height));
			add(button);
		});
		return;
	}

}
