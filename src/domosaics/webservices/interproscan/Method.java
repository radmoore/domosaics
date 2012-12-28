package domosaics.webservices.interproscan;

/**
 * A enumeration of methods which can be used as signature methods for 
 * annotations.
 * 
 * @author Andreas Held
 *
 */
public enum Method {

	blastprodom 	("blastprodom",	false), 
	gene3d			("gene3d",		false), 
	hmmpanther		("hmmpanther",	false), 
	hmmpir			("hmmpir",		false), 
	hmmpfam			("hmmpfam",		true), 
	hmmsmart		("hmmsmart",	false), 
//	signalp			("signalp",		false), 
//	tmhmm			("tmhmm",		false), 
	hmmtigr			("hmmtigr",		false), 
	fprintscan		("fprintscan",	false),
//	scanregexp		("scanregexp",	false), 
	profilescan		("profilescan",	false), 
	superfamily		("superfamily",	false);

	private String title;
	private boolean state;
	
	private Method(String title, boolean state) {
		this.title = title;
		this.state = state;
	}
	
	/**
	 * WSInterproScan title for the signature method
	 * 
	 * @return
	 * 		WSInterproScan title for the signature method
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Initial state of the radio button within the annotator panel.
	 * 
	 * @return
	 * 		Initial state
	 */
	public boolean getInitialState() {
		return state;
	}
	
}
