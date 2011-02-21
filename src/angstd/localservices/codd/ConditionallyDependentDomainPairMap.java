package angstd.localservices.codd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import angstd.model.AngstdData;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.ui.util.MessageUtil;

/**
 * Class for reading and creating the Map of Conditionally Dependent Pairs (CDP).
 * The CDP appear in a file that is generated like described in [TERRAPON et al. 2009].
 * Taking a database (for example the swisspfam file for Pfam) and applying a fisher
 * test (one-tailed) on all the domain pairs (through a contingency table recording
 * the presence/absence of each domain of the pair), a p-value is computed.
 * 
 * In this class the CDP are read and that creates a unique instance (see getInstance()
 * function)
 * 
 * TO DO: 
 * 1) extend to CDP files learn on more databases
 * 2) allow to change the p-value threshold (dynamically?)
 * 
 * @author Nicolas TERRAPON
 *
 */

public class ConditionallyDependentDomainPairMap implements AngstdData
{
	
 private HashMap<String , HashSet<String> > CDP;
 protected static ConditionallyDependentDomainPairMap instance;
  
  /**
	 * constructor for a ConditionallyDependentDomainPairMap object.
	 * 
	 * @param file
	 * 		The file where the CDP are listed 
	 * 
	 * @param pvalueThreshold
	 * 		The threshold for CDP significance 
	 */
 public ConditionallyDependentDomainPairMap(String file, double pvalueThreshold)
 {
  CDP = new HashMap<String , HashSet<String> >();
   
  BufferedReader in;
  try
  {
   URL path = GatheringThresholdsReader.class.getResource(file);
   in = new BufferedReader(new FileReader(path.getFile()));
   String line;
   while((line = in.readLine()) != null)
   {
    if(!line.isEmpty())			
    {
     String[] entryFields = line.split(" ");
     if(Double.parseDouble(entryFields[2])<=pvalueThreshold)
     {
      if(!CDP.containsKey(entryFields[0]))
      {
       CDP.put(entryFields[0], new HashSet<String>() );
      }
      CDP.get(entryFields[0]).add(entryFields[1]);
      if(!CDP.containsKey(entryFields[1]))
      {
       CDP.put(entryFields[1], new HashSet<String>() );
      }
      CDP.get(entryFields[1]).add(entryFields[0]);
     }
    }
   }
   in.close();
  }catch(FileNotFoundException e1)
  {
   MessageUtil.showWarning("No corresponding CDP file: "+file);
   e1.printStackTrace();
  }
  catch(NumberFormatException e2)
  {
   MessageUtil.showWarning("Error while parsing CDP file.");
   e2.printStackTrace();
  } 
  catch (IOException e3)
  {
   MessageUtil.showWarning("Error while reading/parsing CDP file.");
   e3.printStackTrace();
  }
 }
 
 /**
  * Creates the only allowed instance of a ConditionallyDependentDomainPairMap
  * 
  * Currently limited to the Pfam CDP with an arbitrary p-value threshold
  * 
  */
 public static ConditionallyDependentDomainPairMap getInstance()
 {
  if (instance == null)
  {
   instance = new ConditionallyDependentDomainPairMap("CDP_Pfam-v24.0",0.001);
  }
  return instance;
 }
 
/**
 * Returns the CDP HashMap
 */
 public HashSet<String> getCDP(String s)
 {
  return CDP.get(s);
 }
 
 /**
  * Returns the filtered set of arrangement according to the CDP identified
  */
 public static DomainArrangement[] coddProcedure(DomainArrangement[] arrangeSets)
 {
  boolean certified;
  HashSet<String> domFamilies=new HashSet<String>();
  
  //Instantiation of the CDP if not yet done 
  ConditionallyDependentDomainPairMap cdp=ConditionallyDependentDomainPairMap.getInstance();
  
  for(int i=0; i<arrangeSets.length; i++)
  {
   Iterator<Domain> domIt=arrangeSets[i].getDomainIter();
   while(domIt.hasNext())
   {
    domFamilies.add(domIt.next().getFamID());
   }
   domIt=arrangeSets[i].getDomainIter();
   while(domIt.hasNext())
   {
	Domain dom=domIt.next();
    if(dom.isPutative())
    {
     Iterator<String> domFamIt=domFamilies.iterator();
     certified=false;
     while(domFamIt.hasNext())
     {
      if(cdp.getCDP(domIt.next().getFamID()).contains(domFamIt.next()))
      {
       certified=true;
      }
     }
     if(certified)
     {
      domIt.remove();
     }
    }else
    {
    }
   //Si oui mettre le flag putative a false
    //Si non boucle sur le meme vect pour certif si appartient aux cdp
    // si oui putative a true
      // si non erase
   }
  }
  return arrangeSets;
 }
 
}
