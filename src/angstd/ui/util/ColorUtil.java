package angstd.ui.util;

import java.awt.Color;

/**
 * Helper methods which can be applied on colors.
 * 
 * @author Andreas Held
 *
 */
public class ColorUtil {

	/**
	 * Creates a transparent color with a specified alpha grade.
	 * 
	 * @param color
	 * 		the color which should be made transparent
	 * @param alpha
	 * 		the grade of transparency 0..255
	 * @return
	 * 		the rgba color
	 */
	public static Color createAlphaColor(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	/**
	 * Converts a percentage value into an alpha value and uses 
	 * the alpha value to make a color transparent.
	 * 
	 * @param color
	 * 		the color which should be made transparent
	 * @param percent
	 * 		the grade of transparency 0..100%
	 * @return
	 * 		the transparent color
	 */
	public static Color createPercentageAlphaColor(Color color, int percent) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), similarity2Alpha(percent));
	}
	
	/**
	 * Converts a domain distance value into an alpha value and uses 
	 * the alpha value to make a color transparent.
	 * 
	 * @param color
	 * 		the color which should be made transparent
	 * @param editOps
	 * 		the domain distance
	 * @return
	 * 		the transparent color
	 */
	public static Color createDomainDistanceAlphaColor(Color color, int editOps) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), editOps2Alpha(editOps));
	}
	
	/**
	 * Converts a evalue into an alpha value and uses 
	 * the alpha value to make a color transparent.
	 * 
	 * @param color
	 * 		the color which should be made transparent
	 * @param evalue
	 * 		the evalue used for the transparency grade
	 * @return
	 * 		the transparent color
	 */
	public static Color createEvalueColor(Color color, double evalue) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), evalue2alpha(evalue));
	}
	
	/**
	 * Helper method to convert a domain distance into an alpha value
	 * 
	 * @param editOps
	 * 		number of edit operations (distance value)
	 * @return
	 * 		the converted alpha value
	 */
	private static int editOps2Alpha (int editOps) {
		if (editOps == 0)			// identity
			return 0;
		else if(editOps == 1)		// one loss or gain	
			return 40;
		else if(editOps == 2)		// two loss or gain	
			return 80;
		else if(editOps < 4)		// up to 4 loss or gain	
			return 120;
		else if(editOps < 8)		// up to 8 loss or gain	
			return 160;
		else 						//greater than or 8 loss or gain	
			return 200;
	}
	
	/**
	 * Helper method to convert an evalue into an alpha value.
	 * 
	 * @param eval
	 * 		the eval to be converted
	 * @return
	 * 		the computed alpha value
	 */
	private static int evalue2alpha (double eval) {
		if (eval == Double.POSITIVE_INFINITY) // no eval assigned
			return 255;
		if(eval < 1E-100)
			return 255;
		else if(eval < 1E-50)
			return 240;
		else if(eval < 1E-50)
			return 220;
		else if(eval < 1E-6)
			return 200;
		else if(eval < 0.1)
			return 180;
		else if(eval < 1)
			return 150;
		else if(eval < 10)
			return 50;
		else
			return 5;
	}
	
	/**
	 * Helper method to convert a percentage value into an alpha value
	 * 
	 * @param percent
	 * 		the percentage to be converted
	 * @return
	 * 		the converted alpha value
	 */
	private static int similarity2Alpha (int percent) {
		percent = 100 - percent;
		
		if (percent == 0)
			return 0;
		else if(percent <= 10)
			return 25;
		else if(percent <= 20)
			return 50;
		else if(percent <= 30)
			return 75;
		else if(percent <= 40)
			return 100;
		else if(percent <= 50)
			return 125;
		else if(percent <= 60)
			return 150;
		else if(percent <= 70)
			return 175;
		else if(percent <= 80)
			return 200;
		else 
			return 225;
	}
}
