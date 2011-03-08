package angstd.model.GO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeneOntology {

	private Date version, lastUpdate;
	private static GeneOntology instance;
	private static HashMap<String, GeneOntologyTerm> ontology;
	private static int total;
	
	public static GeneOntology getInstance() {
		if (instance == null)
			instance = new GeneOntology(new Date(), new Date());
		
		return instance;
	}
	
	private GeneOntology (Date version, Date lastUpdate) {
		this.version = version;
		this.lastUpdate = lastUpdate;
		total = 0;
		ontology = new HashMap<String, GeneOntologyTerm>();
	}
	
	public void setVersionDate(Date version) {
		this.version = version;
	}
	
	public Date getVersion() {
		return version;
	}
	
	public Date getLastUpdateTime() {
		return lastUpdate;
	}
	
	public void addTerm(String gid, String term) {
		if (!hasTerm(gid)) {
			GeneOntologyTerm goTerm = new GeneOntologyTerm(gid, term, null);
			ontology.put(gid, goTerm);
			lastUpdate = new Date();
			total++;
		}
	}
	
	public boolean hasTerm(String gid) {
		return ontology.containsKey(gid);
	}
	
	public GeneOntologyTerm getTerm(String gid) {
		return ontology.get(gid);
	}
	
	public int totalTerms() {
		return total;
	}

}
