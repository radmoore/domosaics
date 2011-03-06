package angstd.model.GO;

public class GeneOntologyTerm {

	private String id, name, ontology;
	
	public GeneOntologyTerm(String id, String name, String parentOntology) {
		this.id = id;
		this.name = name;
		this.ontology = parentOntology;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getParentOntology(){
		return this.ontology;
	}
	
}

