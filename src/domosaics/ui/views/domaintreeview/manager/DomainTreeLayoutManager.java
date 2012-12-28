package domosaics.ui.views.domaintreeview.manager;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domaintreeview.actions.ShowArrangementsAction;
import domosaics.ui.views.domaintreeview.actions.ShowInDelsAction;
import domosaics.ui.views.domaintreeview.actions.ShowTreeAction;
import domosaics.ui.views.domainview.manager.DomainLayoutManager.DomainAction;
import domosaics.ui.views.treeview.manager.TreeLayoutManager.TreeAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The DomainTreeLayoutManager manages the layout options for the view,
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
public class DomainTreeLayoutManager extends DefaultLayoutManager {

	protected double treeSpace = 3;
	
	public double getTreeSpace() {
		return treeSpace;
	}
	
	public void setTreeSpace(int percent) {
		treeSpace = 100 / (double) percent;
		structuralChange();
	}
	
	/**
	 * Enumeration of all actions specified for the domain tree view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainTreeAction implements ActionEnumeration {
		
		SHOW_TREE			(ShowTreeAction.class),
		SHOW_ARRANGEMENTS	(ShowArrangementsAction.class),
		SHOW_INDELS 		(ShowInDelsAction.class),
		;
		
		private Class<?> clazz;
		
		
		private DomainTreeAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	/**
	 * Basic Constructor for the DomainTreeLayoutManager 
	 * 
	 * @param manager
	 * 		the action manager object to get control over the menu actions states.
	 */
	public DomainTreeLayoutManager(ViewActionManager manager) {
		super(manager);
//		disable(DomainAction.COLLAPSE_BY_SIMILARITY);
//		disable(DomainAction.COLLAPSE_IDENTICAL);
	}

  	/**
  	 * Returns whether or not domain events such as insertion deletions 
  	 * should be drawn
  	 * 
  	 * @return
  	 * 		whether or not domain events should be drawn within the tree
  	 */
	public boolean isShowInDels(){
		return getState(DomainTreeAction.SHOW_INDELS);
	}
	
	public void toggleShowTree() {
		if (isShowTree()) {
			enable(DomainTreeAction.SHOW_ARRANGEMENTS);
			
			enable(DomainTreeAction.SHOW_INDELS);
			
			enable(TreeAction.COLLAPSETREE);
			enable(TreeAction.EXPANDLEAVES);
			enable(TreeAction.SHOWBOOTSTRAP);
			enable(TreeAction.SHOWEDGELABELS);
			enable(TreeAction.SHOWINNERNODES);
			enable(TreeAction.SHOWRULER);
			enable(TreeAction.USEDISTANCES);
			enable(TreeAction.USELABELASBOOTSTRAP);
			enable(TreeAction.DEFAULTEDGESTYLE);
			enable(TreeAction.DEFAULTFONT);
			enable(TreeAction.SELECTIONEDGESTYLE);
			enable(TreeAction.SELECTIONFONT);
			enable(TreeAction.CLEARSELECTION);
			enable(TreeAction.COLORIZESELECTION);
			enable(TreeAction.SELECTALL);
			
//			disable(DomainAction.COLLAPSE_IDENTICAL);
			disable(DomainAction.COLLAPSE_BY_SIMILARITY);
		} else {
			disable(DomainTreeAction.SHOW_ARRANGEMENTS);
			
			disable(DomainTreeAction.SHOW_INDELS);
			
			disable(TreeAction.COLLAPSETREE);
			disable(TreeAction.EXPANDLEAVES);
			disable(TreeAction.SHOWBOOTSTRAP);
			disable(TreeAction.SHOWEDGELABELS);
			disable(TreeAction.SHOWINNERNODES);
			disable(TreeAction.SHOWRULER);
			disable(TreeAction.USEDISTANCES);
			disable(TreeAction.USELABELASBOOTSTRAP);
			disable(TreeAction.DEFAULTEDGESTYLE);
			disable(TreeAction.DEFAULTFONT);
			disable(TreeAction.SELECTIONEDGESTYLE);
			disable(TreeAction.SELECTIONFONT);
			disable(TreeAction.CLEARSELECTION);
			disable(TreeAction.COLORIZESELECTION);
			disable(TreeAction.SELECTALL);
			
//			enable(DomainAction.COLLAPSE_IDENTICAL);
			enable(DomainAction.COLLAPSE_BY_SIMILARITY);
			
			setState(DomainTreeAction.SHOW_INDELS, false);
			
		}
	}
	
	public void toggleShowArrangements() {
		if (isShowArrangements()) {
			enable(DomainTreeAction.SHOW_TREE);
			
			enable(DomainAction.EXPORT_SELECTION);
			enable(DomainAction.EVALUE_COLORIZATION);
			enable(DomainAction.FIT_TO_SCREEN);
			enable(DomainAction.MSA_VIEW);
			enable(DomainAction.PROP_VIEW);
			enable(DomainAction.UNPROP_VIEW);
			enable(DomainAction.SAVE_AS_XDOM);
			enable(DomainAction.SELECT_ARRANGEMENTS);
			enable(DomainAction.SELECT_SEQUENCES);
			enable(DomainAction.SHOW_RULER);
			enable(DomainAction.SHOW_SHAPES);
			enable(DomainAction.COLLAPSE_BY_SIMILARITY);
			enable(DomainAction.COLLAPSE_IDENTICAL);
			enable(DomainAction.OPEN_DISTMATRIX);
			enable(DomainAction.OPEN_DOMGRAPH);
			enable(DomainAction.OPEN_DOMLEGEND);
			enable(DomainAction.OPEN_DOMMATRIX);
			enable(DomainAction.OPEN_DOTPLOT);
			enable(DomainAction.OPEN_STATISTICS);
			enable(DomainAction.RESETSHIFT);
			enable(DomainAction.EDITARRANGEMENT);
			
		} else {
			disable(DomainTreeAction.SHOW_TREE);
			
			disable(DomainAction.EXPORT_SELECTION);
			disable(DomainAction.EVALUE_COLORIZATION);
			disable(DomainAction.FIT_TO_SCREEN);
			disable(DomainAction.MSA_VIEW);
			disable(DomainAction.PROP_VIEW);
			disable(DomainAction.UNPROP_VIEW);
			disable(DomainAction.SAVE_AS_XDOM);
			disable(DomainAction.SELECT_ARRANGEMENTS);
			disable(DomainAction.SELECT_SEQUENCES);
			disable(DomainAction.SHOW_RULER);
			disable(DomainAction.SHOW_SHAPES);
			disable(DomainAction.COLLAPSE_BY_SIMILARITY);
			disable(DomainAction.COLLAPSE_IDENTICAL);
			disable(DomainAction.OPEN_DISTMATRIX);
			disable(DomainAction.OPEN_DOMGRAPH);
			disable(DomainAction.OPEN_DOMLEGEND);
			disable(DomainAction.OPEN_DOMMATRIX);
			disable(DomainAction.OPEN_DOTPLOT);
			disable(DomainAction.OPEN_STATISTICS);
			disable(DomainAction.RESETSHIFT);
			disable(DomainAction.EDITARRANGEMENT);
			
		}
	}
	
  	/**
  	 * Returns whether or not the tree should be drawn
  	 * 
  	 * @return
  	 * 		whether or not tree should be drawn
  	 */
	public boolean isShowTree(){
		return getState(DomainTreeAction.SHOW_TREE);
	}
	
  	/**
  	 * Returns whether or not the arrangements should be drawn
  	 * 
  	 * @return
  	 * 		whether or not arrangements should be drawn
  	 */
	public boolean isShowArrangements(){
		return getState(DomainTreeAction.SHOW_ARRANGEMENTS);
	}
	
}
