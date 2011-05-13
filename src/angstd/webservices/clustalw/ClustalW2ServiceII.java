package angstd.webservices.clustalw;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import org.apache.commons.cli.*;
import uk.ac.ebi.webservices.axis1.stubs.clustalw2.*;
//import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_Service;
//import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_ServiceLocator;

//import uk.ac.ebi.webservices.wsclustalw2.Data;
//import uk.ac.ebi.webservices.wsclustalw2.InputParams;
//import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2Service;
//import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2ServiceLocator;
//import uk.ac.ebi.webservices.wsclustalw2.WSClustalW2_PortType;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.webservices.AbstractEBIWebservice;
import angstd.webservices.EBIServiceType;
import angstd.webservices.WebservicePrinter;

public class ClustalW2ServiceII extends AbstractEBIWebservice {
	/** the service type holding some important informations */
	public static final EBIServiceType type1 = EBIServiceType.CLUSTALW2;
	public static final EBIServiceType type2 = EBIServiceType.CLUSTALW2SCORES;
	private JDispatcherService_PortType srvProxy = null;
	
	/** input parameters needed, such as email address */
	protected InputParameters params;
	
	/**	query sequences */
	//protected Data[] content;
	
	/** the query sequence */
	protected SequenceI[] seqs;
	
	private String sequences, email;
	
	/** the interface object to communicate with the webservice */
	//protected WSClustalW2_PortType service;
	
	
	public ClustalW2ServiceII(EBIServiceType type) {
		super(type);
	}
	
	public ClustalW2ServiceII(EBIServiceType type, WebservicePrinter out) {
		super(type, out);
	}
	
	public SequenceI[] getSequences() {
		return seqs;
	}
	
	public void setQuerySequences(SequenceI[] seqs) {
		this.seqs = seqs;
		StringBuffer contentStr = new StringBuffer();
		for (int i = 0; i < seqs.length; i++) {
			contentStr.append(seqs[i].toFasta(false));
		}
		
		sequences = contentStr.toString();
		//System.out.println("these are the sequences: "+sequences);
//		content = new Data[1];
//		content[0] = new Data();
//		content[0].setType("sequence");
//		content[0].setContent(contentStr.toString());
	}
	
	public void setParams(String email) {
		params = new InputParameters();
		params.setAlignment("fast");
		this.email = email;
		
//		params.setEmail(email);		// User e-mail address             
//		params.setAsync(true);     				// Async submission
//		params.setTree(false);					// generate tree
//		params.setAlign(true);					// perform alignment
		
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
		//WSClustalW2Service wsService = new WSClustalW2ServiceLocator();
		//service = wsService.getWSClustalW2();
	}
	
	// change the run method here depending on the service
	protected String startService() throws RemoteException {
		//return service.runClustalW2(params, content);
		try {
			srvProxyConnect();
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		params.setSequence(this.sequences);
		String jobId = srvProxy.run(email, "", params);
		System.out.println("Triggered job, JOBID: "+jobId);
		return jobId;
	}
	
	// no changes here
	protected String processResults() throws RemoteException {
		byte[] res;
		//res = service.poll(jobId, outFormat);
		res = srvProxy.getResult(jobId, outFormat, null);
		String results = new String(res);

		
		// Uncomment to obtain list of result types obtainable
		WsResultType[] typeList = srvProxy.getResultTypes(jobId);
		for(int i = 0; i < typeList.length; i++) {
			System.out.print(
					typeList[i].getIdentifier() + "\n\t"
					+ typeList[i].getLabel() + "\n\t"
					+ typeList[i].getDescription() + "\n\t"
					+ typeList[i].getMediaType() + "\n\t"
					+ typeList[i].getFileSuffix() + "\n"
					);
		}
		System.out.println("This is in results: ");
		System.out.println(results);
		return results;
	}
	
	// no changes here
	protected String getStatus() throws RemoteException{
		return srvProxy.getStatus(jobId);
		//return service.checkStatus(jobId);
	}
	
    /**
     * Ensure that there is a connection to the service proxy
     * @throws ServiceException
     */
	protected void srvProxyConnect() throws ServiceException {
		if (this.srvProxy == null) {
			JDispatcherService_Service service = new JDispatcherService_ServiceLocator();
			this.srvProxy = service.getJDispatcherServiceHttpPort();
		}
	}

}
