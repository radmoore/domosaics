package domosaics.ui.tools.dotplot;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeListener;

import domosaics.ui.util.DoMosaicsSlider;


/**
 * Sliderbox for the cutoff threshold and window size which are added
 * to the toolframe. Using the method addSliderListener() ChangeListener
 * such as a dotplot can be registered with the sliders and therefore 
 * react on changes.
 * 
 * @author Andreas Held
 *
 */
public class DotplotSliderBox extends Box{
	private static final long serialVersionUID = 1L;
	
	/** the threshold for the cut off score */
	protected DoMosaicsSlider cutoffSlider;
	
	/** the threshold for the window size */
	protected DoMosaicsSlider windowSlider;

	
	/**
	 * Constructor for a new DotplotSliderBox
	 */
	public DotplotSliderBox () {
		super(BoxLayout.X_AXIS);
	
		// upper threshold box
		Box cutOffBox = new Box(BoxLayout.X_AXIS);
		cutoffSlider = new DoMosaicsSlider(DoMosaicsSlider.CUTOFFSLIDER, -20, 20, 12);
		cutOffBox.add(cutoffSlider);
		cutOffBox.setBorder(BorderFactory.createTitledBorder("Cutoff Threshold"));
		
		// window box
		Box windowBox = new Box(BoxLayout.X_AXIS);
		windowSlider = new DoMosaicsSlider(DoMosaicsSlider.WINSLIDER,1, 50, 10);
		windowBox.add(windowSlider);
		windowBox.setBorder(BorderFactory.createTitledBorder("Window Size"));

		add(Box.createHorizontalGlue());
		add(cutOffBox);
		add(Box.createHorizontalGlue());
		add(windowBox);
		add(Box.createHorizontalGlue());
		
		setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
	}
	
	/**
	 * Adds a listener (e.g. a dotplot) to the sliders
	 * 
	 * @param listener
	 * 		a listener reacting on the changes (e.g. a dotplot)
	 */
	public void addSliderListener(ChangeListener listener) {
		cutoffSlider.addChangeListener(listener);
		windowSlider.addChangeListener(listener);
	}

}
