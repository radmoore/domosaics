package angstd.model.arrangement;

import java.util.Vector;

/**
 * Class DomainVector is the basic data structure to store Domains within an
 * arrangement. <br>
 * Besides the basic vector operations to add and remove domains from a vector
 * some methods must be used, for instance to compare two vectors on equality. 
 * 
 * @author Andreas Held
 *
 */
public class DomainVector extends Vector<Domain>{
	private static final long serialVersionUID = 1L;

	public DomainVector() {
	}
	
	public DomainVector(DomainArrangement da) {
		if (da != null)
			add(da.getDomains());
	}
	
	public DomainArrangement toArrangement() {
		try {
			DomainArrangement res = new DomainArrangement();
			for (Domain dom : this)
				res.addDomain((Domain) dom.clone());
			return res;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean contains(Domain dom) {
		for (Domain d : this)
			if (d.getID().equals(dom.getID()))
				return true;
		return false;
	}
	
	
	/**
	 * Adds a hole vector to this one
	 * 
	 * @param vec
	 * 		the vector to add
	 */
	public void add(DomainVector vec) {
		for (int i = 0; i < vec.size(); i++)
			add(vec.get(i));
	}
	
	/**
	 * Checks for equality between two vectors. This method is needed for 
	 * comparison, because  the domain family IDs have to be compared with 
	 * each other.
	 * 
	 * @param vec
	 * 		The somain vector which should be compared
	 * @return
	 * 		Whether or not the vectors are identical
	 */
	public boolean isEqualTo(DomainVector vec) {
		if (size() != vec.size())
			return false;
		
		for (int i = 0; i < size(); i++)
			if (!get(i).getFamily().equals(vec.get(i).getFamily()))
				return false;
		return true;
	}
	
}
