package domosaics.ui.workspace;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import domosaics.model.workspace.Workspace;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.workspace.components.WorkspaceMouseController;




/**
 * The workspace panel which is basically just a JTree wrapped in a JScrollPane.
 * <p>
 * This class only is for the use of displaying. To change the underlying
 * data model see methods in {@link Workspace} which triggers automatically
 * the tree model when its changed which results in an repaint of this view.
 * <p>
 * To access the methods and this view the {@link WorkspaceManager} should be
 * used.
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceView extends JTree {
	private static final long serialVersionUID = 1L;

	/** embedding panel for the workspace */
	protected JPanel parentPane;
	
	protected WorkspaceTreeModel treeModel;
	
	/**
	 * Basic constructor for the workspace view which generates the JTree
	 * based on the backend workspace model.
	 * 
	 * @param workspace
	 * 		the backend data model which should be represented by this view
	 */
	public WorkspaceView(Workspace workspace) {
		super();
		
		// init tree model handling the internal tree organization
		treeModel = new WorkspaceTreeModel(workspace, this);
		setModel(treeModel);
		
		// don't show the workspace root component but start with its children (projects)
		setRootVisible(false);	
		setShowsRootHandles(false);
		
		// editing of names is done by a manually configured right click event
		setEditable(false);
		
		// single selection model
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		// init the renderer
		setCellRenderer(new WorkspaceTreeCellRenderer());
		revalidate();
		
		// init mouselistener
		addMouseListener(new WorkspaceMouseController(this));
		
		// create the workspace panel which embeds this view
		parentPane = new JPanel();
		parentPane.setLayout(new BorderLayout());
		parentPane.setName("Workspace");
		
		// wrap a scroll pane around the workspace
		JScrollPane workspacepane = new JScrollPane(this);		

		// embed workspace into its panel
		parentPane.add(workspacepane, BorderLayout.CENTER);
	}
	
	/**
	 * Method to get a grip on the panel which embeds this view. For instance
	 * to display it within the DoMosaicsDesktop.
	 * 
	 * @return
	 * 		the panel embedding the JTree workspace
	 */
	public JPanel getParentPane() {
		return parentPane;
	}
	
	/**
	 * To access the JTree directly this method can be used.
	 * (But its not within the program so far)
	 * 
	 * @return
	 * 		the JTree component (GUI component for the workspace)
	 */
	public Component getComponent() {
		return this;
	}
	
	/**
	 * Because there is no control over the collapsing behavior of the
	 * JTree within {@link WorkspaceTreeModel} this is a helper method
	 * to expand the hole tree each time it was changed.
	 * This would be a point for future development.
	 * FIXME
	 * there is a seemingly random runtime error here upon startup/project import
	 * which may be due to changes in other nodes of the tree triggering
	 * this method _before_ all children have been added to the tree
	 * (that is, refresh triggered while tree is still in buildup) 
	 */
	public void expandAll() {
		int row = 0;
		while (row < getRowCount()) {
			expandRow(row);
		    row++;
		}
	}
	
	public void refresh() {
		treeModel.refresh();
	}
	
}
