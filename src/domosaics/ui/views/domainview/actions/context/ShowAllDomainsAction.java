package domosaics.ui.views.domainview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.arrangement.Domain;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;




public class ShowAllDomainsAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public ShowAllDomainsAction (){
		super();
		putValue(Action.NAME, "Show hidden domains");
		putValue(Action.SHORT_DESCRIPTION, "Makes all hidden domains visible again");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainViewI view = (DomainViewI) ViewHandler.getInstance().getActiveView();
		
		if (view.getDomainLayoutManager().isCollapseBySimilarity()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"While collapsing by similarity this option can not be triggered. ");
			return;
		}
		
		if (view.isCompareDomainsMode()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"While in comparing domains mode this option is deactivated ");
			return;
		}
		
		if (view instanceof DomainTreeViewI && ((DomainTreeViewI) view).getDomainTreeLayoutManager().isShowInDels()) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"While in show insertion deletion mode this option is deactivated");
			return;
		}
		
		// get the selected arrangement
		ArrangementComponent dac = view.getArrangementSelectionManager().getClickedComp();
		
		for (Domain dom : dac.getDomainArrangement().getHiddenDoms())
			view.getDomainComponentManager().getComponent(dom).setVisible(true);
		
		dac.getDomainArrangement().showAllDomains();
		
		if (view.getDomainLayoutManager().isCollapseSameArrangements()) 
			view.getCollapseSameArrangementsManager().refresh(view, view.getDaSet());
		
		view.getDomainLayoutManager().structuralChange();
	}

}
