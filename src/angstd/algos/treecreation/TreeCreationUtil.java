package angstd.algos.treecreation;

import angstd.model.tree.TreeI;
import angstd.model.tree.io.NewickTreeReader;
import pal.distance.DistanceMatrix;
import pal.tree.Tree;
import pal.tree.TreeTool;

/**
 * TreeCreationUtil provides methods for creating a new tree using the 
 * external PAL library. 
 * The tree is created based on a distance matrix using an algorithm
 * such as UPGMA or NJ, which must be specified when calling createTree().
 * The trees are created based on a PAL distance matrix. Using the class
 * {@link DistanceMatrixUtil} helps to create those PAL objects using
 * Angstd data.
 * 
 * @author Andreas Held
 *
 */
public class TreeCreationUtil {

	/**
	 * Creates a new tree data structure based on a distance matrix object
	 * and an algorithm type such as UPGMA or Neighbor Joining.
	 * 
	 * @param dm
	 * 		the distance matrix (external PAL library object)
	 * @param type
	 * 		the algorithm type, e.g. UPGMA, NJ
	 * @return
	 * 		the created tree
	 */
	public static TreeI createTree(DistanceMatrix dm, TreeCreationAlgoType type) {
		if (type == TreeCreationAlgoType.NJ)
			return createNJTree(dm);
		else if(type == TreeCreationAlgoType.UPGMA)
			return createUPGMATree(dm);
		return null;
	}
	
	/**
	 * Helper method which creates a new tree based on a distance matrix
	 * using the neighbor joining algorithm.
	 * 
	 * @param dm
	 * 		the distance matrix used to create the tree
	 * @return
	 * 		the created tree
	 */
	private static TreeI createNJTree(DistanceMatrix dm) {
		// create neighbor joining tree using PAL library
		Tree palTree = TreeTool.createNeighbourJoiningTree(dm);
		
		// root the tree
		Tree midPointRooted = TreeTool.getMidPointRooted( palTree );
		
		// parse it to Angstd tree
		return new NewickTreeReader().getTreeFromString(midPointRooted.toString());
		//return new NewickTreeReader().getTreeFromString(palTree.toString());
	}
	
	/**
	 * Helper method which creates a new tree based on a distance matrix
	 * using the UPGMA algorithm.
	 * 
	 * @param dm
	 * 		the distance matrix used to create the tree
	 * @return
	 * 		the created tree
	 */
	private static TreeI createUPGMATree(DistanceMatrix dm) {
		// create unrooted UPGMA tree using PAL library
		Tree palTree = TreeTool.createUPGMA(dm);
		
		// root the tree
		Tree midPointRooted = TreeTool.getMidPointRooted( palTree );
		
		// parse it to Angstd tree
		return new NewickTreeReader().getTreeFromString(midPointRooted.toString());
	}
	

	
	
}
