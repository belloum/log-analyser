package tabs;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import utils.Configuration;

public abstract class AbstractTab extends JFrame {

	private static final long serialVersionUID = 1L;

	protected int xOffset = 0;
	protected int yOffset = 0;

	public AbstractTab(String title) throws HeadlessException {
		super(title);
		xOffset = Configuration.MARGINS;
		yOffset = Configuration.MARGINS / 2;
	}

	protected void fillBlank(int pXoffset, int pYoffset) {
		pXoffset = Configuration.MARGINS;
		pYoffset += Configuration.MARGINS;
		JPanel blank = new JPanel();
		blank.setBounds(pXoffset, pYoffset, Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT);
		add(blank);
	}

	protected void addComponent(JComponent pJComponent, Dimension pDimension) {
		int pWidth = pDimension.width;
		int pHeight = pDimension.height;
		// System.out.println(pWidth + " x " + pHeight + " x " + xOffset + " x "
		// + yOffset + " x " + pJComponent);
		pJComponent.setBounds(xOffset, yOffset, pWidth - Configuration.PADDING, pHeight);

		add(pJComponent);
		xOffset += pWidth + Configuration.PADDING;
	}

}
