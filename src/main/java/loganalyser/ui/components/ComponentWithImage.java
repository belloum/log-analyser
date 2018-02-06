package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import loganalyser.utils.Utils;

public class ComponentWithImage extends JPanel {

	public static final Integer ALIGN_LEFT = 0;
	public static final Integer ALIGN_RIGHT = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Component mComponent;
	private File mImage;
	private Integer mAlignement;

	public ComponentWithImage(final File pImage, final Component pContent) {
		this(pImage, pContent, ALIGN_LEFT);
	}

	public ComponentWithImage(final File pImage, final Component pContent, final int pAlignement) {
		setLayout(new BorderLayout());
		this.mComponent = pContent;
		this.mImage = pImage;
		this.mAlignement = pAlignement;
		build();
	}

	private void build() {
		try {
			final JLabel img = new JLabel(new ImageIcon(Utils.scaleImg(this.mImage, 50, 50)));
			img.setBorder(new EmptyBorder(0, 0, 0, 10));
			add(img, this.mAlignement == ALIGN_RIGHT ? BorderLayout.EAST : BorderLayout.WEST);
		} catch (final IOException e) {
			System.err.println("Unable to load file from resources");
		}

		add(this.mComponent, BorderLayout.CENTER);
		validate();
	}

	public void updateComponent(final Component pComponent) {
		this.mComponent = pComponent;
		build();
	}

}
