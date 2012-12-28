package domosaics.ui.tools.domaingraph;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import domosaics.ui.ViewHandler;
import domosaics.ui.tools.ToolFrame;
import domosaics.ui.tools.domaingraph.components.PrefuseGraph;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.view.View;




/**
 * The domain graph frame containing a DomainGraphView.
 * 
 * @author Andreas Held
 *
 */
public class DomainGraphFrame extends ToolFrame implements WindowFocusListener {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for a new DomainGraphFrame
	 */
    public DomainGraphFrame() {
		super();	
		addWindowFocusListener(this);
		setLocation(0,0);
    }
    
    /**
     * @see ToolFrame
     */
	public void addView(View view) {
		setTitle(view.getViewInfo().getName());
	}
    
	/** 
	 * Shows the graph using the graphPanel which is created 
	 * during the initialization of DomainGraphView
	 * 
	 * @param graphPanel
	 * 		the panel which has to be embedded to display the prefuse graph
	 */
    public void showGraph(PrefuseGraph graphPanel) {
		getContentPane().add(graphPanel);
		pack();
    }
    
    /**
     * This method automatically enables forces if the window gains focus
     * again.
     */
	public void windowGainedFocus(WindowEvent arg0) {
		// enable forces 
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		if (!view.getGraphLayoutManager().isDisableForces())
			view.getPrefuseGraph().setForces(true);
	}

    /**
     * This method automatically disables forces if the window lost focus
     * to avoid memory lack.
     */
	public void windowLostFocus(WindowEvent arg0) {
		// disable forces  
		DomainGraphView view = ViewHandler.getInstance().getTool(ViewType.DOMAINGRAPH);
		if (view == null)
			return;
		view.getPrefuseGraph().setForces(false);
	}

}
