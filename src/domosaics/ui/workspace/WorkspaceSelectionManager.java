package domosaics.ui.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.WorkspaceManager;




/**
 * WorkspaceSelectionManager manages the selection of elements within
 * the WorkspaceView. 
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceSelectionManager implements TreeSelectionListener {
	
	/** List of selected nodes */
	protected List<WorkspaceElement> selectedNodes;	
	
	/** maps the TreeSelectionModel to the TreeModel backend data */
	protected Map<TreeSelectionModel, TreeModel> selection2data = new HashMap<TreeSelectionModel, TreeModel>();
	
	
	/**
	 * Constructor for the WorkspaceSelectionManager.
	 */
	public WorkspaceSelectionManager(){
		super();
		this.selectedNodes = new ArrayList<WorkspaceElement>();
	}
	
	
	/* ************************************************************* *
	 * 							Selection operations				 *
	 * ************************************************************* */
	/**
	 * Returns the actual used project. This can differ from the 
	 * getSelectedElement() method. Imagine a view is selected, then this
	 * method would return the project associated with the selected view.
	 * 
	 * @return
	 * 		actually used project.
	 */
	public ProjectElement getSelectedProject() {
		WorkspaceElement selected = getSelectedElement();
		if (selected == null)
			return (ProjectElement) WorkspaceManager.getInstance().getFirstProject();
		return selected.getProject();
	}
	
	/**
	 * Returns the actual selected element
	 * 
	 * @return
	 * 		actually selected element
	 */
	public WorkspaceElement getSelectedElement() {
        if(selectedNodes.size() == 0) 
        	return null;
        return selectedNodes.get(0);
    }
	
    /**
	 * Manually selects an element.
	 * 
	 * @param element
	 * 		the element to be selected
	 */
    public void setSelectedElement(WorkspaceElement element){
    	selectedNodes.clear();
    	if(element == null) 
    		return;
    	selectedNodes.add(element);
    }   
	
    
	/* ************************************************************* *
	 * 					Add and remove Controller					 *
	 * ************************************************************* */

    /**
     * Puts the selection model of the JTree and the TreeModel into the 
     * map which is worked through during the value change event.
     * 
     * @param tree
     * 		the tree to be controlled
     */
	public void addController(JTree tree) {
		addController(tree.getSelectionModel(), tree.getModel());
	}
	
	/**
	 * Puts the selection model of the JTree and the TreeModel into the 
     * map which is worked through during the value change event.
     * 
	 * @param treeSelectionModel
	 * 		selection model of the tree
	 * @param dataModel
	 * 		tree model of the tree
	 */
	public void addController(TreeSelectionModel treeSelectionModel, TreeModel dataModel){	
		treeSelectionModel.addTreeSelectionListener(this);
		selection2data.put(treeSelectionModel, dataModel);		
	}
	
	/**
	 * Removes a TreeSelectionModel from the list which is worked through during a value change.
	 * 
	 * @param treeSelectionModel
	 * 		selection model to be removed
	 */
	public void removeController(TreeSelectionModel treeSelectionModel){
		treeSelectionModel.removeTreeSelectionListener(this);
		selection2data.remove(treeSelectionModel);		
	}	
	
	/* ************************************************************* *
	 * 					TreeSelectionListener methods				 *
	 * ************************************************************* */

	public void valueChanged(TreeSelectionEvent e) {
		if(!(e.getSource() instanceof TreeSelectionModel))
			return;
		
		TreeSelectionModel m = (TreeSelectionModel) e.getSource();
		Object o = selection2data.get(m);

		List<WorkspaceElement> treeModelElements = new ArrayList<WorkspaceElement>();

		if(o != null){
			TreePath[] treepath = m.getSelectionPaths();	
			if(treepath != null) 
				for (TreePath path : treepath) 
					if(m.isPathSelected(path))					
						treeModelElements.add((WorkspaceElement) path.getLastPathComponent());
		}
		
		setSelectedNodes(treeModelElements.toArray(new WorkspaceElement[treeModelElements.size()]));		
	}
	
    protected void setSelectedNodes(WorkspaceElement[] nodes){
		selectedNodes.clear();
		if(nodes != null)
			for (WorkspaceElement node : nodes) 
				selectedNodes.add(node);
    }  

}

