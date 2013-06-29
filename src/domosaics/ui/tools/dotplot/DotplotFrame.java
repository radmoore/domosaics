package domosaics.ui.tools.dotplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import domosaics.ui.tools.ToolFrame;
import domosaics.ui.views.view.View;




/**
 * DotplotFrame contains the domain dotplot view and the slider panel.
 * Using the method addSliderListener() ChangeListener
 * such as a dotplot can be registered with the sliders and therefore 
 * react on changes.
 * 
 * @author Andreas Held
 *
 */
public class DotplotFrame extends ToolFrame {
	private static final long serialVersionUID = 1L;
	
	/** panel to manage the dotplot threshold sliders as well as the dotplot view */
	private JPanel componentHolder;
	
	/** the sliders to be added to the frame */
	private DotplotSliderBox slider;

	/**
	 * Constructor for a new DotplotFrame
	 */
    public DotplotFrame() {
		super();	
		componentHolder = new JPanel(new BorderLayout());
		componentHolder.setBackground(Color.white);
		componentHolder.add(slider = new DotplotSliderBox(), BorderLayout.NORTH);
		add(componentHolder);
	}
    
    /**
     * @see ToolFrame
     */
    public void addView(View view) {
    	setTitle(view.getViewInfo().getName());
    	componentHolder.add(view.getParentPane(), BorderLayout.CENTER);
    	this.setExtendedState( this.getExtendedState()|Frame.MAXIMIZED_BOTH );
//    	this.setSize(610, 640);
    }
    
	/**
	 * Registers a component e.g. the a dotplot as listener to the sliders
	 * 
	 * @param listener
	 * 		listener for the sliders, e.g. a dotplot
	 */
    public void addSliderListener(ChangeListener listener) {
    	slider.addSliderListener(listener);
    }
}
