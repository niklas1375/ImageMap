/**
 * 
 */
package imagemap.panels;

import java.awt.Cursor;
import java.awt.event.*;

import javax.swing.*;

/**
 * @author Niklas Miroll
 *
 */
public class PreviewPanel extends JEditorPane implements MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanel panel;
	private JToolTip tt;
	private Popup popupTt;

	/**
	 * constructor setting up the HTML environment
	 */
	public PreviewPanel(ImagePanel panel) {
		this.panel = panel;
		tt = this.createToolTip();
		setContentType("text/html");
		setEditable(false);
		addMouseMotionListener(this);
	}

	/**
	 * mouse drag handler
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// ignore
	}

	/**
	 * mouse move handler
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (panel.isInside(e.getPoint())) {
			if (popupTt != null)
				popupTt.hide();
			String t = panel.whichShape(e.getPoint()).getTooltip();
			setToolTipText(t);
			tt.setTipText(this.getToolTipText());
			Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			setCursor(cursor);
			popupTt = PopupFactory.getSharedInstance().getPopup(this, tt, e.getXOnScreen() + 10, e.getYOnScreen());
			popupTt.show();
		} else {
			if (popupTt != null) {
				popupTt.hide();
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				setCursor(cursor);
			}
		}
	}

	/**
	 * 
	 * @param panel
	 *            to be set
	 */
	public void setPanel(ImagePanel panel) {
		this.panel = panel;
	}
}
