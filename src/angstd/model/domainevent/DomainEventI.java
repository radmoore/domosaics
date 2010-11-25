package angstd.model.domainevent;

import angstd.model.AngstdData;
import angstd.model.arrangement.Domain;

/** 
 * Interface DomainEvent defines events which may happen during evolution and 
 * can be assigned to an edge within a DomainTree.
 * 
 * @author Andreas Held
 *
 */
public interface DomainEventI extends AngstdData {

	/**
	 * Returns the affected domain.
	 * 
	 * @return
	 * 		affected domain.
	 */
	public Domain getDomain();
	
	/**
	 * Checks whether or not it is an insertion event
	 * 
	 * @return
	 * 		whether or not it is an insertion event
	 */
	public boolean isInsertion();
	
	/**
	 * Checks whether or not it is an deletion event
	 * 
	 * @return
	 * 		whether or not it is an deletion event
	 */
	public boolean isDeletion();
}
