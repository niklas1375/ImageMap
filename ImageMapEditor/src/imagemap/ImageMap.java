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
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

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
	private ImageMapProject currentProject;
	private JTabbedPane projects;
	private HashMap<String, ImageMapProject> projectObjects = new HashMap<String, ImageMapProject>();
	private JButton undoButton;
	private JButton redoButton;
	private JButton copyClip_button;
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
	private int current_toggle;
	private int startId;
	private JFileChooser fc = new JFileChooser();
	private boolean empty = true;
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
		mc = new ImageMap.MouseController();
		mmc = new ImageMap.MouseMotionController();
		currentProject = null;

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
		undo.setEnabled(false);
		edit.add(redo).setAccelerator(KeyStroke.getKeyStroke('Y', menuShortcutKeyMask));
		redo.addActionListener(this);
		redo.setName("redo");
		redo.setEnabled(false);
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
		copyClip_button = new JButton("Copy to Clipboard");
		ImageIcon rectIcon = createImageIcon("images/rectangle.png");
		ImageIcon circleIcon = createImageIcon("images/circle.png");
		ImageIcon polyIcon = createImageIcon("images/polygon.png");
		ImageIcon mouseIcon = createImageIcon("images/cursor.png");
		ImageIcon undoIcon = createImageIcon("images/undo.png");
		ImageIcon redoIcon = createImageIcon("images/redo.png");
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
		undoButton = new JButton(undoIcon);
		undoButton.setEnabled(false);
		undoButton.addActionListener(this);
		undoButton.setName("undo");
		redoButton = new JButton(redoIcon);
		redoButton.setEnabled(false);
		redoButton.addActionListener(this);
		redoButton.setName("redo");
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
		toolbar.add(undoButton);
		toolbar.add(redoButton);
		toolbar.add(copyClip_button);

		// tabbing for several ImageMap projects at the same time
		projects = new JTabbedPane();
		projects.setTabPlacement(SwingConstants.BOTTOM);
		projects.setUI(new CustomTabbedPaneUI());
		projects.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!empty && projects.getTabCount() != 0) {
					if (!projects.getTitleAt(projects.getSelectedIndex()).equals(currentProject.getName())) {
						path.setText(currentProject.getImagePanel().getSavePath());
						currentProject = (ImageMapProject) projects.getSelectedComponent();
					}
				}
			}
		});

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
		frame.add(projects, BorderLayout.CENTER);
		frame.add(status, BorderLayout.SOUTH);
		frame.setVisible(true);
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
			if (currentProject.getImagePanel().getImg() != null) {
				ShapeImporter si = new ShapeImporter(this, currentProject.getImagePanel().getImg(),
						currentProject.getImagePanel());
				si.setVisible(true);
			}
			break;

		case "clear":
			currentProject.getImagePanel().doClear();
			break;

		case "scale":
			if (currentProject.getImagePanel().getImg() != null) {
				ImageResizer ir = new ImageResizer(this, currentProject.getImagePanel().getImg(),
						currentProject.getImagePanel());
				ir.setVisible(true);
			}
			break;

		case "copy":
			currentProject.getImagePanel().doCopy(null);
			break;

		case "paste":
			currentProject.getImagePanel().doPaste();
			break;

		case "cut":
			currentProject.getImagePanel().doCut(null);
			break;

		case "redo":
			currentProject.getImagePanel().doRedo();
			break;

		case "undo":
			currentProject.getImagePanel().doUndo();
			break;

		case "delete":
			currentProject.getImagePanel().doDelete(null);
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
			JToggleButton isMetric = currentProject.getMetric();
			currentProject.getRulers()[1].setIsMetric(isMetric.isSelected());
			currentProject.getRulers()[0].setIsMetric(isMetric.isSelected());
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
		String toclip = currentProject.getHTMLPanel().getText();
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
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				addProject(file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Image could not be loaded.");
			}
		} else {
			JOptionPane.showMessageDialog(frame, "File could not be opened.");
		}
	} // end of doNew()

	/**
	 * save imagemap to html
	 */
	private void doSave() {
		ImagePanel panel = null;
		if (currentProject != null) {
			panel = currentProject.getImagePanel();
		}
		if (panel != null) {
			int retVal = JOptionPane.YES_OPTION;
			if (panel.isEditing()) {
				retVal = JOptionPane.showConfirmDialog(this, "You are currently editing a shape. Do you want to "
						+ "discard changes and save the finished shapes?");
			}
			if (retVal == JOptionPane.YES_OPTION) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Web", "html", "htm", "xhtml");
				fc.setFileFilter(filter);
				temp = null;
				panel.setEditing(false);
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
						panel.setSaved(true);
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
		DetailEditor de = new DetailEditor(s, currentProject.getImagePanel().getDoc(), this);
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
		if (currentProject.getImagePanel().isEditing() && current_toggle != type) {
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

	private void addProject(File file) throws IOException {
		String name = JOptionPane.showInputDialog(this, "How do you want to name the new project?");
		ImageMapProject proj = new ImageMapProject(name, file, this);
		projects.add(name, proj);
		projects.setSelectedComponent(proj);
		projectObjects.put(name, proj);
		currentProject = proj;
		shapeImport.setEnabled(true);
		scale.setEnabled(true);
		empty = false;
	}

	/**
	 * @return the undo
	 */
	public JMenuItem getUndo() {
		return undo;
	}

	/**
	 * 
	 * @return the undoButton
	 */
	public JButton getUndoButton() {
		return undoButton;
	}

	/**
	 * @return the redo
	 */
	public JMenuItem getRedo() {
		return redo;
	}
	
	/**
	 * 
	 * @return the redoButton
	 */
	public JButton getRedoButton() {
		return redoButton;
	}

	/**
	 * 
	 * @return the mouse controller
	 */
	public MouseController getMc() {
		return mc;
	}

	/**
	 * 
	 * @return the mouse motion controller
	 */
	public MouseMotionController getMmc() {
		return mmc;
	}

	/**
	 * 
	 * @return copyclip button
	 */
	public JButton getClip_button() {
		return copyClip_button;
	}

	/**
	 * 
	 * @return mouse_position
	 */
	public JLabel getMouse_position() {
		return mouse_position;
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
			if (currentProject.getImagePanel().getImg() != null) {
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
			if (currentProject.getImagePanel().isInside(e.getPoint())) {
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
			endShape = currentProject.getImagePanel().getDraggedShape();
			currentProject.getImagePanel().setDraggedShape(null);
			dragging = false;
			moving = false;
			resizing = false;
			temp = null;
			currentProject.getImagePanel().addShape(endShape);
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
			currentProject.getImagePanel().endEditing(actionType, startShape, endShape);
			currentProject.getImagePanel().getDoc().getMap().getSubElements().get(startId).updateCoords(endShape);
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
				if (currentProject.getImagePanel().isInside(e.getPoint())) {
					currentProject.getImagePanel().setCurrentShape(
							currentProject.getImagePanel().whichShape(e.getPoint()));
				}
			} else if (e.getClickCount() == 2) {
				if (currentProject.getImagePanel().isInside(e.getPoint())) {
					doEdit(currentProject.getImagePanel().whichShape(e.getPoint()));
				}
			}
		}

		/**
		 * handle clicks while polygon is toggled
		 * 
		 * @param e
		 */
		private void doPoly(MouseEvent e) {
			ImagePanel panel = currentProject.getImagePanel();
			if (!panel.isEditing()) {
				panel.addShape(new PolygonShape(e.getPoint()));
				panel.setEditing(true);
			} else {
				int clicks = e.getClickCount();
				if (clicks == 2) {
					panel.setEditing(false);
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
			boolean isInside = currentProject.getImagePanel().isInside(p);
			if (isInside) {
				AbstractShape s = currentProject.getImagePanel().whichShape(p);
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
						currentProject.getImagePanel().setCurrentShape(s);
						currentProject.getImagePanel().doCopy(null);
					}
				});
				JMenuItem cut = new JMenuItem("Cut");
				cut.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						currentProject.getImagePanel().setCurrentShape(s);
						currentProject.getImagePanel().doCut(null);
					}
				});
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						currentProject.getImagePanel().setCurrentShape(s);
						currentProject.getImagePanel().doDelete(null);
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
						currentProject.getImagePanel().doPaste(p);
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
			AbstractShape shape = currentProject.getImagePanel().isInsideRect(p);
			setCursorStatus(p);
			if (shape == null) {
				if (inside) {
					if (current_toggle == TYPE_MOUSE) {
						AbstractShape tmp = currentProject.getImagePanel().whichShape(p);
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
						currentProject.getImagePanel().repaint();
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
					currentProject.getImagePanel().repaint();
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
				currentProject.getImagePanel().setDraggedShape(new CircleShape(center_tmp, r1));
			} else {
				currentProject.getImagePanel().setDraggedShape(new CircleShape(center_tmp, r));
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
			currentProject.getImagePanel().setDraggedShape(new RectangleShape(x, y, x1, y1));
		}

		/**
		 * mouse moved method
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			AbstractShape shape = currentProject.getImagePanel().isInsideRect(p);
			setCursorStatus(p);
			if (shape != null && current_toggle == TYPE_MOUSE) {
				Cursor cursor = Cursor.getPredefinedCursor(shape.getResizeCursor(p));
				setCursor(cursor);
			} else if (currentProject.getImagePanel().isInside(p) && current_toggle == TYPE_MOUSE) {
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
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private class CustomTabbedPaneUI extends MetalTabbedPaneUI {
		private Rectangle xRect;

		/**
		 * installLIsteners method
		 */
		protected void installListeners() {
			super.installListeners();
			tabPane.addMouseListener(new MyMouseHandler());
		}

		/**
		 * paintTab method to paint tab with cross for closing
		 */
		protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect) {
			super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

			Font f = g.getFont();
			g.setFont(new Font("Arial", Font.BOLD, 10));
			FontMetrics fm = g.getFontMetrics(g.getFont());
			int charWidth = fm.charWidth('x');
			int maxAscent = fm.getMaxAscent();
			g.drawString("x", textRect.x + textRect.width + 3, textRect.y + textRect.height - 4);
			g.drawRect(textRect.x + textRect.width + 1, textRect.y + textRect.height - 1 - maxAscent, charWidth + 2,
					maxAscent - 1);
			xRect = new Rectangle(textRect.x + textRect.width - 5, textRect.y + textRect.height - maxAscent,
					charWidth + 2, maxAscent - 1);
			g.setFont(f);
		}

		/**
		 * class to handle mouse clicks in the closing rectangle
		 * 
		 * @author Joris Van den Bogaert, Niklas Miroll
		 *
		 */
		public class MyMouseHandler extends MouseAdapter {
			public void mousePressed(MouseEvent e) {
				if (xRect.contains(e.getPoint())) {
					int tabIndex = tabForCoordinate(projects, e.getX(), e.getY());
					ImageMapProject proj = (ImageMapProject) projects.getComponentAt(tabIndex);
					if (proj.getImagePanel().isEditing() || !proj.getImagePanel().isSaved()) {
						int retVal = JOptionPane.showConfirmDialog(frame, "You are currently editing this project "
								+ "or haven't saved it yet.\nDo you want to save before closing?", "Confirm action",
								JOptionPane.OK_CANCEL_OPTION);
						switch (retVal) {
						case JOptionPane.YES_OPTION:
							kill(tabIndex);
							break;

						case JOptionPane.NO_OPTION:
							kill(tabIndex);
							break;

						default:
							break;
						}
					} else {
						kill(tabIndex);
					}
				}
			}

			/**
			 * kill tab
			 * 
			 * @param tabIndex
			 */
			private void kill(int tabIndex) {
				projectObjects.remove(projects.getTitleAt(tabIndex));
				projects.remove(tabIndex);
				if (projects.getTabCount() == 0) {
					resetToInit();
				} else {
					currentProject = (ImageMapProject) projects.getSelectedComponent();
				}
			}

			/**
			 * reset ImageMapEditor to initial status after closing of last tab
			 */
			private void resetToInit() {
				currentProject = null;
				empty = true;
				inside = false;
				dragging = false;
				moving = false;
				resizing = false;
				fastClose = false;
				temp = null;
				startShape = null;
				endShape = null;
				mouse_position.setText("   ");
				path.setText("");
			}
		}
	}

} // end of class ImageMap