package angstd.algos.align;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import angstd.algos.align.nw.NW4DomainsAffine;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainVector;
import angstd.model.arrangement.GapDomain;
import angstd.model.tree.TreeI;
import angstd.model.tree.TreeNodeI;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;

public class ConsensusAlignment {
	
	protected static Map<TreeNodeI, DomainVector> node2domains;

	public DomainVector align(TreeI domTree) {
		// 1. preprocessing step: filter the dataset. Take only non redundant DAs
		DomainArrangement[] daSet = domTree.getDaSet();
		
		List<DomainArrangement> nonRedundant = new ArrayList<DomainArrangement>();
		nonRedundant.add(daSet[0]);
		for (int i = 1; i < daSet.length; i++) {
			boolean redundant = false;
			for (DomainArrangement da : nonRedundant)
				if (da.getDomains().isEqualTo(daSet[i].getDomains())) {
					redundant = true;
					break;
				}
			if (!redundant)
				nonRedundant.add(daSet[i]);
		}
		daSet = nonRedundant.toArray(new DomainArrangement[nonRedundant.size()]);
			
		
		// 2. preprocessing step: filter the dataset. split single and multi domain arrangement
		List<DomainArrangement> singleDoms = new ArrayList<DomainArrangement>();
		List<DomainArrangement> multiDoms = new ArrayList<DomainArrangement>();
		for (DomainArrangement da : daSet)
			if (da.countDoms() == 1)
				singleDoms.add(da);
			else if(da.countDoms() > 0)
				multiDoms.add(da);
		daSet = multiDoms.toArray(new DomainArrangement[multiDoms.size()]);
		
		
		// Performance issue: create a mapping between domains and integers to speed things up when working with a hash map
		final Map<String, Integer> dom2id = new HashMap<String, Integer>();
		final Map<Integer, String> id2dom = new HashMap<Integer, String>();
		int id = 0;
		for (DomainArrangement da : daSet) 
			for (Domain dom : da.getDomains()) 
				if (dom2id.get(dom.getID()) == null) {
					dom2id.put(dom.getID(), id);
					id2dom.put(id, dom.getID());
					id++;
				}
		
		
		// 3. Preprocessing step: count 2-domain combinations
		final Map<Point, Integer> comb2count = new HashMap<Point, Integer>();
		List<Point> nonRedundantCombis = new ArrayList<Point>();
		
		for (DomainArrangement da : daSet) {
			DomainVector doms = da.getDomains();
			nonRedundantCombis.clear();
			
			// TODO take only non redundant combinations for an arrangement
			for (int i = 0; i < doms.size()-1; i++) {
				int x = dom2id.get(doms.get(i).getID());
				int y = dom2id.get(doms.get(i+1).getID());
				Point domPair = new Point(x, y);
				
				if (nonRedundantCombis.contains(domPair))
					continue;
			
				if (comb2count.get(domPair) == null) {
					comb2count.put(domPair, 1);
					nonRedundantCombis.add(domPair);
				} 	else {
					int newCount = comb2count.get(domPair)+1;
					comb2count.put(domPair, newCount);
				}
			}
		}
		
		// test output of all combinations inclusive count
//		for (Point comb : comb2count.keySet())
//			System.out.println(">"+id2dom.get(comb.x)+"-"+id2dom.get(comb.y)+":"+comb2count.get(comb));
		
		// sort the dataset in ascending order by the number of conserved neighbourhoods
		Arrays.sort(daSet, new Comparator<DomainArrangement>() {

			public int compare(DomainArrangement da1, DomainArrangement da2) {
				
				// count the score for da1
				int score1 = 0;
				for (int i = 0; i < da1.getDomains().size()-1; i++) {
					int x = dom2id.get(da1.getDomains().get(i).getID());
					int y = dom2id.get(da1.getDomains().get(i+1).getID());
					Point comb = new Point(x, y);
					score1 += comb2count.get(comb);
				}
				
				// count the score for da2
				int score2 = 0;
				for (int i = 0; i < da2.getDomains().size()-1; i++) {
					int x = dom2id.get(da2.getDomains().get(i).getID());
					int y = dom2id.get(da2.getDomains().get(i+1).getID());
					Point comb = new Point(x, y);
					score2 += comb2count.get(comb);
				}
				
				return score2-score1;
			}
			
		});
	
		// Aligning step:
		DomainArrangement da1 = daSet[0];
		DomainArrangement da2 = daSet[1];
		DomainVector[] aligned = new NW4DomainsAffine(da1, da2).getMatch();
		DomainVector consensus = buildConsensus(aligned);
		
		for (int i = 2; i < daSet.length; i++) {
			DomainArrangement da = daSet[i];
			aligned = new NW4DomainsAffine(consensus.toArrangement(), da).getMatch();
			consensus = buildConsensus(aligned);
		}
		
		// Post processing step: add single domain arrangements which lead to a new column
		for (DomainArrangement da : singleDoms)
			if (!(consensus.contains(da.getDomains().get(0))))
				consensus.insertElementAt(da.getDomains().get(0), 0);

//		for (Domain dom : consensus)
//			System.out.println(dom.getFamID());
		return consensus;	
	}
	
	public static void alignInView(DomainVector consensus, DomainTreeViewI view) {
		// align each arrangement against the consensus arrangement
		DomainArrangement domSeq = consensus.toArrangement();
		view.getDomainShiftManager();
		DomainArrangement[] daSet = view.getDomTree().getDaSet();
		for (DomainArrangement da : daSet) {
			DomainVector[] aligned = new NW4DomainsAffine(domSeq, da).getMatch();
			int gapLen = 0;
				
			for (int i = 0; i < aligned[1].size(); i++) {
				if (aligned[1].get(i) instanceof GapDomain) 
					gapLen++;
				else {
					ArrangementComponent dac = view.getArrangementComponentManager().getComponent(da);
					DomainComponent dc = view.getDomainComponentManager().getComponent(aligned[1].get(i));
					view.getDomainShiftManager().setShift(dac, dc, view.getDomainShiftManager().getShiftCol(dc)+gapLen);
					gapLen = 0;
				}
			}
		}
	}
	
	
	private DomainVector buildConsensus(DomainVector[] aligned) {
		DomainVector consensus = new DomainVector();
		for (int i = 0; i < aligned[0].size(); i++) {
			if (!(aligned[0].get(i) instanceof GapDomain))
				consensus.add(aligned[0].get(i));
			else if (!(aligned[1].get(i) instanceof GapDomain))
				consensus.add(aligned[1].get(i));
		}
		return consensus;
	}
}
