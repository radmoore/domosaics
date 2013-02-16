package domosaics.model.arrangement.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domosaics.model.configuration.Configuration;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class XdomUtil {

	private static Pattern xdomHead = Pattern.compile(">\\s*(\\S+).*");
	
	public static boolean validXdomString(String xdom) {
		
		String[] lines = xdom.split("\\n");
		
		if (lines.length < 2)
			return false;
			
		if (! (lines[0].matches(">.+")))
			return false;
		
		for (String line : lines) {
			if (line.matches("^$"))
				continue;
			if (line.matches(">.+"))
				continue;
			
			String fields[] = line.split("\t");
			if (fields.length < 3)
				return false;
			try {
				Integer.valueOf(fields[0]);
				Integer.valueOf(fields[1]);
			}
			catch(NumberFormatException nfe) {
				if (Configuration.getReportExceptionsMode())
					Configuration.getInstance().getExceptionComunicator().reportBug(nfe);
				else			
					Configuration.getLogger().debug(nfe.toString());
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param xdom
	 * @return
	 */
	public static String getIDFromXdom(String xdom) {
		String idline = xdom.split("\\n")[0];
		Matcher m = xdomHead.matcher(idline);
		if (m.find())
			return m.group(1);
		else return "rawseq";
	}
	
	
	
	public static ArrayList<String> getXdomsFromFile(File xdomFile) {
		ArrayList<String> entries = new ArrayList<String>();
		StringBuilder xdom = new StringBuilder();
		String line = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(xdomFile));
			while( (line = br.readLine()) != null ) {
				if (line.matches(">.+")) {
					if (xdom.length() > 0) {
						if (XdomUtil.validXdomString(xdom.toString()))
							entries.add(xdom.toString());
						// TODO: add format exception
						
					}
					xdom = new StringBuilder();
				}
				xdom.append(line+"\n");
			}
			br.close();
			if (XdomUtil.validXdomString(xdom.toString()))
				entries.add(xdom.toString());
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return entries;
	}
	
}
