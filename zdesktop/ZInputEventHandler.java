/**
 * 
 */
package zdesktop;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * This class is the main handler of the event produced by mouse.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 * 
 */
public class ZInputEventHandler extends PBasicInputEventHandler {

	/**
	 * minScale rappresenta la minima scala visualizzabile. Al di sotto di
	 * questa scala, l'operazione di zoom non prosegue
	 */
	private float minScale = 0.1f;

	/**
	 * maxScale rappresenta la massima scala visualizzabile. Al di sopra di
	 * questa scala, l'operazione di zoom non prosegue
	 */
	private float maxScale = Float.MAX_VALUE;

	/**
	 * scaleFactor rappresenta di quanto aumentera` o diminuira la scala, per
	 * ogni passo dello scroll della rotella.
	 */

	private double scaleFactor;

	private PBounds zoomToBounds;

	private PNode picked;

	protected PNode current;

	public PText tooltipNode = null;

	public ZInputEventHandler(double scaleFactor, PText tooltipNode,
			Color tooltipColor) {
		super();
		this.scaleFactor = scaleFactor;
		this.tooltipNode = tooltipNode;
		this.tooltipNode.setPaint(tooltipColor);
		this.tooltipNode.setTransparency(1.0f);
	}

	public void mouseWheelRotated(PInputEvent aEvent) {
		super.mouseWheelRotated(aEvent);
		this.discreteZoom(aEvent);
	}

	public void mouseClicked(PInputEvent aEvent) {
		super.mouseClicked(aEvent);
		if ((aEvent.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (aEvent.getClickCount() == 1) {
				this.zoomTo(aEvent);
			}
		}
		if ((aEvent.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			if (aEvent.getClickCount() == 1) {
				this.resize(aEvent);
			}
		}
		if ((aEvent.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			if (aEvent.getClickCount() == 1) {
				this.zoomBack(aEvent);
			}
		}
	}

	public void mouseEntered(PInputEvent aEvent) {
		super.mouseEntered(aEvent);
		updateToolTip(aEvent);
		tooltipNode.animateToTransparency(0.0f, 1000);
	}

	public void mouseExited(PInputEvent aEvent) {
		super.mouseExited(aEvent);
		tooltipNode.setText("");
		tooltipNode.setTransparency(1.0f);
	}

	public void updateToolTip(PInputEvent aEvent) {
		PNode n = aEvent.getInputManager().getMouseOver().getPickedNode();
		String tooltipString = (String) n.getAttribute("tooltip");
		Point2D p = aEvent.getCanvasPosition();
		aEvent.getPath().canvasToLocal(p, aEvent.getCamera());
		tooltipNode.setText(tooltipString);
		tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
	}

	private void resize(PInputEvent aEvent) {
		PNode picked = aEvent.getPickedNode();
		picked.moveToFront();
		Boolean handles = (Boolean) picked.getAttribute("handles");
		if (handles != null && handles.booleanValue() == true) {
			PBoundsHandle.removeBoundsHandlesFrom(picked);
			picked.addAttribute("handles", false);
			this.saveProperty(picked);
		}
		if (handles != null && handles.booleanValue() == false) {
			PBoundsHandle.addBoundsHandlesTo(picked);
			picked.addAttribute("handles", true);
			this.saveProperty(picked);
		}
	}

	private void saveProperty(PNode node) {
		String filename, path;
		if (node instanceof PNode && node != null) {
			path = (String) node.getAttribute("path");
			if (path != null) {
				filename = path + ".zzz";
				ZProperties zp = new ZProperties();
				zp.loadZProperty(filename);
				zp.setPath(path);
				zp.setX((float) node.getXOffset());
				zp.setY((float) node.getYOffset());
				zp.setHeight((float) node.getBounds().getHeight());
				zp.setWidth((float) node.getBounds().getWidth());
				zp.setScale((float) node.getScale());
				zp.saveZProperty(filename);
			}
		}
	}

	private void zoomTo(final PInputEvent aEvent) {
		picked = aEvent.getPickedNode();
		picked.moveToFront();

		zoomToBounds = picked.getGlobalFullBounds();
		current = picked.getParent();
		aEvent.getCamera().animateViewToCenterBounds(zoomToBounds, true, 500);
	}

	private void zoomBack(final PInputEvent aEvent) {
		picked = aEvent.getPickedNode();
		picked.moveToFront();

		if (current != null && current.getParent() != null) {
			zoomToBounds = current.getGlobalFullBounds();
			current = current.getParent();
			aEvent.getCamera().animateViewToCenterBounds(zoomToBounds, true,
					500);
		}

	}

	private void discreteZoom(PInputEvent aEvent) {
		/*
		 * Il metodo getWheelRotation ritorna: 1 -> rotellina mossa verso il
		 * basso -1 -> rotellina mossa verso l'alto
		 * 
		 */
		float newScale, oldScale;
		float step;
		int logBase = 10;
		Point2D point;

		/*
		 * Dato la camera associata all'evento la uso spesso, salvo il
		 * riferimento in una variabile, per aumentare le prestazioni.
		 */
		PCamera camera = aEvent.getCamera();
		oldScale = (float) camera.getViewScale();
		/*
		 * In pseudocodice: nuovaScala = vecchiaScala + fattoreDiScala * (zoom
		 * in | zoom out)
		 */

		step = (float) Math.pow(10, Math.floor(Math.log10(oldScale)
				/ Math.log10(logBase)));

		scaleFactor = Math.round(oldScale / step) * step;
		if (aEvent.getWheelRotation() < 0)
			if (Math.floor(scaleFactor / step) <= 1)
				step = step / logBase;
		newScale = (float) (scaleFactor + step * (aEvent.getWheelRotation()));

		if (newScale < minScale)
			newScale = minScale;

		if (newScale > maxScale)
			newScale = maxScale;

		point = aEvent.getPosition();

		/*
		 * Scalo la vista. Il rapporto (newScale / oldScale) mi dice di quanto
		 * devo ingrandire (o rimpicciolire) la scala. Es.:
		 */
		camera.scaleViewAboutPoint(newScale / oldScale, point.getX(), point
				.getY());
	}
}
