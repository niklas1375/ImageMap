package imagemap.graphics;

import java.awt.*;

public abstract class AbstractShape {
	public final static int TYPE_RECT = 0;
	public final static int TYPE_CIRC = 1;
	public final static int TYPE_POLY = 2;
	protected final static Color neutralColor = new Color(105, 105, 105, 128);
	protected int type;
	protected Color lineColor = Color.black;
	protected String alt = "";
	protected String href = "";
	protected String tooltip = "";
	protected int id;

	/**
	 * 
	 * @param s
	 * @return whether passed Shape matches this
	 */
	public abstract boolean matches(AbstractShape tmp);

	/**
	 * 
	 * @param x
	 * @param y
	 * @return whether Shape contains given coordinates
	 */
	public abstract boolean contains(int x, int y);

	/**
	 * 
	 * @param p
	 * @return whether any corner contains the given point
	 */
	public abstract boolean cornerContains(Point p);

	/**
	 * draw method for drawing the Shapes
	 * 
	 * @param g2
	 */
	public abstract void draw(Graphics2D g2);

	/**
	 * move this by given directions
	 * 
	 * @param xdir
	 * @param ydir
	 */
	public abstract void move(int xdir, int ydir);

	/**
	 * move matching corner by given directions
	 * 
	 * @param p
	 * @param xdir
	 * @param ydir
	 */
	public abstract void movePoint(Point p, int xdir, int ydir);

	/**
	 * override of clone() to ensure deep copies
	 */
	@Override
	public abstract AbstractShape clone();

	/**
	 * override of toString() for debug purposes via console
	 */
	@Override
	public abstract String toString();

	/**
	 * 
	 * @return the shape specific part of the HTML-String
	 */
	protected abstract String getSpecificHTML();

	/**
	 * 
	 * @param p
	 * @return cursor identificator to be used for resizing
	 */
	public abstract int getResizeCursor(Point p);

	/**
	 * 
	 * @return coordinates to put in html
	 */
	public abstract String getCoords();

	/**
	 * scale shape by a given factor
	 * 
	 * @param scale
	 *            to be used as factor
	 */
	public abstract void scale(float scale);

	/**
	 * 
	 * @return name of type
	 */
	public abstract String getTypeName();

	// end of abstract methods

	/**
	 * 
	 * @return HTML for shape, invoking the shape specific html getter
	 */
	public String getHTML() {
		String html = "<area shape=\"";
		html += getSpecificHTML();
		if (href != "") {
			html += "href=\"" + href + "\"";
		} else {
			html += "nohref";
		}
		if (alt != "") {
			html += " alt=\"" + alt + "\"";
		}
		if (tooltip != "") {
			html += " title=\"" + tooltip + "\"";
		}
		html += ">";
		return html;
	}

	/**
	 * 
	 * @return alternative text for shape in imagemap
	 */
	public String getAlt() {
		return alt;
	} // end of getAlt()

	/**
	 * alternative text for shape in image
	 * 
	 * @param alt
	 */
	public void setAlt(String alt) {
		if (alt == null) {
			this.alt = "";
		} else {
			this.alt = alt;
		}
	} // end of setAlt()

	/**
	 * 
	 * @return link for shape in imagemap
	 */
	public String getHref() {
		return href;
	} // end of getHref()

	/**
	 * link for shape in imagemap
	 * 
	 * @param href
	 */
	public void setHref(String href) {
		if (href == null) {
			this.href = "";
		} else {
			this.href = href;
		}
	} // end of setHref()

	/**
	 * 
	 * @return tooltip for shape in imagemap
	 */
	public String getTooltip() {
		return tooltip;
	} // end of getTooltip()

	/**
	 * set tooltip for shape in imagemap
	 * 
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		if (tooltip == null) {
			this.tooltip = "";
		} else {
			this.tooltip = tooltip;
		}
	} // end of setTooltip(String tooltip)

	/**
	 * 
	 * @return type of shape in imagemap
	 */
	public int getType() {
		return type;
	} // end of getType()

	/**
	 * 
	 * @param color
	 *            color to be set as lineColor
	 */
	public void setColor(Color color) {
		this.lineColor = color;
	} // end of setColor()

	/**
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	} // end of getId()

}
