package angstd.algos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.util.PowerSet;

public class OverlapResolver {

	public List<Domain> resolveOverlaps (DomainArrangement da) {
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
	
	private List<DomainCluster> getClusters(DomainArrangement da) {
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
	
	
	private class DomainCluster  {
		
		protected List<Domain> doms = new ArrayList<Domain>();
		protected int from = Integer.MAX_VALUE;
		protected int to = Integer.MIN_VALUE;
		
		public DomainCluster() {
		}
		
		public DomainCluster(Domain dom) {
			add(dom);
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
		
		public String toString() {
			StringBuffer res = new StringBuffer();
			res.append("Cluster with "+doms.size()+" domains: \n");
			res.append("Coverage "+from+"-"+to+" \n");
			for (Domain dom: doms)
				res.append("Entry: "+dom.getFamID()+" \n");
			res.append("\n");
			return res.toString();
		}
		
	}
}
