package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXLabel;

import loganalyser.old.ui.CustomComponent;

public class TabHeader extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File mImage;
	private JLabel mTitle;
	private JXLabel mDescription;

	public TabHeader(String pTitle, String pDescription, File pImage) {
		this.mImage = pImage;
		this.mTitle = CustomComponent.boldLabel(pTitle);
		this.mDescription = new JXLabel(pDescription) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isLineWrap() {
				return true;
			}
		};
		build();
	}

	private void build() {
		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		JPanel title = new JPanel(new BorderLayout());
		title.add(mTitle, BorderLayout.NORTH);
		title.add(mDescription, BorderLayout.CENTER);

		JPanel panelWithImg = new ComponentWithImage(mImage, title);
		panelWithImg.setBorder(new EmptyBorder(5, 5, 5, 5));

		add(panelWithImg, BorderLayout.CENTER);
	}

	public JXLabel getDescription() {
		return mDescription;
	}

}
