package domosaics.ui.wizards;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import domosaics.algos.distance.DistanceMeasureType;
import domosaics.algos.treecreation.TreeCreationAlgoType;
import domosaics.model.DataType;
import domosaics.model.workspace.CategoryElement;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.ViewInfo;




/**
 * Helper class for GUI components that are used frequently
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class GUIComponentFactory {

	/**
	 * Creates a list of all available {@link DataType}s 
	 * (e.g. sequence, arrangement, tree).
	 * 
	 * @return
	 * 		JList containing all available data types.
	 */
	public static JList createDataTypeList() {
		JList list = new JList(DataType.values());;
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(5);
		list.setCellRenderer(new WizardListCellRenderer());
		return list;
	}
	
	/**
	 * Creates a list containing all existing workspace projects. A 
	 * DefaultModel is used to add the project elements to the list
	 * so that the list can be changed on the fly (e.g. when a new
	 * project is created during the same wizard page).
	 * 
	 * @return
	 * 		list containing all existing workspace projects.
	 */
	public static JList createProjectList() {
		JList list = new JList(new DefaultListModel());
		list.setLayoutOrientation(JList.VERTICAL);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(10);
		list.setCellRenderer(new WizardListCellRenderer());
		
		for (ProjectElement project : WorkspaceManager.getInstance().getProjects())
			((DefaultListModel) list.getModel()).addElement(project);
		
		return list;
	}
	
	
	/**
	 * Creates a JComboBox containing all projects in the workspace
	 * 
	 * @param selectedProject
	 * 		Selected element in the combobox, can be null
	 * @return
	 * 		A JComboBox with all projects (with selectedProject selected)
	 */
	public static JComboBox createSelectProjectBox(ProjectElement selectedProject) {

		// get available projects
		ProjectElement[] projects = WorkspaceManager.getInstance().getProjects();
		JComboBox selectProjectList = new JComboBox(projects);
		selectProjectList.setSelectedItem(selectedProject);
		
		selectProjectList.setRenderer(new WizardListCellRenderer());
		selectProjectList.setPreferredSize(new Dimension(100, 25));
	
		return selectProjectList;
	}
	
	
	/**
	 * Creates a JComboBox containing all views from project
	 * (ie, from all categories)
	 * 
	 * @param project
	 * 		Project from which to retrieve all views
	 * @return
	 * 		A JComboBox with all views belonging to project
	 */
	public static JComboBox createViewBox(ProjectElement project) {
		List<WorkspaceElement> allViews = new ArrayList<WorkspaceElement>();
		allViews = project.getViews();
		JComboBox selectionBox = new JComboBox();
		
		if ( (allViews == null) || (allViews.isEmpty()) ) {
			selectionBox.setEnabled(false);
			return selectionBox;
		}
		ViewElement[] views = allViews.toArray(new ViewElement[allViews.size()]);
		
		selectionBox = new JComboBox(views);
		selectionBox.setRenderer(new WizardListCellRenderer());
		selectionBox.setEnabled(true);
		return selectionBox;
	}
	

	
	
	/**
	 * Creates a JComboBox containing all tree views from all projects
	 * 
	 * @param autoselect
	 * 		flag indicating whether or not the active view should be auto selected
	 * @return
	 * 		JComboBox containing all tree views from all projects
	 */
	public static JComboBox createSelectTreeViewBox(boolean autoselect) {
		// get loaded sequence views
		List<WorkspaceElement> views = WorkspaceManager.getInstance().getTreeViews();
		ViewElement[] treeViews = views.toArray(new ViewElement[views.size()]);
		
		// get the active view for predetermined view choosing
		ViewElement activeView = null;
		if (autoselect) {
			View actView = ViewHandler.getInstance().getActiveView();
			if (actView != null && actView instanceof TreeViewI)
				activeView = WorkspaceManager.getInstance().getViewElement(actView.getViewInfo());
		}
		
		// create the box
		JComboBox selectViewList = new JComboBox(treeViews);
		selectViewList.setSelectedItem(activeView);
		selectViewList.setRenderer(new WizardListCellRenderer());
		selectViewList.setPreferredSize(new Dimension(100, 25));
	
		return selectViewList;
	}
	
	
	/**
	 * Creates a JComboBox containing all tree views from specified project
	 * This is useful when importing data, where the project selection has occurred in
	 * the first step of the wizard.
	 * 
	 * 	@param project
	 * 		project from which the views are considered
	 * @return
	 * 		JComboBox containing all tree views from project
	 */
	public static JComboBox createSelectTreeViewBox(ProjectElement project) {

		JComboBox selectViewList;
		
		// get loaded tree views (for the current project)
		WorkspaceManager wsm = WorkspaceManager.getInstance();
		CategoryElement cat = wsm.getProject(project.getTitle()).getCategory(ViewType.TREE);

		// incase there are no views of this type
		if (cat != null) {
			List<WorkspaceElement> views = cat.getViews();
			ViewElement[] treeViews = views.toArray(new ViewElement[views.size()]);
			// create the box
			selectViewList = new JComboBox(treeViews);
			selectViewList.setSelectedItem(null);
			selectViewList.setRenderer(new WizardListCellRenderer());
			selectViewList.setPreferredSize(new Dimension(100, 25));
		}
		else {
			selectViewList = new JComboBox();
			selectViewList.setEnabled(false);
		}
		
		return selectViewList;
	}
	
	/**
	 * Creates a JComboBox containing all domain views from all projects
	 * 
	 *  @param autoselect
	 * 		flag indicating whether or not the active view should be auto selected
	 * @return
	 * 		JComboBox containing all domain views from all projects
	 */
	public static JComboBox createSelectDomViewBox(boolean autoselect) {
		// get loaded domain views
		List<WorkspaceElement> views = WorkspaceManager.getInstance().getDomainViews();
		ViewElement[] domViews = views.toArray(new ViewElement[views.size()]);
		
		
		// get the active view for predetermined view choosing
		ViewElement activeView = null;
		if (autoselect) {
			View actView = ViewHandler.getInstance().getActiveView();
			if (actView != null && actView instanceof DomainViewI)
				activeView = WorkspaceManager.getInstance().getViewElement(actView.getViewInfo());
		}
	
		// create the box
		JComboBox selectViewList = new JComboBox(domViews);
		
		selectViewList.setSelectedItem(activeView);
		selectViewList.setRenderer(new WizardListCellRenderer());
		selectViewList.setPreferredSize(new Dimension(100, 25));
		
		return selectViewList;
	}
	
	
	/**
	 * Creates a JComboBox containing all domain views from specified project
	 * This is useful when importing data, where the project selection has occurred in
	 * the first step of the wizard.
	 * 
	 * 	@param project
	 * 		project from which the views are considered
	 * @return
	 * 		JComboBox containing all domain views from project
	 */
	public static JComboBox createSelectDomViewBox(ProjectElement project) {

		JComboBox selectViewList;
		
		// get loaded domain views (for the current project)
		WorkspaceManager wsm = WorkspaceManager.getInstance();
		CategoryElement cat = wsm.getProject(project.getTitle()).getCategory(ViewType.DOMAINS);
		if(cat!=null) {
			List<WorkspaceElement> views = cat.getViews();
			ViewElement[] domViews = views.toArray(new ViewElement[views.size()]);
			// create the box
			selectViewList = new JComboBox(domViews);
			selectViewList.setSelectedItem(null);
			selectViewList.setRenderer(new WizardListCellRenderer());
			selectViewList.setPreferredSize(new Dimension(100, 25));
		} else {
			selectViewList = new JComboBox();
			selectViewList.setEnabled(false);
		}
		
		return selectViewList;
	}
	
	/**
	 * Creates a JComboBox containing all sequence views from all projects
	 * 
	 * @param autoselect
	 * 		flag indicating whether or not the active view should be auto selected
	 * @return
	 * 		JComboBox containing all sequence views from all projects
	 */
	public static JComboBox createSelectSeqViewBox(boolean autoselect) {
		// get loaded sequence views
		List<WorkspaceElement> views = WorkspaceManager.getInstance().getSequenceViews();
		ViewElement[] seqViews = views.toArray(new ViewElement[views.size()]);
		
		// get the active view for predetermined view choosing
		ViewElement activeView = null;
		if (autoselect) {
			View actView = ViewHandler.getInstance().getActiveView();
			if (actView != null && actView instanceof SequenceView)
				activeView = WorkspaceManager.getInstance().getViewElement(actView.getViewInfo());
		}
		
		// create the box
		JComboBox selectViewList = new JComboBox(seqViews);
		selectViewList.setSelectedItem(activeView);
		selectViewList.setRenderer(new WizardListCellRenderer());
		selectViewList.setPreferredSize(new Dimension(100, 25));
		
		return selectViewList;
	}
	
	
	/**
	 * Creates a JComboBox containing all sequence views from specified project
	 * 
	 * @param autoselect
	 * 		flag indicating whether or not the active view should be auto selected
	 * 
	 * @param project
	 * 		project from which views are to be selected		
	 * 
	 * @return
	 * 		JComboBox containing all sequence views from project
	 * 
	 */
	public static JComboBox createSelectSeqViewBox(ProjectElement project) {

		JComboBox selectViewList;
		
		// get loaded sequence views (for the current project)
		WorkspaceManager wsm = WorkspaceManager.getInstance();
		CategoryElement cat = wsm.getProject(project.getTitle()).getCategory(ViewType.SEQUENCE);
		
		if (cat != null) {
			List<WorkspaceElement> views = cat.getViews();
			ViewElement[] seqViews = views.toArray(new ViewElement[views.size()]);
			// create the box
			selectViewList = new JComboBox(seqViews);
			selectViewList.setSelectedItem(null);
			selectViewList.setRenderer(new WizardListCellRenderer());
			selectViewList.setPreferredSize(new Dimension(100, 25));
		}
		else {
			selectViewList = new JComboBox();
			selectViewList.setEnabled(false);
		}
		
		return selectViewList;
	}
	
	
	/**
	 * Creates a JComboBox containing all available distance
	 * measures for domain arrangements.
	 * 
	 * @return
	 * 		JComboBox containing all available distance measures for domain arrangements.
	 */
	public static JComboBox createMeasureBox() {
		JComboBox selectSimilarityMeasure = new JComboBox(DistanceMeasureType.values());
		selectSimilarityMeasure.setSelectedItem(DistanceMeasureType.values()[0]);
		selectSimilarityMeasure.setRenderer(new WizardListCellRenderer());
		return selectSimilarityMeasure;
	}
	
	/**
	 * Creates a JComboBox containing all available algorithms which
	 * can be used for tree creation.
	 * 
	 * @return
	 * 		JComboBox containing all available algorithms which can be used for tree creation.
	 */
	public static JComboBox createAlgoBox() {
		JComboBox selectAlgo = new JComboBox( TreeCreationAlgoType.values() );
		selectAlgo.setSelectedItem(TreeCreationAlgoType.values()[0]);
		selectAlgo.setRenderer(new WizardListCellRenderer());
		return selectAlgo;
	}
	
	/**
	 * Creates a JComboBox containing all available substitution matrices
	 * which are supported by the external PAL library.
	 * 
	 * @return
	 * 		JComboBox containing all available substitution matrices which are supported by the external PAL library.
	 */
	public static JComboBox createSubstitutionBox() {
		String[] substitutionMatrix = {"BLOSUM62", "CPREV", "Dayhoff", "JTT", "MTREV24", "VT", "WAG"};
		JComboBox selectSubstitution = new JComboBox(substitutionMatrix);
		selectSubstitution.setSelectedItem(substitutionMatrix[0]);
		selectSubstitution.setRenderer(new WizardListCellRenderer());
		return selectSubstitution;
	}
}
