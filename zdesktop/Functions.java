package zdesktop;

import java.io.File;

/**
 * This class have the the common static functions.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 */
public class Functions {

	/**
	 * Returns the type of file passed in the arguments.
	 * 
	 * @param path
	 *            Path of file.
	 * @return the type of file as a string.
	 */
	public static String typeOfFile(String path) {
		// Verifico se il percorso e` una directory...
		if (new File(path).isDirectory())
			return "dir";

		else { // ...o un file

			// Trovo l'ultima occorrenza del punto.
			int lastPoint = path.lastIndexOf('.');

			// Recupero l'estensione del file, estraendo dal carattere
			// successivo
			// l'ultimo punto, fino alla fine della stringa.
			String extension = path.substring(lastPoint + 1);

			if (extension.equalsIgnoreCase("txt")
					|| extension.equalsIgnoreCase("java")
					|| extension.equalsIgnoreCase("m")
					|| extension.equalsIgnoreCase("tex")
					|| extension.equalsIgnoreCase("htm")
					|| extension.equalsIgnoreCase("html")
					|| extension.equalsIgnoreCase("css"))
				return "text";
			else if (extension.equalsIgnoreCase("png")
					|| extension.equalsIgnoreCase("jpg")
					|| extension.equalsIgnoreCase("jpeg")
					|| extension.equalsIgnoreCase("gif"))
				return "image";
			if (extension.equalsIgnoreCase("zzz")
					|| extension.equalsIgnoreCase("zpng"))
				return "zdesktopfile";
		}
		return new String();
	}
}
