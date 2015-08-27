/**
 * 
 */
package imagemap.util;

import imagemap.graphics.AbstractShape;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Niklas Miroll
 *
 */
public class DetailEditor extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractShape shape = null;
	private JTextField href = new JTextField();
	private JTextField title = new JTextField();
	private JTextField alt = new JTextField();
	private CustomHTMLDoc doc;

	/**
	 * constructor for DetailEditor
	 * 
	 * @throws HeadlessException
	 */
	public DetailEditor(AbstractShape s, CustomHTMLDoc doc, JFrame owner) throws HeadlessException {
		super(owner, "Edit information of shape", true);
		shape = s;
		this.doc = doc;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	}

	/**
	 * creating GUI, partially using parameters from the shape passed in the
	 * constructor
	 */
	private void createGUI() {
		// initializing
		JPanel main = new JPanel(new BorderLayout());
		JPanel middle = new JPanel(new GridLayout(6, 1, 10, 10));
		JPanel bottom = new JPanel();
		JLabel manual = new JLabel("Edit the information of your shape:");
		JLabel hrefDesc = new JLabel("Enter the link it should lead to:");
		hrefDesc.setLabelFor(href);
		JLabel titleDesc = new JLabel("Enter the tootlip which should be shown:");
		titleDesc.setLabelFor(title);
		JLabel altDesc = new JLabel("Enter alternative text:");
		altDesc.setLabelFor(alt);
		JButton ok = new JButton("Save");
		JButton cancel = new JButton("Cancel");

		// assembling
		middle.add(hrefDesc, BorderLayout.NORTH);
		middle.add(href, BorderLayout.SOUTH);
		middle.add(altDesc, BorderLayout.NORTH);
		middle.add(alt, BorderLayout.SOUTH);
		middle.add(titleDesc, BorderLayout.NORTH);
		middle.add(title, BorderLayout.SOUTH);
		middle.setBorder(new EmptyBorder(10, 10, 10, 10));
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Action performSave = new AbstractAction("Save") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				doSave();
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
		bottom.add(ok);
		bottom.add(cancel);
		main.add(manual, BorderLayout.NORTH);
		main.add(middle, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.SOUTH);
		main.setBorder(new EmptyBorder(10, 10, 10, 10));

		// finalizing
		href.setText(shape.getHref());
		alt.setText(shape.getAlt());
		title.setText(shape.getTooltip());
		this.setSize(300, 400);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		this.add(main);
	}

	/**
	 * save changes of attributes
	 */
	private void doSave() {
		shape.setAlt(alt.getText());
		doc.getMap()
		.getSubElements()
		.get(shape.getId())
		.editAttributeValue("alt", alt
				.getText());
		shape.setTooltip(title.getText());
		doc.getMap().getSubElements().get(shape.getId()).editAttributeValue("title", title.getText());
		shape.setHref(href.getText());
		doc.getMap().getSubElements().get(shape.getId()).editAttributeValue("href", href.getText());
		dispose();
	}

}
