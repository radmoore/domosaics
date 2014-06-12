package domosaics.ui.views.domainview.actions.notused;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;




public class FindArrangementAction extends AbstractMenuAction { //implements FindDialogListener{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		new SearchDialog(DoMosaicsUI.getInstance(), false, this);
	}


	
	public void findAll(String label) {
//		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//		view.getDomainLayoutManager().deselectAll();
//		view.getArrangementSelectionManager().clearSelection();
//		view.getParentPane().repaint();
//		
//		Iterator<DomainArrangementComponent> iter = view.getDomainComponentManager().getComponentsIterator();
//		while(iter.hasNext()) {
//			DomainArrangementComponent dac = iter.next();
//			if (dac.getLabel().toUpperCase().equals(label.toUpperCase())) {
//				view.getArrangementSelectionManager().addToSelection(dac);
//			}
//			if (!view.getViewComponent().getVisibleRect().contains(dac.getLocation()))
//				view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dac.getY()-dac.getHeight()));
//		}
//	
//		
//		// if string was not found show a warning
//		if (view.getArrangementSelectionManager().getSelection().size() == 0)
//			showCantFindLabelDialog(label);
	}
	
//	private void showCantFindLabelDialog(String label) {
//		JOptionPane.showMessageDialog(DoMosaicsUI.getInstance(),
//			    "Couldn't find "+label,
//			    "Label Not Found",
//			    JOptionPane.WARNING_MESSAGE);
//	}


	public void find(String label) {
		// TODO Auto-generated method stub
		
	}


	public void findNext(String label) {
		// TODO Auto-generated method stub
		
	}
	



}


//public void find(String label) {
//	DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//	view.getDomainLayoutManager().deselectAll();
//	view.getArrangementSelectionManager().clearSelection();
//	view.getParentPane().repaint();
//	
//	Iterator<DomainArrangementComponent> iter = view.getDomainComponentManager().getComponentsIterator();
//	while(iter.hasNext()) {
//		DomainArrangementComponent dac = iter.next();
//		if (dac.getLabel().toUpperCase().equals(label.toUpperCase())) {
//			view.getArrangementSelectionManager().addToSelection(dac);
//			
//			if (!view.getViewComponent().getVisibleRect().contains(dac.getLocation()))
//				view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dac.getY()-dac.getHeight()));
//			break;
//		}
//	}
//
//	// if string was not found show a warning
//	if (view.getArrangementSelectionManager().getSelection().size() == 0)
//		showCantFindLabelDialog(label);
//}
//
//
//public void findNext(String label) {
//	DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
//	
//	Iterator<DomainArrangementComponent> iter = view.getDomainComponentManager().getComponentsIterator();
//	boolean selectNext = false;
//	while(iter.hasNext()) {
//		DomainArrangementComponent dac = iter.next();
//		
//		// find last selected
//		if (dac.getLabel().toUpperCase().equals(label.toUpperCase()) && view.getArrangementSelectionManager().getSelection().contains(dac)) {
//			selectNext = true;
//			continue;
//		}
//		
//		// select now
//		if (dac.getLabel().toUpperCase().equals(label.toUpperCase()) && selectNext) {
//			view.getDomainLayoutManager().deselectAll();
//			view.getArrangementSelectionManager().clearSelection();
//			view.getArrangementSelectionManager().addToSelection(dac);
//			
//			if (!view.getViewComponent().getVisibleRect().contains(dac.getLocation()))
//				view.getParentPane().setViewPosition(new Point(view.getViewComponent().getVisibleRect().x, dac.getY()-dac.getHeight()));
//			break;
//		}
//	}
//
//	// if string was not found show a warning
//	if (view.getArrangementSelectionManager().getSelection().size() == 0)
//		showCantFindLabelDialog(label);
//}
