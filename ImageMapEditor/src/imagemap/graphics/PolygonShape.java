/**
 * 
 */
package imagemap.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Vector;

/**
 * @author Niklas Miroll
 *
 */
public class PolygonShape extends AbstractShape {
	private Polygon poly;

	/**
	 * Constructor for Polygon with first point of Polygon
	 * 
	 * @param p
	 */
	public PolygonShape(Point p) {
		type = TYPE_POLY;
		poly = new Polygon();
		poly.addPoint((int) p.getX(), (int) p.getY());
		id = hashCode();
	} // end of Constructor for Polygon Shape
	
	/**
	 * Point to be added to polygon if shape is a polygon
	 * 
	 * @param p
	 */
	public void addPolyPoint(Point p) {
		poly.addPoint((int) p.getX(), (int) p.getY());
	} // end of addPolyPoint()
	
	/**
	 * @see imagemap.graphics.AbstractShape#matches(imagemap.graphics.AbstractShape)
	 */
	@Override
	public boolean matches(AbstractShape tmp) {
		if (this.getType() != tmp.getType()) {
			return false;
		}
		PolygonShape tmp2 = (PolygonShape) tmp;
		for (int i = 0; i < poly.npoints; i++) {
			if (poly.xpoints[i] != tmp2.getPolygon().xpoints[i] || poly.ypoints[i] != tmp2.getPolygon().ypoints[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#contains(int, int)
	 */
	@Override
	public boolean contains(int x, int y) {
		if (poly.contains(new Point(x, y))) {
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
		Vector<Rectangle> tmp_poly = new Vector<Rectangle>();
		for (int i = 0; i < poly.npoints; i++) {
			tmp_poly.add(new Rectangle(poly.xpoints[i] - 3, poly.ypoints[i] - 3, 6, 6));
		}
		for (Rectangle rectangle : tmp_poly) {
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
		
		Polygon p = poly;
		// draw polygon with lineColor and white dotted line
		g2.setColor(lineColor);
		g2.drawPolygon(p);
		g2.setStroke(bs);
		g2.setColor(Color.white);
		g2.drawPolygon(p);
		g2.setColor(neutralColor);
		g2.fillPolygon(p);

		// draw boxes at corners of polygon
		g2.setStroke(stroke2);
		g2.setColor(lineColor);
		for (int i = 0; i < poly.npoints; i++) {
			g2.fillRect(poly.xpoints[i] - 2, poly.ypoints[i] - 2, 4, 4);
		}
		g2.setColor(Color.white);
		for (int i = 0; i < poly.npoints; i++) {
			g2.drawRect(poly.xpoints[i] - 2, poly.ypoints[i] - 2, 4, 4);
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#move(int, int)
	 */
	@Override
	public void move(int xdir, int ydir) {
		for (int i = 0; i < poly.npoints; i++) {
			poly.xpoints[i] = poly.xpoints[i] + xdir;
			poly.ypoints[i] = poly.ypoints[i] + ydir;
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#movePoint(java.awt.Point, int, int)
	 */
	@Override
	public void movePoint(Point p, int xdir, int ydir) {
		for (int i = 0; i < poly.npoints; i++) {
			Rectangle tmp_rect = new Rectangle(poly.xpoints[i] - 3, poly.ypoints[i] - 3, 6, 6);
			if (tmp_rect.contains(p)) {
				poly.xpoints[i] = poly.xpoints[i] + xdir;
				poly.ypoints[i] = poly.ypoints[i] + ydir;
				return;
			}
		}
	}

	/**
	 * @see imagemap.graphics.AbstractShape#clone()
	 */
	@Override
	public PolygonShape clone() {
		PolygonShape tmp = new PolygonShape(new Point(poly.xpoints[0], poly.ypoints[0]));
		if (poly.npoints > 1) {
			for (int i = 1; i < poly.npoints; i++) {
				tmp.addPolyPoint(new Point(poly.xpoints[i], poly.ypoints[i]));
			}
		}
		return tmp;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#toString()
	 */
	@Override
	public String toString() {
		String s = "Polygon: [";
		for (int i = 0; i < poly.npoints; i++) {
			s += "(x: " + poly.xpoints[i] + ", y: " + poly.ypoints[i] + ")";
		}
		s += "]";
		return s;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getSpecificHTML()
	 */
	@Override
	public String getSpecificHTML() {
		String html = "poly\" coords=\"";
		int[] xArray = poly.xpoints;
		int[] yArray = poly.ypoints;
		for (int i = 0; i < xArray.length; i++) {
			html += xArray[i] + "," + yArray[i];
			if (i != (xArray.length - 1)) {
				html += ",";
			}
		}
		return html + "\" ";
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getResizeCursor(java.awt.Point)
	 */
	@Override
	public int getResizeCursor(Point p) {
		return Cursor.HAND_CURSOR;
	}
	
	/**
	 * 
	 * @return polygon if it's a polygon
	 */
	public Polygon getPolygon() {
		int[] xArray = poly.xpoints;
		int[] yArray = poly.ypoints;
		Polygon tmp = new Polygon();
		for (int i = 0; i < xArray.length; i++) {
			tmp.addPoint(xArray[i], yArray[i]);
		}
		return tmp;
	} // end of getPolygon

	/**
	 * 
	 * @return x min point of polygon, for orientation
	 */
	public Point getXMinPolyPoint() {
		if (type == TYPE_POLY) {
			int[] xArray = poly.xpoints;
			int[] yArray = poly.ypoints;
			int minX = 0;
			int minY = 0;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					minX = xArray[0];
					minY = yArray[0];
				} else {
					if (xArray[i] < minX) {
						minX = xArray[i];
						minY = yArray[i];
					}
				}
			}
			return new Point(minX, minY);
		} else {
			return null;
		}
	} // end of getXMinPolyPoint()

	/**
	 * 
	 * @return x max point of polygon, for orientation
	 */
	public Point getXMaxPolyPoint() {
		if (type == TYPE_POLY) {
			int[] xArray = poly.xpoints;
			int[] yArray = poly.ypoints;
			int maxX = 0;
			int maxY = 0;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					maxX = xArray[0];
					maxY = yArray[0];
				} else {
					if (xArray[i] > maxX) {
						maxX = xArray[i];
						maxY = yArray[i];
					}
				}
			}
			return new Point(maxX, maxY);
		} else {
			return null;
		}
	} // end of getXMaxPolyPoint()

	/**
	 * 
	 * @return y min point of polygon, for orientation
	 */
	public Point getYMinPolyPoint() {
		if (type == TYPE_POLY) {
			int[] xArray = poly.xpoints;
			int[] yArray = poly.ypoints;
			int minX = 0;
			int minY = 0;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					minX = xArray[0];
					minY = yArray[0];
				} else {
					if (yArray[i] < minY) {
						minX = xArray[i];
						minY = yArray[i];
					}
				}
			}
			return new Point(minX, minY);
		} else {
			return null;
		}
	} // end of getYMinPolyPoint

	/**
	 * 
	 * @return y max point of polygon, for orientation
	 */
	public Point getYMaxPolyPoint() {
		if (type == TYPE_POLY) {
			int[] xArray = poly.xpoints;
			int[] yArray = poly.ypoints;
			int maxX = 0;
			int maxY = 0;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					maxX = xArray[0];
					maxY = yArray[0];
				} else {
					if (yArray[i] > maxY) {
						maxX = xArray[i];
						maxY = yArray[i];
					}
				}
			}
			return new Point(maxX, maxY);
		} else {
			return null;
		}
	} // end of getYMaxPolyPoint()

	/**
	 * @see imagemap.graphics.AbstractShape#getCoords()
	 */
	@Override
	public String getCoords() {
		String html = "";
		int[] xArray = poly.xpoints;
		int[] yArray = poly.ypoints;
		for (int i = 0; i < xArray.length; i++) {
			if (poly.xpoints[i] != 0 || poly.ypoints[i] != 0) {
				html += xArray[i] + "," + yArray[i];
				if (i != (xArray.length - 1)) {
					html += ",";
				}
			}			
		}
		return html;
	}

	/**
	 * @see imagemap.graphics.AbstractShape#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return "poly";
	}
	
	/**
	 * @see imagemap.graphics.AbstractShape#scaleShape(double)
	 */
	@Override
	public void scale(float scale) {
		for (int i = 0; i < poly.xpoints.length; i++) {
			poly.xpoints[i] = Math.round(poly.xpoints[i] * scale);
			poly.ypoints[i] = Math.round(poly.ypoints[i] * scale);
		}
	}

}
