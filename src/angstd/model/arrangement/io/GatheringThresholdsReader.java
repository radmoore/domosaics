package angstd.model.arrangement.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.ui.util.MessageUtil;

public class GatheringThresholdsReader {

	protected static Map<String, DomainFamily> domFamilyMap;
	protected static Map<String, String> id2acc, acc2id;

	
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
		acc2id = new HashMap<String, String>();
		id2acc = new HashMap<String, String>();
		
		BufferedReader in;
		
		try {
			URL path = GatheringThresholdsReader.class.getResource("resources/gath-Thresholds_Pfam-v24.0");
			in = new BufferedReader(new FileReader(path.getFile()));
			String line;
			while((line = in.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] entryFields = line.split(" ");
					DomainFamily d=new DomainFamily(entryFields[1], entryFields[0], DomainType.PFAM, Double.parseDouble(entryFields[2]), Double.parseDouble(entryFields[3]));
					domFamilyMap.put(entryFields[0], d);
				}
			}
		}
  
		catch(Exception e1) {
			MessageUtil.showWarning("No corresponding Gathering threshold file");
			e1.printStackTrace();
		}
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
