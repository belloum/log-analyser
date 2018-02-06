package loganalyser.old.ui;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;

public class ButtonBar extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private LinkedList<JButton> mButtons = new LinkedList<>();

	public ButtonBar() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public ButtonBar(Dimension pDimension) {
		super(pDimension);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public ButtonBar(Dimension pDimension, List<JButton> pButtons) {
		this(pDimension);
		addButtons(pButtons);
		build();
	}

	public int getCount() {
		return this.mButtons.size();
	}

	public void setEnabled(boolean pEnabled) {
		mButtons.forEach(button -> button.setEnabled(pEnabled));
	}

	public void addButton(JButton pButton) {
		this.mButtons.add(pButton);
	}

	public void addButtons(List<JButton> pButtons) {
		pButtons.forEach(button -> addButton(button));
	}

	protected void build() {
		super.build();
		this.mButtons.forEach(button -> {
			button.setMaximumSize(new Dimension(getMaximumSize().width / getCount(), getMaximumSize().height));
			add(button);
		});
		return;
	}

}
