package zdesktop;

import java.io.File;
import java.io.FileFilter;

public class FindFiles implements FileFilter {
	/**
	 * This class filter all files except the *.zzz.
	 * 
	 * @author Michele Bologna (michele.bologna@studenti.unibg.it)
	 */
	public boolean accept(File file) {
		if ((file.toString().lastIndexOf(".zzz") != -1)
				|| (file.toString().lastIndexOf(".zpng") != -1))
			return false;
		else
			return true;
	}
}
