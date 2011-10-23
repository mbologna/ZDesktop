/**
 * 
 */
package zdesktop;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * This class describes the semantic image that acts as container's thumbnail.
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 * 
 */
public class ZImage extends PImage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2470998614468679515L;

	/**
	 * 
	 */
	private ZContainer zc;

	private float transitionScale;

	private int thumbnailWidth, thumbnailHeight;

	private PNode temp;

	private PCamera camera;

	public ZImage() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ZImage(String arg0, ZContainer zc, ZApplicationProperties zap,
			PCamera camera) {
		super(arg0);
		this.translate(zc.getXOffset(), zc.getYOffset());
		this.setWidth(zc.getWidth());
		this.setHeight(zc.getHeight());
		this.setScale(zc.getScale());
		this.zc = zc;
		this.camera = camera;
		this.transitionScale = zap.getTransitionScale();
		this.thumbnailHeight = zap.getThumbnailHeight();
		this.thumbnailWidth = zap.getThumbnailWidth();
		this.addAttribute("path", zc.getAttribute("path"));
		this.addAttribute("tooltip", zc.getAttribute("tooltip")
				+ " (thumbnail)");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */

	public void paint(PPaintContext aPaintContext) {

		double scale = aPaintContext.getScale();

		if (scale >= transitionScale
				&& testIfNodeIsViewedByCamera(this, camera)) {
			if (zc.replaced == true) {
				zc.replaced = false;
				zc.visited = false;
				zc.setTransform(this.getTransformReference(true));
				this.replaceWith(zc);
			}
		}

		else {
			if (zc.visited == true) {
				zc.visited = false;
				this
						.setImage(zc.toImage(thumbnailWidth, thumbnailHeight,
								null));
				File file = new File(this.getAttribute("path") + ".zpng");
				BufferedImage bi = (BufferedImage) this.getImage();
				try {
					ImageIO.write(bi, "PNG", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.setWidth(zc.getWidth());
				this.setHeight(zc.getHeight());
				this.setScale(zc.getScale());
				for (Iterator iter = zc.getChildrenIterator(); iter.hasNext();) {
					temp = (PNode) iter.next();
					temp = null;
				}
				zc.removeAllChildren();
			}
		}

		super.paint(aPaintContext);
		super.repaint();
	}

	public boolean testIfNodeIsViewedByCamera(PNode node, PCamera camera) {
		PLayer nodeLayer = null;

		Iterator iterator = camera.getLayersReference().iterator();
		while (nodeLayer == null && iterator.hasNext()) {
			PLayer eachLayer = (PLayer) iterator.next();
			if (eachLayer.isAncestorOf(node)) {
				nodeLayer = eachLayer;
			}
		}

		if (nodeLayer == null)
			return false;

		// next convert nodes bounds to layer full bounds.
		Rectangle2D nodeBounds = node.getBounds();
		PNode eachNode = node;

		do {
			nodeBounds = eachNode.localToParent(nodeBounds);
			eachNode = eachNode.getParent();
		} while (eachNode != nodeLayer);

		// next go from layer full bounds "view" to camera local bounds.
		nodeBounds = camera.viewToLocal(nodeBounds);

		return camera.getBounds().intersects(nodeBounds);
	}
}
