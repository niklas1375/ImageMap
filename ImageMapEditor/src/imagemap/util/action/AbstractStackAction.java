/**
 * 
 */
package imagemap.util.action;


/**
 * @author Niklas Miroll
 *
 */
public abstract class AbstractStackAction {
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int MOVE = 2;
	public static final int RESIZE = 3;
	public static final int WIPE = 4;
	public static final int SCALE = 5;
	protected int actionType;
	protected String undoDescription;
	protected String redoDescription;
	
	// abstract section
	
	/**
	 * 
	 * @return string specific for type of action
	 */
	protected abstract String specificString();
	
	// end of abstract methods
	
	/**
	 * @return the actionType
	 */
	public int getActionType() {
		return actionType;
	}
	
	/**
	 * @return the undoDescription
	 */
	public String getUndoDescription() {
		return undoDescription;
	}

	/**
	 * @return the redoDescription
	 */
	public String getRedoDescription() {
		return redoDescription;
	}
	
	/**
	 * overridden toString() method for debug purposes
	 */
	@Override
	public String toString() {
		String out = "[Type of Action: " + actionType + "; "
				+ specificString() + "]";
		return out;
	}
}
