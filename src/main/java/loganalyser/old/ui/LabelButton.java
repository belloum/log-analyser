package loganalyser.old.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import loganalyser.utils.Configuration;

public class LabelButton extends JLabel {

	private static final long serialVersionUID = 1L;

	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color DEFAULT_SELECTED_COLOR = Color.LIGHT_GRAY;
	private static final Color DEFAULT_DISABLED_COLOR = Color.GRAY;

	private LabelButtonListener mListener;
	private boolean isSelected = false;

	public LabelButton(Dimension pDimension, String pText, Boolean pEnabled, Color pSelectedColor,
			Color pUnSelectedColor, Color pNotEnabledColor, LabelButtonListener pListener) {
		super(pText);
		mListener = pListener;
		setMinimumSize(pDimension);
		setPreferredSize(pDimension);
		setMaximumSize(pDimension);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setOpaque(true);
		setBackground(DEFAULT_COLOR);
		setEnabled(pEnabled);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (isEnabled()) {
					mListener.click(getText());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (isEnabled() && !isSelected()) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					setBackground(DEFAULT_COLOR);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (isEnabled() && !isSelected()) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					setBackground(DEFAULT_SELECTED_COLOR);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	public LabelButton(Dimension pDimension, String pText, LabelButtonListener pListener) {
		this(pDimension, pText, true, DEFAULT_SELECTED_COLOR, DEFAULT_COLOR, DEFAULT_DISABLED_COLOR, pListener);
	}

	public LabelButton(String pText, LabelButtonListener pListener) {
		this(new Dimension(Configuration.BUTTON_WIDTH, Configuration.ITEM_HEIGHT), pText, true, DEFAULT_SELECTED_COLOR,
				DEFAULT_COLOR, DEFAULT_DISABLED_COLOR, pListener);
	}

	public LabelButton(Dimension pDimension, String pText, Boolean pEnabled, LabelButtonListener pListener) {
		this(pDimension, pText, pEnabled, DEFAULT_SELECTED_COLOR, DEFAULT_COLOR, DEFAULT_DISABLED_COLOR, pListener);
	}

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		if (!isSelected()) {
			setBackground(pEnabled ? DEFAULT_COLOR : DEFAULT_DISABLED_COLOR);
			setForeground(pEnabled ? Color.BLACK : Color.WHITE);
			setFont(pEnabled ? new Font(getFont().getName(), Font.BOLD, getFont().getSize())
					: new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
		}
	}

	public void setSelected(boolean pSelected) {
		isSelected = pSelected;
		if (pSelected) {
			setBackground(DEFAULT_SELECTED_COLOR);
		} else {
			setBackground(DEFAULT_COLOR);
		}
		validate();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public interface LabelButtonListener {
		void click(String pLabel);
	}

}
