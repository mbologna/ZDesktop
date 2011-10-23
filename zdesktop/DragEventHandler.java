/**
 * 
 */
package zdesktop;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * This class handles the drag event, and save the property of the node dragged.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 * 
 */
public class DragEventHandler extends PDragEventHandler {

	private ZInputEventHandler zteh;

	public DragEventHandler(ZInputEventHandler zteh) {
		super();
		this.zteh = zteh;
	}

	/**
	 * This method is called whenever a drag is started.
	 */
	protected void startDrag(PInputEvent aEvent) {
		super.startDrag(aEvent);
		zteh.tooltipNode.setTransparency(1.00f);
		aEvent.getPickedNode().moveToFront();
	}

	/**
	 * This method is called whenever a drag is ended.
	 */
	protected void endDrag(PInputEvent aEvent) {
		super.endDrag(aEvent);
		PNode picked = aEvent.getPickedNode();

		String filename, path;
		if (picked instanceof PNode && picked != null) {
			path = (String) picked.getAttribute("path");
			if (path != null) {
					filename = path + ".zzz";
				ZProperties zp = new ZProperties();
				zp.loadZProperty(filename);
				zp.setPath(path);
				zp.setX((float) picked.getXOffset());
				zp.setY((float) picked.getYOffset());
				zp.saveZProperty(filename);
			}
		}
		zteh.tooltipNode.setText("");
	}

	/**
	 * This method is called repeatedly during a drag operation.
	 */

	protected void drag(PInputEvent aEvent) {
		super.drag(aEvent);
		zteh.updateToolTip(aEvent);
	}
}
