/**
 * 
 */
package imagemap.util.action;

import imagemap.graphics.AbstractShape;

import java.util.Vector;

/**
 * @author Niklas Miroll
 *
 */
public class WipeStackAction extends AbstractStackAction {
	private Vector<AbstractShape> wipedShapeList = new Vector<AbstractShape>();

	/**
	 * constructor
	 * 
	 * @param wipedShapeList
	 */
	public WipeStackAction(Vector<AbstractShape> wipedShapeList) {
		actionType = WIPE;
		undoDescription = "Recover all Shapes.";
		redoDescription = "Re-wipe entire Shapes.";
		for (AbstractShape shape : wipedShapeList) {
			this.wipedShapeList.add(shape.clone());
		}
	}
	
	/**
	 * @return the wipedShapeList
	 */
	public Vector<AbstractShape> getWipedShapeList() {
		return wipedShapeList;
	}

	/**
	 * @see imagemap.util.action.AbstractStackAction#specificString()
	 */
	@Override
	protected String specificString() {
		// TODO Auto-generated method stub
		return null;
	}

}
