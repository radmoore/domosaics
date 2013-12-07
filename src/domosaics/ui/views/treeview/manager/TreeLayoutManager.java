package domosaics.ui.views.treeview.manager;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.actions.ChangeDefaultEdgeStyleAction;
import domosaics.ui.views.treeview.actions.ChangeDefaultFontAction;
import domosaics.ui.views.treeview.actions.ChangeEdgeStyleForSelectionAction;
import domosaics.ui.views.treeview.actions.ChangeFontForSelectionAction;
import domosaics.ui.views.treeview.actions.ClearSelectionAction;
import domosaics.ui.views.treeview.actions.ColorizeSelectionAction;
import domosaics.ui.views.treeview.actions.ExpandLeavesAction;
import domosaics.ui.views.treeview.actions.SelectAllAction;
import domosaics.ui.views.treeview.actions.ShowBootStrapValuesAction;
import domosaics.ui.views.treeview.actions.ShowEdgeLabelsAction;
import domosaics.ui.views.treeview.actions.ShowInnerNodesAction;
import domosaics.ui.views.treeview.actions.ShowTreeRulerAction;
import domosaics.ui.views.treeview.actions.UseDistancesAction;
import domosaics.ui.views.treeview.actions.UseLabelAsBootstrapAction;
import domosaics.ui.views.treeview.actions.context.CollapseTreeAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The TreeLayoutManager manages the layout options for the view,
 * which can be changed within actions. <br>
 * Basically this class is used within various layout and rendering 
 * processes to ensure the correct layout/ rendering by disabling and
 * enabling actions as well as setting their states.
 * <p>
 * When changing a layout setting this manager can be used to trigger a
 * relayout or just a repaint by using the DefaultViewManager methods.
 * <p>
 * If layout options are not compatible with each other
 * the viewActionManager object can be used to ensure correctness
 * within the action states.
 * <p>
 * When an action is triggered it is possible that the processing of 
 * this action is not compatible with other actions. Therefore those 
 * actions must be inactivated. Whenever a layout action is triggered
 * this class manipulates also the enabled state for all actions
 * within the view menu.
 * 
 * @author Andreas Held
 *
 */
public class TreeLayoutManager extends DefaultLayoutManager {

	protected double treeSpace = 1;
	
	public double getTreeSpace() {
		return treeSpace;
	}
	
	public void setTreeSpace(int percent) {
		treeSpace = 100 / (double) percent;
		structuralChange();
	}
	
	
	/**
	 * Enumeration of all actions specified for the tree view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum TreeAction implements ActionEnumeration {
		
		COLLAPSETREE 		(CollapseTreeAction.class),
		EXPANDLEAVES 		(ExpandLeavesAction.class),
		SHOWBOOTSTRAP 		(ShowBootStrapValuesAction.class),
		SHOWEDGELABELS 		(ShowEdgeLabelsAction.class),
		SHOWINNERNODES 		(ShowInnerNodesAction.class),
		SHOWRULER 			(ShowTreeRulerAction.class),
		USEDISTANCES 		(UseDistancesAction.class),
		USELABELASBOOTSTRAP	(UseLabelAsBootstrapAction.class),
		
		DEFAULTEDGESTYLE	(ChangeDefaultEdgeStyleAction.class),
		DEFAULTFONT			(ChangeDefaultFontAction.class),
		SELECTIONEDGESTYLE	(ChangeEdgeStyleForSelectionAction.class),
		SELECTIONFONT		(ChangeFontForSelectionAction.class),
		CLEARSELECTION		(ClearSelectionAction.class),
		COLORIZESELECTION	(ColorizeSelectionAction.class),
		SELECTALL			(SelectAllAction.class),
		;
		
		private Class<?> clazz;
		
		
		private TreeAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}

	/**
	 * Basic Constructor for the TreeLayoutManager 
	 * 
	 * @param manager
	 * 		the action manager object to get control over the menu actions states.
	 */
	public TreeLayoutManager(ViewActionManager manager) {
		super(manager);
	}
	
	/**
	 * Toggles the flag if edge labels should be drawn
	 */
  	public void toggleDrawEdgeWeights() {
  		if (isDrawEdgeWeights()) {
  			setState(TreeAction.SHOWBOOTSTRAP, false);
  		}
  		visualChange();
	}
	
  	/**
  	 * Toggles the flag if bootstrap values should be shown instead of
  	 * edge weights
  	 */
  	public void toggleShowBootstrap() {
		if(isShowBootstrap()) {
			setState(TreeAction.SHOWEDGELABELS, false);
			setState(TreeAction.USELABELASBOOTSTRAP, false);
		}
		visualChange();
	}

	/**
  	 * Returns whether or not the bootstrap values should be shown
  	 * instead of the edge weights
  	 *
	 * @return
	 * 		whether or not the bootstrap values should be shown
	 */
	public boolean isShowBootstrap(){
		return getState(TreeAction.SHOWBOOTSTRAP);
	}
	
	/**
  	 * Returns whether or not numeric node labels should be 
  	 * treated as bootstrap values
  	 *
	 * @return
	 * 		whether or not numeric node labels should be treated as bootstrap values
	 */
	public boolean isTreatLabelAsBootstrap(){
		return getState(TreeAction.USELABELASBOOTSTRAP);
	}

	/**
  	 * Returns whether or not edge labels should be drawn
  	 *
	 * @return
	 * 		whether or not edge labels should be drawn
	 */
	public boolean isDrawEdgeWeights(){
		return getState(TreeAction.SHOWEDGELABELS);
	}

	/**
  	 * Returns whether or not the weight of edges are used
  	 *
	 * @return
	 * 		whether or not the weight of edges are used
	 */
	public boolean isUseDistances() {
		return getState(TreeAction.USEDISTANCES);
	}
	
	/**
  	 * Returns whether or not the leaves are aligned to the right
  	 *
	 * @return
	 * 		whether or not the leaves are aligned to the right
	 */
	public boolean isExpandLeaves() {
		return getState(TreeAction.EXPANDLEAVES);
	}
	
	/**
  	 * Returns whether or not inner nodes should me marked as circles
  	 *
	 * @return
	 * 		whether or not inner nodes should me marked as circles
	 */
	public boolean isShowInnerNodes() {
		return getState(TreeAction.SHOWINNERNODES);
	}
	
	/**
  	 * Returns whether or not the tree ruler should be displayed
  	 *
	 * @return
	 * 		whether or not the tree ruler should be displayed
	 */
	public boolean isShowLegend() {
		return getState(TreeAction.SHOWRULER);
	}
	
	/* **************************************************************** *
	 *                  SET METHODS FOR PROJECT LOADING                 *
	 * **************************************************************** */
	
	public void setShowBootstrap(boolean flag) {
		manager.getAction(ShowBootStrapValuesAction.class).setState(flag);
	}

  	public void setTreatLabelAsBootstrap(boolean flag){
		manager.getAction(UseLabelAsBootstrapAction.class).setState(flag);
	}

  	public void setDrawEdgeLabels(boolean flag){
		manager.getAction(ShowEdgeLabelsAction.class).setState(flag);
	}

	public void setUseDistances(boolean flag) {
		manager.getAction(UseDistancesAction.class).setState(flag);
	}

	public void setExpandLeaves(boolean flag) {
		manager.getAction(ExpandLeavesAction.class).setState(flag);
	}

	public void setShowInnerNodes(boolean flag) {
		manager.getAction(ShowInnerNodesAction.class).setState(flag);
	}

	public void setShowLegend(boolean flag) {
		manager.getAction(ShowTreeRulerAction.class).setState(flag);
	}
}
