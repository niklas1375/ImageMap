/**
 * 
 */
package imagemap.util.action;

/**
 * @author Niklas Miroll
 *
 */
public class ScaleStackAction extends AbstractStackAction {
	private float scaleFactor;

	/**
	 * constructor
	 * 
	 * @param scale
	 */
	public ScaleStackAction(float scale) {
		actionType = SCALE;
		undoDescription = "Scale back to previous state.";
		redoDescription = "Redo scaling.";
		this.scaleFactor = 0 + scale;
	}

	/**
	 * 
	 * @return the scaleFactor
	 */
	public float getScaleFactor() {
		return scaleFactor;
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
