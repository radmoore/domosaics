package angstd.model.arrangement.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class XdomUtil {

	private static Pattern fastaHead = Pattern.compile(">(ENSP00000328478).*");
	
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
				return false;
			}
		}
		return true;
	}
	
	//TODO: refuses to MATCH!!!!
	public static String getIDFromXdom(String xdom) {
		String idline = xdom.split("\\n")[0];
		System.out.println("IDLINE: >"+idline+"<");
		Matcher m = fastaHead.matcher(idline);
		return m.group(1);
	}
	
}
