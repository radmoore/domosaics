package domosaics.model.arrangement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domosaics.algos.distance.JaccardDistance;


/**
 * Class ArrangementManager can store arrangements and provides methods to 
 * retrieve statistical information about the dataset. For instance number of 
 * single and multidomains.
 * 
 * @author Andreas Held
 *
 */
public class ArrangementManager {

	/** all stored domain arrangements in the dataset */
	protected List<DomainArrangement> das;
	
	/** Map of all contained domain familys within the dataset */
	protected Map<String, DomainFamily> domFams;
	
	
	/**
	 * Constructor of a new ArrangementManager
	 */
	public ArrangementManager() {
		das = new ArrayList<DomainArrangement>();
		domFams = new HashMap<String, DomainFamily>();
	}
	
	/**
	 * Returns the domain family object for the given family id
	 * 
	 * @param id
	 * 		domain family id
	 * @return
	 * 		domain family object for the given family id
	 */
	public DomainFamily getFamily(String acc) {
		return domFams.get(acc);
	}
	
	/**
	 * Iterator over all domain families
	 * 
	 * @return
	 * 		Iterator over all domain families
	 */
	public Iterator<DomainFamily> getFamilyIterator() {
		return domFams.values().iterator();
	}
	
	/**
	 * Adds a new domain arrangement to the manager. The contained
	 * domain families are added as well.
	 * 
	 * @param da
	 * 		domain arrangement to be added
	 */
	public void add(DomainArrangement da) {
		for (int i = 0; i < da.countDoms(); i++) {
			if (domFams.get(da.getDomain(i).getID()) != null)
				da.getDomain(i).setFamily(domFams.get(da.getDomain(i).getID()));
			else
				domFams.put(da.getDomain(i).getID(), da.getDomain(i).getFamily());
		}
		das.add(da);
	}
	
	/**
	 * Adds a hole dataset consisting of domain arrangements to the manager.
	 * 
	 * @param das
	 * 		dataset consisting of domain arrangements to be added
	 */
	public void add(DomainArrangement[] das) {
		for (int i = 0; i < das.length; i++) 
			add(das[i]);	
	}
	
	/**
	 * Retrieves the added arrangements as dataset in an array
	 * 
	 * @return
	 * 		arrangements as dataset in an array
	 */
	public DomainArrangement[] get() {
		return das.toArray(new DomainArrangement[das.size()]);
	}
	
	/**
	 * Return the number of added domain families.
	 * 
	 * @return
	 * 		number of added domain families
	 */
	public int getFamilyCount() {
		return domFams.size();
	}
	
	/**
	 * Return the number of added domain arrangements.
	 * 
	 * @return
	 * 		number of added domain arrangements
	 */
	public int getArrangementCount() {
		return das.size();
	}
	
	/**
	 * Returns the maximal number of domains contained in one arrangement
	 * of the manager. This can be used for statistics.
	 * 
	 * @return
	 * 		maximal number of domains
	 */
	public int getMaxDomains() {
		int max = 0;
		for (int i = 0; i < das.size(); i++)
			if (das.get(i).countDoms() > max)
				max = das.get(i).countDoms();
		return max;
	}
	
	/**
	 * Get the number of arrangements containing 1 domain, 2 domains, 
	 * 3 domains, ... and so on. This can be used for statistics.
	 * 
	 * @return
	 * 		number of arrangements containing 1 domain, 2 domains, 3 domains, ... and so on. 
	 */
	public int[] getNumDomainsCount() {
		int maxDoms = getMaxDomains()+1;
		int[] res = new int[maxDoms];
		
		for (int i = 0; i < maxDoms; i++)
			res[i] = 0;
		
		for (int i = 0; i < das.size(); i++) 
			res[das.get(i).countDoms()]++;
		
		return res;
	}
	
	/**
	 * Returns number of arrangements containing one domain. 
	 * This can be used for statistics.
	 * 
	 * @return
	 * 		number of arrangements containing one domain
	 */
	public int getSingleDomCount() {
		int count = 0;
		for (int i = 0; i < das.size(); i++)
			if (das.get(i).countDoms() == 1)
				count++;
		return count;
	}
	
	/** 
	 * Returns number of arrangements containing more than one domain. 
	 * This can be used for statistics.
	 * 
	 * @return
	 * 		number of arrangements containing more than one domain
	 */
	public int getMultiDomCount() {
		int count = 0;
		for (int i = 0; i < das.size(); i++)
			if (das.get(i).countDoms() > 1)
				count++;
		return count;
	}
	
	/**
	 * Returns the average jaccard distance for the dataset
	 * 
	 * @return
	 * 		average jaccard distance for the dataset
	 */
	public double getAvgJaccard() {
		// create distance matrix
		double[][] matrix = new JaccardDistance().calc(get(), true);
		
		// calculate average jaccard
		int rows = matrix.length;
		double sum = 0;
		int num = 0;
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < r; c++) {
				sum+= matrix[r][c];
				num++;
			}
		
		return sum/num;
	}
	
	/**
	 * Returns the number of unique arrangements
	 * 
	 * @return
	 * 		number of unique arrangements
	 */
	public int getNumUniqueArrangements() {
		DomainArrangement[] origSet = get();
		
		// here is a list where only non redundant arrangements may enter
		List<DomainArrangement> nonRedundant = new ArrayList<DomainArrangement>();
		nonRedundant.add(origSet[0]);
		
		// iteratively add the arrangements and check each time for redundancy
		for (int i = 1; i < origSet.length; i++) {
			boolean toAdd = true;
			for (int j = 0; j < nonRedundant.size(); j++) {
				if (nonRedundant.get(j).getDomains().isEqualTo(origSet[i].getDomains())) {
					toAdd = false;
					break;
				}
			}
			if (toAdd)	
				nonRedundant.add(origSet[i]);
		}
		
		return nonRedundant.size();
	}
	
	/**
	 * Returns an array containing the unique domain architectures
	 * 
	 * @return
	 * 		array containing the unique domain architectures
	 */
	public DomainArrangement[] getUniqueArrangements() {
		DomainArrangement[] origSet = get();
		
		// here is a list where only non redundant arrangements may enter
		List<DomainArrangement> nonRedundant = new ArrayList<DomainArrangement>();
		nonRedundant.add(origSet[0]);
		
		// iteratively add the arrangements and check each time for redundancy
		for (int i = 1; i < origSet.length; i++) {
			boolean toAdd = true;
			for (int j = 0; j < nonRedundant.size(); j++) {
				if (nonRedundant.get(j).getDomains().isEqualTo(origSet[i].getDomains())) {
					toAdd = false;
					break;
				}
			}
			if (toAdd)	
				nonRedundant.add(origSet[i]);
		}
		
		return nonRedundant.toArray(new DomainArrangement[nonRedundant.size()]);
	}
	
	public double getAvgDomainCount() {
		DomainArrangement[] das = get();
		int numDoms = 0;
		for (DomainArrangement da : das) 
			numDoms+= da.countDoms();
		return numDoms / (double) das.length;
	}
	
	public Map<DomainFamily, Integer> getDomFamOccurrences() {
		Map<DomainFamily, Integer> res = new HashMap<DomainFamily, Integer>();
		DomainArrangement[] das = get();
		for (DomainArrangement da : das) 
			for (Domain dom : da.getDomains()) {
				DomainFamily actFam = dom.getFamily();
				Integer old = res.get(actFam);
				if (old == null) 
					res.put(actFam, 1);
				else 
					res.put(actFam, old+1);
			}

		return res;
	}
}
