package angstd.ui.util;

import java.awt.FontMetrics;

import javax.swing.JComponent;

/**
 * Class StringUtils. This class provides som useful method to 
 * process labels of view components.
 * 
 * @author Thasso Griebel (taken from the EPOS code - thasso@minet.uni-jena.de)
 */
public class StringUIUtils {
	
    /**
     * Clips the passed in String to the space provided.  
     * NOTE: this assumes the string does not fit in the available space.
     *
     * @param c 
     * 		JComponent that will display the string, may be null
     * @param fm 
     * 		FontMetrics used to measure the String width
     * @param string 
     * 		String to display
     * @param availTextWidth 
     * 		Amount of space that the string can be drawn in
     * @return Clipped 
     * 		string that can fit in the provided space.
     */
    public static String clipString(JComponent c, FontMetrics fm, String string, int availTextWidth) {
        // c may be null here.
        String clipString = "...";
        int width = stringWidth(c, fm, clipString);
        // NOTE: This does NOT work for surrogate pairs and other fun stuff
        int nChars = 0;
        for(int max = string.length(); nChars < max; nChars++) {
            width += fm.charWidth(string.charAt(nChars));
            if (width > availTextWidth) {
                break;
            }
        }
        string = string.substring(0, nChars) + clipString;
        return string;
    }

    /**
     * Returns the width of the passed in String.
     *
     * @param c 
     * 		JComponent that will display the string, may be null
     * @param fm 
     * 		FontMetrics used to measure the String width
     * @param string 
     * 		String to get the width of
     */
    public static int stringWidth(JComponent c, FontMetrics fm, String string){
        return fm.stringWidth(string);
    }
    /**
     * Clips the passed in String to the space provided.
     *
     * @param c 
     * 		JComponent that will display the string, may be null
     * @param fm 
     * 		FontMetrics used to measure the String width
     * @param string
     * 		String to display
     * @param availTextWidth 
     * 		Amount of space that the string can be drawn in
     * @return 
     * 		Clipped string that can fit in the provided space.
     */
    public static String clipStringIfNecessary(JComponent c, FontMetrics fm,
                                               String string,
                                               int availTextWidth) {
        if ((string == null) || (string.equals("")))  {
            return "";
        }
        int textWidth = stringWidth(c, fm, string);
        if (textWidth > availTextWidth) {
            return clipString(c, fm, string, availTextWidth);
        }
        return string;
    }
    
}