package domosaics.webservices;



public enum EBIServiceType {
	
	CLUSTALW2SCORES("ClustalW2", "tooloutput"), 
	//CLUSTALW2("ClustalW2", "toolaln"),
	CLUSTALW2("ClustalW2", "aln-clustalw"),
	TCOFFEE("TCoffee", "tooloutput"), 
	DBFETCH("DBFetch", "raw"),
	MAFFT("Mafft","tooloutput" )
	;

	private String name;
	private String outFormat;
	
	private EBIServiceType(String name, String outFormat) { 
		this.name = name;
		this.outFormat = outFormat;
	}
	
	public String getServiceName() {
		return name;
	}
	
	public String getOutFormat() {
		return outFormat;
	}

}
