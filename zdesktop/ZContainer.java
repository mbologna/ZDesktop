package zdesktop;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * This class describes the semantic node container.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 * 
 */
public class ZContainer extends PNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5435884812761829155L;

	private PImage image;

	private ZImage thumbnail = null;

	private PNode newNode = null;

	private PText text;

	private ZContainer dir;

	private Color colorDir, initialForegroundColor, initialBackgroundColor,
			finalBackgroundColor;

	private ZApplicationProperties zap;

	private ZProperties newNodeProperties;

	private int i, nSqrt, dot, nx, ny, numberOfLines = 0;;

	private float squareSize, dx, dy, redValue, greenValue, blueValue,
			transitionScale, margin, alpha;

	private char[] space = { ' ', '\t', '-' };

	private File f;

	private File[] fileList;

	private String path, s, type;

	private StringBuffer sb;

	private BufferedReader in;

	public boolean visited;

	public boolean replaced;

	public double originalScale;

	private PCamera camera;

	public ZContainer(ZProperties properties, ZApplicationProperties zap,
			PCamera camera) {
		this.translate(properties.getX(), properties.getY());
		this.camera = camera;
		this.setHeight(properties.getHeight());
		this.setWidth(properties.getWidth());
		this.setPickable(true);
		this.setChildrenPickable(true);
		this.setPaint(properties.getInitialBackgroundColor());
		this.initialBackgroundColor = properties.getInitialBackgroundColor();
		this.initialForegroundColor = properties.getInitialForegroundColor();
		this.finalBackgroundColor = properties.getFinalBackgroundColor();
		this.transitionScale = zap.getTransitionScale();
		this.margin = properties.getMargin();
		this.alpha = zap.getAlpha();
		this.zap = zap;
		this.addAttribute("tooltip", properties.getPath().substring(
				properties.getPath().lastIndexOf("\\") + 1));
		this.addAttribute("path", properties.getPath());
		this.addAttribute("handles", false);
		this.visited = false;
		this.replaced = false;
	}

	public void paint(PPaintContext aPaintContext) {
		double scale = aPaintContext.getScale();

		if (scale >= transitionScale
				&& testIfNodeIsViewedByCamera(this, camera)) {
			if (this.visited == false) {
				this.visited = true;
				this.loadChildren();
			}
		} else {
			if (this.replaced == false) {
				this.replaced = true;
				if (new File(this.getAttribute("path") + ".zpng").exists()) {
					this.thumbnail = new ZImage(this.getAttribute("path")
							+ ".zpng", this, zap, camera);
				} else {
					this.thumbnail = new ZImage("Folder.gif", this, zap, camera);
				}
				this.replaceWith(thumbnail);
			}
		}

		super.paint(aPaintContext);
		super.repaint();
	}

	private void loadChildren() {
		path = (String) this.getAttribute("path");

		f = new File(path);
		fileList = f.listFiles(new FindPropertiesFiles());
		if (zap.isIgnoreZZZfiles() || fileList.length == 0) {
			fileList = f.listFiles(new FindFiles());
		}
		Arrays.sort(fileList);
		nSqrt = (int) Math.ceil(Math.sqrt(fileList.length));
		squareSize = (float) this.getWidth();
		dx = (float) (squareSize / nSqrt) * (1 - margin) / 2;
		dy = dx;

		newNodeProperties = new ZProperties();

		for (i = 0; i < fileList.length; i++) {
			dot = fileList[i].toString().lastIndexOf(".") + 1;
			if (fileList[i].toString().substring(dot).equals("zzz")) {
				newNodeProperties.loadZProperty(fileList[i].toString());
				fileList[i] = new File(newNodeProperties.getPath());
			} else {
				nx = (i % nSqrt);
				ny = (int) Math.floor(i / nSqrt);
				newNodeProperties
						.setX((float) (nx * (squareSize / nSqrt) + dx));
				newNodeProperties
						.setY((float) (ny * (squareSize / nSqrt) + dy));

				newNodeProperties.setWidth((float) this.getWidth());
				newNodeProperties.setHeight((float) this.getHeight());
				newNodeProperties.setScale((float) 1 / nSqrt * margin);
				newNodeProperties.setPath(fileList[i].toString());
				/*
				 * Imposto le proprieta` specifiche del nodo con i valori di
				 * default dell'applicazione
				 * 
				 */
				newNodeProperties.setFit("max");
				newNodeProperties.setWordWrap(true);
				newNodeProperties.setWordWrapColumn(40);
				newNodeProperties.setMargin(margin);
				newNodeProperties.setFinalBackgroundColor(finalBackgroundColor);
				newNodeProperties
						.setInitialForegroundColor(initialForegroundColor);
				newNodeProperties
						.setFinalForegroundColor(initialBackgroundColor);
				redValue = (float) initialBackgroundColor.getRed();
				greenValue = (float) initialBackgroundColor.getGreen();
				blueValue = (float) initialBackgroundColor.getBlue();
				redValue = redValue * alpha + (1 - alpha)
						* newNodeProperties.getFinalBackgroundColor().getRed();
				greenValue = greenValue
						* alpha
						+ (1 - alpha)
						* newNodeProperties.getFinalBackgroundColor()
								.getGreen();
				blueValue = blueValue * alpha + (1 - alpha)
						* newNodeProperties.getFinalBackgroundColor().getBlue();

				colorDir = new Color((int) redValue, (int) greenValue,
						(int) blueValue);
				newNodeProperties.setInitialBackgroundColor(colorDir);
				
				newNodeProperties.saveZProperty(fileList[i].toString()
							.concat(".zzz"));
			}
			if (fileList[i].isFile()) {

				numberOfLines = 0;

				type = Functions.typeOfFile(fileList[i].toString());

				if (type.equals("text")) {
					text = new PText();
					text.setGreekThreshold(0);

					// Carico il contenuto del file
					sb = new StringBuffer();
					try {
						in = new BufferedReader(new FileReader(fileList[i]
								.toString()));
						numberOfLines = 0;

						// Carico al massimo le prime 20 linee del file
						while ((s = in.readLine()) != null
								&& numberOfLines < 20) {

							/*
							 * La classe PText non supporta la visualizzazione
							 * della tabulazione. Cosi` lo sostituisco con 4
							 * spazi.
							 */
							if (s.contains("\t"))
								s = s.replace("\t", "    ");
							if (newNodeProperties.isWordWrap()) {
								sb.append(this.textWrap(s, newNodeProperties
										.getWordWrapColumn(), space, '\n'));
							} else {
								sb.append(s);
							}
							sb.append("\n");

							numberOfLines++;
						}

						// Se il file è più lungo di 20 linee, metto i
						// puntini
						// di
						// sospensione...
						if (s != null)

							sb.append("[...]");

						in.close();
					} catch (FileNotFoundException e) {
						// File non trovato.
						e.printStackTrace();
					} catch (IOException e) {
						// Errore di I/O.
						e.printStackTrace();
					}

					text.setText(sb.toString());
					text.setTextPaint(newNodeProperties
							.getInitialForegroundColor());
					if (newNodeProperties.getFinalForegroundColor() == initialBackgroundColor) {
						text.setPaint(null);
					} else {
						text.setPaint(newNodeProperties
								.getFinalForegroundColor());
					}
					newNode = text;

					if (newNodeProperties.getFit().equals("min")) {
						newNodeProperties.setScale(newNodeProperties.getWidth()
								* newNodeProperties.getScale()
								/ (float) Math.min(newNode.getHeight(), newNode
										.getWidth()));
					}

					if (newNodeProperties.getFit().equals("max")) {
						newNodeProperties.setScale(newNodeProperties.getWidth()
								* newNodeProperties.getScale()
								/ (float) Math.max(newNode.getHeight(), newNode
										.getWidth()));
					}

					if (newNodeProperties.getFit().equals("height")) {
						newNodeProperties.setScale(newNodeProperties.getWidth()
								* newNodeProperties.getScale()
								/ (float) newNode.getHeight());
					}

					if (newNodeProperties.getFit().equals("width")) {
						newNodeProperties.setScale(newNodeProperties.getWidth()
								* newNodeProperties.getScale()
								/ (float) newNode.getWidth());
					}
				}
				if (type.equals("image")) {
					image = new PImage(fileList[i].toString());
					if (image.getWidth() > image.getHeight()) {
						image.setHeight(image.getHeight()
								/ (image.getWidth() / newNodeProperties
										.getWidth()));
						image.setWidth(newNodeProperties.getWidth());
					} else if (image.getWidth() < image.getHeight()) {
						image.setWidth(image.getWidth()
								/ (image.getHeight() / newNodeProperties
										.getWidth()));
						image.setHeight(newNodeProperties.getWidth());
					} else {
						image.setWidth(newNodeProperties.getWidth());
						image.setHeight(newNodeProperties.getWidth());
					}
					newNode = image;
				}

				newNode.translate(newNodeProperties.getX(), newNodeProperties
						.getY());
				newNode.setScale(newNodeProperties.getScale());
				newNode.addAttribute("tooltip",
						newNodeProperties.getPath()
								.substring(
										newNodeProperties.getPath()
												.lastIndexOf("\\") + 1));
				newNode.addAttribute("path", newNodeProperties.getPath());
				newNode.addAttribute("handles", false);
				newNode.setPickable(true);
				this.addChild(newNode);

			}
			if (fileList[i].isDirectory()) {

				dir = new ZContainer(newNodeProperties, zap, camera);
				dir.setScale(newNodeProperties.getScale());
				this.addChild(dir);
			}
		}
	}

	public String textWrap(String s, int wrapColumns, char[] delimChars,
			char wrapChar) {

		char[] a = s.toCharArray();
		boolean found;
		int index = 0, prevIndex = 0;

		while (a.length - index > wrapColumns) {

			found = false;

			for (index += wrapColumns; index > prevIndex; --index) {

				for (int i = 0; i < delimChars.length; i++) {

					if (a[index] == delimChars[i]) {

						found = true;
						break;

					}

				}

				if (found)
					break;

			}

			if (!found)
				index += wrapColumns;

			a[index] = wrapChar;
			prevIndex = index;

		}

		return new String(a);

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
