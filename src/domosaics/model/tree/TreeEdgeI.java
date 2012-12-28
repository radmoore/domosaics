package domosaics.model.tree;


import java.util.List;

import domosaics.model.domainevent.DomainEventI;





/**
 * The TreeEdge class represents Edges within a {@link TreeI} which connects two
 * {@link TreeNodeI}s. 
 * <p>
 * New edges are automatically created, when using the the methods {@link TreeI#addEdge(TreeNodeI, TreeNodeI)}
 * and {@link TreeI#addEdge(TreeNodeI, TreeNodeI, double)}.
 * 
 * @author Andreas Held
 *
 */
public interface TreeEdgeI {

	/**
	 * Returns the source node of the edge.
	 * 
	 * @return 
	 * 		source node of the edge.
	 */
	public TreeNodeI getSource();

	/**
	 * Returns the target node of the edge.
	 * 
	 * @return 
	 * 		target node of the edge.
	 */
    public TreeNodeI getTarget();
    
    /**
     * Returns the weight of the edge, if the weight was set.
     * 
     * @return 
     * 		the weight of the edge
     */
    public double getWeight();

    /**
     * Sets a new weight for the edge
     * 
     * @param weight 
     * 		new weight for the edge
     */
    public void setWeight(double weight);
    
    
    
    
	/**
	 * Returns the bootstrap value for this edge
	 * 
	 * @return
	 * 		bootstrap value for this edge
	 */
    public double getBootstrap();

    /**
     * Sets the bootstrap value for this edge
     * 
     * @param bootstrap
     * 		bootstrap value for this edge
     */
    public void setBootstrap(double bootstrap);
    
    /* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */
    
	/**
	 * Adds a domain event to the edge.
	 * 
	 * @param evt
	 * 		the domain event to be added
	 */
	public void addDomainEvent(DomainEventI evt);
	
	/**
	 * Returns all domain events which were assigned to the edge
	 * 
	 * @return
	 * 		all domain events which were assigned to the edge
	 */
	public List<DomainEventI> getDomainEvents();
	
	/**
	 * Checks whether or not there are domain events assigned to edge.
	 * 
	 * @return
	 * 		whether or not there are domain events assigned to edge.
	 */
	public boolean hasDomainEvent();
	
	/**
	 * Removes all assigned domain events from this edge
	 */
	public void removeDomainEvenets();

}
