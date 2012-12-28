package domosaics.ui.tools.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import domosaics.model.arrangement.ArrangementManager;




public enum Stats {

	PROTS				("arrangements"),
	UNIQUEPROTS			("unique arrangements"),
	SINGLEDOMPROTS		("single domain arrangements"),
	MULTIDOMPROTS		("multi domain arangements"),
	DOMFAMS				("domain familys"),
	AVGNUMDOMS			("average # domains"),
	MAXDOMS				("maximal # domains"),
	AVGJACCARD			("average Jaccard-Index"),
	;
	
	protected String label;
	
	private Stats(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getVal(ArrangementManager manager) {
		NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH);
		formatter.setMaximumFractionDigits(2);
		
		switch(this) {
			case PROTS: 		return ""+manager.getArrangementCount();
			case UNIQUEPROTS:	return ""+manager.getNumUniqueArrangements();
			case SINGLEDOMPROTS:return ""+manager.getSingleDomCount();
			case MULTIDOMPROTS: return ""+manager.getMultiDomCount(); 
			case DOMFAMS: 		return ""+manager.getFamilyCount(); 
			case AVGNUMDOMS:	return formatter.format(manager.getAvgDomainCount()); 
			case MAXDOMS: 		return ""+manager.getMaxDomains(); 
			case AVGJACCARD: 	return formatter.format(manager.getAvgJaccard()); 
			
			default: 			return "N/A";
		}
	}
	
}
