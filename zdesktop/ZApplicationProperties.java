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
 * This class is the container of the application properties.
 * 
 * @author Michele
 * 
 */

public class ZApplicationProperties {

	private float alpha;

	private float scaleFactor;

	private String ZDesktopRoot = null;

	private boolean ignoreZZZfiles;

	private int thumbnailWidth;

	private int thumbnailHeight;

	private float transitionScale;

	private Color tooltipColor;

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public ZApplicationProperties() {

	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public boolean isIgnoreZZZfiles() {
		return ignoreZZZfiles;
	}

	public void setIgnoreZZZfiles(boolean ignoreZZZfiles) {
		this.ignoreZZZfiles = ignoreZZZfiles;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public String getZDesktopRoot() {
		return ZDesktopRoot;
	}

	public void setZDesktopRoot(String desktopRoot) {
		ZDesktopRoot = desktopRoot;
	}

	public synchronized boolean loadZConfiguration(String filename) {
		File fconf = new File(filename);
		Properties p = new Properties();
		try {
			p.loadFromXML(new FileInputStream(fconf));
		} catch (InvalidPropertiesFormatException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		ZDesktopRoot = p.getProperty("ZDesktopRoot");
		scaleFactor = Float.parseFloat(p.getProperty("ScaleFactor"));
		alpha = Float.parseFloat(p.getProperty("Alpha"));
		ignoreZZZfiles = Boolean.parseBoolean(p.getProperty("IgnoreZZZfiles"));
		thumbnailHeight = Integer.parseInt(p.getProperty("ThumbnailHeight"));
		thumbnailWidth = Integer.parseInt(p.getProperty("ThumbnailWidth"));
		transitionScale = Float.parseFloat(p.getProperty("TransitionScale"));
		tooltipColor = new Color(Integer
				.parseInt(p.getProperty("TooltipColor")));
		return true;
	}

	public void saveZConfiguration(String filename) {
		File fconf = new File(filename);
		Properties p = new Properties();
		p.setProperty("ZDesktopRoot", this.ZDesktopRoot);
		p.setProperty("ScaleFactor", Float.toString(scaleFactor));
		p.setProperty("IgnoreZZZfiles", Boolean.toString(ignoreZZZfiles));
		p.setProperty("Alpha", Float.toString(alpha));
		p.setProperty("ThumbnailHeight", Integer.toString(thumbnailHeight));
		p.setProperty("ThumbnailWidth", Integer.toString(thumbnailWidth));
		p.setProperty("TransitionScale", Float.toString(transitionScale));
		p.setProperty("TooltipColor", Integer.toString(tooltipColor.getRGB()));
		try {
			p.storeToXML(new FileOutputStream(fconf),
					"ZUI ZDesktop Configuration File");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public float getTransitionScale() {
		return transitionScale;
	}

	public void setTransitionScale(float transitionScale) {
		this.transitionScale = transitionScale;
	}

	public Color getTooltipColor() {
		return tooltipColor;
	}

	public void setTooltipColor(Color tooltipColor) {
		this.tooltipColor = tooltipColor;
	}
}
