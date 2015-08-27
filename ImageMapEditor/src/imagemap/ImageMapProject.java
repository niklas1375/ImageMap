/**
 * 
 */
package imagemap;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import imagemap.panels.*;
import imagemap.util.Rule;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

/**
 * @author Niklas Miroll
 *
 */
public class ImageMapProject extends JTabbedPane implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanel imagePanel;
	private HTMLPanel html;
	private JTextPane htmlLines;
	private PreviewPanel htmlPreview;
	private String name;
	private ImageMap frame;
	private JScrollPane scrollableImage;
	private JScrollPane scrollableText;
	private JScrollPane scrollableWeb;
	private Rule columnView;
	private Rule rowView;
	private JToggleButton isMetric;
	private File file;

	/**
	 * constructor for ImageMapProjects
	 * 
	 * @param name
	 * @param file
	 * @param frame
	 * @throws IOException 
	 */
	public ImageMapProject(String name, File file, ImageMap frame) throws IOException {
		this.name = name;
		this.frame = frame;
		this.file = file;
		this.setTabPlacement(SwingConstants.TOP);
		createGUI();
	}

	/**
	 * method to create GUI
	 * @throws IOException 
	 */
	private void createGUI() throws IOException {
		// ImagePanel stuff
		imagePanel = new ImagePanel(frame, this);
		scrollableImage = new JScrollPane();
		scrollableImage.add(imagePanel);
		scrollableImage.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		scrollableImage.setViewportView(imagePanel);
		KeyStroke keyExit = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		Action performExit = new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (imagePanel.isEditing()) {
					imagePanel.setEditing(false);
				}
			}
		};
		imagePanel.getActionMap().put("performExit", performExit);
		imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyExit, "performExit");
		columnView = new Rule(Rule.HORIZONTAL, true);
		columnView.setPreferredHeight(35);
		rowView = new Rule(Rule.VERTICAL, true);
		rowView.setPreferredWidth(35);
		scrollableImage.setColumnHeaderView(columnView);
		scrollableImage.setRowHeaderView(rowView);
		JPanel buttonCorner = new JPanel(); // use FlowLayout
		isMetric = new JToggleButton("cm", true);
		isMetric.setFont(new Font("SansSerif", Font.PLAIN, 11));
		isMetric.setMargin(new Insets(2, 2, 2, 2));
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
		htmlPreview = new PreviewPanel(imagePanel);
		scrollableWeb = new JScrollPane();
		scrollableWeb.add(htmlPreview);
		scrollableWeb.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		scrollableWeb.setViewportView(htmlPreview);

		// tab stuff
		addTab("Image", scrollableImage);
		addTab("HTML", scrollableText);
		addTab("Preview", scrollableWeb);
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!getTitleAt(getSelectedIndex()).equals("Image")) {
					frame.getMouse_position().setText("");
				}
				if (getTitleAt(getSelectedIndex()).equals("Preview")) {
					htmlPreview.setPanel(imagePanel);
				}
				if (getTitleAt(getSelectedIndex()).equals("HTML")) {
					Image img = imagePanel.getImg();
					if (img != null) {
						String htmlcode = imagePanel.getDoc().toString();
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
					frame.getClip_button().setVisible(true);
				} else {
					frame.getClip_button().setVisible(false);
				}
			}
		});
		KeyStroke keyTabForward = KeyStroke.getKeyStroke("ctrl TAB");
		KeyStroke keyTabBackward = KeyStroke.getKeyStroke("ctrl shift TAB");
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(
				getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.remove(keyTabForward);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(
				getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.remove(keyTabBackward);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
		Action tabForward = new AbstractAction("TabForward") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setSelectedIndex((getSelectedIndex() + 1) % 3);
				getSelectedComponent().requestFocusInWindow();
			}
		};
		Action tabBackward = new AbstractAction("TabBackward") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (getSelectedIndex() == 0) {
					setSelectedIndex(2);
				} else {
					setSelectedIndex(getSelectedIndex() - 1);
				}
				getSelectedComponent().requestFocusInWindow();
			}
		};
		getActionMap().put("tabForward", tabForward);
		getActionMap().put("tabBackward", tabBackward);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyTabForward, "tabForward");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyTabBackward, "tabBackward");
		
		imagePanel.setImagePath(file.getAbsolutePath());
		imagePanel.setImg((Image) ImageIO.read(file));
		imagePanel.setPreferredSize(new Dimension(imagePanel.getWidth(), imagePanel.getHeight()));
		rowView.setPreferredHeight(imagePanel.getImg().getHeight(null));
		rowView.repaint();
		columnView.setPreferredWidth(imagePanel.getImg().getWidth(null));
		columnView.repaint();
		imagePanel.revalidate();
		html.setText("");
		setListeners();
	}

	/**
	 * 
	 * @return imagePanel of project
	 */
	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	/**
	 * intialize listeners
	 */
	private void setListeners() {
		imagePanel.addMouseListener(ImageMap.getMc());
		imagePanel.addMouseMotionListener(ImageMap.getMmc());
		// still missing most
	}

	/**
	 * handle actions taken in project
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((Component) e.getSource()).getName();
		switch (name) {
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
		}
	}
	
	/**
	 * Getter for name of project
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return whether the ruler is set to metric or not
	 * 
	 * @return isMetric
	 */
	public JToggleButton getMetric() {
		return isMetric;
	}

	/**
	 * Getter for HTML-Panel of project
	 * 
	 * @return HTML Panel
	 */
	public HTMLPanel getHTMLPanel() {
		return html;
	}
	
	/**
	 * return x- and y-Rulers
	 * 
	 * @return Rulers
	 */
	public Rule[] getRulers() {
		Rule[] r = new Rule[2];
		r[0] = rowView;
		r[1] = columnView;
		return r;
	}

}
