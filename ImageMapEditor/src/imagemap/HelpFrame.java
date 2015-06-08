/**
 * 
 */
package imagemap;

import java.awt.*;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Niklas Miroll
 *
 */
public class HelpFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Border margin = new EmptyBorder(5, 5, 5, 5);
	private JTabbedPane tabs;
	private JLabel aboutMe;

	/**
	 * private constructor to implement Singleton
	 */
	private HelpFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createGUI();
			}
		});
	}

	/**
	 * creating GUI once
	 */
	private void createGUI() {
		// initializations
		JPanel general = new JPanel(new GridLayout(3, 1, 0, 5));
		JTextArea rect = new JTextArea();
		JTextArea circ = new JTextArea();
		JTextArea poly = new JTextArea();
		JPanel editing = new JPanel(new GridLayout(3, 1, 0, 5));
		JTextArea res = new JTextArea();
		JTextArea mov = new JTextArea();
		JTextArea bf = new JTextArea();
		JPanel extras = new JPanel(new GridLayout(2, 1, 0, 5));
		JTextArea scale = new JTextArea();
		JTextArea imp = new JTextArea();
		JPanel about = new JPanel(new BorderLayout());
		aboutMe = new JLabel();
		tabs = new JTabbedPane();

		// assembling
		String r = "To draw a rectangle click on beginning edge and drag to opposite edge of rectangle.\nReleasing the mouse will finish it.";
		setUpTextArea(rect, "Rectangle", r);
		String c = "To draw a circle click on the center of your desired circle and drag the radius.\nReleasing the mouse will finish it.";
		setUpTextArea(circ, "Circle", c);
		String p = "TO draw a polygon click every corner your polygon should have in the intended order.\nPressing escape or using a double click to set a point will finish it.";
		setUpTextArea(poly, "Polygon", p);
		general.add(rect);
		general.add(circ);
		general.add(poly);
		tabs.add(general, "General");
		general.setBorder(new EmptyBorder(5, 5, 5, 5));
		general.setBackground(new Color(232, 232, 232));

		String m = "To move a shape switch to mouse mode and move the cursor inside of a shape.\nDrag to new position and release to finish.";
		setUpTextArea(mov, "Moving", m);
		String re = "To resize a shape siwtch to mouse mode and move the cursor to the marked corner you want to change.\nDrag to new position and release to finish.";
		setUpTextArea(res, "Resizing", re);
		String b = "Undo: Ctrl + Z or via menubar.\nRedo: Ctrl + Y or via menubar";
		setUpTextArea(bf, "Undo/Redo", b);
		editing.add(mov);
		editing.add(res);
		editing.add(bf);
		tabs.add(editing, "Editing");
		editing.setBorder(new EmptyBorder(5, 5, 5, 5));
		editing.setBackground(new Color(232, 232, 232));

		String s = "To scale your picture and shapes access the scaling tool via the menubar.\nScaling happens percent based after you confirm the dialog.";
		setUpTextArea(scale, "Scaling", s);
		String i = "To import shapes from HTML-code access the import tool via the menubar or Ctrl + I.\nImports are being made after you confirm the dialog.";
		setUpTextArea(imp, "Import from HTML", i);
		extras.add(scale);
		extras.add(imp);
		tabs.add(extras, "Extras");
		extras.setBorder(new EmptyBorder(5, 5, 5, 5));
		extras.setBackground(new Color(232, 232, 232));
		
		about.add(aboutMe);
		updateAbout();
		aboutMe.setHorizontalAlignment(JLabel.CENTER);
		tabs.add(about, "About");

		// finalizing
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.add(tabs);
		this.setTitle("Help");
		this.setSize((int) d.getWidth() / 3, (int) d.getHeight() / 3);
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
	}

	/**
	 * 
	 * @param target
	 * @param title
	 * @param help
	 */
	private void setUpTextArea(JTextArea target, String title, String help) {
		String r = help;
		target.setText(r);
		target.setBorder(BorderFactory.createTitledBorder(title));
		Border border = target.getBorder();
		target.setBorder(new CompoundBorder(border, margin));
		target.setBackground(new Color(232, 232, 232));
		target.setEditable(false);
	}
	
	/**
	 * show about and set text of it
	 */
	public void doAbout() {
		updateAbout();
		tabs.setSelectedIndex(3);
	}
	
	/**
	 * update about text
	 */
	private void updateAbout() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String period = "" + 2015;
		if (year > 2015) {
			period += " - " + year;
		}
		aboutMe.setText("\u00A9 Copyright " + period + " by Niklas Miroll");
	}

	/**
	 * 
	 * @return HelpFrame Instance
	 */
	public static HelpFrame getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * 
	 * @author Niklas Miroll
	 *
	 */
	private static final class InstanceHolder {
		static final HelpFrame INSTANCE = new HelpFrame();
	}

}
