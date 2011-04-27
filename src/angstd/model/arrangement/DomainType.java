package angstd.model.arrangement;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import angstd.model.configuration.Configuration;

/**
 * Supported domain types
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public enum DomainType {
	
	/*PFAM ("Pfam", "HMMPfam", "http://pfam.sanger.ac.uk", "PF.+"),
	SUPERFAMILY ("Superfamily", "SUPERFAMILY", "http://supfam.cs.bris.ac.uk/SUPERFAMILY/", "SSF.+"),
	SMART ("SMART", "HMMSmart", "http://smart.embl-heidelberg.de/", "SM.+"),
	PIRSF ("PIRSF", "hmmpir", "http://pir.georgetown.edu/", "PIRSF.+"),
	PROSITE ("PROSITE", "profilescan", "http://www.expasy.org/prosite/", "PS.+"),
	PANTHER ("PANTHER", "hmmpanther", "http://www.pantherdb.org/", "PTHR.+"),
	GENE3D ("Gene3D", "gene3d", "http://gene3d.biochem.ucl.ac.uk/Gene3D/", "G3D.+"),
	TIGRFAMs ("TIGRFAMs", "TIGRFAMs", "http://www.jcvi.org/cgi-bin/tigrfams/index.cgi", "TIGR.+"),
	PRINTS ("PRINTS", "fprintscan", "http://www.bioinf.manchester.ac.uk/dbbrowser/PRINTS/index.php", "PR.+"),
	PRODOM ("ProDom", "BlastProDom", "http://prodom.prabi.fr/prodom/current/html/home.php", "PD.+"),
	UNKNOWN ("unknown", null, null, null);*/
	
	PFAM ("Pfam", "http://pfam.sanger.ac.uk", "PF.+"),
	SUPERFAMILY ("Superfamily", "http://supfam.cs.bris.ac.uk/SUPERFAMILY/", "SSF.+"),
	SMART ("SMART", "http://smart.embl-heidelberg.de/", "SM.+"),
	PIRSF ("PIRSF", "http://pir.georgetown.edu/", "PIRSF.+"),
	PROSITE ("PROSITE", "http://www.expasy.org/prosite/", "PS.+"),
	PANTHER ("PANTHER", "http://www.pantherdb.org/", "PTHR.+"),
	GENE3D ("Gene3D", "http://gene3d.biochem.ucl.ac.uk/Gene3D/", "G3D.+"),
	TIGRFAMs ("TIGRFAMs", "http://www.jcvi.org/cgi-bin/tigrfams/index.cgi", "TIGR.+"),
	PRINTS ("PRINTS", "http://www.bioinf.manchester.ac.uk/dbbrowser/PRINTS/index.php", "PR.+"),
	PRODOM ("ProDom", "http://prodom.prabi.fr/prodom/current/html/home.php", "PD.+"),
	GAPDOM ("gapdom", null, null),
	UNKNOWN ("unknown", null, null);

/*	private static Map<String, DomainType> ipr2type = new HashMap<String, DomainType>();
	
	static {
		for ( DomainType domType : EnumSet.allOf(DomainType.class) )
			ipr2type.put(domType.getIprScanMethod(), domType);
	}*/

	private String name/*, iprScanMethod*/, homeUrl, regexp;

    public String getName() { return name; }
    //public String getIprScanMethod() { return iprScanMethod; }
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
	    	else if ( Pattern.matches(PIRSF.getPattern().toString(), id) ) {
	    		return PIRSF;
	    	}
	    	else if ( Pattern.matches(PROSITE.getPattern().toString(), id) ) {
	    		return PROSITE;
	    	}
	    	else if ( Pattern.matches(PANTHER.getPattern().toString(), id) ) {
	    		return PANTHER;
	    	}
	    	else if ( Pattern.matches(GENE3D.getPattern().toString(), id) ) {
	    		return GENE3D;
	    	}
	    	else if ( Pattern.matches(TIGRFAMs.getPattern().toString(), id) ) {
	    		return TIGRFAMs;
	    	}
	    	else if ( Pattern.matches(PRINTS.getPattern().toString(), id) ) {
	    		return PRINTS;
	    	}
	    	else
	    		return UNKNOWN;
    	
    	}	
    	catch (Exception e) {
    	//	e.printStackTrace();
    		Configuration.getLogger().debug(e.toString());
    		return UNKNOWN;
		}
  
    }
    
    
   /*public static DomainType getTypeByMethod(String scanMethod) {
    	return ipr2type.get(scanMethod);
    }
    
     private DomainType(String name, String iprScanMethod, String homeUrl, String regexp) {
    	this.name = name;
    	this.iprScanMethod = iprScanMethod;
        this.homeUrl = homeUrl;
        this.regexp = regexp;
    }*/

    private DomainType(String name, String homeUrl, String regexp) {
    	this.name = name;
        this.homeUrl = homeUrl;
        this.regexp = regexp;
    }
    
}
