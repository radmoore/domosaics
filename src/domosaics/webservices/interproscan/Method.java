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

	ProDom			("ProDom", false, 4, 4, 5, 11, 6, 7, 8),
	PRINTS			("PRINTS", false, 4, 4, 5, 11, 6, 7, 8),
	PIRSF			("PIRFS", false, 4, 4, 5, 11, 6, 7, 8),
	Pfam			("PfamA", true, 4, 5, 6, 11, 6, 7, 8),
	SMART			("SMART", false, 4, 4, 5, 11, 6, 7, 8),
	TIGRFAM			("TIGRFAM", false, 4, 4, 5, 11, 6, 7, 8),
	SUPERFAMILY		("SuperFamily", false, 4, 4, 5, 10, 5, 6, 7),
	PrositeProfiles ("PrositeProfiles", false, 4, 4, 5, 11, 6, 7, 8),
	Gene3D 			("Gene3d", false, 4, 4, 11, 10, 5, 6, 7);		
	
	private String title;
	private boolean state;
	private int domainAccPos;
	private int domainNamePos;
	private int domainDescPos;
	private int interproIdPos;
	private int fromPos;
	private int toPos;
	private int evaluePos;
	
	private Method(String title, boolean selected, int domainAccPos, int domainNamePos, int domainDescPos, 
			int interproIdPos, int fromPos, int toPos, int evaluePos) {
		this.title = title;
		this.state = selected;
		this.domainAccPos = domainAccPos;
		this.domainNamePos = domainNamePos;
		this.domainDescPos = domainDescPos;
		this.interproIdPos = interproIdPos;
		this.fromPos = fromPos;
		this.toPos = toPos;
		this.evaluePos = evaluePos;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String[] getMethodNames() {
		Method[] methods = Method.values();
		String[] methodsNames = new String[Method.values().length];
		for (int i=0; i < Method.values().length; i++) {
			methodsNames[i] = methods[i].getTitle();
		}
		return methodsNames;
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
	
	/**
	 * 
	 * 
	 * @return
	 */
	public int getDomainAccPosition() {
		return domainAccPos; 
	}

	/**
	 * 
	 * @return
	 */
	public int getDomainNamePosition() {
		return domainNamePos;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDomainDescPosition() {
		return domainDescPos;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getInterproIdPosition() {
		return interproIdPos;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDomainFromPos() {
		return fromPos;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDomainToPos() {
		return toPos;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDomainEvaluePos() {
		return evaluePos;
	}

	
}

	
