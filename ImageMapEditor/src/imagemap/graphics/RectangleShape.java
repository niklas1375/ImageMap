/**
 * 
 */
package imagemap.graphics;

import java.awt.*;
import java.util.Vector;

/**
 * @author Niklas Miroll
 *
 */
public class RectangleShape extends AbstractShape {
	private Rectangle rect;

	/**
	 * Constructor for rectangle shapes
	 * 
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 */
	public RectangleShape(int x, int y, int x1, int y1) {
		type = TYPE_RECT;
		if (x > x1) {
			int tmp = x;
			x = x1;
			x1 = tmp;
		}
		if (y > y1) {
			int tmp = y;
			y = y1;
			y1 = tmp;
		}
		rect = new Rectangle(x, y, x1 - x, y1 - y);
		id = hashCode();
	} // end of Constructor for Rectangle Shape

	/**
	 * @see imagemap.graphics.AbstractShape#matches(imagemap.Shape)
	 */
	@Override
	public boolean matches(AbstractShape tmp) {
		if (this.getType() != tmp.getType()) {
			return false;
		}
		RectangleShape tmp2 = (RectangleShape) tmp;
		if (rect.x == tmp2.getRectangle().x && rect.y == tmp2.getRectangle().y
				&& rect.width == tmp2.getRectangle().width && rect.height == tmp2.getRectangle().height) {
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
		if (rect.contains(new Point(x, y))) {
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
		Vector<Rectangle> tmp_rect = new Vector<Rectangle>();
		tmp_rect.add(new Rectangle(rect.x - 3, rect.y - 3, 6, 6));
		tmp_rect.add(new Rectangle(rect.x + rect.width - 3, rect.y - 2, 6, 6));
		tmp_rect.add(new Rectangle(rect.x - 3, rect.y + rect.height - 2, 6, 6));
		tmp_rect.add(new Rectangle(rect.x + rect.width - 3, rect.y + rect.height - 3, 6, 6));
		for (Rectangle rectangle : tmp_rect) {
			if (rectangle.contains(p)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g2) {
		float dashArray[] = { 4.0f };
		Stroke stroke2 = g2.getStroke();
		BasicStroke bs = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, dashArray, 0.0f);

		int xr = (int) rect.getX();
		int yr = (int) rect.getY();
		int wr = (int) rect.getWidth();
		int hr = (int) rect.getHeight();

		// draw rectangle with lineColor and white dotted line
		g2.setColor(lineColor);
		g2.drawRect(xr, yr, wr, hr);
		g2.setStroke(bs);
		g2.setColor(Color.white);
		g2.drawRect(xr, yr, wr, hr);
		g2.setColor(neutralColor);
		g2.fillRect(xr, yr, wr, hr);

		// draw boxes at corners of rectangle
		g2.setStroke(stroke2);
		g2.setColor(lineColor);
		g2.fillRect(xr - 2, yr - 2, 4, 4);
		g2.fillRect(xr + wr - 2, yr - 2, 4, 4);
		g2.fillRect(xr + wr - 2, yr + hr - 2, 4, 4);
		g2.fillRect(xr - 2, yr + hr - 2, 4, 4);
		g2.setColor(Color.white);
		g2.drawRect(xr - 2, yr - 2, 4, 4);
		g2.drawRect(xr + wr - 2, yr - 2, 4, 4);
		g2.drawRect(xr + wr - 2, yr + hr - 2, 4, 4);
		g2.drawRect(xr - 2, yr + hr - 2, 4, 4);
	}

	/**
	 * @see imagemap.graphics.AbstractShape#move(int, int)
	 */
	@Override
	public void move(int xdir, int ydir) {
		rect.x = rect.x + xdir;
		rect.y = rect.y + ydir;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#movePoint(java.awt.Point, int, int)
	 */
	@Override
	public void movePoint(Point p, int xdir, int ydir) {
		Rectangle upperLeft = new Rectangle(rect.x - 3, rect.y - 3, 6, 6);
		Rectangle upperRight = new Rectangle(rect.x + rect.width - 3, rect.y - 3, 6, 6);
		Rectangle lowerLeft = new Rectangle(rect.x - 3, rect.y + rect.height - 3, 6, 6);
		Rectangle lowerRight = new Rectangle(rect.x + rect.width - 3, rect.y + rect.height - 3, 6, 6);
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		if (upperLeft.contains(p)) {
			x = rect.x + xdir;
			y = rect.y + ydir;
			w = rect.width - xdir;
			h = rect.height - ydir;
		} else if (upperRight.contains(p)) {
			y = rect.y + ydir;
			w = rect.width + xdir;
			h = rect.height - ydir;
		} else if (lowerLeft.contains(p)) {
			x = rect.x += xdir;
			w = rect.width -= xdir;
			h = rect.height += ydir;
		} else if (lowerRight.contains(p)) {
			w = rect.width += xdir;
			h = rect.height += ydir;
		}
		if (w < 4) {
			w = 4;
		}
		if (h < 4) {
			h = 4;
		}
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		rect.x = x;
		rect.y = y;
		rect.width = w;
		rect.height = h;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#clone()
	 */
	@Override
	public RectangleShape clone() {
		return new RectangleShape(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
	}

	/**
	 * @see imagemap.graphics.AbstractShape#toString()
	 */
	@Override
	public String toString() {
		String s = "Rectangle: [x1: " + rect.x + " y1: " + rect.y + " x2: " + (rect.x + rect.width) + " y2: ";
		s += (rect.x + rect.height) + "]";
		return s;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getSpecificHTML()
	 */
	@Override
	public String getSpecificHTML() {
		String html = "rect\" coords=\"" + (int) rect.getMinX() + "," + (int) rect.getMinY() + ",";
		html += (int) rect.getMaxX() + "," + (int) rect.getMaxY() + "\" ";
		return html;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getResizeCursor(java.awt.Point)
	 */
	@Override
	public int getResizeCursor(Point p) {
		Rectangle upperLeft = new Rectangle(rect.x - 3, rect.y - 3, 6, 6);
		Rectangle upperRight = new Rectangle(rect.x + rect.width - 3, rect.y - 3, 6, 6);
		Rectangle lowerLeft = new Rectangle(rect.x - 3, rect.y + rect.height - 3, 6, 6);
		Rectangle lowerRight = new Rectangle(rect.x + rect.width - 3, rect.y + rect.height - 3, 6, 6);
		if (upperLeft.contains(p)) {
			return Cursor.NW_RESIZE_CURSOR;
		} else if (upperRight.contains(p)) {
			return Cursor.NE_RESIZE_CURSOR;
		} else if (lowerLeft.contains(p)) {
			return Cursor.SW_RESIZE_CURSOR;
		} else if (lowerRight.contains(p)) {
			return Cursor.SE_RESIZE_CURSOR;
		}
		return Cursor.DEFAULT_CURSOR;
	}

	/**
	 * 
	 * @return this's rectangle
	 */
	public Rectangle getRectangle() {
		return (Rectangle) rect.clone();
	} // end of getRectangle()

	/**
	 * @see imagemap.graphics.AbstractShape#getCoords()
	 */
	@Override
	public String getCoords() {
		return rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "rect";
	}
	
	/**
	 * @see imagemap.graphics.AbstractShape#scaleShape(double)
	 */
	@Override
	public void scale(float scale) {
		rect.x = Math.round(rect.x * scale);
		rect.y = Math.round(rect.y * scale);
		rect.width = Math.round(rect.width * scale);
		rect.height = Math.round(rect.height * scale);
	}

}
