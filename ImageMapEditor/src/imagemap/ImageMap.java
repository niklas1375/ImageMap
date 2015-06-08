/**
 * 
 */
package imagemap;

import imagemap.graphics.*;
import imagemap.panels.*;
import imagemap.util.*;
import imagemap.util.action.AbstractStackAction;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

/**
 * @author Niklas Miroll
 *
 */
public class ImageMap extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int TYPE_MOUSE = 3;
	private static ImageMap frame;
	private static ImageMap.MouseController mc;
	private static ImageMap.MouseMotionController mmc;
	private static WindowController wc;
	private ImagePanel panel;
	private HTMLPanel html;
	private JTextPane htmlLines;
	private PreviewPanel htmlPreview;
	private JScrollPane scrollableImage;
	private JScrollPane scrollableText;
	private JScrollPane scrollableWeb;
	private ButtonGroup group;
	private JMenuItem undo;
	private JMenuItem redo;
	private JMenuItem shapeImport;
	private JMenuItem scale;
	private JLabel mouse_position;
	private JLabel path;
	private Point temp;
	private AbstractShape startShape;
	private AbstractShape endShape;
	private Rule columnView;
	private Rule rowView;
	private JToggleButton isMetric;
	private int current_toggle;
	private int startId;
	private JFileChooser fc = new JFileChooser();
	private boolean editing;
	private boolean empty = true;
	private boolean saved = true;
	private boolean inside = false;
	private boolean dragging = false;
	private boolean moving = false;
	private boolean resizing = false;
	private boolean fastClose = false;

	/**
	 * Constructor for ImageMap
	 * 
	 * @throws HeadlessException
	 */
	public ImageMap() throws HeadlessException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	} // end of Constructor for ImageMap

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		frame = new ImageMap();
	} // end of main method

	/**
	 * create GUI
	 */
	private void createGUI() {
		// general declarations and instantiations
		editing = false;
		KeyStroke fastExit = KeyStroke.getKeyStroke('E', java.awt.event.InputEvent.CTRL_DOWN_MASK
				| java.awt.event.InputEvent.SHIFT_DOWN_MASK);
		Action performWindowExit = new AbstractAction("FastExit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				fastClose = true;
				System.exit(NORMAL);
			}
		}; // to make fast escape from program without save prompt
		frame.getRootPane().getActionMap().put("performWindowExit", performWindowExit);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(fastExit, "performWindowExit");

		// menubar
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("?");
		JMenu lookandfeel = new JMenu("Change look and feel...");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem newMap = new JMenuItem("New");
		shapeImport = new JMenuItem("Import shapes from HTML...");
		JMenuItem save = new JMenuItem("Save");
		undo = new JMenuItem("Undo");
		redo = new JMenuItem("Redo");
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem clear = new JMenuItem("Clear All Shapes");
		scale = new JMenuItem("Scale...");
		JMenuItem paste = new JMenuItem("Paste");
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem delete = new JMenuItem("Delete");
		JMenuItem helper = new JMenuItem("Help");
		JMenuItem crosslf = new JMenuItem("CrossPlatformLookAndFeel");
		JMenuItem systemlf = new JMenuItem("SystemLookAndFeel");
		JMenuItem motiflf = new JMenuItem("MotifLookAndFeel");
		JMenuItem about = new JMenuItem("About");
		int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		file.add(newMap).setAccelerator(KeyStroke.getKeyStroke('N', menuShortcutKeyMask));
		newMap.addActionListener(this);
		newMap.setName("new");
		file.add(shapeImport).addActionListener(this);
		shapeImport.setName("import");
		shapeImport.setEnabled(false);
		shapeImport.setToolTipText("Load image to import shapes from HTML");
		shapeImport.setAccelerator(KeyStroke.getKeyStroke('I', menuShortcutKeyMask));
		file.add(save).setAccelerator(KeyStroke.getKeyStroke('S', menuShortcutKeyMask));
		save.addActionListener(this);
		save.setName("save");
		file.addSeparator();
		file.add(exit).addActionListener(this);
		exit.setName("exit");
		edit.add(undo).setAccelerator(KeyStroke.getKeyStroke('Z', menuShortcutKeyMask));
		undo.addActionListener(this);
		undo.setName("undo");
		edit.add(redo).setAccelerator(KeyStroke.getKeyStroke('Y', menuShortcutKeyMask));
		redo.addActionListener(this);
		redo.setName("redo");
		edit.addSeparator();
		edit.add(clear).addActionListener(this);
		clear.setName("clear");
		edit.addSeparator();
		edit.add(scale).addActionListener(this);
		scale.setEnabled(false);
		scale.setName("scale");
		edit.addSeparator();
		edit.add(copy).setAccelerator(KeyStroke.getKeyStroke('C', menuShortcutKeyMask));
		copy.addActionListener(this);
		copy.setName("copy");
		edit.add(paste).setAccelerator(KeyStroke.getKeyStroke('V', menuShortcutKeyMask));
		paste.addActionListener(this);
		paste.setName("paste");
		edit.add(cut).setAccelerator(KeyStroke.getKeyStroke('X', menuShortcutKeyMask));
		cut.addActionListener(this);
		cut.setName("cut");
		edit.addSeparator();
		edit.add(delete).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		delete.addActionListener(this);
		delete.setName("delete");
		lookandfeel.add(crosslf).addActionListener(this);
		crosslf.setName("crosslf");
		lookandfeel.add(systemlf).addActionListener(this);
		systemlf.setName("systemlf");
		lookandfeel.add(motiflf).addActionListener(this);
		motiflf.setName("motiflf");
		help.add(lookandfeel);
		help.addSeparator();
		help.add(helper).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helper.addActionListener(this);
		helper.setName("help");
		help.addSeparator();
		help.add(about).addActionListener(this);
		about.setName("about");
		menubar.add(file);
		menubar.add(edit);
		menubar.add(help);

		// toolbar
		group = new ButtonGroup();
		JToolBar toolbar = new JToolBar("Tools", JToolBar.HORIZONTAL);
		JButton copyClip_button = new JButton("Copy to Clipboard");
		ImageIcon rectIcon = createImageIcon("images/rectangle.png");
		ImageIcon circleIcon = createImageIcon("images/circle.png");
		ImageIcon polyIcon = createImageIcon("images/polygon.png");
		ImageIcon mouseIcon = createImageIcon("images/cursor.png");
		JToggleButton rectangle_button = new JToggleButton(rectIcon);
		JToggleButton circle_button = new JToggleButton(circleIcon);
		JToggleButton polygon_button = new JToggleButton(polyIcon);
		JToggleButton mouse_button = new JToggleButton(mouseIcon);
		group.add(rectangle_button);
		group.add(circle_button);
		group.add(polygon_button);
		group.add(mouse_button);
		rectangle_button.setToolTipText("Press and hold to drag the wanted rectangle.");
		circle_button.setToolTipText("Press and hold to drag the wanted circle.");
		polygon_button.setToolTipText("Click the corners of your wanted polygon. The last point will always connect "
				+ "with the first. Double click or Escape will end editing of the polygon.");
		mouse_button.setToolTipText("Click shape once to select, twice to edit information. Drag to move.");
		group.setSelected(rectangle_button.getModel(), true);
		current_toggle = AbstractShape.TYPE_RECT;
		copyClip_button.setVisible(false);
		copyClip_button.addActionListener(this);
		copyClip_button.setName("clip");
		toolbar.add(rectangle_button);
		rectangle_button.setName("rect_button");
		rectangle_button.setActionCommand("" + AbstractShape.TYPE_RECT);
		rectangle_button.addActionListener(this);
		toolbar.add(circle_button);
		circle_button.setName("circ_button");
		circle_button.setActionCommand("" + AbstractShape.TYPE_CIRC);
		circle_button.addActionListener(this);
		toolbar.add(polygon_button);
		polygon_button.setName("poly_button");
		polygon_button.setActionCommand("" + AbstractShape.TYPE_POLY);
		polygon_button.addActionListener(this);
		toolbar.addSeparator();
		toolbar.add(mouse_button);
		mouse_button.addActionListener(this);
		mouse_button.setActionCommand("" + TYPE_MOUSE);
		mouse_button.setName("mouse_button");
		toolbar.addSeparator();
		toolbar.add(copyClip_button);

		// ImagePanel stuff
		panel = new ImagePanel(frame);
		mc = new ImageMap.MouseController();
		mmc = new ImageMap.MouseMotionController();
		panel.addMouseListener(mc);
		panel.addMouseMotionListener(mmc);
		scrollableImage = new JScrollPane();
		scrollableImage.add(panel);
		scrollableImage.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		scrollableImage.setViewportView(panel);
		KeyStroke keyExit = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action performExit = new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (editing) {
					editing = false;
				}
			}
		};
		panel.getActionMap().put("performExit", performExit);
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyExit, "performExit");
		columnView = new Rule(Rule.HORIZONTAL, true);
		columnView.setPreferredHeight(35);
		rowView = new Rule(Rule.VERTICAL, true);
		rowView.setPreferredWidth(35);
		scrollableImage.setColumnHeaderView(columnView);
		scrollableImage.setRowHeaderView(rowView);
		JPanel buttonCorner = new JPanel(); //use FlowLayout
		isMetric = new JToggleButton("cm", true);
		isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
		isMetric.setMargin(new Insets(2,2,2,2));
		isMetric.addActionListener(this);
		isMetric.setName("corner");
		buttonCorner.add(isMetric);
		scrollableImage.setCorner(JScrollPane.UPPER_LEFT_CORNER, buttonCorner);

		// HTMLPanel stuff
		StyleContext sc = new StyleContext();
		final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
		html = new HTMLPanel(doc);
		scrollableText = new JScrollPane();
		scrollableText.add(html);
		scrollableText.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		scrollableText.setViewportView(html);
		htmlLines = new JTextPane();
		htmlLines.setBackground(Color.lightGray);
		htmlLines.setEditable(false);
		scrollableText.add(htmlLines);
		scrollableText.setRowHeaderView(htmlLines);

		// preview stuff
		htmlPreview = new PreviewPanel(panel);
		scrollableWeb = new JScrollPane();
		scrollableWeb.add(htmlPreview);
		scrollableWeb.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		scrollableWeb.setViewportView(htmlPreview);

		// tab stuff
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Image", scrollableImage);
		tabs.addTab("HTML", scrollableText);
		tabs.addTab("Preview", scrollableWeb);
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!tabs.getTitleAt(tabs.getSelectedIndex()).equals("Image")) {
					mouse_position.setText("");
				}
				if (tabs.getTitleAt(tabs.getSelectedIndex()).equals("Preview")) {
					htmlPreview.setPanel(panel);
				}
				if (tabs.getTitleAt(tabs.getSelectedIndex()).equals("HTML")) {
					Image img = panel.getImg();
					if (img != null) {
						String htmlcode = panel.getDoc().toString();
						html.setText(htmlcode);
						htmlPreview.setText(htmlcode);
						htmlLines.setText(html.getLineNumbers());
						html.setPreferredSize(new Dimension(html.getWidth(), html.getHeight()));
						html.revalidate();
						htmlPreview.revalidate();
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								scrollableWeb.getHorizontalScrollBar().setValue(0);
								scrollableWeb.getVerticalScrollBar().setValue(0);
							}
						});
					}
					copyClip_button.setVisible(true);
				} else {
					copyClip_button.setVisible(false);
				}
			}
		});
		KeyStroke keyTabForward = KeyStroke.getKeyStroke("ctrl TAB");
		KeyStroke keyTabBackward = KeyStroke.getKeyStroke("ctrl shift TAB");
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(
				tabs.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.remove(keyTabForward);
		tabs.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(
				tabs.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.remove(keyTabBackward);
		tabs.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
		Action tabForward = new AbstractAction("TabForward") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				tabs.setSelectedIndex((tabs.getSelectedIndex() + 1) % 3);
				tabs.getSelectedComponent().requestFocusInWindow();
			}
		};
		Action tabBackward = new AbstractAction("TabBackward") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (tabs.getSelectedIndex() == 0) {
					tabs.setSelectedIndex(2);
				} else {
					tabs.setSelectedIndex(tabs.getSelectedIndex() - 1);
				}
				tabs.getSelectedComponent().requestFocusInWindow();
			}
		};
		tabs.getActionMap().put("tabForward", tabForward);
		tabs.getActionMap().put("tabBackward", tabBackward);
		tabs.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyTabForward, "tabForward");
		tabs.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyTabBackward, "tabBackward");

		// status bar
		JPanel status = new JPanel(new BorderLayout());
		path = new JLabel("ImageMap wurde noch nicht gespeichert.");
		mouse_position = new JLabel("  ");
		status.add(path, BorderLayout.WEST);
		status.add(mouse_position, BorderLayout.EAST);
		Font font = mouse_position.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		mouse_position.setFont(boldFont);
		path.setFont(boldFont);

		// build frame and assemble components
		wc = new WindowController();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setTitle("ImageMap-Editor");
		frame.setSize((int) d.getWidth() * 2 / 3, (int) d.getHeight() * 2 / 3);
		frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
		frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(wc);
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(menubar);
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(tabs, BorderLayout.CENTER);
		frame.add(status, BorderLayout.SOUTH);
		frame.setVisible(true);
		tabs.getComponentAt(0).requestFocusInWindow();
	} // end of createGUI()

	/**
	 * action performed; switching over sources' names
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((Component) e.getSource()).getName();
		switch (name) {
		case "exit":
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			break;

		case "save":
			doSave();
			break;

		case "new":
			doNew();
			break;

		case "import":
			if (panel.getImg() != null) {
				ShapeImporter si = new ShapeImporter(this, panel.getImg(), panel);
				si.setVisible(true);
			}
			break;

		case "clear":
			panel.doClear();
			break;

		case "scale":
			if (panel.getImg() != null) {
				ImageResizer ir = new ImageResizer(this, panel.getImg(), panel);
				ir.setVisible(true);
			}
			break;

		case "copy":
			panel.doCopy(null);
			break;

		case "paste":
			panel.doPaste();
			break;

		case "cut":
			panel.doCut(null);
			break;

		case "redo":
			panel.doRedo();
			break;

		case "undo":
			panel.doUndo();
			break;

		case "delete":
			panel.doDelete(null);
			break;

		case "about":
			doAbout();
			break;

		case "help":
			doHelp();
			break;

		case "clip":
			doClip();
			break;

		case "rect_button":
			doToggle(AbstractShape.TYPE_RECT);
			break;

		case "circ_button":
			doToggle(AbstractShape.TYPE_CIRC);
			break;

		case "poly_button":
			doToggle(AbstractShape.TYPE_POLY);
			break;

		case "mouse_button":
			doToggle(TYPE_MOUSE);
			break;

		case "crosslf":
			doLF(name);
			break;

		case "systemlf":
			doLF(name);
			break;

		case "motiflf":
			doLF(name);
			break;
			
		case "corner":
			columnView.setIsMetric(isMetric.isSelected());
			rowView.setIsMetric(isMetric.isSelected());
			if (isMetric.isSelected()) {
				isMetric.setText("cm");
			} else {
				isMetric.setText("in ");
			}
			break;

		default:
			break;
		} // end of switch case over event's source's name
	} // end of actionPerformed(ActionEvent e)

	/**
	 * show help window
	 */
	private void doHelp() {
		HelpFrame.getInstance().setVisible(true);
	} // end of doHelp()

	/**
	 * copy HTML to clipboard
	 */
	private void doClip() {
		String toclip = html.getText();
		StringSelection strSel = new StringSelection(toclip);
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(strSel, null);
	} // end of doClip()

	/**
	 * show about information
	 */
	private void doAbout() {
		HelpFrame.getInstance().setVisible(true);
		HelpFrame.getInstance().doAbout();
	} // end of doAbout()

	/**
	 * open new image
	 */
	private void doNew() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Bilder", "gif", "png", "jpg", "jpeg", "bmp");
		fc.setFileFilter(filter);
		if (!saved || editing) {
			int retVal = JOptionPane.showConfirmDialog(this,
					"Do you want to discard unsaved changes and open a new image?");
			if (retVal != JOptionPane.YES_OPTION) {
				int returnVal = fc.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						panel.die();
						panel.setImagePath(file.getAbsolutePath());
						panel.setImg((Image) ImageIO.read(file));
						panel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
						rowView.setPreferredHeight(panel.getImg().getHeight(null));
						rowView.repaint();
						columnView.setPreferredWidth(panel.getImg().getWidth(null));
						columnView.repaint();
						panel.revalidate();
						html.setText("");
						shapeImport.setEnabled(true);
						scale.setEnabled(true);
						empty = false;
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, "Image could not be loaded.");
					}
				} else {
					JOptionPane.showMessageDialog(frame, "File could not be opened.");
				}
			}
		} else {
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					panel.die();
					panel.setImagePath(file.getAbsolutePath());
					panel.setImg(ImageIO.read(file));
					panel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
					rowView.setPreferredHeight(panel.getImg().getHeight(null));
					rowView.repaint();
					columnView.setPreferredWidth(panel.getImg().getWidth(null));
					columnView.repaint();
					panel.revalidate();
					html.setText("");
					shapeImport.setEnabled(true);
					scale.setEnabled(true);
					empty = false;
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Image could not be loaded.");
				}
			} else {
				JOptionPane.showMessageDialog(frame, "File could not be opened.");
			}
		}
	} // end of doNew()

	/**
	 * save imagemap to html
	 */
	private void doSave() {
		Image img = panel.getImg();
		if (img != null) {
			int retVal = 0;
			if (editing) {
				retVal = JOptionPane.showConfirmDialog(this, "You are currently editing a shape. Do you want to "
						+ "discard changes and save the finished shapes?");
			}
			if (retVal == JOptionPane.YES_OPTION) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Web", "html", "htm", "xhtml");
				fc.setFileFilter(filter);
				temp = null;
				editing = false;
				String html = panel.getDoc().toString();
				fc.setSelectedFile(new File("imagemap.html"));
				int returnVal = fc.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File content = fc.getSelectedFile();
					try {
						FileOutputStream fos = new FileOutputStream(content);
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
						bw.write(html);
						bw.close();
						fos.close();
						panel.setSavePath(content.getAbsolutePath());
						path.setText("Save path: " + content.getAbsolutePath());
						JOptionPane.showMessageDialog(this,
								"Remember changing source of picture according to the webserver!", "Reminder",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} // end try catch
				} else {
					JOptionPane.showMessageDialog(this, "File wasn't saved.");
				} // end if (returnVal == JFileChooser.APPROVE_OPTION)
			} // end if
		} else {
			JOptionPane.showMessageDialog(this, "There is nothing to save");
		} // end if (img != null)
	} // end of doSave()

	/**
	 * edit given shape (either by right-click or by end of editing of a shape)
	 * 
	 * @param s
	 */
	private void doEdit(AbstractShape s) {
		DetailEditor de = new DetailEditor(s, panel.getDoc(), this);
		de.setVisible(true);
	} // end of doEdit(Shape s)

	/**
	 * change look and feel of program
	 * 
	 * @param name
	 */
	private void doLF(String name) {
		switch (name) {
		case "crosslf":
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "systemlf":
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "motiflf":
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * @param type
	 */
	private void doToggle(int type) {
		if (editing && current_toggle != type) {
			int returnVal = JOptionPane.showConfirmDialog(this, "You are currently editing a shape "
					+ "of another type. Do you want to switch the tool and discard changes?");
			if (returnVal == JOptionPane.YES_OPTION) {
				temp = null;
				current_toggle = type;
			} else {
				while (group.getElements().hasMoreElements()) {
					AbstractButton local = group.getElements().nextElement();
					if (local.getName().equals(current_toggle)) {
						group.setSelected(local.getModel(), true);
						break;
					}
				}
			}
		} else {
			current_toggle = type;
		}
	}

	/**
	 * 
	 * @param path
	 * @param description
	 * @return image icon of given path
	 */
	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			ImageIcon icon = new ImageIcon(imgURL);
			Image img = icon.getImage();
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
			icon = new ImageIcon(newimg);
			return icon;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	/**
	 * update rulers according to content of imagepanel
	 */
	public void updateRulers() {
		rowView.repaint();
		columnView.repaint();
	}

	/**
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private class MouseController extends MouseAdapter {
		/**
		 * mouseReleased Event Handler
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if (panel.getImg() != null) {
				if (SwingUtilities.isRightMouseButton(e)) {
					handleRightClick(e);
				} else {
					handleLeftClick(e);
				} // end of if for right clicks
			}
		} // end of mouseReleased(MouseEvent e)

		/**
		 * mouse pressed EventHandler
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (panel.isInside(e.getPoint())) {
				inside = true;
			} else {
				inside = false;
			}
			// remember start point
			temp = e.getPoint();
		}

		/**
		 * handle left clicks
		 * 
		 * @param e
		 */
		private void handleLeftClick(MouseEvent e) {
			if (dragging) {
				handleDrag();
			} else if (moving) {
				handleMoveAndResize(AbstractStackAction.MOVE);
			} else if (resizing) {
				handleMoveAndResize(AbstractStackAction.RESIZE);
			} else {
				handleRegular(e);
			}
		} // end of handleLeftClick(MouseEvent e)

		/**
		 * handle end of creating circle or rectangle Shape
		 */
		private void handleDrag() {
			endShape = panel.getDraggedShape();
			panel.setDraggedShape(null);
			dragging = false;
			moving = false;
			resizing = false;
			temp = null;
			panel.addShape(endShape);
			startShape = null;
			endShape = null;
		}

		/**
		 * handle end of Shape-moving and Shape-resizing
		 */
		private void handleMoveAndResize(int actionType) {
			dragging = false;
			moving = false;
			resizing = false;
			panel.endEditing(actionType, startShape, endShape);
			panel.getDoc().getMap().getSubElements().get(startId).updateCoords(endShape);
			startShape = null;
			endShape = null;
		}

		/**
		 * @param e
		 *            passed mouse event
		 */
		private void handleRegular(MouseEvent e) {
			int type = Integer.parseInt(group.getSelection().getActionCommand());
			switch (type) {
			case AbstractShape.TYPE_POLY:
				doPoly(e);
				break;

			case TYPE_MOUSE:
				doMouse(e);
				break;

			default:
				break;
			}
		}

		/**
		 * handle selection of shape
		 * 
		 * @param e
		 */
		private void doMouse(MouseEvent e) {
			if (e.getClickCount() == 1) {
				if (panel.isInside(e.getPoint())) {
					panel.setCurrentShape(panel.whichShape(e.getPoint()));
				}
			} else if (e.getClickCount() == 2) {
				if (panel.isInside(e.getPoint())) {
					doEdit(panel.whichShape(e.getPoint()));
				}
			}
		}

		/**
		 * handle clicks while polygon is toggled
		 * 
		 * @param e
		 */
		private void doPoly(MouseEvent e) {
			if (!editing) {
				panel.addShape(new PolygonShape(e.getPoint()));
				editing = true;
			} else {
				int clicks = e.getClickCount();
				if (clicks == 2) {
					editing = false;
					((PolygonShape) panel.getShapeList().lastElement()).addPolyPoint(e.getPoint());
					panel.repaint();
				}
				if (clicks == 1) {
					((PolygonShape) panel.getShapeList().lastElement()).addPolyPoint(e.getPoint());
					panel.repaint();
				}
			}
		}

		/**
		 * handle right clicks
		 * 
		 * @param e
		 */
		private void handleRightClick(MouseEvent e) {
			Point p = e.getPoint();
			boolean isInside = panel.isInside(p);
			if (isInside) {
				AbstractShape s = panel.whichShape(p);
				// open right-click-menu with options: edit information,
				// copy,
				// cut, delete
				JPopupMenu popupout = new JPopupMenu();
				JMenuItem edit = new JMenuItem("Edit information");
				edit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						doEdit(s);
					}
				});
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						panel.setCurrentShape(s);
						panel.doCopy(null);
					}
				});
				JMenuItem cut = new JMenuItem("Cut");
				cut.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						panel.setCurrentShape(s);
						panel.doCut(null);
					}
				});
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						panel.setCurrentShape(s);
						panel.doDelete(null);
					}
				});
				popupout.add(edit);
				popupout.addSeparator();
				popupout.add(copy);
				popupout.add(cut);
				popupout.add(delete);
				popupout.show(e.getComponent(), (int) p.getX(), (int) p.getY());
			} else {
				// open right-click-menu with options: paste
				JPopupMenu popupout = new JPopupMenu();
				JMenuItem paste = new JMenuItem("Paste here");
				paste.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						panel.doPaste(p);
					}
				});
				popupout.add(paste);
				popupout.show(e.getComponent(), (int) p.getX(), (int) p.getY());
			} // end of ife for inside shape
		}
	} // end of inner class MouseController

	/**
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private class MouseMotionController implements MouseMotionListener {
		/**
		 * mouse dragged function
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			AbstractShape shape = panel.isInsideRect(p);
			setCursorStatus(p);
			if (shape == null) {
				if (inside) {
					if (current_toggle == TYPE_MOUSE) {
						AbstractShape tmp = panel.whichShape(p);
						if (startShape == null) {
							startShape = tmp.clone();
							startId = 0 + tmp.getId();
						}
						moving = true;
						int xdir = (int) p.getX() - (int) temp.getX();
						int ydir = (int) p.getY() - (int) temp.getY();
						temp = p;
						tmp.move(xdir, ydir);
						endShape = tmp.clone();
						panel.repaint();
					}
				} else {
					if (current_toggle == AbstractShape.TYPE_RECT) {
						dragging = true;
						dragRect(p);
					} else if (current_toggle == AbstractShape.TYPE_CIRC) {
						dragging = true;
						dragCirc(p);
					}
				}
			} else {
				if (current_toggle == TYPE_MOUSE) {
					if (startShape == null) {
						startShape = shape.clone();
						startId = 0 + shape.getId();
					}
					resizing = true;
					int xdir = (int) e.getPoint().getX() - (int) temp.getX();
					int ydir = (int) e.getPoint().getY() - (int) temp.getY();
					temp = p;
					shape.movePoint(p, xdir, ydir);
					endShape = shape.clone();
					panel.repaint();
				}
			}
		}

		/**
		 * draw dragged circle shape
		 * 
		 * @param p
		 *            passed mouse point
		 */
		private void dragCirc(Point p) {
			int x = (int) temp.getX() + (int) ((p.getX() - temp.getX()) / 2);
			int y = (int) temp.getY() + (int) ((p.getY() - temp.getY()) / 2);
			Point center_tmp = new Point(x, y);
			int r = Math.abs((int) temp.getX() - x);
			int r1 = Math.abs((int) temp.getY() - y);
			if (r > r1) {
				panel.setDraggedShape(new CircleShape(center_tmp, r1));
			} else {
				panel.setDraggedShape(new CircleShape(center_tmp, r));
			}
		}

		/**
		 * draw dragged rectangle shape
		 * 
		 * @param p
		 *            passed mouse point
		 */
		private void dragRect(Point p) {
			int x = (int) temp.getX();
			int y = (int) temp.getY();
			int x1 = (int) p.getX();
			int y1 = (int) p.getY();
			panel.setDraggedShape(new RectangleShape(x, y, x1, y1));
		}

		/**
		 * mouse moved method
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			AbstractShape shape = panel.isInsideRect(p);
			setCursorStatus(p);
			if (shape != null && current_toggle == TYPE_MOUSE) {
				Cursor cursor = Cursor.getPredefinedCursor(shape.getResizeCursor(p));
				setCursor(cursor);
			} else if (panel.isInside(p) && current_toggle == TYPE_MOUSE) {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
				setCursor(cursor);
			} else {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
				setCursor(cursor);
			}
		}

		/**
		 * @param p
		 *            passed Point to set in status bar
		 */
		private void setCursorStatus(Point p) {
			int x = (int) p.getX();
			int y = (int) p.getY();
			mouse_position.setText("x = " + x + ", y = " + y);
		}

	} // end of inner class MouseMotionController

	/**
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private class WindowController extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (!empty || fastClose) {
				int res = JOptionPane.showConfirmDialog(frame,
						"Do you really want to exit the program? \nUnsaved changes will be lost.");
				if (res == JOptionPane.YES_OPTION) {
					System.exit(NORMAL);
				}
			} else
				System.exit(NORMAL);
		}
	} // end of inner class WindowController

	/**
	 * @return the undo
	 */
	public JMenuItem getUndo() {
		return undo;
	}

	/**
	 * @return the redo
	 */
	public JMenuItem getRedo() {
		return redo;
	}

} // end of class ImageMap