/**
 * 
 */
package imagemap.util.action;

import imagemap.graphics.AbstractShape;

/**
 * @author Niklas Miroll
 *
 */
public class RemoveStackAction extends AbstractStackAction {
	private AbstractShape editedShape;
	private AbstractShape originalShape;

	/**
	 * constructor
	 * 
	 * @param actionType
	 * @param editedShape
	 */
	public RemoveStackAction(int actionType, AbstractShape editedShape) {
		this.actionType = actionType;
		this.editedShape = editedShape.clone();
		undoDescription = "Remove Shape last added.";
		redoDescription = "Recover removed Shape.";
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
