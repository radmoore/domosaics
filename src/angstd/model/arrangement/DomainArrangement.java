package angstd.model.arrangement;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import angstd.model.AngstdData;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.model.tree.TreeNodeI;

/**
 * Class DomainArrangement represents a data structure do describe domain 
 * arrangements of proteins. 
 * <br>
 * Basically this structure stores the domains of a protein in their linear 
 * order within a {@link DomainVector}.
 * <br>
 * A Sequence can be assigned to a domain arrangement, which is allowed to 
 * contain gap characters. The assignment of a sequence is for instance
 * necessary to visualize domain arrangements in MSA view,mark subsequences of
 * it and export sequences of domains.
 * <br>
 * 
 * @author Andreas Held
 * @author Andrew Moore <radmoore@uni-muenster.de>
 *
 */
public class DomainArrangement implements Cloneable, AngstdData{

	/** arrangement name to connect arrangement with a tree and sequence */
	protected String id;
	
	/** annotated domains within the arrangement */
	protected DomainVector doms;
	
	/** 
	 * Its possible to use not all possible domains of an arrangement. 
	 * E.g. the user may be interested in hiding some domains with 
	 * bad evalue. Its not enough to hide just graphical components, 
	 * because most of the algorithms are using the backend data structure.
	 * This domain vector stores the domains which are currently not 
	 * used for the arrangement but could be.
	 */
	protected DomainVector hiddenDoms;
	
	/** underlying sequence for a domain arrangement */
	protected SequenceI seq;

	/** if no sequence is assigned take the value from the xdom format */
	protected int seqLen;
	
	/** protein description which may contain accession numbers */
	protected String description;
	
	/** When arrangements are associated with a TreeNode a cross reference can be made */
	protected TreeNodeI treeNode = null;
	
	public void hideDomain(Domain dom) {
		if (!contains(dom))
			return;
		
		doms.remove(dom);
		hiddenDoms.add(dom);
		Collections.sort(doms);
	}	
	
	public void showAllDomains() {
		doms.add(hiddenDoms);
		hiddenDoms.clear();
		Collections.sort(doms);
	}
	
	/**
	 * Sorts the DomainVector by
	 * from position
	 * (using Domain implementation of
	 * compareTo())
	 */
	public void sortDomains() {
		Collections.sort(doms);
	}
	
	
	public DomainVector getHiddenDoms() {
		return hiddenDoms;
	}
	
	/**
	 * Constructor for a domain arrangement.
	 */
	public DomainArrangement() {
		this.seqLen = -1;
		this.doms = new DomainVector();
		this.hiddenDoms = new DomainVector();
	}
	
	/**
	 * resets the domains within the arrangement. 
	 * (Needed for the clone process)
	 */
	public void clear() {
		this.seqLen = -1;
		this.doms = new DomainVector();
		seq = null;
		hiddenDoms = new DomainVector();
	}
	
	/**
	 * Clones an domain arrangement object. Therefore all domains within the 
	 * arrangement are cloned as well.
	 * 
	 * @return
	 * 		a cloned copy of the domain arrangement.
	 */
    public Object clone() throws CloneNotSupportedException {
    	DomainArrangement copy = (DomainArrangement) super.clone();
    	SequenceI seq = copy.getSequence();
    	copy.clear();
    	
    	Iterator<Domain> iter = doms.iterator();
    	while (iter.hasNext()) {
    		Domain domCopy = (Domain) iter.next().clone();
    		copy.addDomain(domCopy);
    	}
    	 
    	for (Domain d : hiddenDoms) {
    		Domain domCopy = (Domain) d.clone();
    		copy.addDomain(domCopy);
    		copy.hideDomain(domCopy);
    	}

    	// finally add the cloned sequence
    	if (seq != null)
    		copy.setSequence((SequenceI) seq.clone());
    		
        return copy;
    }
	

    
    
	/**
	 * Sets the underlying sequence for this arrangement. This can be 
	 * a gapped sequence from a MSA as well. The sequences for the domains 
	 * are set automatically.
	 * 
	 * @param seq
	 * 		sequence object for this arrangement
	 */
	public void setSequence (SequenceI seq) {
		this.seq = seq;
		
		DomainVector allDoms = new DomainVector();
		allDoms.add(doms);
		allDoms.add(hiddenDoms);
		
		// delete sequence
		if (seq == null) {
			Iterator <Domain> iter = allDoms.iterator();
			while(iter.hasNext())  {
				Domain dom = iter.next();
				dom.setSequence(dom.getFrom(), null);
			}
			return;
		}
		
		// iterate over all domains to set their sequence
		Iterator <Domain> iter = allDoms.iterator();
		while(iter.hasNext()) {
			Domain dom = iter.next();
			
			char[] seqArray = seq.getSeq(true).toCharArray();
			
			// get start position within the gapped sequence
			int start = 0, count = 0;
			while (count < dom.getFrom()-1) {
				if (seqArray[start] != '-')
					count++;
				start++;
			}

			// get width within the gapped sequence
			try {
				count = 0;
				int lenWithGaps = 0;
				while (count < dom.getLen()) {
					if (seqArray[start+lenWithGaps] != '-')
						count++;
					lenWithGaps++;
				}
				dom.setSequence(start, new Sequence(dom.getID(), seq.getSeq(start, start+lenWithGaps, true)));
			} 
			catch(Exception e) {
				Configuration.getLogger().debug(e.toString());
			}
			
		}
	}
	
	/**
	 * Returns the underlying sequence data for this arrangement, 
	 * if it was assigned.
	 * 
	 * @return
	 * 		underlying sequence
	 */
	public SequenceI getSequence () {
		return seq;
	}
	
	/**
	 * Check if a specified domain is contained by the arrangement.
	 * 
	 * @param dom
	 * 		the domain which should be checked for containment 
	 * @return
	 * 		whether or not the domain is contained by the arrangement.
	 */
	public boolean contains(Domain dom) {
		return contains(dom.getFamily());
	}
	
	/**
	 * Check if a specified domain family is contained by the arrangement.
	 * 
	 * @param domFamily
	 * 		the domain family which should be checked for containment 
	 * @return
	 * 		whether or not the domain family is contained by the arrangement.
	 */
	public boolean contains(DomainFamily domFamily) {
		for (int i = 0; i < doms.size(); i++)
			if (doms.get(i).getFamily().equals(domFamily))
				return true;
		return false;
	}
	
	/**
	 * Returns the index of a domain within the arrangement. If the 
	 * arrangement was aligned, the index of the aligned domain composition
	 * is taken.
	 * 
	 * @param dom
	 * 		the domain from which the index is requested
	 * @return
	 * 		the index of the specified domain within the arrangement
	 */
	public int getIndexOf(Domain dom) {
		return doms.indexOf(dom);
	}
	
	/**
	 * Returns the domain vector of the arrangement. If the arrangement was 
	 * aligned, the domain vector of the aligned domain composition is taken.
	 * 
	 * @return
	 * 		domain vector of the arrangement
	 */
	public DomainVector getDomains() {
		Collections.sort(doms);
		return doms;
	}
	
	/**
	 * Returns the length of the protein sequence. A flag has to be specified
	 * whether or not the length should contain counts for gaps. If no sequence
	 * was assigned to the protein, the length gained from the xdom format is
	 * returned.
	 * 
	 * @param gaps
	 * 		flag if the length is allowed to contain gaps
	 * @return
	 * 		sequence length with or without gap counting
	 */
	public int getLen(boolean gaps) {
		if (seq == null)
			return seqLen;
		return seq.getLen(gaps);
	}
	
	/**
	 * Counts the number of domains. If the arrangement was aligned, 
	 * the number of domains containing GAP-domains is returned.
	 * FIXME: If a user aligns and calculates from this view domain distances
	 *        the values are wrong.
	 *        
	 * @return
	 * 		the number of domains within the arrangement.
	 */
	public int countDoms() {
		return doms.size();
	}
	
	/**
	 * Returns an iterator over all domain objects stored in the arrangement. 
	 * If the arrangement was aligned, also GAP-Domains may occur in this iterator. 
	 *
	 * @return
	 * 		iterator over all domain objects stored in the arrangement
	 */
	public Iterator<Domain> getDomainIter() {
		return doms.iterator();
	}
	
	/**
	 * Returns the domain at the specified position. If the arrangement was aligned,
	 * the changed position of domains caused by introduced GAP-Domains are taken
	 * into account.
	 * 
	 * @param index
	 * 		position within the arrangement
	 * @return
	 * 		domain at the specified position within the arrangement
	 */
	public Domain getDomain(int index) {
		return doms.get(index);
	}
	
	/**
	 * Returns the proteins name.
	 * 
	 * @return
	 * 		name of the protein.
	 */
	public String getName() {
		return id;
	}
	
	/**
	 * Sets the name of the protein, which is gained from the xdom file.
	 * 
	 * @param name
	 * 		proteins name
	 */
	public void setName(String name) {
		this.id = name;
	}
	
	public boolean hasSeq() {
		return seq != null;
	}
	
	/**
	 * Sets the sequence length of the arrangement, 
	 * which might be stored in the xdom file
	 * 
	 * @param len
	 * 		length of the arrangement
	 */
	public void setSeqLen(int len) {
		this.seqLen = len;
	}
	
	/**
	 * Adds a domain to the arrangement. If the domains to-parameter
	 * exceeds the current protein length, this length is updated.  
	 * 
	 * @param dom
	 * 		the domain to add
	 */
	public void addDomain(Domain dom) {
		doms.add(dom);
		dom.setArrangement(this);
		if (dom.getTo() > seqLen) 
			seqLen = dom.getTo();
	}
	
	public void changeDomain(Domain target, Domain vals) {
		target.setFamily(vals.getFamily());
		target.setFrom(vals.getFrom());
		target.setTo(vals.getTo());
		target.setEvalue(vals.getEvalue());
		target.setScore(vals.getScore());
		
		if (target.getTo() > seqLen)
			seqLen = target.getTo();
	}
	
	public void setDesc(String desc) {
		if (desc.trim().isEmpty())
			return;
		this.description = desc;
	}
	
	public String getDesc() {
		return description;
	}
	
	public void setTreeNode(TreeNodeI node) {
		this.treeNode = node;
	}

	public TreeNodeI getTreeNode() {
		return treeNode;
	}
	
	public boolean hasOverlap(Domain dom) {
		for (int i = 0; i < doms.size(); i++) {
			if (doms.get(i).equals(dom))
				continue;
			if (dom.intersect(doms.get(i)))
				return true;
		}
		return false;
	}
	
	public String toString() {
		return getName();
	}
	

}
