package domosaics.model.GO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import domosaics.model.configuration.Configuration;


public class GeneOntology {

	private static String url="resources/gene_ontology.1_2.obo";
	//private static String url="http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology.1_2.obo";
	private static Date lastUpdate;
	private static GeneOntology instance;
	private static HashMap<String, GeneOntologyTerm> ontology;
	private static int total;
	
	public static GeneOntology getInstance() {
		if (instance == null)
			instance = new GeneOntology();
		return instance;
	}
	
	private GeneOntology () {
		lastUpdate = new Date();
		total = 0;
		ontology = new HashMap<String, GeneOntologyTerm>();

		String line, field, id="", name="", name_space="", alt;
		boolean hasBegun=false, ended=false, obsolete=false;
		Iterator<String> itAltId;
		ArrayList<String> alt_id = new ArrayList<String>();
		HashSet<String> parents = new HashSet<String>();
		GeneOntologyTerm go;
		BufferedReader in;
		
		URL oboURL;
		try {
			oboURL = GeneOntology.class.getResource(url);
			in = new BufferedReader(new FileReader(oboURL.getFile()));
			//in = new BufferedReader(new InputStreamReader(oboURL.openStream()));
			
			int pos;
			while((line = in.readLine()) != null && !ended) {
			if(!hasBegun) {
				if(line.equals("[Term]")) {
					hasBegun=true;
					}
				}
				else {
					if(line.equals("[Typedef]")) {
						ended=true;
						}
						else {
							if(!line.isEmpty() && !line.equals("[Term]")) {
								pos = line.indexOf(':',0);
								field = line.substring(0,pos);
								if(field.equals("id")) {
									id = line.substring(pos+2,line.length());
									alt_id.clear();
									parents.clear();
									obsolete=false;
								}
								else {
									if(field.equals("alt_id")) {
										alt_id.add(line.substring(pos+2,line.length()));
									}
									else {
										if(field.equals("name")) {
											name = line.substring(pos+2,line.length());
										}
										else {
											if(field.equals("namespace")) {
												name_space = line.substring(pos+2,line.length());
											}
											else {
												if(field.equals("is_obsolete")) {
													obsolete=true; 
												}
												else {
													if(field.equals("is_a")) {
														parents.add(line.substring(line.indexOf("GO"),10)); 
													}
												}
											}
										}
									}
								}
							}
							else {
								if(!obsolete) {
									go = new GeneOntologyTerm(id, name, name_space, parents);
									//System.out.println(go.toString());
									ontology.put(id, go);
				        		
									itAltId=alt_id.iterator();
									while(itAltId.hasNext()) {
										alt = itAltId.next();
										if(!ontology.containsKey(alt))
											ontology.put(alt, go);
									}
								}
							}
						}
					}
				 }
			} catch (FileNotFoundException e) {
				Configuration.getLogger().debug(e.toString());
			} catch (MalformedURLException e) {
				Configuration.getLogger().debug(e.toString());
			} catch (IOException e) {
				Configuration.getLogger().debug(e.toString());
			}
		       
	}
	
	public Date getLastUpdateTime() {
		return lastUpdate;
	}
	
	public void addTerm(String gid, String term) {
		if (!hasTerm(gid)) {
			GeneOntologyTerm goTerm = new GeneOntologyTerm(gid, term, null, null);
			ontology.put(gid, goTerm);
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
