package domosaics.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import domosaics.model.configuration.Configuration;




/**
 * A collection of some string converter methods which comes in handy at various
 * places within the program.
 * 
 * @author Andreas Held
 * 
 */
public class StringUtils {

	/** formatter used to format strings into x.xx */
	private static NumberFormat formatter;
	{
		formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH);
		formatter.setMaximumFractionDigits(2);
	}

	/**
	 * Specifies whether or not the string can be parsed into a Number
	 * 
	 * @param word
	 *            the string which should be parsed into a number
	 * @return whether or not the string can be parsed into a number
	 */
	public static boolean isNumber(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
			return false;
		}
	}

	/**
	 * Converts all spaces into "_" characters.
	 * 
	 * @param s
	 *            the string to convert
	 * @return the converted string
	 */
	public static String convertSpaces(String s) {
		return s.replace(" ", "_");
	}

}
