package angstd.model.tree.io;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.util.MessageUtil;



/**
 * The NexusTreeReader class extends {@link AbstractTreeReader} where the  
 * file handling is done. This class does only the parsing for Nexus formatted
 * trees and creates a {@link TreeI} object as result.
 * <p>
 * A Nexus file format consists of different blocks. To parse the tree out of 
 * a Nexus file only the TAXA and TREE block are important. Generally 
 * comments within square brackets are always possible, so the parser 
 * removes them.
 * <p>
 * The TAXA block consists of TAXLABELS which can be used as shortcut 
 * for taxonomy names within the tree. A description for the TAXA section 
 * within a Nexus file looks like follows: 
 * <pre>
 * BEGIN TAXA;
 *   ...
 *   TAXLABELS taxon_name [taxon_name] ...;
 * END;
 * </pre>
 * The TREE block can also consist of a TRANSLATE clause where TAXLABELS can
 * be specified. The actual tree is indicate by a TREE clause followed by
 * a Nexus formatted tree which may use TAXLABELS as shortcut. A description 
 * for the TREE section within a Nexus file looks like follows: 
 * <pre>
 * BEGIN TRRES;
 *   [TRANSLATE]
 *     Number	String
 *     [...]	[...]
 *   ;
 *   TREE [*] treename = (NexusTree);
 * END;
 * </pre>
 * The Parser parses both blocks and uses the NewickTreeParser 
 * to parse the actual tree and replaces TAXLABELS with their real names.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 */
public class NexusTreeReader extends AbstractTreeReader {
	
	/**
	 * Parses a Nexus formatted tree. In a first step the TAXA and TREE blocks 
	 * are extracted from the Nexus string and comments are removed. 
	 * Both sections are parsed to get the maps for taxlabel-shortcuts and their
	 * associated real taxonomy names. In a next step the Newick string is 
	 * extracted and parsed.
	 * 
	 * @param nexusStr 
	 * 		The string which is gained from the Nexus file
	 * @return 
	 * 		the parsed tree
	 */
	public TreeI getTreeFromString(String nexusStr) {
		String treeStr = ""; 
		String taxaStr = "";
		
		// extract taxa block
		int taxaBlockStart = nexusStr.toUpperCase().indexOf("BEGIN TAXA;");
		int taxaBlockEnd = nexusStr.toUpperCase().indexOf("END;", taxaBlockStart);
		if (taxaBlockStart != -1 && taxaBlockEnd != -1)
			taxaStr = nexusStr.substring(taxaBlockStart, taxaBlockEnd);
		
		// extract tree block
		int treeBlockStart = nexusStr.toUpperCase().indexOf("BEGIN TREES;");
		int treeBlockEnd = nexusStr.toUpperCase().indexOf("END;", treeBlockStart);
		if (treeBlockStart != -1 && treeBlockEnd != -1)
			treeStr = nexusStr.substring(treeBlockStart, treeBlockEnd);
		else {
			MessageUtil.showWarning("Wrong format Exception: the Nexus file does not contain a TREE-block");
			return null;
		}
		
		// remove comments
		treeStr = removeComments(treeStr);
		taxaStr = removeComments(taxaStr);
		
		// create label maps from taxa block and translate block
		Map<String, String> taxaMap = readTaxLabels(taxaStr);
		Map<String, String> translateMap = readTranslation(treeStr);
		
		// get Newick String from tree block
		String newickTreeStr = readNewickTree(treeStr);
		
		// parse tree
		return parse(newickTreeStr, taxaMap, translateMap);
	}
	
	/**
	 * The Newick tree string is parsed using a {@link NewickTreeReader} and 
	 * the node labels are replaced using the taxa respectively the 
	 * translation map if present.
	 * 
	 * @param newickTreeStr
	 * 		the Newick tree string within the nexus string
	 * @param taxaMap 
	 * 		the parsed taxa map
	 * @param translateMap 
	 * 		the parsed translation map
	 * @return the parsed tree
	 */
	private TreeI parse(String newickTreeStr, Map<String, String> taxaMap, Map<String, String> translateMap) {
		if (newickTreeStr.isEmpty())
			return null;

		// parse the tree using the nexus parser
		TreeI tree = new NewickTreeReader().getTreeFromString(newickTreeStr);
		
		// if there are no mapping definitions within the nexus file return the tree as it is
		if (translateMap == null && taxaMap == null)
			return tree;
		
		// else map the labels using taxamap and translate Map
		Iterator<TreeNodeI> nodeIter = tree.getNodeIterator();
		
		while (nodeIter.hasNext()) {
			TreeNodeI actNode = nodeIter.next();

			if (actNode.getLabel().isEmpty())
				continue;
			
			if(translateMap != null && translateMap.get(actNode.getLabel()) != null)
				actNode.setLabel(translateMap.get(actNode.getLabel()));
			else if(taxaMap != null && taxaMap.get(actNode.getLabel()) != null)
				actNode.setLabel(taxaMap.get(actNode.getLabel()));           
		
		}
		return tree;
	}
	
	/**
	 * Extracts the Newick tree from a TREE block in a Nexus string
	 * 
	 * @param treeStr 
	 * 		the TREE block
	 * @return 
	 * 		the Newick tree string
	 */
    private String readNewickTree(String treeStr) {
    	int startPos = treeStr.indexOf("TREE", 12);			// look behind the "BEGIN TREES;"-tag
    	int endPos = treeStr.indexOf(';', startPos);
    	
    	 if (startPos == -1 || endPos == -1)
    		 return "";
    	 
    	 // now find the actual start of the Newick format (tree);
    	 startPos = treeStr.indexOf("(", startPos);
    	 
    	 if (startPos == -1)
    		 return "";
    	 
    	return treeStr.substring(startPos, endPos+1);
    }
	
    /**
     * Creates a HashMap for taxlabel shortcuts and their real names
     * from a TAXLABELS clause within the TAXA block.
     * 
     * @param taxaStr 
     * 		the TAXA block within the Nexus format
     * @return 
     * 		the mapping between taxlabel shortcuts and their real names
     */
	private Map<String, String> readTaxLabels(String taxaStr) {
		// if a taxa block is not specified within the Nexus file
		if (taxaStr.isEmpty())
			return null;
		
		Map<String, String> taxaMap = new HashMap<String, String>();
		
		// remove all comments within the string
		taxaStr = removeComments(taxaStr);
		
		// get only the taxalabels as string
		int startPos = taxaStr.toUpperCase().indexOf("TAXLABELS");
		int stopPos = taxaStr.toUpperCase().indexOf(";", startPos);
		
		// skip taxlabels tag and ";"
		taxaStr = taxaStr.substring(startPos+9, stopPos -1);	

		// split the taxlabel string into an array
		String[] taxa = taxaStr.split("\\s+");
		
		for (int i = 1; i < taxa.length; i++) 
			taxaMap.put(Integer.valueOf(i).toString(), taxa[i]);
		
		return taxaMap;
	}
	
	/**
	 * Creates a HashMap for taxlabel shortcuts and their real names from
	 * a TRANSLATE clause within a TREE block.
	 * 
	 * @param treeStr 
	 * 		the TREE block within the Nexus format
	 * @return 
	 * 		the mapping between taxlabel shortcuts and their real names
	 */
	private Map<String, String> readTranslation (String treeStr) {		
		Map<String, String> taxaMap = new HashMap<String, String>();
		
		// get the translation part within the tree block if it occurs
		int startPos = treeStr.toUpperCase().indexOf("TRANSLATE");
		int stopPos = treeStr.toUpperCase().lastIndexOf("TREE");
		
		if (startPos == -1)
			return null;
		
		treeStr = treeStr.substring(startPos, stopPos);
		
		// remove all comments within the string
		treeStr = removeComments(treeStr);
		
		// modify the translation String
		treeStr = treeStr.replace("TRANSLATE","");
		treeStr = treeStr.replace(";","");
		String[] taxa = treeStr.split(",");
		
		// create Hashmap
		String[] keyAndValue;
		int j;
		for (int i = 0; i < taxa.length; i++) {
			// split translate-entry into key and value for map
			keyAndValue = taxa[i].split("\\s+");
			
			// find key and value within the array
			j = 0;
			while (keyAndValue[j].isEmpty())
				j++;
			
			taxaMap.put(keyAndValue[j], keyAndValue[j+1]);
		}
		return taxaMap;
	}
}
