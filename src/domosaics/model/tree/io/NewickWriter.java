package domosaics.model.tree.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domosaics.model.configuration.Configuration;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;




/**
 * Class to export tree data structures into the Newick format.
 * 
 * @author Andreas Held (based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class NewickWriter {
	
	/**
	 * Writes a tree datastructure into a Newick formatted file.
	 * 
	 * @param file
	 * 		output file
	 * @param tree
	 * 		tree structure to convert into Newick
	 */
	public static void write(File file, TreeI tree) {
		try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            write(out, tree);        
            out.close();
        } 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
    }
	
	public static void write(BufferedWriter out, TreeI tree) {
		try {
            String theString = getStringFromTree(tree);     
            out.write(theString);
            out.write("\r\n");
            out.flush();            
        } 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
        }
    }
	
	private static String getStringFromTree(TreeI tree) {
		StringBuffer treeString = new StringBuffer();
	    TreeNodeI root = tree.getRoot();
	    appendNode(root, treeString);
	    return treeString.toString() + ";";    	
	}
	
	/**
     * This is an internal method to parse the given String buffer.
     * The method converts the given node to newick string and appends 
     * the result to the given StringBuffer.
     * 
     * @param node
     * @param treeString
     */
    private static void appendNode(TreeNodeI node, StringBuffer s) {
        if(node == null)
            return;
        
        if(!node.isLeaf()){
            s.append("(");
            boolean added = false;
            
            Iterator<TreeNodeI> iter = node.getChildIter();
            while(iter.hasNext()) {
            	TreeNodeI n = iter.next();
                appendNode(n, s);
                s.append(',');
                added = true;
            }
            if(added)
                s.deleteCharAt(s.length()-1);
            s.append(")");
        }
        if(node.getLabel() != null){
     
        	// replace label characters: ":"
        	
        	String label = node.getLabel();
        	if(label.indexOf(":") >=0){
        		Pattern p = Pattern.compile(":");
        		Matcher matcher = p.matcher(label);;
        		label = matcher.replaceAll("_");        		
        	}
//    		Pattern spacePattern = Pattern.compile("\\s");
//    		Matcher matcher = spacePattern.matcher(label);;
//    		label = matcher.replaceAll("\\\\$0");        		

            s.append(label);
        }        
        if(node.getDistanceToParent() != 1.0 && node.getDistanceToParent() >= 0){
            s.append(":" + node.getDistanceToParent());
        }            
    }
	
}
