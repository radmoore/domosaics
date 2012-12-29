package domosaics.ui.views.domainview.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.algos.distance.DistanceMeasureType;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.SimilarityChooser;
import domosaics.ui.views.view.manager.DefaultViewManager;




/**
 * DomainSimilarityManager manages the collapsing and calculating
 * of similarities of all arrangements to a reference arrangement,
 * which must be the actually selected arrangement.<br> 
 * This manager communicates with the similarity Chooser dialog.
 * The collapsing is done by setting redundant arrangements to 
 * invisible, which means that they are not processed by the layout 
 * or renderer anymore.
 * The decollapsing can be done by triggering the reset() method 
 * which automatically sets all arrangements to visible.
 * 
 * @author Andreas Held
 *
 */
public class DomainSimilarityManager extends DefaultViewManager {

	/** similarity of the stored domain arrangements to a reference arrangement */
	protected Map<ArrangementComponent, Integer> dac2similarity;
	
	/** the similarity measure used to comparethe domain arrangements */
	protected DistanceMeasureType type, memorizedSettingDMT;
	
	/** the threshold used to collapse arrangements */
	protected int threshold, memorizedSettingTdd=-1, memorizedSettingTjaccard=-1;
	
	/** the dialog used to communicate with the user */
	protected SimilarityChooser chooser;
	
	/**
	 * Constructor for a new similarity manager.
	 * 
	 */
	public DomainSimilarityManager() {
		dac2similarity = new HashMap<ArrangementComponent, Integer>();
	}
	
	/**
	 * Returns the maximal domain distance which is not infinity
	 * 
	 * @return
	 * 		maximal domain distance which is not infinity
	 */
	public int getMaxDomainDistance() {
		if (!(type == DistanceMeasureType.DOMAINDISTANCE))
			return 0; // percent
		
		int max = 0; 			// reference da
		for (Integer i : dac2similarity.values())
			max = (i != Integer.MAX_VALUE && i > max) ? i : max;
		return max;
	}
	
	/**
	 * Initializing the manager with the currently set similarity measure.
	 * This is for instance used when switching to a new 
	 * reference arrangement.
	 * 
	 * @param view
	 * 		the view providing the functionality 
	 * @param da
	 * 		the new reference arrangement
	 */
	public void init(DomainViewI view, DomainArrangement da) {
		init(view, da, this.type);
		
		switch(type) {
			case JACARD: chooser.createJaccardSliderBox(); break;
			case DOMAINDISTANCE: chooser.createDomainDistanceSliderBox(); break;
		}
		chooser.processSlider();
	}
	
	/**
	 * Initializing method for the manager. The reference arrangement
	 * as well as the similarity measure must be declared.
	 * 
	 * @param view
	 * 		the view providing the functionality
	 * @param da
	 * 		the reference arrangement
	 * @param type
	 * 		the similarity measure used to compare the arrangements
	 */
	public void init(DomainViewI view, DomainArrangement da, DistanceMeasureType typeDMT) {
		reset(view);
		this.type = typeDMT;
		setMemorizedSettingDMT(type);
		
		// create similarity matrix
		DomainArrangement[] daSet = view.getDaSet();
		double[][] matrix = type.getAlgo().calc(daSet, false);
		
		// find row containing the selected protein
		double[] vals = null;
		for (int i = 0; i < daSet.length; i++)
			if (da.equals(daSet[i])) {
				vals = matrix[i];
				break;
			}
		
		// fill the manager with values
		for (int i = 0; i < daSet.length; i++) {
			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(daSet[i]);
			int percent = 100;
			
			if (type == DistanceMeasureType.JACARD)
				percent = jaccard2Percent(vals[i]);
			else if(type == DistanceMeasureType.DOMAINDISTANCE)
				percent = (int) vals[i];
				
			setSimilarity(dac, percent);
		}
		visualChange();
	}
	
	/**
	 * Starts the feature by setting the correct action settings using 
	 * the DomainLayoutManager and opening the SimilarityChooser dialog.
	 * 
	 * @param view
	 * 		the view providing the functionality 
	 */
	public void start(DomainViewI view) {
		view.getDomainLayoutManager().toggleCollapseBySimilarity();
		chooser = new SimilarityChooser(view);
		chooser.showDialog(DoMosaicsUI.getInstance(), "DoMosaicS");
	}
	
	/**
	 * Flag indicating whether or not the manager is active.
	 * 
	 * @return
	 * 		whether or not the manager is active
	 */
	public boolean isActive() {
		return chooser != null;
	}
	
	/**
	 * Returns the distance measure used for distance calculations
	 * 
	 * @return
	 * 		similarity/distance measure type
	 */
	public DistanceMeasureType getMeasureType() {
		return type;
	}
	
	/**
	 * Ends the manager by performing all necessary actions. This method
	 * can be used whenever the manager should shut down.
	 * 
	 * @param view
	 * 		the view providing the functionality 
	 */
	public void end(DomainViewI view) {
		dac2similarity.clear();
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) 
			view.getArrangementComponentManager().setVisible(iter.next(), true);
		view.getArrangementSelectionManager().clearSelection();
	
		view.getDomainLayoutManager().setCollapseBySimilarityState(false);
		view.getDomainLayoutManager().toggleCollapseBySimilarity();
		
		chooser.dispose();
		structuralChange();
	}
	
	/**
	 * resets the similarity mapping
	 * 
	 * @param view
	 * 		the view providing the functionality 
	 */
	public void reset(DomainViewI view) {
		dac2similarity.clear();
		
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) 
			view.getArrangementComponentManager().setVisible(iter.next(), true);
	}
	
	/**
	 * Sets the threshold below which arrangements are collapsed.
	 * 
	 * @param view
	 * 		the view providing the functionality 
	 * @param thres
	 * 		the new threshold
	 */
	public void setThreshold(DomainViewI view, int thres) {
		if (type == DistanceMeasureType.JACARD) {
			this.memorizedSettingTjaccard = thres;
		} else {
			if (type == DistanceMeasureType.DOMAINDISTANCE) {
					this.memorizedSettingTdd = thres;
			}
		}
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();
			
			if (type == DistanceMeasureType.JACARD) {
				if (view.getDomainSimilarityManager().getSimilarity(dac) < thres) 
					view.getArrangementComponentManager().setVisible(dac, false);
				else
					view.getArrangementComponentManager().setVisible(dac, true);
			} else if (type == DistanceMeasureType.DOMAINDISTANCE) {
				if (view.getDomainSimilarityManager().getSimilarity(dac) <= thres) 
					view.getArrangementComponentManager().setVisible(dac, true);
				else
					view.getArrangementComponentManager().setVisible(dac, false);
			}
		}
		structuralChange();
	}
	
	/**
	 * Stores a similarity in percent for an arrangement compared
	 * with the reference arrangement.
	 * 
	 * @param dac
	 * 		the arrangement which similarity should be stored
	 * @param percent
	 * 		the similarity to the reference arrangement
	 */
	public void setSimilarity(ArrangementComponent dac, int percent) {
		dac2similarity.put(dac, percent);
	}
	
	/**
	 * Return the similarity for an arrangement compared to a
	 * reference arrangement.
	 * 
	 * @param dac
	 * 		the arrangement which similarity score is requested
	 * @return
	 * 		the similarity to the reference arrangement
	 */
	public int getSimilarity(ArrangementComponent dac) {
		if (dac2similarity.get(dac) == null)
			return 0;
		return dac2similarity.get(dac);
	}

	/**
	 * Helper method to transform the Jaccard distance to a
	 * percent similarity.
	 * 
	 * @param val
	 * 		the jaccard distance to be converted
	 * @return
	 * 		the jaccard similarity in percent
	 */
	private int jaccard2Percent(double val) {
		double similarityVal = 1 - val;
		return (int) (similarityVal * 100);
	}

	public int getMemorizedSettingTdd() {
		return memorizedSettingTdd;
	}
	
	public int getMemorizedSettingTjaccard() {
		return memorizedSettingTjaccard;
	}
	
	public void setMemorizedSettingDMT(DistanceMeasureType dmt) {
		memorizedSettingDMT=dmt;
	}
	
	public DistanceMeasureType getMemorizedSettingDMT() {
		return memorizedSettingDMT;
	}
}

