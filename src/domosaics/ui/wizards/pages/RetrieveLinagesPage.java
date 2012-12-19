package domosaics.ui.wizards.pages;

import java.awt.Component;

import org.netbeans.spi.wizard.WizardPage;

public class RetrieveLinagesPage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the lineages after the wizard finished */
	public static final String LINEAGES_KEY = "lineages";
	
//	private DPFetchPanel panel;
	
	public RetrieveLinagesPage() {
//		panel = new DPFetchPanel(null, this);
//		add(panel);
	}
	
	public void setIDs(String[] ids) {
//		panel.setIDs(ids);
	}

    public static final String getDescription() {
        return "Retrieve lineages from UniProt";
    }
    
    protected String validateContents (Component component, Object o) {
    	/*
    	 * if necessary set the query ids here, even if this is not a good
    	 * place to do it, i cant find a better one
    	 * FIXME if the user goes back and chooses another view, this
    	 * workaround fails 
    	 */
//    	if (panel.getIDs() == null) {
//        	// retrieve the ids of the selected view (gather ids for query)
//    		ViewElement viewElt = (ViewElement) getWizardData(CreateSpeciesTreePage.DOMVIEW_KEY);
//    		DomainViewI view =  ViewHandler.getInstance().getView(viewElt.getViewInfo());	
//    		DomainArrangement[] daSet = view.getDaSet();
//    		
//    		String[] ids = new String[daSet.length];
//    		for (int i = 0; i < daSet.length; i++)
//    			ids[i] = daSet[i].getDesc();
//    		
//    		panel.setIDs(ids);
//    	}
//    		
//    		
//    	if (!panel.isJobDone())
//			return "Please wait for DBFetch to finsih";
    	
        return null;
    }
    
    public void finish(String result) {
    	putWizardData("lineages", result);
    }
   
}
