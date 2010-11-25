package angstd.model.tree.io;

import java.io.File;

import angstd.model.tree.TreeI;

/**
 * The TreeReader interface defines all methods necessary to parse a 
 * tree-format file. Actual implementations for TreeReader are the 
 * {@link NexusTreeReader} and {@link NewickTreeReader}. Both classes
 * extend the abstract class {@link AbstractTreeReader} which implements this 
 * interface.
 * 
 * @author Andreas Held
 *
 */
public interface TreeReader {

	/**
	 * Handles the file opening and reading from a file. The parsing itself 
	 * should be initiated by {@link #getTreeFromString(String)}. Therefore 
	 * the #getTreeFromString(String) method should be called within this method.
	 *  
	 * @param file 
	 * 		the tree file
	 * @return 
	 * 		the Tree after it is parsed
	 */
	public TreeI getTreeFromFile(File file);
	
	/**
	 * Parses a tree format string, e.g. a Newick or Nexus format and returns 
	 * a {@link TreeI} object. 
	 * 
	 * @param treeStr 
	 * 		the tree string
	 * @return 
	 * 		the parsed Tree
	 */
	public TreeI getTreeFromString(String treeStr);

}
