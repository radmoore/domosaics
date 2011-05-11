package angstd.model.arrangement.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.GapDomain;
import angstd.model.configuration.Configuration;
import angstd.model.tree.io.NewickTreeReader;
import angstd.ui.util.MessageUtil;

public class GatheringThresholdsReader {

	protected static Map<String, DomainFamily> domFamilyMap;
	protected static Map<String, String> id2acc/*, acc2id*/;

	
	 /**
	  * REturns the only allowed instance of the domFamilyMap.
	  * 
	  */
	 public static Map<String, DomainFamily> getInstance() {
		 if (domFamilyMap == null)
			 read();
		 
		 return domFamilyMap;
	 }
	
	 
	public static void read() {
		domFamilyMap = new HashMap<String , DomainFamily >();
		domFamilyMap.put(GapDomain.getGapID(), new DomainFamily(GapDomain.getGapID(), GapDomain.getGapID(), DomainType.GAPDOM));
		//acc2id = new HashMap<String, String>();
		id2acc = new HashMap<String, String>();
		
		BufferedReader in;
		
		try {
			InputStream is = GatheringThresholdsReader.class.getResourceAsStream("resources/gath-Thresholds_Pfam-v24.0");
			in = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = in.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] entryFields = line.split(" ");
					DomainFamily d=new DomainFamily(entryFields[1], entryFields[0], DomainType.PFAM, Double.parseDouble(entryFields[2]), Double.parseDouble(entryFields[3]));
					domFamilyMap.put(entryFields[0], d);
					id2acc.put(entryFields[1],entryFields[0]);
				}
			}
		}
  
		catch(Exception e1) {
			e1.printStackTrace();
			MessageUtil.showWarning("No corresponding Gathering threshold file");
			Configuration.getLogger().debug(e1.toString());
		}
	}

	
	public static String getAccFromID(String id) {
     return id2acc.get(id);
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
