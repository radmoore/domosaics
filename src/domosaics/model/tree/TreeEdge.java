package domosaics.model.tree;

import java.util.ArrayList;
import java.util.List;

import domosaics.model.domainevent.DomainEventI;



/**
 * see {@link TreeEdgeI}
 * 
 * @author Andreas Held
 *
 */
public class TreeEdge implements TreeEdgeI {

	/** bootstrap value */
    protected double bootstrap = -1;
    
    /** a list of assigned domain events */
	protected List<DomainEventI> domEvents; 
	
    /** The source respectively parent node of the edge	 */
    protected TreeNodeI source;
    
    /** The target respectively child node of the edge */
    protected TreeNodeI target;
    
    /** the weight of the edge if it is weightened */
    protected double weight;

    /**
     * Basic constructor for unweightened TreeEdges.
     * 
     * @param source 
     * 		source node of the edge
     * @param target 
     * 		target node of the edge
     */
    public TreeEdge(TreeNodeI source, TreeNodeI target){
        this(source, target, 1.0);
    }
   
    /**
     * Basic constructor for weightened TreeEdges.
     * 
     * @param source 
     * 		source node of the edge
     * @param target 
     * 		target node of the edge
     */
    public TreeEdge(TreeNodeI source, TreeNodeI target, double weight){
    	this.source = source;
        this.target = target;
        this.weight = weight;
        domEvents = new ArrayList<DomainEventI>();
    }

	public TreeNodeI getSource() {
        return source;
    }

    public TreeNodeI getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public double getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(double bootstrap) {
        this.bootstrap = bootstrap;
    }
    
    /* *********************************************************** *
	 *          			 DOMAIN TREE  METHODS				   *
	 * *********************************************************** */
    
	public void addDomainEvent(DomainEventI evt) {
		domEvents.add(evt);
	}
	
	public List<DomainEventI> getDomainEvents() {
		return domEvents;
	}
	
	public boolean hasDomainEvent() {
		return domEvents.size() != 0;
	}
	
	public void removeDomainEvenets() {
		domEvents.clear();
	}
}
