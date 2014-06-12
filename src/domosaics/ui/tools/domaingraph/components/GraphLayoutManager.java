package domosaics.ui.tools.domaingraph.components;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domaingraph.actions.DisableForcesAction;
import domosaics.ui.tools.domaingraph.actions.DrawCirclesAction;
import domosaics.ui.tools.domaingraph.actions.DrawDomainNamesAction;
import domosaics.ui.tools.domaingraph.actions.DrawDomainShapesAction;
import domosaics.ui.tools.domaingraph.actions.UseCurvedEdgesAction;
import domosaics.ui.tools.domaingraph.actions.UseForceLayoutAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The GraphLayoutManager can be used to react on action events 
 * triggered by the user. For instance if theuser wants to change the style 
 * in which domains are rendered. Therefore this class keeps track of the 
 * menus action states.
 * 
 * 
 * @author Andreas Held
 *
 */
public class GraphLayoutManager extends DefaultLayoutManager {

	/** flag indicating that domains are rendered as circular shapes */
	public static final int CIRCULAR_RENDERER = 0;
	
	/** flag indicating that domains are rendered with their names */
	public static final int LABEL_RENDERER = 1;
	
	/** flag indicating that domains are rendered as their domain shapes */
	public static final int DOMAINSHAPE_RENDERER = 2;
	
	/**
	 * Enumeration of all actions specified for the legend view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	enum GraphAction implements ActionEnumeration {
		
		FORCELAYOUT 		(UseForceLayoutAction.class),
		DISABLEFORCES 		(DisableForcesAction.class),
		CURVEDEDGES 		(UseCurvedEdgesAction.class),
		DOMAINSHAPES 		(DrawDomainShapesAction.class),
		DOMAINNAMES 		(DrawDomainNamesAction.class),
		CIRCLES 			(DrawCirclesAction.class),
		;
		
		private Class<?> clazz;
		
		private GraphAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	/**
	 * Constructor for a new GraphLayoutManager
	 * 
	 * @param manager
	 * 		the views action manager
	 */
	public GraphLayoutManager(ViewActionManager manager) {
		super(manager);
		
		if (isDrawCircles())
			disable(GraphAction.CIRCLES);
		if (isDrawDomainNames())
			disable(GraphAction.DOMAINNAMES);
		if (isDrawDomainShapes())
			disable(GraphAction.DOMAINSHAPES);
		
		disable(GraphAction.DISABLEFORCES);
	}
	
	/**
	 * Method returning the renderer which should be used to render the
	 * graph domains.
	 * 
	 * @return
	 * 		renderer for graph domains
	 */
	public int getRendererIndex() {
		if (isDrawCircles())
			return CIRCULAR_RENDERER;
		if (isDrawDomainNames())
			return LABEL_RENDERER;
		if (isDrawDomainShapes())
			return DOMAINSHAPE_RENDERER;
		return -1;
	}
	
	/**
	 * Sets the rendering of domains to circular shapes
	 */
	public void setToCircularShapes() {
		setState(GraphAction.DOMAINSHAPES, false);
		setState(GraphAction.DOMAINNAMES, false);
		enable(GraphAction.DOMAINSHAPES);
		enable(GraphAction.DOMAINNAMES);
		disable(GraphAction.CIRCLES);
	}
	
	/**
	 * Sets the rendering of domains using their names
	 */
	public void setToNames() {
		setState(GraphAction.DOMAINSHAPES, false);
		setState(GraphAction.CIRCLES, false);
		enable(GraphAction.DOMAINSHAPES);
		enable(GraphAction.CIRCLES);
		disable(GraphAction.DOMAINNAMES);
	}
	
	/**
	 * Sets the rendering of domains using their domain shapes
	 */
	public void setToDomainShapes() {
		setState(GraphAction.CIRCLES, false);
		setState(GraphAction.DOMAINNAMES, false);
		enable(GraphAction.CIRCLES);
		enable(GraphAction.DOMAINNAMES);
		disable(GraphAction.DOMAINSHAPES);
	}
	
	/**
	 * Sets the action flags if the use of a force driven layout changes
	 */
	public void toggleUseForceLayout() {
		if (isUseForceLayout()) {
			enable(GraphAction.DISABLEFORCES);
			setState(GraphAction.DISABLEFORCES, false);
		} else
			disable(GraphAction.DISABLEFORCES);
	}
	
	/**
	 * Returns whether curved or straight edges should be rendered
	 * 
	 * @return
	 * 		whether curved or straight edges should be rendered
	 */
	public boolean isUseCurvedEdges() {
		return getState(GraphAction.CURVEDEDGES);
	}
	
	/**
	 * Returns whether or not a force driven layout should be used
	 * 
	 * @return
	 * 		whether or not a force driven layout should be used
	 */
	public boolean isUseForceLayout(){
		return getState(GraphAction.FORCELAYOUT);
	}
	
	/**
	 * Returns whether or not the forces within the force layout 
	 * should be disabled
	 * 
	 * @return
	 * 		whether or not the forces should be disabled
	 */
	public boolean isDisableForces(){
		return getState(GraphAction.DISABLEFORCES);
	}
	
	/**
	 * Returns whether or not domains should be drawn as circles
	 * 
	 * @return
	 * 		whether or not domains should be drawn as circles
	 */
	public boolean isDrawCircles(){
		return getState(GraphAction.CIRCLES);
	}
	
	/**
	 * Returns whether or not domains should be drawn using their names
	 * 
	 * @return
	 * 		whether or not domains should be drawn using their names
	 */
	public boolean isDrawDomainNames(){
		return getState(GraphAction.DOMAINNAMES);
	}
	
	/**
	 * Returns whether or not domains should be drawn using their shapes
	 * 
	 * @return
	 * 		whether or not domains should be drawn using their shapes
	 */
	public boolean isDrawDomainShapes(){
		return getState(GraphAction.DOMAINSHAPES);
	}
	
}
