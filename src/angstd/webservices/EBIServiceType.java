package angstd.webservices;



public enum EBIServiceType {
	
	CLUSTALW2SCORES("ClustalW2", "tooloutput"), 
	CLUSTALW2("ClustalW2", "toolaln"), 
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

//public WebserviceResultParser getResultParser() {
//if (resultParserClass == null)
//	return null;
//
//try {
//	return (WebserviceResultParser) resultParserClass.newInstance();
//} catch (Exception e) {
//	e.printStackTrace();
//}
//return null;
//}
