package domosaics.ui.views.domainview.mousecontroller;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.detectors.SequenceDetector;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.view.View;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;


/**
 * The SequenceSelectionMouseController is more than just an ordinary
 * MouseController. First it handles the mouse events being relevant
 * for the selection of underlying sequences. <br>
 * But it also holds the Area of already selected sequences and also
 * asks the user on right click if he wants to export the export
 * the selected sequences.
 * <p>
 * If the user decides to export the sequence this class also manages
 * the export details like opening a file dialog and so on.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class SequenceSelectionMouseController extends MouseAdapter {

	/** the domain view to be handled */
	protected DomainViewI view;
	
	/** detector for underlying sequences of domain arrangements */
	protected SequenceDetector seqDetector;
	
	/** the current selection rectangle for sequences */
	protected Rectangle selectionRectangle = null;
	
	/** the last point on the screen to where the cursor moved */
	protected Point movePoint;
	
	/** the last point on the screen on which was clicked */
	protected Point clickPoint;
	
	/** flag indicating whether or not the mouse is in drag mode */
	protected boolean dragging = false;
	
	/** area containing the already selected sequences */
	protected Area area;
	
	
	/**
	 * Constructor for a new sequence selection mouse controller
	 * 
	 * @param view
	 * 		the view which mouse events should be handled.
	 */
	public SequenceSelectionMouseController(DomainViewI view) {
		this.view = view;
		seqDetector = new SequenceDetector(view);
		area = new Area();
	}
	
	/**
	 * Return the current selection rectangle
	 * 
	 * @return
	 * 		selection rectangle drawn by the user
	 */
	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}
	
	/**
	 * Returns the area of already selected underlying sequences
	 * 
	 * @return
	 * 		area of already selected underlying sequences
	 */
	public Area getSelectionArea() {
		return area;
	}
	
	/**
	 * Helper method to determine if the cursor is within the arrangement
	 * drawing area
	 * 
	 * @param p
	 * 		the actual point to be checked
	 * @return
	 * 		if p lies within the arrangement bounds
	 */
	private boolean isInBounds(Point p) { 
		// just check if it is in the right side of the view
		return view.getDomainLayout().getDomainBounds().x-50 < p.x;
	}
	
	/**
	 * On right click: ends the process by asking the user if he wants 
	 * to save the selected sequences.
	 * On left click: resets the selection
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (movePoint.equals(e.getPoint())) {
				area = new Area();
				view.getParentPane().repaint();
			}
			return;
		}
		
		// on right click process results
		SequenceI[] seqs = seqDetector.searchSequenceComponents(area);

		if (seqs == null || seqs.length == 0) {
			view.getViewComponent().repaint();
			return;
		}
	
		Object[] options = {"Yes", "Save to fasta file", "Cancel"};
		int choice = MessageUtil.show3ChoiceDialog("Create new sequence view with selected sequences?", options);
		
		if (choice == 0) { 		// create new sequence view
			
			String defaultName = view.getViewInfo().getName()+"_seqs";
			String viewName=null;
			String projectName=null;
			
			// get currently active project
			View activeView = ViewHandler.getInstance().getActiveView();
			ViewElement elem = WorkspaceManager.getInstance().getViewElement(view.getViewInfo());
			ProjectElement project = elem.getProject();
			
			while(viewName == null) {
				Map m = WizardManager.getInstance().selectNameWizard(defaultName, "sequence view", project, true);
				viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
				projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
			}
			project = WorkspaceManager.getInstance().getProject(projectName);
						
			SequenceView seqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName);
			seqView.setSeqs(seqs);
			ViewHandler.getInstance().addView(seqView, project, true);
			
		} else if (choice == 1) {// Save sequences as fasta file
			
			File file =FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "fasta");
			if (file == null){
				view.getViewComponent().repaint();
				return;
			}
			new FastaWriter().write(file, seqs);
		} else if (choice == 2) {// cancel quit
			return;
		}
		
		area = new Area();
		view.getParentPane().repaint();
	}

	/**
	 * starts the selection process via selection rectangle
	 */
	public void mousePressed(MouseEvent e) {	
		if (!view.getDomainLayoutManager().isSelectSequences())
			return;
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			movePoint = e.getPoint();
			clickPoint = e.getPoint();
			
			if (isInBounds(e.getPoint())) 
				dragging = true;
			
			view.getViewComponent().repaint();
			
			if (view.getArrangementSelectionManager().getMouseOverComp() == null)
				view.getArrangementSelectionManager().clearSelection();
			
		} else 
			dragging = false;
	}
	
	/**
	 * ends the expansion of the selection rectangle and adds the 
	 * result to selection area
	 */
	public void mouseReleased(MouseEvent e) {
		if (!view.getDomainLayoutManager().isSelectSequences())
			return;
		
		if (e.getButton() == MouseEvent.BUTTON1) {

			// left mouse and not dragged
			if (movePoint.equals(clickPoint))			
				return;
			
			// left mouse and dragged
			intersectwithDACs();

			// reset click and move point and repaint
			movePoint = null;
			clickPoint = null;
			selectionRectangle = null;
			dragging = false;	
			
			view.getViewComponent().repaint();
			return;
		}
	}
	
	/**
	 * expands the selection rectangle
	 */
	public void mouseDragged(MouseEvent e) {
		if (!view.getDomainLayoutManager().isSelectSequences())
			return;
		
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
	 * Helper method intersecting the selection rectangle with 
	 * all domain arrangement componentd and adding the result to the 
	 * selection area
	 */
	private void intersectwithDACs() {
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();

			if (!dac.isVisible())
				continue;
			
			// check if rectangle intersects with the arrangement
			if (dac.getDisplayedShape().intersects(selectionRectangle)) {
				area.add(new Area(selectionRectangle.intersection(dac.getDisplayedShape().getBounds())));
			}
		}	
	}
	
}
