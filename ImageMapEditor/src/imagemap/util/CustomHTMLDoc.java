/**
 * 
 */
package imagemap.util;

import java.awt.Image;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Niklas Miroll
 * @category util classes
 */
public class CustomHTMLDoc {
	private HashMap<String, CustomHTMLElement> elementList;

	/**
	 * 
	 */
	public CustomHTMLDoc(Image img, String imagePath) {
		elementList = new HashMap<String, CustomHTMLElement>();
		elementList.put("img", new CustomHTMLElement("img", true));
		elementList.put("map", new CustomHTMLElement("map", false));
		elementList.get("map").addAttribute("name", "imageMap");
		elementList.get("img").addAttribute("src", imagePath);
		elementList.get("img").addAttribute("width", "" + img.getWidth(null));
		elementList.get("img").addAttribute("height", "" + img.getHeight(null));
		elementList.get("img").addAttribute("usemap", "#imageMap");
	}
	
	/**
	 * to String method; overrides toString() of Object
	 */
	@Override
	public String toString(){
		String html = "";
		for (Entry<String, CustomHTMLElement> entry : elementList.entrySet()) {
			html += entry.getValue().toString();
		}
		return html;
	}
	
	/**
	 * 
	 * @return every area node in the doc
	 */
	public static Vector<CustomHTMLElement> getShapeNodesFromHTML(String html){
		Vector<CustomHTMLElement> retList = new Vector<CustomHTMLElement>();
		Pattern pattern = Pattern.compile("<area.*>");
		Matcher m = pattern.matcher(html);
		while (m.find()) {
			CustomHTMLElement ce = new CustomHTMLElement("area", true);
			String[] tmp = html.substring(m.start(), m.end()).split(" ");
			for (int i = 0; i < tmp.length; i++) {
				String[] tmpInner = tmp[i].split("=");
				if (tmpInner.length == 2) {
					ce.addAttribute(tmpInner[0], tmpInner[1].substring(1, tmpInner[1].length() - 1));
				}
			}
			retList.add(ce);
		}
		return retList;
	}
	
	/**
	 * 
	 * @return the map node
	 */
	public CustomHTMLElement getMap() {
		return elementList.get("map");
	}
	
	/**
	 * 
	 * @return every node
	 */
	public HashMap<String, CustomHTMLElement> getElements() {
		return elementList;
	}

}
