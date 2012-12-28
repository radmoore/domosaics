package domosaics.ui.views.domainview.components;

import java.awt.Color;

/**
 * DomainColors defines the default colors for domains.
 * 
 * @author Andreas Held
 *
 */
public enum DomainColors {
		
	LIGHTBLUE (new Color(5, 180, 230)), 
	GREEN (new Color(95, 200, 100)),
	BLUE (new Color(110, 125, 220)),	
	RED (new Color(240, 115, 115)),		
	ORANGE (new Color(240, 170, 70)),	
	
	MAGENTA (new Color(225, 125, 180)),
	PURPLE (new Color(180, 110, 200)),
	BROWN (new Color(195, 130, 100)),	
	YELLOW (new Color(255, 215, 50)),	
	ROSE (new Color(255, 170, 175)),
	
	GRAY (new Color(165, 160, 158)), 
	TURKY (new Color(50, 205, 170)), 
	
	;
		
	private Color color;
	
	private DomainColors(Color color){
		this.color = color;
	}
	
	/**
	 * Returns the color object
	 * 
	 * @return
	 * 		color object
	 */
	public Color getColor() {
		return color;
	}
	
}
