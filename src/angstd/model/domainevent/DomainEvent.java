package angstd.model.domainevent;

import angstd.model.arrangement.Domain;
import angstd.model.tree.TreeEdgeI;

/**
 * see {@link DomainEventI}
 * 
 * @author Andreas Held
 *
 */
public class DomainEvent implements DomainEventI{

	public static final int INSERTION = 0;
	public static final int DELETION = 1;
	
	/** type of the event */
	protected int type;
	
	/** the edge where the event is assigned to */
	protected TreeEdgeI edge;
	
	/** the domain which is affected */
	protected Domain dom;
	
	/**
	 * Constructor for a new DomainEvent.
	 * 
	 * @param type
	 * 		type of the event
	 * @param edge
	 * 		affected edge
	 * @param dom
	 * 		affected domain
	 */
	public DomainEvent (int type, TreeEdgeI edge, Domain dom) {
		this.type = type;
		this.edge = edge;
		this.dom = dom;
	}
	
	public Domain getDomain() {
		return dom;
	}
	
	public boolean isInsertion() {
		return type == INSERTION;
	}
	
	public boolean isDeletion() {
		return type == DELETION;
	}
}

