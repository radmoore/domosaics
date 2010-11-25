package angstd.webservices.clustalw;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import uk.ac.ebi.webservices.wsclustalw2.Data;
import uk.ac.ebi.webservices.wsclustalw2.InputParams;
import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2Service;
import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2ServiceLocator;
import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2_PortType;
import angstd.model.sequence.SequenceI;
import angstd.webservices.AbstractEBIWebservice;
import angstd.webservices.EBIServiceType;
import angstd.webservices.WebservicePrinter;

public class ClustalW2Service extends AbstractEBIWebservice {
	/** the service type holding some important informations */
	public static final EBIServiceType type1 = EBIServiceType.CLUSTALW2;
	public static final EBIServiceType type2 = EBIServiceType.CLUSTALW2SCORES;
	
	/** input parameters needed, such as email address */
	protected InputParams params;
	
	/**	query sequences */
	protected Data[] content;
	
	/** the query sequence */
	protected SequenceI[] seqs;		
	
	/** the interface object to communicate with the webservice */
	protected WSClustalW2_PortType service;
	
	
	public ClustalW2Service(EBIServiceType type) {
		super(type);
	}
	
	public ClustalW2Service(EBIServiceType type, WebservicePrinter out) {
		super(type, out);
	}
	
	public SequenceI[] getSequences() {
		return seqs;
	}
	
	public void setQuerySequences(SequenceI[] seqs) {
		this.seqs = seqs;
		StringBuffer contentStr = new StringBuffer();
		for (int i = 0; i < seqs.length; i++)
			contentStr.append(seqs[i].toString());
		
		content = new Data[1];
		content[0] = new Data();
		content[0].setType("sequence");
		content[0].setContent(contentStr.toString());
	}
	
	public void setParams(String email) {
		params = new InputParams();
		params.setEmail(email);		// User e-mail address             
		params.setAsync(true);     				// Async submission
		params.setTree(false);					// generate tree
		params.setAlign(true);					// perform alignment
		
//		Multiple alignment options
//		params.setGapopen(10);				// penalty for opening a gap
//		params.setGapdist(8);				// gap separation penalty
//		params.setGapext(0.05f);			// penalty for extending a gap
//		params.setGapclose(arg0);

//		params.setMatrix("blosum");
//		matrix 	string 	Scoring matrix set. Values: 'blosum', 'pam', 'gonnet' or 'id' 
	
	}
	

	/* ********************************************************************* *
	 * 						Abstract Methods for the service 				 *
	 * ********************************************************************* */
	
	// change the class types here
	protected void createService() throws ServiceException {
		WSClustalW2Service wsService = new WSClustalW2ServiceLocator();
		service = wsService.getWSClustalW2();
	}
	
	// change the run method here depending on the service
	protected String startService() throws RemoteException {
		return service.runClustalW2(params, content);
	}
	
	// no changes here
	protected String processResults() throws RemoteException {
		byte[] res;
		res = service.poll(jobId, outFormat);
		return new String(res);
	}
	
	// no changes here
	protected String getStatus() throws RemoteException{
		return service.checkStatus(jobId);
	}

}
