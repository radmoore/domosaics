package angstd.ui.views;

import angstd.model.configuration.Configuration;


/**
 * An enumeration of all possible views, defined by their class path.
 * When creating a new view with the {@link AngstdViewFactory} this ViewType 
 * is used to initiate the building process. Look into AngstdViewFactory for
 * further details on this.
 * 
 * @author Andreas Held
 *
 */
public enum ViewType {
	TREE(				"angstd.ui.views.treeview.TreeView", "TREE"),
	DOMAINS(			"angstd.ui.views.domainview.DomainView", "XDOM"),
	DOMAINTREE(			"angstd.ui.views.domaintreeview.DomainTreeView", "DOMTREE"),
	SEQUENCE(			"angstd.ui.views.sequenceview.SequenceView", "FASTA"),
	DOMAINLEGEND(		"angstd.ui.tools.domainlegend.DomainLegendView", ""),
	DOMAINGRAPH(		"angstd.ui.tools.domaingraph.DomainGraphView", ""),
	DOTPLOT(			"angstd.ui.tools.dotplot.DotplotView", ""),
	DISTANCEMATRIX(		"angstd.ui.tools.distmatrix.DistMatrixView", "TXT"),
	DOMAINMATRIX(	  	"angstd.ui.tools.domainmatrix.DomainMatrixView", ""),
	CHANGEARRANGEMENT(	"angstd.ui.tools.changearrangement.ChangeArrangementView", ""),
	RADSCAN(          	"angstd.ui.tools.RADSTool.RADSScanView", ""),
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
			Configuration.getLogger().debug(e.toString());
			Configuration.getLogger().debug("Could not find view class: "+classURL);
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
