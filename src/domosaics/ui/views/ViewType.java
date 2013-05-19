package domosaics.ui.views;

import domosaics.model.configuration.Configuration;


/**
 * An enumeration of all possible views, defined by their class path.
 * When creating a new view with the {@link DoMosaicsViewFactory} this ViewType 
 * is used to initiate the building process. Look into DoMosaicsViewFactory for
 * further details on this.
 * 
 * @author Andreas Held
 *
 */
public enum ViewType {
	TREE(				"domosaics.ui.views.treeview.TreeView", "TREE"),
	DOMAINS(			"domosaics.ui.views.domainview.DomainView", "XDOM"),
	DOMAINTREE(			"domosaics.ui.views.domaintreeview.DomainTreeView", "DOMTREE"),
	SEQUENCE(			"domosaics.ui.views.sequenceview.SequenceView", "FASTA"),
	DOMAINLEGEND(		"domosaics.ui.tools.domainlegend.DomainLegendView", ""),
	DOMAINGRAPH(		"domosaics.ui.tools.domaingraph.DomainGraphView", ""),
	DOTPLOT(			"domosaics.ui.tools.dotplot.DotplotView", ""),
	DISTANCEMATRIX(		"domosaics.ui.tools.distmatrix.DistMatrixView", "TXT"),
	DOMAINMATRIX(	  	"domosaics.ui.tools.domainmatrix.DomainMatrixView", ""),
	CHANGEARRANGEMENT(	"domosaics.ui.tools.changearrangement.ChangeArrangementView", ""),
	RADSCAN(          	"domosaics.ui.tools.RADSTool.RADSScanView", ""),
	;

	/** the class for the view */
	private Class<?> clazz;
	
	/** the file extension when saving the view data into a file */
	private String fileExt;
	
	
	/**
	 * Constructor trying to generate the class out of its classURL.
	 * 
	 * @param classURL
	 * 		the URL within the package structure to the class
	 */
	private ViewType (String classURL, String fileExt) {
		this.fileExt = fileExt;
		try {
			clazz = Class.forName(classURL);
		} 
		catch (ClassNotFoundException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else {			
				Configuration.getLogger().debug(e.toString());
				Configuration.getLogger().debug("Could not find view class: "+classURL);
			}
			
		}
	}
	
	/**
	 * Return the class object for this ViewType.
	 * 
	 * @return
	 * 		class object for this ViewType.
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	
	/**
	 * Return the file extension when saving the view data into a file
	 * 
	 * @return
	 * 		file extension for the data (e.g. XDOM)
	 */
	public String getFileExtension() {
		return fileExt;
	}
	
}
