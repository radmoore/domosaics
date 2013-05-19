package domosaics.model.tree.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import domosaics.model.configuration.Configuration;
import domosaics.model.tree.TreeI;
import domosaics.ui.util.MessageUtil;




/**
 * The AbstractTreeReader class handles the file reading for tree-format 
 * files like Newick and Nexus files. The actual parsing of the file is done in 
 * implemented subclasses like {@link NewickTreeReader} and {@link NexusTreeReader}
 * by implementing the method {@link #getTreeFromString(String)}.
 * <p>
 * If a parser has to parse the document line by line it should override the 
 * {@link #getTreeFromFile(File)} method and append a line separator after each line.
 *
 * @author Andreas Held
 * 
 */
public abstract class AbstractTreeReader implements TreeReader{

	/**
	 * Handles the stream opening and reading it. The parsing itself 
	 * is initiated by {@link #getTreeFromString(String)}. 
	 * This comes in handy for parsing files within a JAR file.
	 * 
	 * @param is
	 * 		input stream to a tree file
	 * @return
	 * 		the Tree after it is parsed	
	 */
	public TreeI getTreeFromStream(InputStream is) {
		try {		
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			StringBuffer strBuffer = new StringBuffer();
			String line;
		
			while((line = in.readLine()) != null) 
				strBuffer.append(line);

			is.close();
			in.close();	
			return getTreeFromString(strBuffer.toString());
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("Reading Tree file aborted");
		}
		return null;
	}
	
	
	/**
	 * Handles the file opening and reading from a file. The parsing itself 
	 * is initiated by {@link #getTreeFromString(String)}.
	 *
	 * @param file 
	 * 		the tree file
	 * @return 
	 * 		the Tree after it is parsed
	 */
	public TreeI getTreeFromFile(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file)); 	
			StringBuffer strBuffer = new StringBuffer();
			String line;
		
			while((line = in.readLine()) != null) 
				strBuffer.append(line);

			in.close();	
			return getTreeFromString(strBuffer.toString());
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("Reading Tree file aborted");
		}
		return null;
	}

	/**
	 * The method does the actual parsing and is implemented by format
	 * specific subclasses.
	 * 
	 * @param treeStr 
	 * 		a string containing a tree
	 * @return 
	 * 		the Tree after it is parsed
	 */
	public abstract TreeI getTreeFromString(String treeStr);
	
	/**
	 * Method which removes comments within square brackets.
	 * 
	 * @param str 
	 * 		tree String
	 * @return 
	 * 		string without comments
	 */
    protected static String removeComments(String str) {
    	int startPos = str.indexOf('[');
    	int stopPos = str.indexOf(']', startPos);
    	while (startPos != -1 && stopPos != -1) {
    		// eliminate comment
    		str = str.substring(0, startPos) + str.substring(stopPos + 1);

    		// find next comment
    		startPos = str.indexOf('[');
        	stopPos = str.indexOf(']', startPos);
    	}
    	return str;
    }
}
