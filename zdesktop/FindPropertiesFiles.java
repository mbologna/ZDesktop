package zdesktop;

import java.io.File;
import java.io.FileFilter;

/**
 * This class filter all the *.zzz files.
 * 
 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
 */
public class FindPropertiesFiles implements FileFilter {
	/**
	 * Funzione che filtra da una lista di file quelli che non hanno estensione
	 * .zproperties.
	 * 
	 * @return true se l'estensione del file è zproperties, false altrimenti.
	 */
	public boolean accept(File file) {
		if (file.toString().lastIndexOf(".zzz") != -1)
			return true;
		else
			return false;
	}

}
