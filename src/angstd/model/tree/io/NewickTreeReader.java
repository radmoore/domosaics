package angstd.model.tree.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import angstd.model.tree.Tree;
import angstd.model.tree.TreeEdge;
import angstd.model.tree.TreeEdgeI;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeFactory;
import angstd.model.tree.TreeNodeI;
import angstd.ui.util.MessageUtil;

/**
 * The NewickTreeReader class extends {@link AbstractTreeReader} where the  
 * file handling is done. This class does only the parsing for Newick formatted
 * trees and creates a {@link TreeI} object as result.
 * <p>
 * Therefore it is necessary to create new {@link TreeNodeI}s using 
 * a {@link TreeNodeFactory}.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class NewickTreeReader extends AbstractTreeReader {
	private TreeNodeFactory nodeFactory;
 
	/**
	 * Parses a Newick formatted tree. If the string contains more than one 
	 * tree, only the first tree is parsed. The method removes in a first step 
	 * all comments within the text.
	 * 
	 * @param newickStr 
	 * 		The string which contains a Newick formatted tree
	 * @return 
	 * 		the parsed tree
	 */
	public TreeI getTreeFromString(String newickStr) {
		try {
			TreeI tree;
		
			// remove the comments within the tree
			removeComments(newickStr);
		
			// split tree strings if there is more than one tree in the file
			String[] treeStrs = newickStr.split(";");	
		
			// return only the first tree as tree object
			tree = parse(treeStrs[0]);
			return tree;
		} catch (IOException e) {
			MessageUtil.showWarning("IO Exception: failed to parse newick tree from string");
		}
		return null;				
    }
	
	/**
	 * Here the actual parsing of a Newick tree takes place.
	 * 
	 * @param treeStr 
	 * 		the Newick tree string
	 * @return 
	 * 		the parsed tree
	 */
	private TreeI parse(String treeStr) throws IOException  {	
		this.nodeFactory = new TreeNodeFactory();
		
		// create NewickStreamTokenizer (inner class) which is used to parse the format
		NewickStreamTokenizer nst = new NewickStreamTokenizer(new StringReader(treeStr));
		
		// create a new empty tree containing an dummy root and a first empty child
		TreeI tree = new Tree();
		TreeNodeI root = nodeFactory.createNode();
		TreeNodeI node = nodeFactory.createNode();
		TreeNodeI child = null;
		tree.addNode(root);
		tree.addNode(node);
		TreeEdgeI edge = new TreeEdge(root, node);
		tree.addEdge(edge);
		tree.setRoot(root);
		
		// start parsing the string
		String label = "";
		double distance = -1.0;
		int token;
		boolean distanceFlag = false;
		boolean escapedFlag = false;
		
	
		while ((token = nst.nextToken()) != StreamTokenizer.TT_EOF) {
			
			// escapeChars are appended to the label
			if(escapedFlag){
            	if(nst.sval != null)
            		label.concat(nst.sval);
            	else
            		label.concat(Character.toString((char)token));
            	escapedFlag = false;
            	continue;
            }

			// if token is a word
			if (token == StreamTokenizer.TT_WORD) {
				if (distanceFlag) {	// check if a distance have to be set next
					distance = Double.parseDouble(nst.sval);
					distanceFlag = false;
				}					// else its a label
				else 
					label = nst.sval;
				continue;
			}
			
			// if token is specified as functional trigger appropriate function
			switch ((char) nst.ttype) {
			case '(':
                child = nodeFactory.createNode(); 
                tree.addNode(child);
                edge = new TreeEdge(node, child);
                tree.addEdge(edge);
                node = child;
                break;
			case ')':
                node.setLabel(label); label = "";
				if (distance != -1.0) {
					node.getEdgeToParent().setWeight(distance);
					distance = -1.0;
				}                      
                node = (TreeNodeI) node.getParent();
                break;
			case ',':
				node.setLabel(label); label = "";
				if (distance != -1.0) {
					node.getEdgeToParent().setWeight(distance);
					distance = -1.0;
				}    
                child = nodeFactory.createNode();
                tree.addNode(child);
                edge = new TreeEdge((TreeNodeI) node.getParent(), child);
                tree.addEdge(edge);
                node = child;
                break;
			case ':':
				distanceFlag = true;
				break;
			case ';':
                node.setLabel(label); label="";
				if (distance != -1.0) 
					if (node.getParent() != null) {
						node.getEdgeToParent().setWeight(distance);
						distance = -1.0;
					} 
                break;
			 case '\'':
             case '"':                    	
                 label.concat(nst.sval);
                 break;
             case '\\':                    	
                 escapedFlag = true;
                 break;
			}	
			
		}
	
		// kick the dummy root if necessary
		if(((TreeNodeI)tree.getRoot()).childCount() == 1){
			child = (TreeNodeI) (tree.getRoot()).getChildAt(0);
			child.removeEdge(child.getEdgeToParent());
	        tree.removeNode(tree.getRoot());
	        tree.setRoot(child);
	    } 
		
		tree.initBootstrapValues();
		 
		return tree;
	}
	
	/**
	 * Inner class NewickStreamTokenizer defines the functional
	 * token within a Newick string.
	 */
	class NewickStreamTokenizer extends StreamTokenizer {

	    public NewickStreamTokenizer(Reader r) {
	        super(r);
	        resetSyntax();
	        wordChars(0, 255);
	        whitespaceChars(0, '\n');

	        ordinaryChar(';');
	        ordinaryChar(',');
	        ordinaryChar(')');
	        ordinaryChar('(');
	        ordinaryChar(':');
	        ordinaryChar('\\');
	        quoteChar('"');
	        quoteChar('\'');
	    }
	}
}
