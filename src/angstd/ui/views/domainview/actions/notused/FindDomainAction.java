package angstd.ui.views.domainview.actions.notused;

import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;



// TODO move the selecting procedure to the selectionmanager
public class FindDomainAction extends AbstractMenuAction { //implements FindDialogListener{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
//		new SearchDialog(AngstdUI.getInstance(), false, this);
	}

//
//	public void find(String label) {
//		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//		view.getDomainSelectionManager().clearSelection();
//		view.getParentPane().repaint();
//		
//		Iterator<DomainComponent> iter = view.getDomainComponentManager().getDomainComponentsIterator();
//		while(iter.hasNext()) {
//			DomainComponent dc = iter.next();
//			if (dc.getLabel().toUpperCase().equals(label.toUpperCase())) {
//				view.getDomainSelectionManager().addToSelection(dc);
//				
//				if (!view.getViewComponent().getVisibleRect().contains(dc.getLocation()))
//					view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dc.getY()-dc.getHeight()));
//				break;
//			}
//		}
//	
//		// if string was not found show a warning
//		if (view.getDomainSelectionManager().getSelection().size() == 0)
//			showCantFindLabelDialog(label);
//	}
//
//
//	public void findNext(String label) {
//		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//		
//		Iterator<DomainComponent> iter = view.getDomainComponentManager().getDomainComponentsIterator();
//		boolean selectNext = false;
//		while(iter.hasNext()) {
//			DomainComponent dc = iter.next();
//			
//			// find last selected
//			if (dc.getLabel().toUpperCase().equals(label.toUpperCase()) && view.getArrangementSelectionManager().getSelection().contains(dc)) {
//				selectNext = true;
//				continue;
//			}
//			
//			// select now
//			if (dc.getLabel().toUpperCase().equals(label.toUpperCase()) && selectNext) {
//				view.getDomainSelectionManager().clearSelection();
//				view.getDomainSelectionManager().addToSelection(dc);
//				
//				if (!view.getViewComponent().getVisibleRect().contains(dc.getLocation()))
//					view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dc.getY()-dc.getHeight()));
//				break;
//			}
//		}
//	
//		// if string was not found show a warning
//		if (view.getDomainSelectionManager().getSelection().size() == 0)
//			showCantFindLabelDialog(label);
//	}
	
	public void findAll(String label) {
//		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//		view.getDomainSelectionManager().clearSelection();
//		view.getParentPane().repaint();
//		
//		Iterator<DomainComponent> iter = view.getDomainComponentManager().getDomainComponentsIterator();
//		while(iter.hasNext()) {
//			DomainComponent dc = iter.next();
//			if (dc.getLabel().toUpperCase().equals(label.toUpperCase())) 
//				view.getDomainSelectionManager().addToSelection(dc);
//			
//			if (!view.getViewComponent().getVisibleRect().contains(dc.getLocation()))
//				view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dc.getY()-dc.getHeight()));
//		}
//	
//		
//		// if string was not found show a warning
//		if (view.getDomainSelectionManager().getSelection().size() == 0)
//			showCantFindLabelDialog(label);
	}
//
//	private void showCantFindLabelDialog(String label) {
//		JOptionPane.showMessageDialog(AngstdUI.getInstance(),
//			    "Couldn't find "+label,
//			    "Label Not Found",
//			    JOptionPane.WARNING_MESSAGE);
//	}
//
//
	public void find(String label) {
		// TODO Auto-generated method stub
		
	}


	public void findNext(String label) {
		// TODO Auto-generated method stub
		
	}
}
