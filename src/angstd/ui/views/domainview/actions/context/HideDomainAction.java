package angstd.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;

public class HideDomainAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public HideDomainAction (){
		super();
		putValue(Action.NAME, "Hide Domain");
		putValue(Action.SHORT_DESCRIPTION, "Hides a domain");
	}
	
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		if (view.getDomainLayoutManager().isCollapseBySimilarity()) {
			MessageUtil.showWarning("While collapsing by similarity this option can not be triggered.");
			return;
		}
			
		if (view.isCompareDomainsMode()) {
			MessageUtil.showWarning("While in comparing domains mode this option is deactivated");
			return;
		}
		
		if (view instanceof DomainTreeViewI && ((DomainTreeViewI) view).getDomainTreeLayoutManager().isShowInDels()) {
			MessageUtil.showWarning("While in show insertion deletion mode this option is deactivated");
			return;
		}
		
		// get the selected arrangement
		DomainComponent dc = view.getDomainSelectionManager().getClickedComp();
		dc.setVisible(false);
		dc.getDomain().getArrangement().hideDomain(dc.getDomain());
		
		if (view.getDomainLayoutManager().isCollapseSameArrangements()) 
			view.getCollapseSameArrangementsManager().refresh(view, view.getDaSet());
		
		
//		if (view instanceof DomainTreeViewI) {
//			view = (DomainTreeViewI) view;
//			
//			if (view.getCollapseSameArrangementsManager().)
//		}
		view.getDomainLayoutManager().structuralChange();
	}

}
