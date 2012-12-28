package domosaics.algos.treecreation;

import domosaics.algos.distance.DistanceMeasureType;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.sequence.SequenceI;
import pal.alignment.Alignment;
import pal.alignment.AlignmentUtils;
import pal.alignment.SimpleAlignment;
import pal.datatype.DataType;
import pal.datatype.DataTypeTool;
import pal.distance.DistanceMatrix;
import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.SimpleIdGroup;


/**
 * PALAdapter is a helper class which helps to create objects of the
 * external PAL library using DoMosaicS data objects. For instance a
 * PAL distance matrix object using domain arrangements and
 * a distance measure can be created.
 * Those PAL objects are used during the tree creation process.
 * 
 * @author Andreas Held
 *
 */
public class PALAdapter {

	/**
	 * Method which creates a PAL distance matrix based on a data set 
	 * consisting of arrangements and a distance measure to calculate the 
	 * distances.
	 * 
	 * @param daSet
	 * 		the arrangements which pairwise distances should be calculated
	 * @param measure
	 * 		the used distance measure for the task
	 * @return
	 * 		a PAL distance matrix object
	 */
	public static DistanceMatrix createMatrix(DomainArrangement[] daSet, DistanceMeasureType measure) {
		// create an IDGroup object from the PAL library
		Identifier[] identifers = new Identifier[daSet.length];
		for (int i = 0; i < daSet.length; i++) 
			identifers[i] = new Identifier(daSet[i].getName());
		IdGroup idGroup = new SimpleIdGroup(identifers);
		
		// calculate the matrix using an DoMosaicS similarity measure
		double[][] matrix = measure.getAlgo().calc(daSet, false);
		
		return new DistanceMatrix(matrix, idGroup);
	}
	
	/**
	 * Adapter to convert an array of sequences into the alignment data 
	 * structure of the PAL library.
	 * 
	 * @param seqs
	 * 		aligned sequences 
	 * @return
	 * 		PAL alignment data structure
	 */
	public static Alignment createAlignment(SequenceI[] seqs) {
		Identifier[] identifers = new Identifier[seqs.length];
		String[] seqStrs = new String[seqs.length];
	
		
		for (int i = 0; i < seqs.length; i++) {
			identifers[i] = new Identifier(seqs[i].getName());
			seqStrs[i] = seqs[i].getSeq(true);
		}
		
		//Nico: Only alignments of amino-acids 
		//DataType type = AlignmentUtils.getSuitableInstance(seqStrs);
		DataType type = DataTypeTool.getUniverisalAminoAcids();
		
		return new SimpleAlignment(identifers, seqStrs, type);
	}
	
	/**
	 * Helper method checking whether or not the sequences are already aligned
	 * 
	 * @param seqs
	 * 		the sequences which are used to search for an alignment character
	 * @return
	 * 		whether or not the specified set of sequences is aligned
	 * 		
	 */
	public static boolean isAligned(SequenceI[] seqs) {
		if (seqs == null)
			return true;
		
		for (int i = 0; i < seqs.length; i++) 
			if (seqs[i].getSeq(true).contains("-"))
				return true;
		return false;
	}
}
