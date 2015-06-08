/**
 * 
 */
package imagemap.util.action;

import imagemap.graphics.AbstractShape;

/**
 * @author Niklas Miroll
 *
 */
public class ResizeStackAction extends AbstractStackAction {
	private AbstractShape editedShape;
	private AbstractShape originalShape;
	
	/**
	 * constructor
	 * 
	 * @param actionType
	 * @param start
	 * @param end
	 */
	public ResizeStackAction(int actionType, AbstractShape start, AbstractShape end) {
		this.actionType = actionType;
		originalShape = start.clone();
		editedShape = end.clone();
		undoDescription = "Recover original size/form of Shape.";
		redoDescription = "Redo resize of Shape.";
	}
	
	/**
	 * @return the editedShape
	 */
	public AbstractShape getEditedShape() {
		return editedShape;
	}

	/**
	 * @return the originalShape
	 */
	public AbstractShape getOriginalShape() {
		return originalShape;
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
