package loganalyser.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ComponentUtils {

	/**
	 * Update given JLabel font style to the specified one font style
	 * 
	 * @param pJLabel
	 *            the JLabel to update
	 * @param pFontStyle
	 *            the specified Font style {@link Font}
	 */
	public static void setJLabelFontStyle(final JLabel pJLabel, final int pFontStyle) {
		pJLabel.setFont(new Font(pJLabel.getFont().getName(), pFontStyle, pJLabel.getFont().getSize()));
	}

	/**
	 * Apply bold style to the given JLabel text
	 * 
	 * @param pJLabel
	 *            the JLabel to update
	 * @return the given JLabel
	 */
	public static JLabel boldLabel(final JLabel pJLabel) {
		pJLabel.setFont(new Font(pJLabel.getFont().getName(), Font.BOLD, pJLabel.getFont().getSize()));
		return pJLabel;
	}

	/**
	 * Create a new JLabel with bold style
	 * 
	 * @param pText
	 *            the text of the JLabel
	 * @return the bold JLabel
	 */
	public static JLabel boldLabel(final String pText) {
		final JLabel pJlLabel = new JLabel(pText);
		setJLabelFontStyle(pJlLabel, Font.BOLD);
		return pJlLabel;
	}

	/**
	 * Create an empty new JLabel with bold style
	 * 
	 * @return the bold JLabel
	 */
	public static JLabel boldLabel() {
		return boldLabel("");
	}

	/**
	 * Add empty border to the given JComponent
	 * 
	 * @param pComponent
	 *            the JComponent to be bordered
	 * @param pTop
	 *            the empty border top width
	 * @param pLeft
	 *            the empty border left width
	 * @param pBottom
	 *            the empty border bottom width
	 * @param pRight
	 *            the empty border right width
	 * @return A panel which contains the empty-bordered-JComponent
	 */
	public static JPanel addEmptyBorder(final JComponent pComponent, final int pTop, final int pLeft, final int pBottom,
			final int pRight) {
		final JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createEmptyBorder(pTop, pLeft, pBottom, pRight));
		container.add(pComponent, BorderLayout.CENTER);
		return container;
	}

	/**
	 * Add empty border to the given JComponent
	 * 
	 * @param pComponent
	 *            the JComponent to be bordered
	 * @param pThickness
	 *            the 4-borders empty border width
	 * @return A panel which contains the empty-bordered-JComponent
	 */
	public static JPanel addEmptyBorder(final JComponent pComponent, final int pThickness) {
		return addEmptyBorder(pComponent, pThickness, pThickness, pThickness, pThickness);
	}

	/**
	 * Add line border to the given JComponent
	 * 
	 * @param pComponent
	 *            the JComponent to be bordered
	 * @param pColor
	 *            the color of the line border
	 * @param pThickness
	 *            the 4-borders line border width
	 * @return A panel which contains the line-bordered-JComponent
	 */
	public static JPanel addLineBorder(final JComponent pComponent, final Color pColor, final int pThickness) {
		final JPanel container = new JPanel(new BorderLayout());
		container.setBorder(BorderFactory.createLineBorder(pColor, pThickness, false));
		container.add(pComponent, BorderLayout.CENTER);
		return container;
	}
}
