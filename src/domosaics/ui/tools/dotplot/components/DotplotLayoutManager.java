package domosaics.ui.tools.dotplot.components;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.dotplot.actions.Blosum65MatrixAction;
import domosaics.ui.tools.dotplot.actions.IdentityMatrixAction;
import domosaics.ui.tools.dotplot.actions.ShowDomainMatchesAction;
import domosaics.ui.tools.dotplot.actions.ShowPlotAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The DotplotLayoutManager can be used to react on action events 
 * triggered by the user. For instance if domain similarity boxes
 * should be drawn. Therefore this class keeps track of the 
 * menus action states.
 * 
 * @author Andreas Held
 *
 */
public class DotplotLayoutManager extends DefaultLayoutManager {
	
	/**
	 * Enumeration of all actions specified for the dotplot view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	enum DotplotAction implements ActionEnumeration {
		
		BLOSUM65 			(Blosum65MatrixAction.class),
		IDENTITY 			(IdentityMatrixAction.class),
		DOMAINMATCH 		(ShowDomainMatchesAction.class),
		SHOWPLOT 			(ShowPlotAction.class),
		;
		
		private Class<?> clazz;
		
		private DotplotAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	/**
	 * Constructor for a new DotplotLayoutManager
	 * 
	 * @param manager
	 * 		the views action manager
	 */
	public DotplotLayoutManager(ViewActionManager manager) {
		super(manager);
	}
	
	/**
	 * Returns whether or not the dotplot should be shown.
	 * 
	 * @return
	 * 		whether or not the dotplot should be shown.
	 */
	public boolean isShowDotplot(){
		return getState(DotplotAction.SHOWPLOT);
	}
	
	/**
	 * Returns whether or not the domain similarity boxes should be shown.
	 * 
	 * @return
	 * 		whether or not the domain similarity boxes should be shown.
	 */
	public boolean isShowDomainMatches(){
		return getState(DotplotAction.DOMAINMATCH);
	}

}
