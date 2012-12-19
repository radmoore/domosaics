package domosaics.webservices.RADS;

/**
 * This enummerator describes the default parameters of a RADS
 * or RAMPAGE scan
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 */
public enum RADSParms {
	
	DEFAULT_MATCHSCORE(150),
	DEFAULT_MISMATCH_PEN(-100),
	DEFAULT_INTERNAL_GAP_OPEN_PEN(-50),
	DEFAULT_INTERNAL_GAP_EXTEN_PEN(-25),
	DEFAULT_TERMINAL_GAP_OPEN_PEN(-100),
	DEFAULT_TERMINAL_GAP_EXTEN_PEN(-50),
	RAM_DEFAULT_INTERNAL_GAP_OPEN_PEN(-10),
	RAM_DEFAULT_INTERNAL_GAP_EXTEN_PEN(-1),
	RAM_DEFAULT_TERMINAL_GAP_OPEN_PEN(0),
	RAM_DEFAULT_TERMINAL_GAP_EXTEN_PEN(0);
	
	private int defaultValue;
	
	private RADSParms(int defaultValue) {
		this.defaultValue = defaultValue;   
	}
	
	/**
	 * Get the default value for a give parameter
	 * @return - the default parameter value
	 */
	public int getDeafultValue() {
		return defaultValue;
	}

}
