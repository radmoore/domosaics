package domosaics.ui.views.treeview.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import domosaics.ui.views.treeview.TreeViewI;




/**
 * TreeMouseController handles the mouse events which are made on
 * a tree view. Therefore this class must decide whether or
 * not on a specified position is a node component present. To do so 
 * a TreeComponentDetector is used.
 * <p>
 * Basically this mouse adapter provides the opportunities to select
 * nodes via dragging a selection rectangle or just select nodes by 
 * mouse over or clicking on them.
 * 
 * @author Andreas Held
 *
 */
public class TreeMouseController extends MouseAdapter {
	
	/** the tree view to be handled */
	protected TreeViewI view;
	
	/** detector for node components on the screen */
	protected NodeComponentDetector nodeDetector;
	
	/** the point currently being clicked on */
	protected Point clickPoint;
	
	/** the last cursor position */
	protected Point movePoint;
	
	/** the actual selection rectangle */
	protected Rectangle selectionRectangle = null;
	
	/** flag indicating whether or not the mouse currently being dragged */
	protected boolean dragging = false;
	
	/** the additional space around the cursor in which nodes are searched */
	protected int searchSpace = 20;
	
	
	/**
	 * Constructor for a new TreeMouseController
	 * 
	 * @param view
	 * 		the tree view to be handled
	 */
	public TreeMouseController(TreeViewI view) {
		this.view = view;
		nodeDetector = new NodeComponentDetector(view);
	}
	
	/**
	 * Helper method to select nodes based on a selection rectangle
	 * 
	 * @param r
	 * 		the selection rectangle in which all nodes are going to be selected
	 */
	private void selectNodes(Rectangle r) {
		if (r == null)
			return;
		List<NodeComponent> toSelect = nodeDetector.searchNodeComponents(r);
		if (toSelect == null || toSelect.size() == 0) 
			view.getTreeSelectionManager().setSelection(null);
		else 
			view.getTreeSelectionManager().setSelection(toSelect);
	}
	
	/**
	 * Helper method to select a mouse over node
	 * 
	 * @param p
	 * 		the actual location of the cursor
	 * @return
	 * 		the selected node under the cursor (or null)
	 */
	private NodeComponent selectOverNode(Point p) {
		NodeComponent toSelect = nodeDetector.searchNodeComponent(p);
		view.getTreeSelectionManager().setMouseOverComp(toSelect);
		return toSelect;
	}
	
	/**
	 * Returns the selection rectangle
	 * 
	 * @return
	 * 		 the selection rectangle
	 */
	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}
	
	/**
	 * Helper method just opening a context menu for node components
	 * 
	 * @param e
	 * 		the mouse event which lead to this course of action
	 */
	private void showNodePopupMenu(MouseEvent e) {
		 new NodePopupMenu(view).show(view.getViewComponent(), e.getX(), e.getY());
	}
	
	/**
	 * On left click deselect all nodes, on right click
	 * open a context menu if it was triggered on a node.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// if left button check for keystrokes and add to selection
		if (e.getButton() == MouseEvent.BUTTON1) {
			clickPoint = null;
			movePoint = null;
			selectionRectangle = null;
			dragging = false;
			view.getTreeSelectionManager().clearSelection();
		}
		
		// show node popup menu if a component was clicked
		if (e.getButton() == MouseEvent.BUTTON3) {
			NodeComponent selectedNode = selectOverNode(e.getPoint());
			view.getTreeSelectionManager().setClickedComp(selectedNode);
			if (selectedNode != null) 
				showNodePopupMenu(e);
			
			clickPoint = null;
			movePoint = null;
			selectionRectangle = null;
			dragging = false;
		}
		
	}

	/**
	 * the mouse release method is just relevant for ending the 
	 * dragging process of the selection rectangle. The finished
	 * rectangle is then used to select nodes.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// if leftbutton => check selection rectangle
		if (e.getButton() == MouseEvent.BUTTON1) {
			
			// left mouse and not dragged
			if (!dragging)		
				return;
			
			// left mouse and dragged
			// get selection rectangle size and position
			Rectangle r = getSelectionRectangle();
			
			// call the selection manager to select the nodes within the rectangle
			if (r != null)
				selectNodes(r);
			
			// reset click and move point and repaint
			clickPoint = null;
			movePoint = null;
			selectionRectangle = null;
			dragging = false;
			view.getViewComponent().repaint();
			return;
		}
	}
	
	/**
	 * The mouse pressed method starts the selection process via
	 * a selection rectangle
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			view.getTreeSelectionManager().clearSelection();
			clickPoint = e.getPoint();
			movePoint = e.getPoint();
			
			if (view.getTreeLayout().getTreeBounds().contains(e.getPoint()))
				dragging = true;
			
			view.getViewComponent().repaint();
		}
		if (e.getButton() != MouseEvent.BUTTON1) {
			dragging = false;
		}
	}

	/**
	 * the mouse dragged method expands the selection rectangle
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!dragging)
			return;
		
		// scroll scrollpane if needed
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		view.getViewComponent().scrollRectToVisible(r);
		
		if (clickPoint != null) {
			movePoint = e.getPoint();
			view.getViewComponent().repaint();
		}
		
		// calculate selection rectangle
		int x = clickPoint.x > movePoint.x ? movePoint.x : clickPoint.x;
		int y = clickPoint.y > movePoint.y ? movePoint.y : clickPoint.y;
		int w = Math.abs(clickPoint.x - movePoint.x);
		int h = Math.abs(clickPoint.y - movePoint.y);
		selectionRectangle =  new Rectangle(x, y, w, h);
	}

	/**
	 * The mouse moved method highlights nodes on mouse over.
	 * 
	 * A repaint is forced by changing the temporary selection
	 */
	@Override
	public void mouseMoved(MouseEvent e) {	
		int s = (searchSpace / 2);
		List<NodeComponent> nearestNodes = nodeDetector.searchNodeComponents(new Rectangle(e.getX() - s, e.getY() - s, searchSpace, searchSpace));
		
		if (nearestNodes.size() == 0)  {
			if (view.getTreeSelectionManager().getMouseOverComp() != null)
				view.getTreeSelectionManager().setMouseOverComp(null);
			return;
		}
		
		if (nearestNodes.size() == 1) {
			if (!nearestNodes.get(0).equals(view.getTreeSelectionManager().getMouseOverComp()))
				view.getTreeSelectionManager().setMouseOverComp(nearestNodes.get(0));
			return;
		}  
		
		// check what node is nearest to the point if there are more than one within the area
		NodeComponent nearest = nearestNodes.get(0);
		for (int i = 1; i < nearestNodes.size(); i++) {
			if (nearestNodes.get(i).getLocation().distance(e.getPoint()) < nearest.getLocation().distance(e.getPoint())) 
				nearest = nearestNodes.get(i);
		}
		
		if (!nearest.equals(view.getTreeSelectionManager().getMouseOverComp()))
			view.getTreeSelectionManager().setMouseOverComp(nearest);
		
	}

}
