/**
 * 
 */
package imagemap.util.action;

import imagemap.graphics.AbstractShape;

/**
 * @author Niklas Miroll
 *
 */
public class AddStackAction extends AbstractStackAction {
	private AbstractShape editedShape;

	/**
	 * constructor
	 * 
	 * @param actionType
	 * @param editedShape
	 */
	public AddStackAction(int actionType, AbstractShape editedShape) {
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
	 * @see imagemap.util.action.AbstractStackAction#specificString()
	 */
	@Override
	protected String specificString() {
		// TODO Auto-generated method stub
		return null;
	}

}
