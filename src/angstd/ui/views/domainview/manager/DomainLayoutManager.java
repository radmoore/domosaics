package angstd.ui.views.domainview.manager;

import angstd.model.configuration.Configuration;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.domainview.actions.ChangeArrangementAction;
import angstd.ui.views.domainview.actions.CollapseSameArrangementsAction;
import angstd.ui.views.domainview.actions.CreateViewUsingSelectionAction;
import angstd.ui.views.domainview.actions.EvalueColorizationAction;
import angstd.ui.views.domainview.actions.FitDomainsToScreenAction;
import angstd.ui.views.domainview.actions.MsaViewAction;
import angstd.ui.views.domainview.actions.ProportionalViewAction;
import angstd.ui.views.domainview.actions.ResetShiftAction;
import angstd.ui.views.domainview.actions.SaveXdomFileAction;
import angstd.ui.views.domainview.actions.SelectDomainArrangementsAction;
import angstd.ui.views.domainview.actions.SelectSequencesAction;
import angstd.ui.views.domainview.actions.ShowAccAction;
import angstd.ui.views.domainview.actions.ShowDistMatrixAction;
import angstd.ui.views.domainview.actions.ShowDomainGraphAction;
import angstd.ui.views.domainview.actions.ShowDomainLegendAction;
import angstd.ui.views.domainview.actions.ShowDomainMatrixAction;
import angstd.ui.views.domainview.actions.ShowDomainRulerAction;
import angstd.ui.views.domainview.actions.ShowDotplotAction;
import angstd.ui.views.domainview.actions.ShowIdAction;
import angstd.ui.views.domainview.actions.ShowNotesAction;
import angstd.ui.views.domainview.actions.ShowShapesAction;
import angstd.ui.views.domainview.actions.SimilarityColorizationAction;
import angstd.ui.views.domainview.actions.UnproportionalViewAction;
import angstd.ui.views.domainview.actions.notused.ShowStatisticsAction;
import angstd.ui.views.view.ViewActionManager;
import angstd.ui.views.view.actions.ToggleZoomModeAction;
import angstd.ui.views.view.manager.ActionEnumeration;
import angstd.ui.views.view.manager.DefaultLayoutManager;

/**
 * The DomainLayoutManager manages the layout options for the view,
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
public class DomainLayoutManager extends DefaultLayoutManager {
	
	/**
	 * Enumeration of all actions specified for the domain view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainAction implements ActionEnumeration {
		
		EXPORT_SELECTION 		(CreateViewUsingSelectionAction.class),
		EVALUE_COLORIZATION 	(EvalueColorizationAction.class),
		FIT_TO_SCREEN 			(FitDomainsToScreenAction.class),
		ID_RATHER_THAN_ACC		(ShowIdAction.class),
		ACC_RATHER_THAN_ID		(ShowAccAction.class),
		MSA_VIEW 				(MsaViewAction.class),
		PROP_VIEW 				(ProportionalViewAction.class),
		UNPROP_VIEW 			(UnproportionalViewAction.class),
		SAVE_AS_XDOM 			(SaveXdomFileAction.class),
		SELECT_ARRANGEMENTS 	(SelectDomainArrangementsAction.class),
		SELECT_SEQUENCES 		(SelectSequencesAction.class),
		SHOW_RULER 				(ShowDomainRulerAction.class),
		SHOW_SHAPES 			(ShowShapesAction.class),
		SHOW_NOTES				(ShowNotesAction.class),
		COLLAPSE_BY_SIMILARITY 	(SimilarityColorizationAction.class),
		COLLAPSE_IDENTICAL 		(CollapseSameArrangementsAction.class),
		OPEN_DISTMATRIX 		(ShowDistMatrixAction.class),
		OPEN_DOMGRAPH 			(ShowDomainGraphAction.class),
		OPEN_DOMLEGEND 			(ShowDomainLegendAction.class),
		OPEN_DOMMATRIX 			(ShowDomainMatrixAction.class),
		OPEN_DOTPLOT 			(ShowDotplotAction.class),
		OPEN_STATISTICS 		(ShowStatisticsAction.class),
		
		ZOOMMODE				(ToggleZoomModeAction.class),
		RESETSHIFT				(ResetShiftAction.class),
		EDITARRANGEMENT			(ChangeArrangementAction.class),
		;
		
		private Class<?> clazz;
		
		
		private DomainAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	/** old state for shapes when switching to unproportional */
	private boolean oldPropShapeState = false;
	
	/**
	 * Basic Constructor for the DomainLayoutManager 
	 * 
	 * @param manager
	 * 		the action manager object to get control over the menu actions states.
	 */
	public DomainLayoutManager(ViewActionManager manager) {
		super(manager);
		
		if (Configuration.isIdPreferedToAcc()) {
			setState(DomainAction.ID_RATHER_THAN_ACC, false);
			setState(DomainAction.ACC_RATHER_THAN_ID, true);
			disable(DomainAction.ACC_RATHER_THAN_ID);
		}
		else {
			disable(DomainAction.ID_RATHER_THAN_ACC);
		}
		if (isProportionalView())
			disable(DomainAction.PROP_VIEW);
		
		if (isUnproportionalView())
			
			disable(DomainAction.UNPROP_VIEW);
		if (isMsaView())
			
			disable(DomainAction.MSA_VIEW);
	}
	
	/**
	 * Enables all action
	 */
	public void enableAll() {
		for (DomainAction action : DomainAction.values())
			enable(action);
	}
	
	/**
	 * switches the domain view by ID.
	 */
	public void changeIdOrAccView() {
		Configuration.setIdPreferedToAcc(!Configuration.isIdPreferedToAcc());
		if(Configuration.isIdPreferedToAcc()) {
			setState(DomainAction.ID_RATHER_THAN_ACC, false);
			disable(DomainAction.ACC_RATHER_THAN_ID);
			enable(DomainAction.ID_RATHER_THAN_ACC);
		}
		else {
			setState(DomainAction.ACC_RATHER_THAN_ID, false);
			disable(DomainAction.ID_RATHER_THAN_ACC);
			enable(DomainAction.ACC_RATHER_THAN_ID);
		}
	}
	
	/**
	 * switches the layout to proportional view.
	 */
	public void setToProportionalView() {
		enableAll();
		
		disable(DomainAction.PROP_VIEW);
		
		setState(DomainAction.PROP_VIEW, true);
		setState(DomainAction.UNPROP_VIEW, false);
		setState(DomainAction.MSA_VIEW, false);
		setState(DomainAction.SHOW_SHAPES, oldPropShapeState);
		setState(DomainAction.SELECT_SEQUENCES, false);

		structuralChange();	
	}
	
	/**
	 * switches the layout to unproportional view.
	 */
	public void setToUnproportionalView() {
		enableAll();
		disable(DomainAction.UNPROP_VIEW);
		
		if(isProportionalView())
			oldPropShapeState = getState(DomainAction.SHOW_SHAPES);
		
		setState(DomainAction.UNPROP_VIEW, true);
		setState(DomainAction.PROP_VIEW, false);
		setState(DomainAction.MSA_VIEW, false);
		setState(DomainAction.SHOW_SHAPES, true);
		setState(DomainAction.SELECT_SEQUENCES, false);
		
		disable(DomainAction.SHOW_RULER);
		disable(DomainAction.SELECT_SEQUENCES);
		
		structuralChange();	
	}
	
	/**
	 * switches the layout to MSA view. 
	 */
	public void setToMsaView() {
		enableAll();
		disable(DomainAction.MSA_VIEW);
		
		setState(DomainAction.MSA_VIEW, true);
		setState(DomainAction.PROP_VIEW, false);
		setState(DomainAction.UNPROP_VIEW, false);
		setState(DomainAction.SELECT_SEQUENCES, false);
	
		disable(DomainAction.MSA_VIEW);
		disable(DomainAction.FIT_TO_SCREEN);
		disable(DomainAction.SHOW_SHAPES);
		disable(DomainAction.SELECT_ARRANGEMENTS);
		disable(DomainAction.SELECT_SEQUENCES);
		disable(DomainAction.COLLAPSE_BY_SIMILARITY);
		disable(DomainAction.EVALUE_COLORIZATION);
		
		structuralChange();	
	}
	
	/**
	 * Toggles the CollapseSameArrangements flag.
	 */
	public void toggleCollapseSameArrangements() {
		if (getState(DomainAction.COLLAPSE_IDENTICAL)) {
			disable(DomainAction.COLLAPSE_BY_SIMILARITY);
			disable(DomainAction.ZOOMMODE);
			disable(DomainAction.SHOW_NOTES);
		} 
		else {
			enable(DomainAction.COLLAPSE_BY_SIMILARITY);
			enable(DomainAction.ZOOMMODE);
			enable(DomainAction.SHOW_NOTES);
		}
			
	}
	
	
	/**
	 * toggles the internal flag for the select underlying 
	 * sequences mode
	 */
	public void toggleSelectSequences() {
		if (getState(DomainAction.SELECT_SEQUENCES)) {
			disable(DomainAction.COLLAPSE_IDENTICAL);
			disable(DomainAction.COLLAPSE_BY_SIMILARITY);
			disable(DomainAction.SELECT_ARRANGEMENTS);
			disable(DomainAction.FIT_TO_SCREEN);
			disable(DomainAction.ZOOMMODE);
			disable(DomainAction.EXPORT_SELECTION);
			disable(DomainAction.SHOW_RULER);
		} 
		else {
			enable(DomainAction.COLLAPSE_IDENTICAL);
			enable(DomainAction.COLLAPSE_BY_SIMILARITY);
			enable(DomainAction.SELECT_ARRANGEMENTS);
			enable(DomainAction.FIT_TO_SCREEN);
			enable(DomainAction.ZOOMMODE);
			enable(DomainAction.EXPORT_SELECTION);
			enable(DomainAction.SHOW_RULER);
		}
	}
	
	/**
	 * Set the OrthologousMode flag to the specified state.
	 * 
	 * @param state
	 * 		the new state for the OrthologousMode flag.
	 */
	public void toggleCompareDomainsMode(boolean isCompareDomainsMode) {
		if (isCompareDomainsMode) {
			disable(DomainAction.EVALUE_COLORIZATION);
			disable(DomainAction.SELECT_SEQUENCES);
			disable(DomainAction.SELECT_ARRANGEMENTS);
			disable(DomainAction.EXPORT_SELECTION);
			disable(DomainAction.ZOOMMODE);
			disable(DomainAction.MSA_VIEW);
			if (isProportionalView())
				disable(DomainAction.UNPROP_VIEW);
			else
				disable(DomainAction.PROP_VIEW);
		} 
		else {
			enable(DomainAction.EVALUE_COLORIZATION);
			enable(DomainAction.SELECT_SEQUENCES);
			enable(DomainAction.SELECT_ARRANGEMENTS);
			enable(DomainAction.EXPORT_SELECTION);
			enable(DomainAction.ZOOMMODE);
			enable(DomainAction.MSA_VIEW);
			if (isProportionalView())
				enable(DomainAction.UNPROP_VIEW);
			else
				enable(DomainAction.PROP_VIEW);
		}
	}
	
	/**
	 * Sets the state for the CollapseBySimilarity action
	 * 
	 * @param state
	 * 		the new state for this action
	 */
	public void setCollapseBySimilarityState(boolean state) {
		setState(DomainAction.COLLAPSE_BY_SIMILARITY, state);
	}
	
	/**
	 * Toggles the SimilarityColorization flag.
	 */
	public void toggleCollapseBySimilarity() {
		if (getState(DomainAction.COLLAPSE_BY_SIMILARITY)) {
			disable(DomainAction.COLLAPSE_IDENTICAL);
			disable(DomainAction.MSA_VIEW);
			disable(DomainAction.ZOOMMODE);
			disable(DomainAction.SELECT_SEQUENCES);
			disable(DomainAction.EXPORT_SELECTION);
			disable(DomainAction.SELECT_ARRANGEMENTS);
			disable(DomainAction.COLLAPSE_BY_SIMILARITY);
			disable(DomainAction.SHOW_NOTES);
			
			if (isProportionalView())
				disable(DomainAction.UNPROP_VIEW);
			else
				disable(DomainAction.PROP_VIEW);
		} 
		else {
			enable(DomainAction.COLLAPSE_IDENTICAL);
			enable(DomainAction.MSA_VIEW);
			enable(DomainAction.ZOOMMODE);
			enable(DomainAction.SELECT_SEQUENCES);
			enable(DomainAction.EXPORT_SELECTION);
			enable(DomainAction.SELECT_ARRANGEMENTS);
			enable(DomainAction.COLLAPSE_BY_SIMILARITY);
			enable(DomainAction.SHOW_NOTES);
			
			if (isProportionalView())
				enable(DomainAction.UNPROP_VIEW);
			else
				enable(DomainAction.PROP_VIEW);
			
		}
	}
	
	/**
	 * Toggles the EvalueColorization flag.
	 */
	public void toggleEvalueColorization() {
		visualChange();
	}

	/**
	 * Return whether or not the view is in MSA mode
	 * 
	 * @return
	 * 		whether or not the view is in MSA mode
	 */
	public boolean isMsaView() {
		return getState(DomainAction.MSA_VIEW);
	}
	
	/**
	 * Return whether or not the view is in unproportional mode
	 * 
	 * @return
	 * 		whether or not the view is in unproportional mode
	 */
	public boolean isUnproportionalView() {
		return getState(DomainAction.UNPROP_VIEW);
	}
	
	/**
	 * Return whether or not the view is in proportional mode
	 * 
	 * @return
	 * 		whether or not the view is in proportional mode
	 */
	public boolean isProportionalView() {
		return getState(DomainAction.PROP_VIEW);
	}
	
	/**
	 * Return whether or not the arrangement selection mode is enabled
	 * 
	 * @return
	 * 		whether or not the arrangement selection mode is enabled
	 */
	public boolean isSelectArrangements() {
		if(!isEnabled(DomainAction.SELECT_ARRANGEMENTS))
			return false;
		return getState(DomainAction.SELECT_ARRANGEMENTS);
	}
	
	/**
	 * Return whether or not the select underlying sequence mode is enabled
	 * 
	 * @return
	 * 		whether or not the select underlying sequence mode is enabled
	 */
	public boolean isSelectSequences() {
		if(!isEnabled(DomainAction.SELECT_SEQUENCES))
			return false;
		return getState(DomainAction.SELECT_SEQUENCES);
	}
	
	/** 
	 * toggles the fit domains to screen flag and triggers a relayout 
	 */
	public void toggleFitDomainsToScreen() {
		structuralChange();	
	}
	
	/**
	 * Return whether or not the domains should be fit into screen size
	 * 
	 * @return
	 * 		whether or not the domains should be fit into screen size
	 */
	public boolean isFitDomainsToScreen() {
		if(!isEnabled(DomainAction.FIT_TO_SCREEN))
			return false;
		return getState(DomainAction.FIT_TO_SCREEN);
	}
	
	/**
	 * Return whether or not shapes should be drawn
	 * 
	 * @return
	 * 		whether or not shapes should be drawn
	 */
	public boolean isShowShapes() {
		if(!isEnabled(DomainAction.SHOW_SHAPES))
			return false;
		return getState(DomainAction.SHOW_SHAPES);
	}
	
	/**
	 * Return whether or not the amino acid ruler should be drawn
	 * 
	 * @return
	 * 		whether or not the amino acid ruler should be drawn
	 */
	public boolean isShowLineal() {
		if(!isEnabled(DomainAction.SHOW_RULER))
			return false;
		return getState(DomainAction.SHOW_RULER);
	}
	
	/**
	 * Return whether or not CollapseSameArrangements mode is active
	 * 
	 * @return
	 * 		whether or not CollapseSameArrangements mode is active
	 */
	public boolean isCollapseSameArrangements() {
		if(!isEnabled(DomainAction.COLLAPSE_IDENTICAL))
			return false;
		return getState(DomainAction.COLLAPSE_IDENTICAL);
	}
	
	/**
	 * Return whether or not EvalueColorization mode is active
	 * 
	 * @return
	 * 		whether or not EvalueColorization mode is active
	 */
	public boolean isEvalueColorization() {
		if(!isEnabled(DomainAction.EVALUE_COLORIZATION))
			return false;
		return getState(DomainAction.EVALUE_COLORIZATION);
	}
	
	/**
	 * Return whether or not SimilarityColorization mode is active
	 * 
	 * @return
	 * 		whether or not SimilarityColorization mode is active
	 */
	public boolean isCollapseBySimilarity() {
		return getState(DomainAction.COLLAPSE_BY_SIMILARITY);
	}
	
	
	public void setFitDomainsToScreen(boolean val) {
		setState(DomainAction.FIT_TO_SCREEN, val);
	}

	public void setShowShapes(boolean val) {
		setState(DomainAction.SHOW_SHAPES, val);
	}
	
	public boolean isShowNotes() {
		if (!isEnabled(DomainAction.SHOW_NOTES))
			return false;
		return getState(DomainAction.SHOW_NOTES);
	}
}








