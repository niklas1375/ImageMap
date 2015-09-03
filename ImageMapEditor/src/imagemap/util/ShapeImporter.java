/**
 * 
 */
package imagemap.util;

import imagemap.ImageMap;
import imagemap.panels.ImagePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Niklas Miroll
 *
 */
public class ShapeImporter extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int xMax;
	private int yMax;
	private boolean[] enabledFlags;
	private String[] items;
	private Vector<CustomHTMLElement> itemObjects;
	private JList<String> jList;
	private ImagePanel panel;

	/**
	 * Constructor
	 * 
	 * @param ImageMap
	 *            parent component
	 */
	public ShapeImporter(ImageMap parent, Image image, ImagePanel panel) {
		super(parent, "Import shapes from HTML", true);
		this.xMax = image.getWidth(null);
		this.yMax = image.getHeight(null);
		this.panel = panel;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	}

	/**
	 * create GUI
	 */
	private void createGUI() {
		// initializing
		JFileChooser fc = new JFileChooser();
		JPanel main = new JPanel(new BorderLayout());
		JPanel top = new JPanel();
		JPanel middle = new JPanel();
		JPanel bottom = new JPanel();
		JLabel instruction = new JLabel("Choose which Shapes to import.");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Web (*.html, *.htm)", "html", "htm");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				byte[] encoded = Files.readAllBytes(fc.getSelectedFile().toPath());
				itemObjects = CustomHTMLDoc.getShapeNodesFromHTML(new String(encoded, Charset.defaultCharset()));
				if (!itemObjects.isEmpty()) {
					items = new String[itemObjects.size()];
					enabledFlags = new boolean[itemObjects.size()];
					int j = 0;
					for (CustomHTMLElement customHTMLElement : itemObjects) {
						items[j] = customHTMLElement.toString();
						j++;
					}
					setEnabledFlags();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "File could not be opened.");
		}
		jList = new JList<String>(items);
		JScrollPane scroll = new JScrollPane(jList);
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jList.setCellRenderer(new DisabledItemListCellRenderer());
		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				jList.repaint();
			}
		});
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JButton ok = new JButton("Import selected");
		JButton all = new JButton("Select All");
		JButton cancel = new JButton("Cancel");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSelection();
			}
		});
		all.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int valid = 0;
				for (boolean b : enabledFlags) {
					if (b)
						valid++;
				}
				int[] selections = new int[valid];
				int j = 0;
				for (int i = 0; i < enabledFlags.length; i++) {
					if (enabledFlags[i]) {
						selections[j] = i;
						j++;
					}
				}
				jList.setSelectedIndices(selections);
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		// assembling
		top.add(instruction);
		scroll.setViewportView(jList);
		middle.add(scroll);
		bottom.add(ok);
		bottom.add(all);
		bottom.add(cancel);
		main.add(top, BorderLayout.NORTH);
		main.add(middle, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.SOUTH);
		main.setBorder(new EmptyBorder(10, 10, 10, 10));
		KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Action performSave = new AbstractAction("Save") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				doSelection();
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

		// finalizing
		this.add(main);
		this.setVisible(true);
		this.pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		SwingUtilities.updateComponentTreeUI(this);
	}

	/**
	 * write selection to shapelist
	 */
	public void doSelection() {
		int[] tmp = jList.getSelectedIndices();
		if (tmp.length > 0) {
			for (int i = 0; i < tmp.length; i++) {
				panel.addShape(itemObjects.get(tmp[i]).convertToShape());
			}
		}
		dispose();
	}

	/**
	 * sets enabled flags depending on coordinates of shape
	 */
	private void setEnabledFlags() {
		int j = 0;
		for (CustomHTMLElement customHTMLElement : itemObjects) {
			enabledFlags[j] = matchesMax(customHTMLElement);
			j++;
		}
	}

	/**
	 * returns whether shape is inside of picture
	 * 
	 * @param element
	 * @return is shape inside picture?
	 */
	private boolean matchesMax(CustomHTMLElement element) {
		String[] coordsStrings = element.getAttributeValue("coords").split(",");
		boolean tmp = true;
		for (int i = 0; i < coordsStrings.length; i++) {
			if (i % 2 == 0) {
				tmp = tmp && xMax >= Integer.parseInt(coordsStrings[i]);
			} else {
				tmp = tmp && yMax >= Integer.parseInt(coordsStrings[i]);
			}
		}
		return tmp;
	}

	/**
	 * private Sub-Class to handle disabling of List-Items depending on their
	 * coordinates
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private class DisabledItemListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list, value, index, false, false);
			if (enabledFlags[index]) {
				if (isSelected & cellHasFocus) {
					comp.setForeground(Color.black);
					comp.setBackground(new Color(184, 207, 229));
				} else {
					comp.setForeground(Color.black);
					comp.setBackground(new Color(75, 120, 175));
				}
				if (!isSelected) {
					comp.setForeground(Color.black);
					comp.setBackground(new Color(229, 229, 229));
				}
				return comp;
			}
			comp.setForeground(Color.DARK_GRAY);
			comp.setEnabled(false);
			return comp;
		}
	}

} // end class ShapeImporter