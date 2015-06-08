/**
 * 
 */
package imagemap.panels;

import java.awt.Color;
import java.util.regex.*;

import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * @author Niklas Miroll
 *
 */
public class HTMLPanel extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SimpleAttributeSet blue = new SimpleAttributeSet();
	private static final SimpleAttributeSet red = new SimpleAttributeSet();
	private static final SimpleAttributeSet gray = new SimpleAttributeSet();
	private static final SimpleAttributeSet black = new SimpleAttributeSet();
	private int offset;

	/**
	 * Constructor
	 */
	public HTMLPanel(DefaultStyledDocument doc) {
		super(doc);
		StyleConstants.setForeground(blue, Color.blue);
		StyleConstants.setForeground(red, Color.red);
		StyleConstants.setForeground(gray, Color.darkGray);
		StyleConstants.setForeground(black, Color.black);
		setEditable(false);
	}
	
	/**
	 * 
	 * @return line numbers to be shown
	 */
	public String getLineNumbers() {
		int totalCharacters = this.getText().length();
		int lineCount = (totalCharacters == 0) ? 1 : 0;
		try {
			int offset = totalCharacters;
			while (offset > 0) {
				offset = Utilities.getRowStart(this, offset) - 1;
				lineCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String numbers = "";
		for (int i = 1; i < lineCount + 1; i++) {
			numbers += i + " \n";
		}
		return numbers;
	}

	/**
	 * overridden setText to highlight code
	 */
	@Override
	public void setText(String arg0) {
		super.setText(arg0);
		offset = 0;
		this.getStyledDocument().setCharacterAttributes(0, this.getText().length(), black, false);
		while (offset < getText().length() - 9) {
			highlightTags(arg0);
			highlightAttributeNames(arg0);
			highlightAttributeValues(arg0);
		}
	}

	/**
	 * highlight every occurence of an html tag
	 * 
	 * @param arg0
	 */
	private void highlightTags(String arg0) {
		Pattern pattern = Pattern.compile("<[a-z]+|</[a-z]+>|>");
		Matcher m = pattern.matcher(arg0);
		boolean tmp = m.find(offset);
		if (tmp) this.getStyledDocument().setCharacterAttributes(m.start(0), m.end(0), blue, false);
	}

	/**
	 * highlight every occurence of an attribute name
	 * 
	 * @param arg0
	 */
	private void highlightAttributeNames(String arg0) {
		Pattern pattern = Pattern.compile(" [a-z]+=\"");
		Matcher m = pattern.matcher(arg0);
		boolean tmp = m.find(offset);
		if (tmp) this.getStyledDocument().setCharacterAttributes(m.start(0) + 1, m.end(0) - 2, red, false);
	}

	/**
	 * highlight every occurence of an attribute value
	 * 
	 * @param arg0
	 */
	private void highlightAttributeValues(String arg0) {
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		Matcher m = pattern.matcher(arg0);
		boolean tmp = m.find(offset);
		if (tmp) this.getStyledDocument().setCharacterAttributes(m.start(0), m.end(0), gray, false);
		offset++;
	}

}
