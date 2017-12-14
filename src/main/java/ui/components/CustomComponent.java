package ui.components;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Configuration;

public abstract class CustomComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int xOffset = 0;
	protected int yOffset = 0;
	protected int MAX_WIDTH = Configuration.MAX_WIDTH - 2 * Configuration.MARGINS;

	public CustomComponent() {
		setLayout(null);
	}

	protected void addComponentHorizontally(JComponent pJComponent, int pWidth, boolean pResizable) {
		pJComponent.setBounds(xOffset, yOffset, pWidth, Configuration.ITEM_HEIGHT);

		double preferedWidth = pJComponent.getPreferredSize().getWidth();

		if (pResizable && preferedWidth > 0 && (pWidth - preferedWidth) > 80) {
			pWidth = (int) preferedWidth;
		}

		pJComponent.setSize((int) pWidth - Configuration.PADDING, pJComponent.getHeight());
		add(pJComponent);
		xOffset += pWidth + Configuration.PADDING;
	}

	protected void addComponentVertically(JComponent pJComponent, int pHeight, boolean pResizable) {
		pJComponent.setBounds(xOffset, yOffset, Configuration.LABEL_WIDTH_LONG, pHeight);

		double preferedHeight = pJComponent.getPreferredSize().getHeight();
		if (pResizable && preferedHeight > 0 && (pHeight - preferedHeight) > 80) {
			pHeight = (int) preferedHeight;
		}

		pJComponent.setSize(pJComponent.getWidth(), pHeight);

		add(pJComponent);

		yOffset += pHeight;// + Configuration.PADDING;
	}

	public static void setJLabelFontStyle(JLabel pJlLabel, int pFontStyle) {
		pJlLabel.setFont(new Font(pJlLabel.getFont().getName(), pFontStyle, pJlLabel.getFont().getSize()));
	}

	protected void build() {
		removeAll();
		xOffset = 0;
		yOffset = 0;
		redraw();
	};
	
	private void redraw(){
		setVisible(false);
		setVisible(true);
	}

}
