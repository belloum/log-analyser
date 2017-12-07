package ui.mytabs;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import utils.Configuration;

public abstract class AbstractTab extends JFrame {

	private static final long serialVersionUID = 1L;

	public AbstractTab(String title) throws HeadlessException {
		super(title);
	}

	protected void fillBlank(int pXoffset, int pYoffset) {
		pXoffset = Configuration.MARGINS;
		pYoffset += Configuration.MARGINS;
		JPanel blank = new JPanel();
		blank.setBounds(pXoffset, pYoffset, Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT);
		add(blank);
	}

}
