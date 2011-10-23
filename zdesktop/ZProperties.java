package zdesktop;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * This class describes the properties of the nodes and store/load them from a
 * file.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 */
public class ZProperties {

	private float height;

	private String path;

	private float width;

	private float x;

	private float y;
	
	private float margin;

	private float scale;

	private String fit = null;

	private Color initialBackgroundColor = null;

	private Color finalBackgroundColor = null;

	private Color initialForegroundColor = null;

	private Color finalForegroundColor = null;

	private boolean wordWrap;

	private int wordWrapColumn;

	public ZProperties() {

	}

	/**
	 * @return Returns the height.
	 * @uml.property name="height"
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @return Percorso dell'oggetto grafico.
	 * @uml.property name="path"
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return Returns the scaleFactor.
	 * @uml.property name="scaleFactor"
	 */

	public float getWidth() {
		return width;
	}

	/**
	 * @return Coordinata x dell'angolo superiore sinistro.
	 * @uml.property name="x"
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return Coordinata y dell'angolo superiore sinistro.
	 * @uml.property name="y"
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return Returns the zDesktopRoot.
	 * @uml.property name="zDesktopRoot"
	 */

	public boolean loadZProperty(String filename) {
		File fproperties = new File(filename);
		Properties p = null;

		if (fproperties.exists()) {
			p = new Properties();
			try {
				p.loadFromXML(new FileInputStream(fproperties));
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				return false;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
			this.setPath(p.getProperty("Path"));
			this.setX(Float.parseFloat(p.getProperty("X")));
			this.setY(Float.parseFloat(p.getProperty("Y")));
			this.setWidth(Float.parseFloat(p.getProperty("Width")));
			this.setHeight(Float.parseFloat(p.getProperty("Height")));
			this.setScale(Float.parseFloat(p.getProperty("Scale")));
			this.setMargin(Float.parseFloat(p.getProperty("Margin")));
			this.setFit(p.getProperty("Fit"));
			this.setWordWrap(Boolean.parseBoolean(p.getProperty("WordWrap")));
			this.setWordWrapColumn(Integer.parseInt(p
					.getProperty("WordWrapColumn")));
			this.setInitialForegroundColor(new Color(Integer.parseInt(p
					.getProperty("InitialForegroundColor"))));
			this.setFinalForegroundColor(new Color(Integer.parseInt(p
					.getProperty("FinalForegroundColor"))));
			this.setInitialBackgroundColor(new Color(Integer.parseInt(p
					.getProperty("InitialBackgroundColor"))));
			this.setFinalBackgroundColor(new Color(Integer.parseInt(p
					.getProperty("FinalBackgroundColor"))));
			return true;
		}
		return false;
	}

	public void saveZProperty(String filename) {
		Properties p = new Properties();
		p.setProperty("Path", this.getPath());
		p.setProperty("X", Float.toString(this.getX()));
		p.setProperty("Y", Float.toString(this.getY()));
		p.setProperty("Width", Float.toString(this.getWidth()));
		p.setProperty("Height", Float.toString(this.getHeight()));
		p.setProperty("Scale", Float.toString(this.getScale()));
		p.setProperty("Margin", Float.toString(this.getMargin()));
		p.setProperty("Fit", this.fit);
		p.setProperty("WordWrap", Boolean.toString(this.isWordWrap()));
		p.setProperty("WordWrapColumn", Integer.toString(this
				.getWordWrapColumn()));
		p.setProperty("FinalBackgroundColor", Integer
				.toString(finalBackgroundColor.getRGB()));
		p.setProperty("InitialBackgroundColor", Integer
				.toString(initialBackgroundColor.getRGB()));
		p.setProperty("FinalForegroundColor", Integer
				.toString(finalForegroundColor.getRGB()));
		p.setProperty("InitialForegroundColor", Integer
				.toString(initialForegroundColor.getRGB()));
		try {
			p.storeToXML(new FileOutputStream(filename),
					"ZDesktop File Object Properties for " + filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @param path
	 *            Percorso dell'oggetto grafico da settare.
	 * @uml.property name="path"
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @param x
	 *            Coordinata x dell'angolo superiore sinistro da settare.
	 * @uml.property name="x"
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            Coordinata y dell'angolo superiore sinistro da settare.
	 * @uml.property name="y"
	 */
	public void setY(float y) {
		this.y = y;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getFit() {
		return fit;
	}

	public void setFit(String fit) {
		this.fit = fit;
	}

	public Color getFinalBackgroundColor() {
		return finalBackgroundColor;
	}

	public void setFinalBackgroundColor(Color finalBackgroundColor) {
		this.finalBackgroundColor = finalBackgroundColor;
	}

	public Color getFinalForegroundColor() {
		return finalForegroundColor;
	}

	public void setFinalForegroundColor(Color finalForegroundColor) {
		this.finalForegroundColor = finalForegroundColor;
	}

	public Color getInitialBackgroundColor() {
		return initialBackgroundColor;
	}

	public void setInitialBackgroundColor(Color initialBackgroundColor) {
		this.initialBackgroundColor = initialBackgroundColor;
	}

	public Color getInitialForegroundColor() {
		return initialForegroundColor;
	}

	public void setInitialForegroundColor(Color initialForegroundColor) {
		this.initialForegroundColor = initialForegroundColor;
	}

	public boolean isWordWrap() {
		return wordWrap;
	}

	public void setWordWrap(boolean wordWrap) {
		this.wordWrap = wordWrap;
	}

	public int getWordWrapColumn() {
		return wordWrapColumn;
	}

	public void setWordWrapColumn(int wordWrapColumn) {
		this.wordWrapColumn = wordWrapColumn;
	}

	public float getMargin() {
		return margin;
	}

	public void setMargin(float margin) {
		this.margin = margin;
	}

}
