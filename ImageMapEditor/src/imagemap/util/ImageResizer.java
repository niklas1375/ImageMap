/**
 * 
 */
package imagemap.util;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import imagemap.ImageMap;
import imagemap.panels.ImagePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Niklas Miroll
 *
 */
public class ImageResizer extends JDialog implements PropertyChangeListener, FocusListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private JFormattedTextField aHField;
	private JFormattedTextField aWField;
	private JFormattedTextField rField;
	private float scale = 1;

	/**
	 * constructor
	 */
	public ImageResizer(ImageMap parent, Image img, ImagePanel panel) {
		super(parent, "Scale Image and contained shapes");
		width = img.getWidth(null);
		height = img.getHeight(null);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI(panel);
			}
		});
	}

	/**
	 * create GUI
	 */
	private void createGUI(ImagePanel panel) {
		// intializations
		JPanel absolutePanel = new JPanel(new GridLayout(2, 2, 10, 10));
		JPanel relativePanel = new JPanel(new GridLayout(2, 2, 10, 10));
		BorderLayout border = new BorderLayout(10, 10);
		JPanel main = new JPanel(border);
		JPanel helpPane = new JPanel();
		JLabel aHLabel = new JLabel("absolute Height: ");
		JLabel aWLabel = new JLabel("absolute Width: ");
		JLabel rLabel = new JLabel("scaling factor: ");
		JLabel percLabel = new JLabel("%");
		aHField = new JFormattedTextField();
		aHField.setEditable(false);
		aHField.setHorizontalAlignment(JTextField.RIGHT);
		aWField = new JFormattedTextField();
		aWField.setEditable(false);
		aWField.setHorizontalAlignment(JTextField.RIGHT);
		rField = new JFormattedTextField();
		rField.setHorizontalAlignment(JTextField.RIGHT);
		JButton ok = new JButton("Apply");
		JButton cancel = new JButton("Cancel");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyChanges(panel);
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		aHField.setValue(new Integer(height));
		aHField.setColumns(5);
		aWField.setValue(new Integer(width));
		aWField.setColumns(5);
		rField.setValue(new Integer(100));
		rField.setColumns(5);
		rField.addPropertyChangeListener("value", this);
		rField.addFocusListener(this);
		rLabel.setLabelFor(rField);
		aWLabel.setLabelFor(aWField);
		aHLabel.setLabelFor(aHField);
		KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Action performSave = new AbstractAction("Save") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				applyChanges(panel);
			}
		};
		ok.getActionMap().put("performSave", performSave);
		ok.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "performSave");
		KeyStroke keyExit = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action performExit = new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		cancel.getActionMap().put("performExit", performExit);
		cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyExit, "performExit");

		// assembling
		helpPane.add(rField);
		helpPane.add(percLabel);
		absolutePanel.add(aWLabel);
		absolutePanel.add(aWField);
		absolutePanel.add(aHLabel);
		absolutePanel.add(aHField);
		relativePanel.add(rLabel);
		relativePanel.add(helpPane);
		relativePanel.add(ok);
		relativePanel.add(cancel);
		main.setBorder(new EmptyBorder(10, 10, 10, 10));
		main.add(absolutePanel, BorderLayout.NORTH);
		main.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
		main.add(relativePanel, BorderLayout.SOUTH);

		// finalizing
		this.add(main);
		this.pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		this.addMouseWheelListener(this);
	}

	/**
	 * 
	 */
	private void applyChanges(ImagePanel panel) {
		panel.scale(scale, false);
		dispose();
	}

	/**
	 * propertyChangeListener
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		scale = Float.parseFloat(rField.getValue().toString()) / 100;
		updateField(aHField);
		updateField(aWField);
	}

	/**
	 * aux method to update field values
	 * 
	 * @param field
	 *            to be updated
	 */
	private void updateField(JFormattedTextField field) {
		if (field == rField) {
			rField.setValue(Math.round(scale * 100));
		} else {
			if (field == aWField) {
				field.setValue(Math.round(width * scale));
			} else {
				field.setValue(Math.round(height * scale));
			}
		}
	}

	/**
	 * focusGained
	 */
	@Override
	public void focusGained(FocusEvent e) {
		rField.setCaretPosition(rField.getValue().toString().length());
	}

	/**
	 * focusLost
	 */
	@Override
	public void focusLost(FocusEvent e) {
		// ignore
	}

	/**
	 * mousewheel spun
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			int notches = e.getWheelRotation();
			System.out.println("1: " + scale + " delta: " + (notches / 100.0));
			scale = (float) (scale - (notches / 100.0));
			if (scale <= 0) {
				scale = new Float(0.01);
			}
			System.out.println("2: " + scale);
			updateField(rField);
			updateField(aHField);
			updateField(aWField);
			System.out.println("fire2 " + notches);
		}
	}
}
