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

import angstd.algos.overlaps.OverlapResolver;
import angstd.model.AngstdData;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.model.configuration.Configuration;
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
   URL path = ConditionallyDependentDomainPairMap.class.getResource(file);
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
   Configuration.getLogger().debug(e1.toString());
  }
  catch(NumberFormatException e2)
  {
   MessageUtil.showWarning("Error while parsing CDP file.");
   Configuration.getLogger().debug(e2.toString());
  } 
  catch (IOException e3)
  {
   MessageUtil.showWarning("Error while reading/parsing CDP file.");
   Configuration.getLogger().debug(e3.toString());
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
   instance = new ConditionallyDependentDomainPairMap("resources/CDP_Pfam-v24.0",0.001);
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
  boolean certified, hasCertified=false;
  String cooccur;
  List<DomainArrangement> arrList = new ArrayList<DomainArrangement>();
  
  //Instantiation of the CDP if not yet done 
  ConditionallyDependentDomainPairMap cdp=ConditionallyDependentDomainPairMap.getInstance();
  
  //Filtering overlaps based on E-value
  arrangeSets=OverlapResolver.resolveOverlaps(arrangeSets,"OverlapFilterEvalue");
  
  for(int i=0; i<arrangeSets.length; i++)
  {
   List<Domain> toRemove = new ArrayList<Domain>();
   /*System.out.println("Arrangement "+i+" Prot "+arrangeSets[i].getName());
   System.out.println("Dom. number with overlaps "+arrangeSets[i].countDoms());
   List<Domain> toRemove = OverlapResolver.resolveOverlapsByBestEvalue(arrangeSets[i]);
   for (Domain domToRemove : toRemove)
   {
    //arrangeSets[i].getDomains().removeElement(domToRemove);
    arrangeSets[i].hideDomain(domToRemove);
   }
   System.out.println("Dom. number without overlaps "+arrangeSets[i].countDoms());*/
   
   //Collecting domain families used to co-occurrency certification
   Iterator<Domain> domIt=arrangeSets[i].getDomainIter();
   HashSet<String> domFamilies=new HashSet<String>();
   while(domIt.hasNext())
   {       
    domFamilies.add(domIt.next().getID());
   }
   
   /*Iterator<String> it=domFamilies.iterator();
   while(it.hasNext())
   {
	System.out.println("Families "+it.next()); 
   }*/
   domIt=arrangeSets[i].getDomainIter();
   while(domIt.hasNext())
   {
	Domain dom=domIt.next();
	//System.out.println("Domaine "+dom.toString()); 
    if(dom.isPutative())
    {
     hasCertified=true;
     //System.out.println("Putative"); 
     Iterator<String> domFamIt=domFamilies.iterator();
     certified=false;
     while(domFamIt.hasNext())
     {
      cooccur=domFamIt.next();
      HashSet<String> CDPdom=cdp.getCDP(dom.getID());
      if(CDPdom!=null)
      {
       /*Iterator<String> testIt=CDPdom.iterator();
       while(testIt.hasNext())
       {
      	System.out.println("Cdp "+testIt.next()); 
       }*/
       if(cdp.getCDP(dom.getID()).contains(cooccur))
       {
        //System.out.println("Certified by "+cooccur); 
        certified=true;
       }
      }
     }
     if(certified)
     {
    	 //System.out.println("Certified");
     }else
     {
      //System.out.println("Not certified");    
      toRemove.add(dom);
      //domIt.remove();  	 
     }
    }else
    {
    	//System.out.println("Asserted"); 
    }
   }
   for(int j=0; j<toRemove.size(); j++)
   {
    Domain dom=toRemove.get(j);
    arrangeSets[i].hideDomain(dom);
   }
   if(arrangeSets[i].countDoms()!=0)
   {
    arrList.add(arrangeSets[i]);
   }
  }
  if(!hasCertified)
  {
   MessageUtil.showWarning("No putative domains in this data set. Try to Hmmscan with higher E-values.");	
  }
  return arrList.toArray(new DomainArrangement[arrList.size()]);
 }
 
 
}
