package angstd.webservices.RADS;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public enum RadsParms {
	DEFAULT_MATCHSCORE(150),
	DEFAULT_MISMATCH_PEN(-100),
	DEFAULT_INTERNAL_GAP_OPEN_PEN(-50),
	DEFAULT_INTERNAL_GAP_EXTEN_PEN(-25),
	DEFAULT_TERMINAL_GAP_OPEN_PEN(-100),
	DEFAULT_TERMINAL_GAP_EXTEN_PEN(-50);
	
	private int defaultValue;
	
	private RadsParms(int defaultValue) {
		this.defaultValue = defaultValue;   
	}
	
	public int getDeafultValue() {
		return defaultValue;
	}

}
