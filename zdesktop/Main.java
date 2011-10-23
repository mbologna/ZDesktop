package zdesktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

/**
 * This class acts as a starter for the main application.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 */
public class Main extends PFrame {

	private static final long serialVersionUID = 1L;

	private ZProperties zp;

	private ZApplicationProperties zap;

	public Main() {
		zap = new ZApplicationProperties();
		zp = new ZProperties();
	}

	/**
	 * This is the main method for the Piccolo applications. In order to use the
	 * Piccolo facilities, all the code needing Piccolo must be in this method.
	 */
	public synchronized void initialize() {

		final PCanvas canvas = getCanvas();
		final PLayer layer = canvas.getLayer();
		final PText tooltipNode = new PText();
		final PCamera camera = canvas.getCamera();
		final ZContainer zc;
		tooltipNode.setPickable(false);
		camera.addChild(tooltipNode);

		File ftemp;
		boolean readConf;
		FileOutputStream fos;

		try {
			readConf = zap.loadZConfiguration("conf.zc");
		} catch (Exception e) {
			readConf = false;
		}

		if (!readConf) {
			/*
			 * Set the default options if the options file is not found.
			 */
			zap.setZDesktopRoot("Test");
			zap.setAlpha(0.5f);
			zap.setIgnoreZZZfiles(false);
			zap.setScaleFactor(0.25f);
			zap.setThumbnailHeight(100);
			zap.setThumbnailWidth(100);
			zap.setTransitionScale(1);
			zap.setTooltipColor(Color.YELLOW);
			System.out
					.println("==> Configuration file (conf.zc) not found or corrupted, using default configuration and saving it");
			System.out
					.println("==> Edit conf.zc in program's root folder to change the default behaviour");

		} else {

			/*
			 * The options file is found, reading the options supplied.
			 */

			System.out
					.println("==> Configuration file (conf.zc) found, using customized configuration");
		}
		ftemp = new File(zap.getZDesktopRoot());
		if (!ftemp.exists() || ftemp.listFiles().length == 0) {
			ftemp = new File("Test");
			if (!ftemp.exists()) {
				ftemp.mkdir();
			}
			if (ftemp.listFiles().length == 0) {
				fos = null;
				try {
					fos = new FileOutputStream(new File("Test\\example.txt"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new PrintStream(fos)
						.println("If you are seeing this, it means that you haven't specified a config file (it's in ZDesktop's root directory, conf.zc) and the default directory (\"Test\") is empty or not exists.\nChange the configuration by exiting ZDesktop and open conf.zc with an ASCII editor, or put some files in \"Test\" directory.");
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		zp.setPath(ftemp.toString());
		zp.setX(0);
		zp.setY(0);
		zp.setWidth(200);
		zp.setHeight(200);
		zp.setInitialBackgroundColor(Color.YELLOW);
		zp.setFinalBackgroundColor(Color.WHITE);
		zp.setFinalForegroundColor(Color.WHITE);
		zp.setInitialForegroundColor(Color.BLACK);
		zp.setMargin(0.8f);
		zp.setFit("max");
		zp.setWordWrap(true);
		zp.setWordWrapColumn(40);
		zp.saveZProperty(ftemp.toString() + ".zzz");

		zc = new ZContainer(zp, zap, getCanvas().getCamera());
		layer.addChild(zc);

		/*
		 * Removing the old zoom handler, and adding the click to zoom facility,
		 * discrete zoom handling and tooltip handler.
		 */

		canvas.removeInputEventListener(canvas.getZoomEventHandler());
		ZInputEventHandler zi = new ZInputEventHandler(zap.getScaleFactor(),
				tooltipNode, zap.getTooltipColor());
		canvas.addInputEventListener(zi);

		DragEventHandler pdeh = new DragEventHandler(zi);
		pdeh.setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		canvas.addInputEventListener(pdeh);

		/*
		 * Associating the pan event handler to mouse button 2.
		 */

		canvas.getPanEventHandler().setEventFilter(
				new PInputEventFilter(InputEvent.BUTTON2_MASK));

		/*
		 * Setting frame title and icon.
		 */

		setTitle("ZUI ZDesktop");
		setIconImage(Toolkit.getDefaultToolkit().getImage("icon.zicon"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle aBounds = getBounds();
		setFullScreenMode(false);
		setSize(dim.width / 2, dim.height / 2);
		setLocation((dim.width - aBounds.width) / 2,
				(dim.height - aBounds.height) / 2);
		camera.animateViewToCenterBounds(zc.getGlobalFullBounds(), true, 500);
		validate();
		setVisible(true);
		canvas.requestFocus();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				zap.saveZConfiguration("conf.zc");
				System.out.println("==> ZDesktop closed, have a nice day");
			}
		});
	}

	/**
	 * Entry point of the Java applications. Based on the arguments specified,
	 * this method runs the graphical ZUI or the cleaner.
	 */

	public synchronized static void main(String[] args) {
		switch (args.length) {
		case 0:
			new Main();
			break;
		case 2:
			if (args[0].equals("clean")) {
				System.out.println("==> Cleaning " + args[1]);
				recursiveDelete(args[1]);
				System.out.println("-----------------------------");
				System.out
						.println("\n==> Current directory successfully cleaned");
			}
			break;
		default:
			System.out.println("==> Usage: ZDesktop");
			System.out
					.println("==> edit conf.zc to see/change the program options");
			System.out.println("==> Usage: ZDesktop clean <dir>");
			System.out.println("==> to clean out <dir> from .zzz files");
			break;
		}
	}

	/**
	 * This method deletes the *.zzz and other ZUI ZDesktop's files from the
	 * path specified as argument and ALL of his subdirectories.
	 * 
	 * @param path,
	 *            the path where deletes the ZUI ZDesktop's files.
	 */
	public static void recursiveDelete(String path) {
		File f = new File(path);
		File[] fileList = f.listFiles();
		int i;

		for (i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				System.out.println("\tCleaning " + fileList[i]);
				recursiveDelete(fileList[i].toString());
			}
			if (fileList[i].isFile()
					&& Functions.typeOfFile(fileList[i].toString()).equals(
							"zdesktopfile")) {
				System.out.println("\t\tDeleting " + fileList[i]);
				fileList[i].delete();
			}
		}
	}
}
