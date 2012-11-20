package angstd.algos.overlaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.util.PowerSet;

/**
 * Class initiated by Andreas Held to resolve arrangements with overlaps
 * by a maximum coverage heuristic.
 * 
 * Became static and was extended to compute a resolution by best E-values.
 * 
 * @author Nicolas TERRAPON
 *
 */

public class OverlapResolver {

	/**
	 * Return an array of DomainArrangement where overlapping domains are hidden.
	 * @param daSet : original DomainArrangement[]
	 * @param method : String for the chosen heuristic
	 */
	
	public static DomainArrangement[] resolveOverlaps(DomainArrangement[] daSet, String method)
	{
	 List<Domain> toRemove = new ArrayList<Domain>();
	 for (DomainArrangement da : daSet)
	 {
	  if(method.equals("OverlapFilterCoverage"))
	  {
	   //System.out.println(toRemove.size());
	   toRemove = resolveOverlapsByBestCoverage(da);
	   //System.out.println(toRemove.size());
	  }else
	  {
	   toRemove = resolveOverlapsByBestEvalue(da);
	  }
	  for (Domain dom : toRemove)
	  {
	   da.hideDomain(dom);
	  }
	 }
	 return daSet;
	}
	
	
	/**
	 * Heuristic to identify overlapping domains with worst E-values.
	 * @param da : original DomainArrangement
	 */
	public static List<Domain> resolveOverlapsByBestEvalue (DomainArrangement da)
	{
	 List<Domain> toRemove = new ArrayList<Domain>();
	 List<Domain> ordered = new ArrayList<Domain>();
	 Domain dom;
	 Iterator <Domain> iter = da.getDomainIter();
	 while(iter.hasNext())
	 {
	  dom=iter.next();
	  int i=0;
	  for(; i< ordered.size(); i++)
	  {
	   Domain orderDom=ordered.get(i);
	   if(orderDom.getEvalue()>dom.getEvalue())
		   break;
	  }
  	  ordered.add(i, dom);
	 }
	 for(int i=1; i< ordered.size(); i++)
	 {
	  Domain currentDom=ordered.get(i);
	  //System.out.println(da.getName()+" "+currentDom.getID()+" "+currentDom.getFrom()+" "+currentDom.getTo());
	  for(int j=i-1; j>= 0; j--)
	  {
	   Domain bestDom=ordered.get(j);
	   if(bestDom.getFrom() <= currentDom.getTo() && currentDom.getFrom() <= bestDom.getTo())
	   {
		//System.out.println("Inconsistent with "+bestDom.getID()+" "+bestDom.getFrom()+" "+bestDom.getTo());
	    toRemove.add(currentDom);
	    ordered.remove(i);
	    i--;
	    break;
	   }
	  }
	 }
	 return toRemove;
	}
	
	/**
	 * Heuristic to identify overlapping domains with less coverage.
	 * @param da : original DomainArrangement
	 */
	public static List<Domain> resolveOverlapsByBestCoverage (DomainArrangement da) {
		List<Domain> toRemove = new ArrayList<Domain>();
		
		// create regions of connected overlaps called clusters
		List<DomainCluster> clusters = getClusters(da);
		
		for (DomainCluster cluster : clusters) {
			
			// create all possibilities for a cluster
			PowerSet<Domain> all = new PowerSet<Domain>(cluster.getDoms());
			int bestCoverage = 0;
			Vector<Domain> bestVec = null; // vector with best coverage
			
			// check for the one with best coverage
			while(all.hasNext()) {
				Vector<Domain> vec = all.next();
				
				// check for overlaps if there are more than one domain in this solution
				if (vec.size() > 1) {
					Collections.sort(vec);
					boolean overlap = false;
					for (int i = 1; i < vec.size(); i++) 
						if (vec.get(i-1).intersect(vec.get(i))) {
							overlap = true;
							break;
						}
					if (overlap) 
						continue;
				}
				
				// if its a non overlapping solution calculate the coverage
				int actCoverage = getCoverage(vec);
				if (actCoverage < bestCoverage) 
					continue;
				
				bestCoverage = actCoverage;
				bestVec = vec;
			}
			
			// the vector with best coverage was found. Now remove domains which show not up within the vector
			for (Domain dom : cluster.getDoms())
				if (!bestVec.contains(dom))
					toRemove.add(dom);
		}
		
		return toRemove;
	}
	
	private static int getCoverage(Vector<Domain> doms) {
		int coverage = 0;
		for (Domain dom : doms)
			coverage += dom.getLen();
		return coverage;
	}
	
	private static List<DomainCluster> getClusters(DomainArrangement da) {
		List<DomainCluster> clusters = new ArrayList<DomainCluster>();
		
		for (Domain dom : da.getDomains()) {
			boolean added = false;
			for (DomainCluster cluster : clusters)
				if (cluster.intersects(dom)) {
					cluster.add(dom);
					added = true;
				}
			
			if (added)
				continue;
			
			// if domain were not added into a cluster, create a new one
			clusters.add(new DomainCluster(dom));
		}
		
		return clusters;
	}
	
	
}
