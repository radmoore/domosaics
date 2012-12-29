package domosaics.model.domainevent;

import domosaics.model.DoMosaicsData;
import domosaics.model.arrangement.Domain;

/** 
 * Interface DomainEvent defines events which may happen during evolution and 
 * can be assigned to an edge within a DomainTree.
 * 
 * @author Andreas Held
 *
 */
public interface DomainEventI extends DoMosaicsData {

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
