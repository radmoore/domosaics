package angstd.model.arrangement;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supported domain types
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public enum DomainType {
	
	PFAM ("Pfam", "HMMPfam", "http://pfam.sanger.ac.uk", "PF\\d+"),
	SUPERFAMILY ("Superfamily", "superfam", "http://supfam.cs.bris.ac.uk/SUPERFAMILY/", null),
	SMART ("SMART", "HMMSmart", "http://smart.embl-heidelberg.de/", "SM\\d+"),
	PIR ("PIR", "HMMPir", "http://pir.georgetown.edu/", null),
	UNKNOWN ("unknown", null, null, null);

	private static Map<String, DomainType> ipr2type = new HashMap<String, DomainType>();
	
	static {
		for ( DomainType domType : EnumSet.allOf(DomainType.class) )
			ipr2type.put(domType.getIprScanMethod(), domType);
	}

	private String name, iprScanMethod, homeUrl, regexp;

    public String getName() { return name; }
    public String getIprScanMethod() { return iprScanMethod; }
    public String homeUrl() { return homeUrl; }
    public Pattern getPattern() { return Pattern.compile(regexp); }
    
    
    public static DomainType getType(String id) {
    	
    	
    	
    	try {
    	
	    	if ( Pattern.matches(PFAM.getPattern().toString(), id) ) {
	    		return PFAM;
	    	}
	    	else if ( Pattern.matches(SMART.getPattern().toString(), id) ) {
	    		return SMART;
	    	}
	    	else if ( Pattern.matches(SUPERFAMILY.getPattern().toString(), id) ) {
	    		return SUPERFAMILY;
	    	}
	    	else if ( Pattern.matches(SUPERFAMILY.getPattern().toString(), id) ) {
	    		return PIR;
	    	}
	    	else
	    		return UNKNOWN;
    	
    	}	
    	catch (Exception e) {
    	//	e.printStackTrace();
    		return UNKNOWN;
		}
  
    }
    
    
    public static DomainType getTypeByMethod(String scanMethod) {
    	return ipr2type.get(scanMethod);
    }
    
    
    private DomainType(String name, String iprScanMethod, String homeUrl, String regexp) {
    	this.name = name;
    	this.iprScanMethod = iprScanMethod;
        this.homeUrl = homeUrl;
        this.regexp = regexp;
    }

    
}
