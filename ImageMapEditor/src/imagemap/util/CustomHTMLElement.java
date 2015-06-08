/**
 * 
 */
package imagemap.util;

import imagemap.graphics.*;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Niklas Miroll
 *
 */
public class CustomHTMLElement {
	private String tagName;
	private boolean empty;
	private HashMap<Integer, CustomHTMLElement> subElements = new HashMap<Integer, CustomHTMLElement>();
	private HashMap<String, String> attributeMap = new HashMap<String, String>();

	/**
	 * 
	 */
	public CustomHTMLElement(String tagName, boolean empty) {
		this.tagName = tagName;
		this.empty = empty;
	}

	/**
	 * add Attribute to Element
	 * 
	 * @param attributeName
	 * @param value
	 * @return this to allow method chaining
	 */
	public CustomHTMLElement addAttribute(String attributeName, String value) {
		attributeMap.put(attributeName, value);
		return this;
	}

	/**
	 * edit attributes of element
	 * 
	 * @param attribute
	 * @param value
	 * @return this to allow method chaining
	 */
	public CustomHTMLElement editAttributeValue(String attribute, String value) {
		attributeMap.put(attribute, value);
		return this;
	}

	/**
	 * 
	 * @param attribute
	 * @return value of given attribute, null if non-existent
	 */
	public String getAttributeValue(String attribute) {
		try {
			return attributeMap.get(attribute);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param element
	 * @return this to allow method chaining
	 */
	public CustomHTMLElement addSubElement(CustomHTMLElement element, int hash) {
		subElements.put(hash, element);
		return this;
	}

	/**
	 * 
	 * @return this to allow method chaining
	 */
	public CustomHTMLElement removeSubElement(int id) {
		subElements.remove(id);
		return this;
	}

	/**
	 * to String method
	 */
	@Override
	public String toString() {
		String html = "<" + tagName + " ";
		for (Entry<String, String> entry : attributeMap.entrySet()) {
			html += entry.getKey() + "=\"";
			html += entry.getKey().equals("src") ? "file:///" : "";
			html += entry.getValue() + "\" ";
		}
		html += ">\n";
		for (Entry<Integer, CustomHTMLElement> entry : subElements.entrySet()) {
			html += "\t" + entry.getValue().toString();
		}
		if (!empty) {
			html += "</" + tagName + ">";
		}
		return html;
	}

	/**
	 * 
	 * @return the subElements
	 */
	public HashMap<Integer, CustomHTMLElement> getSubElements() {
		return subElements;
	}

	/**
	 * clear subelement hashmap
	 */
	public void clearSubs() {
		subElements = new HashMap<Integer, CustomHTMLElement>();
	}

	/**
	 * update coords after moving/resizing
	 * 
	 * @param startShape
	 */
	public void updateCoords(AbstractShape startShape) {
		attributeMap.put("coords", startShape.getCoords());
	}

	/**
	 * 
	 * @return corresponding AbstractShape
	 */
	public AbstractShape convertToShape() {
		String type = attributeMap.get("shape");
		AbstractShape shape = null;
		switch (type) {
		case "rect":
			String[] tmpR = attributeMap.get("coords").split(",");
			int x = Integer.parseInt(tmpR[0]);
			int y = Integer.parseInt(tmpR[1]);
			int x1 = x + Integer.parseInt(tmpR[2]);
			int y1 = y + Integer.parseInt(tmpR[3]);
			shape = new RectangleShape(x, y, x1, y1);
			break;

		case "circle":
			String[] tmpC = attributeMap.get("coords").split(",");
			Point p = new Point(Integer.parseInt(tmpC[0]), Integer.parseInt(tmpC[1]));
			shape = new CircleShape(p, Integer.parseInt(tmpC[2]));
			break;

		case "poly":
			String[] tmpP = attributeMap.get("coords").split(",");
			shape = new PolygonShape(new Point(Integer.parseInt(tmpP[0]), Integer.parseInt(tmpP[1])));
			if (tmpP.length != 2) {
				for (int i = 1; i < (tmpP.length / 2); i++) {
					((PolygonShape) shape).addPolyPoint(new Point(Integer.parseInt(tmpP[2 * i]), Integer
							.parseInt(tmpP[2 * i + 1])));
				}
			}
			break;
		}
		shape.setAlt(attributeMap.get("alt"));
		shape.setHref(attributeMap.get("href"));
		shape.setTooltip(attributeMap.get("title"));
		return shape;
	}

}
