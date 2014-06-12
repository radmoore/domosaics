package domosaics.ui.tools.distmatrix.components;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.distmatrix.actions.CalcDomainDistanceAction;
import domosaics.ui.tools.distmatrix.actions.CalcJacardAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The MatrixLayoutManager can be used to react on action events 
 * triggered by the user. For instance if the distance measure is 
 * changed this class keeps track of the menus action states.
 * 
 * 
 * @author Andreas Held
 *
 */
public class MatrixLayoutManager extends DefaultLayoutManager{

	/**
	 * Enumeration of all actions specified for the matrix view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DistMatrixAction implements ActionEnumeration {
		
		JACCARD 		(CalcJacardAction.class),
		DOMAINEDIT 		(CalcDomainDistanceAction.class),
		;
		
		private Class<?> clazz;
		
		private DistMatrixAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	/**
	 * Constructor for a new MatrixLayoutManager
	 * 
	 * @param manager
	 * 		the views action manager
	 */
	public MatrixLayoutManager(ViewActionManager manager) {
		super(manager);
	}
	
	/**
	 * Sets the distance measure to Jaccard distance
	 */
	public void setToJaccardMode() {
		setState(DistMatrixAction.DOMAINEDIT, false);
	}
	
	/**
	 * Sets the distance measure to domain edit distance
	 */
	public void setToDomainDistanceMode() {
		setState(DistMatrixAction.JACCARD, false);
	}
	
	/**
	 * Returns whether or not the actually used distance 
	 * measure is the Jaccard distance
	 * 
	 * @return
	 * 		whether or not Jaccard distance is the actually used distance measure
	 */
	public boolean isJacard() {
		return getState(DistMatrixAction.JACCARD);
	}

	/**
	 * Returns whether or not the actually used distance 
	 * measure is the domain edit distance
	 * 
	 * @return
	 * 		whether or not domain edit distance is the actually used distance measure
	 */
	public boolean isDomainDistance() {
		return getState(DistMatrixAction.DOMAINEDIT);
	}
}
