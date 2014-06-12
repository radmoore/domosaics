package domosaics.model.GO;

import java.util.HashSet;

public class GeneOntologyTerm {

	private String id, name, ontology;
	private HashSet<String> parents;
	
	public GeneOntologyTerm(String id, String name, String parentOntology, HashSet<String> parents) {
		this.id = id;
		this.name = name;
		this.ontology = parentOntology;
		this.parents = parents;
	}
	
	public GeneOntologyTerm(String id) {
		this.id = id;
		this.parents = new HashSet<String>();
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getParentOntology(){
		return this.ontology;
	}

	public void setParentOntology(String ontology){
		this.ontology=ontology;
	}

	public HashSet<String> getParents(){
		return this.parents;
	}

	public void addParent(String go){
		parents.add(go);
	}
	
	@Override
	public String toString(){
		return id+" "+name+" "+ontology+" has "+parents.size()+" parent terms.";
	}
}

