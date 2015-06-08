/**
 * 
 */
package imagemap.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * @author Niklas Miroll
 *
 */
public class CircleShape extends AbstractShape {
	private Point circ_cent;
	private int circ_r;

	/**
	 * Constructor for circle shapes
	 * 
	 * @param m
	 * @param r
	 */
	public CircleShape(Point p, int r) {
		type = TYPE_CIRC;
		circ_cent = p;
		circ_r = r;
		hashCode();
	}

	/**
	 * @see imagemap.graphics.AbstractShape#matches(imagemap.graphics.AbstractShape)
	 */
	@Override
	public boolean matches(AbstractShape tmp) {
		if (this.getType() != tmp.getType()) {
			return false;
		}
		CircleShape tmp2 = (CircleShape) tmp;
		if (circ_cent.getX() == tmp2.getCenter().getX() && circ_cent.getY() == tmp2.getCenter().getY()
				&& circ_r == tmp2.getRadius()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		int x_tmp = (int) circ_cent.getX();
		int y_tmp = (int) circ_cent.getY();
		double dist = Math.sqrt((Math.pow((x - x_tmp), 2) + Math.pow((y - y_tmp), 2)));
		if (dist < circ_r) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#cornerContains(java.awt.Point)
	 */
	@Override
	public boolean cornerContains(Point p) {
		Rectangle tmp_circ_rect = new Rectangle(circ_cent.x + circ_r - 3, circ_cent.y - 3, 6, 6);
		if (tmp_circ_rect.contains(p)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g2) {
		float dashArray[] = { 4.0f };
		Stroke stroke2 = g2.getStroke();
		BasicStroke bs = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, dashArray, 0.0f);

		int xc = (int) circ_cent.getX();
		int yc = (int) circ_cent.getY();
		int r = circ_r;

		// draw circle with lineColor and white dotted line
		g2.setColor(lineColor);
		g2.drawOval(xc - r, yc - r, r * 2, r * 2);
		g2.setStroke(bs);
		g2.setColor(Color.white);
		g2.drawOval(xc - r, yc - r, r * 2, r * 2);
		g2.setColor(neutralColor);
		g2.fillOval(xc - r, yc - r, r * 2, r * 2);

		// draw box for resize
		g2.setStroke(stroke2);
		g2.setColor(lineColor);
		g2.fillRect(xc + r - 2, yc - 2, 4, 4);
		g2.setColor(Color.white);
		g2.drawRect(xc + r - 2, yc - 2, 4, 4);
	}

	/**
	 * @see imagemap.graphics.AbstractShape#move(int, int)
	 */
	@Override
	public void move(int xdir, int ydir) {
		circ_cent.setLocation((int) circ_cent.getX() + xdir, (int) circ_cent.getY() + ydir);
	}

	/**
	 * @see imagemap.graphics.AbstractShape#movePoint(java.awt.Point, int, int)
	 */
	@Override
	public void movePoint(Point p, int xdir, int ydir) {
		double xcomponent = p.getX() - circ_cent.getX() + xdir;
		double ycomponent = p.getY() - circ_cent.getY() + ydir;
		circ_r = (int) Math.sqrt((Math.pow(xcomponent, 2) + Math.pow(ycomponent, 2)));
		if (circ_r < 4) {
			circ_r = 4;
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#clone()
	 */
	@Override
	public CircleShape clone() {
		return new CircleShape(new Point((int) circ_cent.getX(), (int) circ_cent.getY()), circ_r);
	}

	/**
	 * @see imagemap.graphics.AbstractShape#toString()
	 */
	@Override
	public String toString() {
		String s = "Circle: [center: " + "(x: " + circ_cent.getX() + "; y: " + circ_cent.getY();
		s += "), radius: " + circ_r + "]";
		return s;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getSpecificHTML()
	 */
	@Override
	public String getSpecificHTML() {
		return "circle\" coords=\"" + (int) circ_cent.getX() + "," + (int) circ_cent.getY() + "," + circ_r + "\" ";
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getResizeCursor(java.awt.Point)
	 */
	@Override
	public int getResizeCursor(Point p) {
		return Cursor.E_RESIZE_CURSOR;
	}

	/**
	 * 
	 * @return radius of circle if it's a circle
	 */
	public int getRadius() {
		int radius = 0 + circ_r;
		return radius;
	} // end of getRadius()

	/**
	 * 
	 * @return center of circle if it's a circle
	 */
	public Point getCenter() {
		return (Point) circ_cent.clone();
	} // end of getCenter()

	/**
	 * @see imagemap.graphics.AbstractShape#getCoords()
	 */
	@Override
	public String getCoords() {
		return circ_cent.x + "," + circ_cent.y + "," + circ_r;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "circle";
	}

	/**
	 * @see imagemap.graphics.AbstractShape#scaleShape(double)
	 */
	@Override
	public void scale(float scale) {
		circ_cent = new Point(Math.round(circ_cent.x * scale), Math.round(circ_cent.y * scale));
		circ_r = Math.round(circ_r * scale);
	}

}
