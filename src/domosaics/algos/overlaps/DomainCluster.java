package domosaics.algos.overlaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import domosaics.model.arrangement.Domain;




/**
 * Class written by Andreas Held, first created inside the OverlapREsolver
 * class, to computed cluster of regions of connected overlaps.
 * 
 * Isolated in a new class and bug-fixed
 * 
 * @author Nicolas TERRAPON
 *
 */

public class DomainCluster  {
	
	protected List<Domain> doms = new ArrayList<Domain>();
	protected int from = Integer.MAX_VALUE;
	protected int to = Integer.MIN_VALUE;
	
	public DomainCluster() {
	}
	
	public DomainCluster(Domain dom) {
		doms.add(dom);
		from =  dom.getFrom();
		to = dom.getTo();
	}
	
	public List<Domain> getDoms() {
		return doms;
	}
	
	public void add(Domain dom) {
		doms.add(dom);
		Collections.sort(doms);
		if (from > dom.getFrom())
			from =  dom.getFrom();
		if (to < dom.getTo())
			to = dom.getTo();
	}
	
	public boolean intersects(Domain dom) {
		if (this.to < dom.getFrom())
			return false;
		if (dom.getTo()< this.from)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("Cluster with "+doms.size()+" domains: \n");
		res.append("Coverage "+from+"-"+to+" \n");
		for (Domain dom: doms)
			res.append("Entry: "+dom.getID()+" \n");
		res.append("\n");
		return res.toString();
	}
	
}
