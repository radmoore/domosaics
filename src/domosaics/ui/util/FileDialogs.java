package domosaics.ui.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import domosaics.model.configuration.Configuration;
import domosaics.ui.views.ViewType;




/**
 * Class FileDialogs is able to open a JfileChooser for opening and saving 
 * files. <br>
 * If a file was chosen correctly the file object is returned else null 
 * is returned.
 * <p>
 * The SaveDialog must be called using a {@link DoMosaicsFileFilter} object to ensure
 * that the correct extension is chosen. This type can be accessed by using 
 * {@link ViewType}. <br>
 * Also the last used file location is remembered by this class.
 * 
 * @author Andreas Held
 *
 */
public class FileDialogs {
	
	/** remember the last used location */
	private static String lastLocation = "";
	
	/**
	 * Opens a file dialog for opening files. If a file was correctly chosen 
	 * this file is returned else null is returned.
	 * 
	 * @param parent
	 * 		the component showing the dialog.
	 * @return 
	 * 		the selected file or null if the chooser was aborted
	 */
	public static File showOpenDialog(Component parent) {
		final JFileChooser f;
		
		// start the file chooser depending on the last used location
		if (!lastLocation.isEmpty())
			f = new JFileChooser(lastLocation);
		else
			f = new JFileChooser(Configuration.getInstance().getDefaultLocation());
		
		// wait for the chooser to be approved and set the file as well as the last location.
		if (f.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File file = f.getSelectedFile();
			lastLocation = file.getAbsolutePath();
			return file;
		}

		// aborted
		return null;
	}
	
	/**
	 * Opens a file dialog for saving files. If a file was correctly chosen 
	 * this file is returned else null is returned. If the file already exists,
	 * the user is asked if he wants to overwrite it.
	 * 
	 * @param extension 
	 * 		the extension for the file
	 * @param parent
	 * 		the component showing the dialog.
	 * @return 
	 * 		the selected file or null (if aborted)
	 */
	public static File showSaveDialog(Component parent, String extension) {
		final JFileChooser f;
		
		// start the file chooser depending on the last used location
		if (!lastLocation.isEmpty())
			f = new JFileChooser(lastLocation);
		else
			f = new JFileChooser(Configuration.getInstance().getDefaultLocation());
		
		FileFilter filter = new DoMosaicsFileFilter(extension);
		f.setFileFilter(filter);
		
		if (f.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File file = f.getSelectedFile();
			
			// if no extension was set, set it depending on the file filter
			if (getExtension (file) == null)
	    		file = new File(f.getSelectedFile().getAbsolutePath()+"."+filter.getDescription().toLowerCase());
		
			// check whether or not the file already exists
			if (file.exists()) 
				if (!MessageUtil.showDialog("File already exists. Overwrite it?"))
					return null;	        	
			
			// file is valid
	    	lastLocation = file.getAbsolutePath();
	    	return file;
		}
		return null;
	}
	
	public static File openChooseDirectoryDialog(Component parent) {
		final JFileChooser fc;
	
		// start the file chooser depending on the last used location
		fc = new JFileChooser(Configuration.getInstance().getDefaultLocation());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		
		if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}

		// aborted
		return null;
    }

	
	/**
	 * Returns the extension from a file object.
	 * 
	 * @param f 
	 * 		the file to check its extension
	 * @return 
	 * 		the extension of the given file
	 */
    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) 
            ext = s.substring(i+1).toLowerCase();
        return ext;
    }

}

/**
 * Class DoMosaicsFileFilter adds a file filter to a save dialog.
 * <p>
 * Therefore a pre selection of a specific export format is done 
 * by setting the correct file extension.
 * <p>
 * When a filter is used within a save dialog and no extension is specified
 * the extension of the filter is added to the file.
 *  
 * @author Andreas Held
 *
 */
class DoMosaicsFileFilter extends FileFilter{
	
	/**
	 * The file extension
	 */
	protected String description;
	
	/**
	 * Basic Constructor for a new file filter with a given extension
	 * 
	 * @param desc the filters extension
	 */
	public DoMosaicsFileFilter (String desc) {
		this.description = desc;
	}
	
	/**
	 * checks whether or not the file has the correct extension
	 */
	public boolean accept(File f) {
		if (f.isDirectory())	// display sub directories
			return true;
		return f.getName().toLowerCase().endsWith("."+description.toLowerCase());
	}

	/**
	 * Returns the file extension for the filter
	 */
	public String getDescription() { 
		return description; 
	}  
	
}