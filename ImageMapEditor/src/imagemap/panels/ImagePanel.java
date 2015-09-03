/**
 * 
 */
package imagemap.panels;

import imagemap.*;
import imagemap.graphics.*;
import imagemap.util.*;
import imagemap.util.action.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

/**
 * @author Niklas Miroll
 *
 */
public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image img = null;
	private ImageMap frame;
	private ImageMapProject parent;
	private Vector<AbstractShape> shapeList = new Vector<AbstractShape>();
	private Stack<AbstractStackAction> undoStack = new Stack<AbstractStackAction>();
	private Stack<AbstractStackAction> redoStack = new Stack<AbstractStackAction>();
	private AbstractShape tempShape;
	private AbstractShape draggedShape;
	private AbstractShape currentShape;
	private String imagePath;
	private String savePath;
	private CustomHTMLDoc doc;
	private boolean saved = true;
	private boolean editing = false;

	/**
	 * constructor carrying parent component
	 * 
	 * @param parent
	 */
	public ImagePanel(ImageMap frame, ImageMapProject parent) {
		this.frame = frame;
		this.parent = parent;
	}

	/**
	 * paint component method
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		try {
			if (img != null) {
				g2.drawImage(img, 0, 0, img.getWidth(this), img.getHeight(this), this);
				if (!shapeList.isEmpty()) {
					for (AbstractShape shape : shapeList) {
						if (currentShape != null && shape.matches(currentShape)) {
							shape.setColor(Color.red);
						} else {
							shape.setColor(Color.black);
						}
						shape.draw(g2);
					}
				}
				if (draggedShape != null) {
					draggedShape.draw(g2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		parent.getRulers()[0].repaint();
		parent.getRulers()[1].repaint();
	}

	/**
	 * 
	 * @param shape
	 *            shape to be added
	 */
	public void addShape(AbstractShape shape) {
		shapeList.add(shape);
		addBlankElement(shape);
		undoStack.push(new AddStackAction(AbstractStackAction.ADD, shape.clone()));
		frame.getUndo().setEnabled(true);
		frame.getUndoButton().setEnabled(true);
		currentShape = shape;
		upRevRep();
	}

	/**
	 * complete editing (move or resize) of a shape
	 * 
	 * @param start
	 *            original Shape
	 * @param end
	 *            new Shape
	 */
	public void endEditing(int actionType, AbstractShape start, AbstractShape end) {
		if (actionType == AbstractStackAction.MOVE) {
			undoStack.push(new MoveStackAction(actionType, start, end));
		} else {
			undoStack.push(new ResizeStackAction(actionType, start, end));
		}
		upRevRep();
	}

	/**
	 * scaling image and shapes, updating HTMLDoc, adding Action to undoStack
	 * depending on undo/redo or not
	 * 
	 * @param scale
	 *            factor for scaling
	 */
	public void scale(float scale, boolean unre) {
		// scaling the picture
		int newWidth = Math.round(img.getWidth(null) * scale);
		int newHeight = Math.round(img.getHeight(null) * scale);
		BufferedImage dbi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dbi.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
		g.drawRenderedImage((RenderedImage) img, at);

		img = dbi;

		// scaling the shapes and updating HTMLDoc
		doc.getElements().get("img").editAttributeValue("width", "" + img.getWidth(null));
		doc.getElements().get("img").editAttributeValue("height", "" + img.getHeight(null));
		for (int i = 0; i < shapeList.size(); i++) {
			shapeList.get(i).scale(scale);
			doc.getMap().getSubElements().get(shapeList.get(i).getId()).updateCoords(shapeList.get(i));
		}

		// push action to undoStack if it is not a undo or redo action
		if (!unre) {
			undoStack.push(new ScaleStackAction(scale));
		}

		// update panel
		upRevRep();
	}

	/**
	 * 
	 * @param p
	 *            point to be checked if it is inside a shape
	 * @return whether Point is inside a Shape
	 */
	public boolean isInside(Point p) {
		if (!shapeList.isEmpty()) {
			for (AbstractShape shape : shapeList) {
				if (shape.contains((int) p.getX(), (int) p.getY())) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @param p
	 *            passed Point
	 * @return Shape whose Rectangle contains passed Point
	 */
	public AbstractShape isInsideRect(Point p) {
		for (AbstractShape shape : shapeList) {
			if (shape.cornerContains(p)) {
				return shape;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param p
	 *            point which is inside of a shape
	 * @return shape the point is inside of
	 */
	public AbstractShape whichShape(Point p, int id) {
		AbstractShape tmp = null;
		if (isInside(p)) {
			for (AbstractShape shape : shapeList) {
				if (shape.contains((int) p.getX(), (int) p.getY())) {
					if (id == shape.getId()) {
						return shape;
					} else if (tmp == null) {
						tmp = shape;
					}
				}
			}
		} 
		return tmp;
	}
	
	/**
	 * 
	 * @param s
	 * @returnshape instance out of shapeList
	 */
	public AbstractShape whichShape(AbstractShape s){
		return shapeList.elementAt(getShapeIndex(s));
	}

	/**
	 * empty imagePanel
	 */
	public void die() {
		this.img = null;
		this.currentShape = null;
		this.redoStack = new Stack<AbstractStackAction>();
		this.undoStack = new Stack<AbstractStackAction>();
		this.shapeList = new Vector<AbstractShape>();
		this.tempShape = null;
		this.imagePath = null;
		this.savePath = null;
		this.doc = null;
		upRevRep();
	}

	/**
	 * try to paste at 0,0
	 */
	public void doPaste() {
		int type = tempShape.getType();
		switch (type) {
		case AbstractShape.TYPE_RECT:
			RectangleShape locRecShape = (RectangleShape) tempShape;
			Rectangle tmp = locRecShape.getRectangle();
			int dx = (int) tmp.getMaxX() - (int) tmp.getMinX();
			int dy = (int) tmp.getMaxY() - (int) tmp.getMinY();
			shapeList.add(new RectangleShape(0, 0, dx, dy));
			break;

		case AbstractShape.TYPE_CIRC:
			CircleShape locCirShape = (CircleShape) tempShape;
			int rad_tmp = locCirShape.getRadius();
			Point cent_tmp = new Point(rad_tmp, rad_tmp);
			shapeList.add(new CircleShape(cent_tmp, rad_tmp));
			break;

		case AbstractShape.TYPE_POLY:
			PolygonShape locPolShape = (PolygonShape) tempShape;
			int dx1 = (int) locPolShape.getXMinPolyPoint().getX();
			int dy1 = (int) locPolShape.getYMinPolyPoint().getY();
			Polygon poly_tmp = locPolShape.getPolygon();
			int[] xArray = poly_tmp.xpoints;
			int[] yArray = poly_tmp.ypoints;
			PolygonShape shape_tmp = null;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					shape_tmp = new PolygonShape(new Point(xArray[0] - dx1, yArray[0] - dy1));
				} else {
					shape_tmp.addPolyPoint(new Point(xArray[i] - dx1, yArray[i] - dy1));
				}
			}
			shapeList.add(shape_tmp);
			break;

		default:
			break;
		}
		undoStack.push(new AddStackAction(AbstractStackAction.ADD, shapeList.lastElement()));
		addBlankElement(shapeList.lastElement());
		upRevRep();
	}

	/**
	 * 
	 * @param p
	 *            paste at Point p
	 */
	public void doPaste(Point p) {
		int type = tempShape.getType();
		switch (type) {
		case AbstractShape.TYPE_RECT:
			RectangleShape locRecShape = (RectangleShape) tempShape;
			Rectangle tmp = locRecShape.getRectangle();
			int dx = (int) tmp.getMaxX() - (int) tmp.getMinX();
			int dy = (int) tmp.getMaxY() - (int) tmp.getMinY();
			int xnew = (int) p.getX();
			int ynew = (int) p.getY();
			if (dx + xnew > img.getWidth(null)) {
				xnew = img.getWidth(null) - dx;
			}
			if (dy + ynew > img.getHeight(null)) {
				ynew = img.getWidth(null) - dy;
			}
			shapeList.add(new RectangleShape(xnew, ynew, xnew + dx, ynew + dy));
			break;

		case AbstractShape.TYPE_CIRC:
			int xnewc = (int) p.getX();
			int ynewc = (int) p.getY();
			CircleShape locCirShape = (CircleShape) tempShape;
			int r_tmp = locCirShape.getRadius();
			if (r_tmp + xnewc > img.getWidth(null)) {
				xnewc = img.getWidth(null) - r_tmp;
			}
			if (r_tmp + ynewc > img.getWidth(null)) {
				ynewc = img.getWidth(null) - r_tmp;
			}
			Point cent_tmp = new Point(xnewc, ynewc);
			shapeList.add(new CircleShape(cent_tmp, r_tmp));
			break;

		case AbstractShape.TYPE_POLY:
			// first search for orientation of Polygon (always towards the
			// center of the image) then create new Polygon
			PolygonShape locPolShape = (PolygonShape) tempShape;
			int wi = img.getWidth(null);
			int hi = img.getHeight(null);
			int wpoly = (int) locPolShape.getXMaxPolyPoint().getX() - (int) locPolShape.getXMinPolyPoint().getX();
			int hpoly = (int) locPolShape.getYMaxPolyPoint().getY() - (int) locPolShape.getYMinPolyPoint().getY();
			int px = (int) p.getX();
			int py = (int) p.getY();
			int dxpoly = 0;
			int dypoly = 0;
			Polygon poly_tmp = locPolShape.getPolygon();
			int[] xArray = poly_tmp.xpoints;
			int[] yArray = poly_tmp.ypoints;
			if (px > wi / 2 && py < hi / 2) {
				// 1. quadrant
				if (px + wpoly > wi) {
					px = wi - wpoly;
				}
				if (py - hpoly < 0) {
					py = hpoly;
				}
				dxpoly = px - (int) locPolShape.getXMinPolyPoint().getX();
				dypoly = py - (int) locPolShape.getYMaxPolyPoint().getY();
			}
			if (px < wi / 2 && py < hi / 2) {
				// 2. quadrant
				if (px - wpoly < 0) {
					px = wpoly;
				}
				if (py - hpoly < 0) {
					py = hpoly;
				}
				dxpoly = px - (int) locPolShape.getXMaxPolyPoint().getX();
				dypoly = py - (int) locPolShape.getYMaxPolyPoint().getY();
			}
			if (px < wi / 2 && py > hi / 2) {
				// 3. quadrant
				if (px - wpoly < 0) {
					px = wpoly;
				}
				if (py + hpoly > hi) {
					py = hi - hpoly;
				}
				dxpoly = px - (int) locPolShape.getXMaxPolyPoint().getX();
				dypoly = py - (int) locPolShape.getYMinPolyPoint().getY();
			}
			if (px > wi / 2 && py > hi / 2) {
				// 4. quadrant
				if (px + wpoly > wi) {
					px = wi - wpoly;
				}
				if (py + hpoly > hi) {
					py = hi - hpoly;
				}
				dxpoly = px - (int) locPolShape.getXMinPolyPoint().getX();
				dypoly = py - (int) locPolShape.getYMinPolyPoint().getY();
			}
			PolygonShape shape_tmp = null;
			for (int i = 0; i < xArray.length; i++) {
				if (i == 0) {
					shape_tmp = new PolygonShape(new Point(xArray[0] + dxpoly, yArray[0] + dxpoly));
				} else {
					shape_tmp.addPolyPoint(new Point(xArray[i] + dxpoly, yArray[i] + dypoly));
				}
			}
			shapeList.add(shape_tmp);
			break;

		default:
			break;
		}
		undoStack.push(new AddStackAction(AbstractStackAction.ADD, shapeList.lastElement()));
		addBlankElement(shapeList.lastElement());
		upRevRep();
	}

	/**
	 * 
	 * @param s
	 *            shape to be deleted
	 */
	public void doDelete(AbstractShape target) {
		if (target == null) {
			target = currentShape;
		}
		shapeList.remove(target);
		doc.getMap().removeSubElement(target.getId());
		currentShape = null;
		undoStack.push(new RemoveStackAction(AbstractStackAction.REMOVE, target));
		upRevRep();
	}

	/**
	 * 
	 * @param s
	 *            shape to be cut
	 */
	public void doCut(AbstractShape target) {
		doCopy(target);
		doDelete(target);
	}

	/**
	 * 
	 * @param s
	 *            Shape to be copied
	 */
	public void doCopy(AbstractShape target) {
		if (target == null) {
			target = currentShape;
		}
		int type = target.getType();
		switch (type) {
		case AbstractShape.TYPE_RECT:
			tempShape = (RectangleShape) target.clone();
			break;

		case AbstractShape.TYPE_CIRC:
			tempShape = (CircleShape) target.clone();
			break;

		case AbstractShape.TYPE_POLY:
			tempShape = (PolygonShape) target.clone();
			break;

		default:
			break;
		}
	}

	/**
	 * undo the last action, undo methods depend on type of action
	 */
	public void doUndo() {
		if (!undoStack.isEmpty()) {
			AbstractStackAction sa = undoStack.pop();
			int shapeIndex = -1;
			switch (sa.getActionType()) {
			case AbstractStackAction.ADD:
				shapeIndex = getShapeIndex(((AddStackAction) sa).getEditedShape());
				break;

			case AbstractStackAction.MOVE:
				shapeIndex = getShapeIndex(((MoveStackAction) sa).getEditedShape());
				break;

			case AbstractStackAction.REMOVE:
				shapeIndex = getShapeIndex(((RemoveStackAction) sa).getEditedShape());
				break;

			case AbstractStackAction.RESIZE:
				shapeIndex = getShapeIndex(((ResizeStackAction) sa).getEditedShape());
				break;

			default:
				break;
			}
			AbstractShape tmp;
			if (shapeIndex < 0
					&& !(sa.getActionType() == AbstractStackAction.WIPE || sa.getActionType() == AbstractStackAction.SCALE)) {
				System.out.println("index fail");
				return;
			}
			redoStack.push(sa);
			switch (sa.getActionType()) {
			case AbstractStackAction.ADD:
				doc.getMap().removeSubElement(shapeList.remove(shapeIndex).getId());
				break;

			case AbstractStackAction.MOVE:
				doc.getMap().removeSubElement(shapeList.remove(shapeIndex).getId());
				tmp = ((MoveStackAction) sa).getOriginalShape().clone();
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.REMOVE:
				tmp = ((RemoveStackAction) sa).getOriginalShape().clone();
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.RESIZE:
				doc.getMap().removeSubElement(shapeList.remove(shapeIndex).getId());
				tmp = ((ResizeStackAction) sa).getOriginalShape().clone();
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.WIPE:
				shapeList.clear();
				doc.getMap().clearSubs();
				for (AbstractShape shape : ((WipeStackAction) sa).getWipedShapeList()) {
					tmp = shape.clone();
					shapeList.add(tmp);
					addFilledElement(tmp);
				}
				break;

			case AbstractStackAction.SCALE:
				scale(1 / ((ScaleStackAction) sa).getScaleFactor(), true);
				break;

			default:
				break;
			}
			tmp = null;
			upRevRep();
		}
	}

	/**
	 * redo the last undo, redo methods depend on type of action
	 */
	public void doRedo() {
		if (!redoStack.isEmpty()) {
			AbstractStackAction sa = redoStack.pop();
			AbstractShape tmp;
			undoStack.push(sa);
			switch (sa.getActionType()) {
			case AbstractStackAction.ADD:
				tmp = ((AddStackAction) sa).getEditedShape().clone();
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.MOVE:
				tmp = ((MoveStackAction) sa).getEditedShape().clone();
				doc.getMap().removeSubElement(
						shapeList.remove(getShapeIndex(((MoveStackAction) sa).getOriginalShape())).getId());
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.REMOVE:
				doc.getMap().removeSubElement(
						shapeList.remove(getShapeIndex(((RemoveStackAction) sa).getEditedShape())).getId());
				break;

			case AbstractStackAction.RESIZE:
				tmp = ((ResizeStackAction) sa).getEditedShape().clone();
				doc.getMap().removeSubElement(
						shapeList.remove(getShapeIndex(((ResizeStackAction) sa).getOriginalShape())).getId());
				shapeList.add(tmp);
				addFilledElement(tmp);
				break;

			case AbstractStackAction.WIPE:
				shapeList.clear();
				doc.getMap().clearSubs();
				break;

			case AbstractStackAction.SCALE:
				scale(((ScaleStackAction) sa).getScaleFactor(), true);
				break;

			default:
				break;
			}
			tmp = null;
			upRevRep();
		}
	}
	
	/**
	 * edit given shape (either by right-click or by end of editing of a shape)
	 * 
	 * @param s
	 */
	public void doEdit(Point p) {
		DetailEditor de = new DetailEditor(whichShape(p, 0), doc, frame);
		de.setVisible(true);
	} // end of doEdit(Shape s)
	
	/**
	 * clear all shapes
	 */
	public void doClear() {
		undoStack.push(new WipeStackAction(shapeList));
		shapeList.clear();
		doc.getMap().clearSubs();
		upRevRep();
	} // end of doClear()
	
	/**
	 * move given shape to top of stack to make it "most visible"
	 * 
	 * @param s
	 */
	public void toTop(AbstractShape s) {
		shapeList.remove(s);
		shapeList.add(0, s);
	}
	
	/**
	 * move given shape to bottom of stack to make it "out of focus"
	 * 
	 * @param s
	 */
	public void toBottom(AbstractShape s) {
		shapeList.remove(s);
		shapeList.add(s);
	}

	/**
	 * auxilliary method to keep up with updating and revalidating the panel's display
	 */
	private void upRevRep() {
		updateTooltips();
		revalidate();
		repaint();
	}

	/**
	 * update tooltips
	 */
	private void updateTooltips() {
		JMenuItem tmp = frame.getUndo();
		JMenuItem tmp1 = frame.getRedo();
		JButton tmp2 = frame.getUndoButton();
		JButton tmp3 = frame.getRedoButton();
		// undo part
		if (!undoStack.isEmpty()) {
			tmp.setToolTipText(undoStack.peek().getUndoDescription());
			tmp.setEnabled(true);
			tmp2.setEnabled(true);
		} else {			
			tmp.setToolTipText("Nothing to undo");
			tmp.setEnabled(false);
			tmp2.setEnabled(false);
		}
		// redo part
		if (!redoStack.isEmpty()) {
			tmp1.setToolTipText(redoStack.peek().getRedoDescription());
			tmp1.setEnabled(true);
			tmp3.setEnabled(true);
		} else {
			tmp1.setToolTipText("Nothing to redo");
			tmp1.setEnabled(false);
			tmp3.setEnabled(false);
		}
	}

	/**
	 * find index of given Shape in shapeList
	 * 
	 * @param shape
	 *            shape to be searched for
	 * @return index of given Shape in shapeList
	 */
	private int getShapeIndex(AbstractShape shape) {
		if (!shapeList.isEmpty() && shape != null) {
			for (int i = 0; i < shapeList.size(); i++) {
				AbstractShape tmp = shapeList.get(i);
				if (shape.matches(tmp)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * add blank shape element to html model
	 * 
	 * @param shape
	 */
	private void addBlankElement(AbstractShape shape) {
		doc.getMap().addSubElement(new CustomHTMLElement("area", true), shape.getId());
		switch (shape.getType()) {
		case AbstractShape.TYPE_RECT:
			doc.getMap().getSubElements().get(shape.getId()).addAttribute("shape", "rect");
			break;

		case AbstractShape.TYPE_CIRC:
			doc.getMap().getSubElements().get(shape.getId()).addAttribute("shape", "circle");
			break;

		case AbstractShape.TYPE_POLY:
			doc.getMap().getSubElements().get(shape.getId()).addAttribute("shape", "poly");
			break;

		default:
			break;
		}
		doc.getMap().getSubElements().get(shape.getId()).addAttribute("coords", shape.getCoords())
				.addAttribute("alt", "").addAttribute("href", "").addAttribute("title", "");
	}

	/**
	 * add a filled shape to html model
	 * 
	 * @param tmp
	 * @return created element to allow method chaining
	 */
	private CustomHTMLElement addFilledElement(AbstractShape tmp) {
		doc.getMap().addSubElement(new CustomHTMLElement("area", true), tmp.getId());
		CustomHTMLElement toBeFilled = doc.getMap().getSubElements().get(tmp.getId());
		toBeFilled.addAttribute("shape", tmp.getTypeName()).addAttribute("coords", tmp.getCoords())
				.addAttribute("href", tmp.getHref()).addAttribute("alt", tmp.getAlt())
				.addAttribute("title", tmp.getTooltip());
		return toBeFilled;
	}

	/**
	 * Getter for Image
	 * 
	 * @return the img
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * Setter for Image
	 * 
	 * @param img
	 *            the img to set
	 */
	public void setImg(Image img) {
		this.img = img;
		this.setSize(new Dimension(img.getWidth(this), img.getHeight(this)));
		this.repaint();
		doc = new CustomHTMLDoc(img, imagePath);
	}

	/**
	 * Getter for ShapeList
	 * 
	 * @return the shapeList
	 */
	public Vector<AbstractShape> getShapeList() {
		return shapeList;
	}

	/**
	 * Setter for current Shape
	 * 
	 * @param currentShape
	 *            the currentShape to set
	 */
	public void setCurrentShape(AbstractShape currentShape) {
		this.currentShape = currentShape;
		repaint();
	}

	/**
	 * Getter for currently dragged shape
	 * 
	 * @return the draggedShape
	 */
	public AbstractShape getDraggedShape() {
		return draggedShape;
	}

	/**
	 * Setter for currently dragged shape
	 * 
	 * @param draggedShape
	 *            the draggedShape to set
	 */
	public void setDraggedShape(AbstractShape draggedShape) {
		this.draggedShape = draggedShape;
		repaint();
	}

	/**
	 * Getter for ImagePath
	 * 
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * Setter for ImagePath
	 * 
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * Getter for save path of project
	 * 
	 * @return the savePath
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * Setter for SavePath of current project
	 * 
	 * @param savePath
	 *            the savePath to set
	 */
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	/**
	 * Getter for HTML Document of project
	 * 
	 * @return the doc
	 */
	public CustomHTMLDoc getDoc() {
		return doc;
	}

	/**
	 * Getter whether project progress is currently saved
	 * 
	 * @return whether panel has been saved yet
	 */
	public boolean isSaved() {
		return saved;
	}

	/**
	 * Getter whether the panel is currently being edited
	 * 
	 * @return whether panel is currently being edited
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Setter for saved status
	 * 
	 * @param saved
	 *            the saved state to set
	 */
	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	/**
	 * Setter for current editing status
	 * 
	 * @param editing
	 *            the editing state to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

}