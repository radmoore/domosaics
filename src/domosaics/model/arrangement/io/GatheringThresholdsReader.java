package domosaics.model.arrangement.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import domosaics.model.arrangement.DomainFamily;
import domosaics.model.arrangement.DomainType;
import domosaics.model.arrangement.GapDomain;
import domosaics.model.configuration.Configuration;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.ui.util.MessageUtil;




public class GatheringThresholdsReader {

	protected static Map<String, DomainFamily> domFamilyMap;
	protected static Map<String, Vector<String> > name2id/*, acc2id*/;
	protected static String refVersion="v27.0";
	
	 /**
	  * REturns the only allowed instance of the domFamilyMap.
	  * 
	  */
	public static Map<String, DomainFamily> getInstance() {
		if (domFamilyMap == null)
			read(refVersion);
		return domFamilyMap;
	}
	
	 
	public static void read(String version) {
		refVersion=version;
		domFamilyMap = new HashMap<String , DomainFamily >();
		name2id = new HashMap<String , Vector<String> >();
		domFamilyMap.put(GapDomain.getGapID(), new DomainFamily(GapDomain.getGapID(), GapDomain.getGapID(), DomainType.GAPDOM));
		//acc2id = new HashMap<String, String>();
		name2id = new HashMap<String, Vector<String> >();
		
		BufferedReader in;
		
		try {
			// TODO Update to the most recent Pfam + Create automatic script
			InputStream is = GatheringThresholdsReader.class.getResourceAsStream("resources/gath-Thresholds_Pfam-"+refVersion);
			in = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = in.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] entryFields = line.split("\t");
					DomainFamily d=new DomainFamily(entryFields[0], entryFields[1], DomainType.PFAM, Double.parseDouble(entryFields[2]), Double.parseDouble(entryFields[3]));
					add(d);
				}
			}
		}
  
		catch(Exception e1) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e1);
			else			
				Configuration.getLogger().debug(e1.toString());
		}
	}


	public static void changeVersion(String version) {
		if(!refVersion.equals(version))
			read(version);
	}


	public static void add(DomainFamily d) {
		domFamilyMap.put(d.getId(), d);
		if(name2id.get(d.getName())==null)
			name2id.put(d.getName(), new Vector<String>());
		name2id.get(d.getName()).add(d.getId());
	}
	
	public static Vector<String> getIDFromName(String id) {
     return name2id.get(id);
	}

	
	
 /* catch(NumberFormatException e2)
  {
   MessageUtil.showWarning("Error while parsing Gathering threshold file.");
   e2.printStackTrace();
  } 
  catch (IOException e3)
  {
   MessageUtil.showWarning("Error while reading/parsing Gathering threshold file.");
   e3.printStackTrace();
  }*/

 

}
