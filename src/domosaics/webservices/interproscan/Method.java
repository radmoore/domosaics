package domosaics.webservices.interproscan;

/**
 * A enumeration of methods which can be used as signature methods for 
 * annotations.
 * 
 * @author Andreas Held
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public enum Method {

	ProDom			("ProDom", false),
	PRINTS			("PRINTS", false),
	PIRSF			("PIRFS", false),
	PfamA			("PfamA", true),
	SMART			("SMART", false),
	TIGRFAM			("TIGRFAM", false),
	PrositeProfiles ("PrositeProfiles", false);

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
