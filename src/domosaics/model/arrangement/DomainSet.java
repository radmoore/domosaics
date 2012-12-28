package domosaics.model.arrangement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class DomainSet represents a non redundant list of domains,  therefore each
 * domain occurs maximal one time. This can be useful, for instance to 
 * compute domainwise events within a phylogenetic tree or just to compute
 * the Jaccard-Index between two arrangements.
 * 
 * @author Andreas Held
 *
 */
public class DomainSet {

	/** basic data structure behind the set */
	protected List<Domain> doms = new ArrayList<Domain>();
	
	public DomainSet() {
		this(null);
	}
	
	public DomainSet(DomainArrangement da) {
		doms = new ArrayList<Domain>();
		if (da != null) 
			add(da);
	}
	
	
	
	/**
	 * Resets a domain set to a new set based on an arrangement. If the
	 * arrangement is null, the result is an empty domain set.
	 * 
	 * @param da
	 * 		The arrangement which is used for the initialization
	 */
	public void reset (DomainArrangement da) {
		doms.clear();
		if (da != null) 
			add(da);
	}
	
	/**
	 * Returns the size of the domain set.
	 * 
	 * @return
	 * 		size of the domain set.
	 */
	public int size() {
		return doms.size();
	}
	
	/**
	 * Compares two sets on equality.
	 * 
	 * @param set
	 * 		The compared set.
	 * @return
	 * 		Whether or not the two sets are identical.
	 */
	public boolean equals(DomainSet set) {
		if (size() != set.size())
			return false;
		
		DomainSet except1 = this.except(set);
		DomainSet except2 = set.except(this);
		
		return except1.size() == 0 && except2.size() == 0;
	}
	
	/**
	 * Checks if a specific domain occurs within the dataset.
	 * 
	 * @param dom
	 * 		The domain to check for occurrence.
	 * @return
	 * 		Whether or not the domain occurs in the set.
	 */
	public boolean contains(Domain dom) {
		for (int i = 0; i < doms.size(); i++)
			if (doms.get(i).getFamily().equals(dom.getFamily())) 
				return true;
		return false;
	}
	
	/**
	 * Returns the domain at a specified position within the set.
	 * 
	 * @param index
	 * 		the position within the set
	 * @return
	 * 		the domain at the specified position
	 */
	public Domain get(int index) {
		return doms.get(index);
	}
	
	/**
	 * Returns the domain set as a List object.
	 * 
	 * @return
	 * 		domain set as a List object	
	 */
	public List<Domain> getDomList() {
		return doms;
	}
	
	/**
	 * Removes a domain from the set.
	 * 
	 * @param dom
	 * 		The domain which has to be removed.
	 */
	public void remove(Domain dom) {
		if (!contains(dom)) 
			return;
		
		for (int i = 0; i < doms.size(); i++)
			if (doms.get(i).getFamily().equals(dom.getFamily())) {
				doms.remove(doms.get(i));
				break;
			}
	}
	
	/**
	 * Adds a domain to the set.
	 * 
	 * @param dom
	 * 		the domain which should be added.
	 */
	public void add(Domain dom) {
		if (!contains(dom)) 
			doms.add(dom);
	}
	
	/**
	 * Adds all domains from an arrangement to the set (non redundant).
	 * 
	 * @param da
	 * 		The arrangement used to fill the set.
	 */
	public void add(DomainArrangement da) {
		Iterator<Domain> doms = da.getDomainIter();
		while (doms.hasNext()) 
			add(doms.next());
	}

	/**
	 * Adds all domains from a domain set to the set.
	 * 
	 * @param domSet
	 * 		the set to be added
	 */
	public void add(DomainSet domSet) {
		for (int i = 0; i < domSet.size(); i++)
			add(domSet.get(i));
	}
	
	/**
	 * Adds all domains from a domain list to the set.
	 * 
	 * @param domList
	 * 		List of domains which should be added to the set
	 */
	public void add(List<Domain> domList) {
		for (int i = 0; i < domList.size(); i++)
			add(domList.get(i));
	}
	
	/**
	 * Returns a DomainSet which is the intersection of two sets.
	 * 
	 * @param domSet
	 * 		the set for the intersection
	 * @return
	 * 		intersection of two sets
	 */
	public DomainSet intersect(DomainSet domSet) {
		DomainSet res = new DomainSet();
		
		for (int i = 0; i < domSet.size(); i++)
			if (this.contains(domSet.get(i)))
				res.add(domSet.get(i));

		for (int i = 0; i < doms.size(); i++)
			if (domSet.contains(doms.get(i)))
				res.add(doms.get(i));
		
		return res;
	}
	
	/**
	 * Returns the Exception between two sets.
	 * 
	 * @param domSet
	 * 		the set for the exception operation
	 * @return
	 * 		Exception between two sets
	 */
	public DomainSet except(DomainSet domSet) {
		DomainSet res = new DomainSet();
		res.add(this);
		
		for (int i = 0; i < domSet.size(); i++)
			res.remove(domSet.get(i));
		
		return res;
	}
	
	/**
	 * The union between two sets.
	 * 
	 * @param domSet
	 * 		the set for the union operation
	 * @return
	 * 		Union between two sets
	 */
	public DomainSet union(DomainSet domSet) {
		DomainSet res = new DomainSet();
		res.add(this);
		
		for (int i = 0; i < domSet.size(); i++)
			res.add(domSet.get(i));
		
		return res;
	}
	

}
