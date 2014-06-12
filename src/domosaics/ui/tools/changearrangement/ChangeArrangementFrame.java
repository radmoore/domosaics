package domosaics.ui.tools.changearrangement;

import javax.swing.JPanel;

import domosaics.ui.tools.ToolFrame;
import domosaics.ui.views.view.View;




/**
 * The ChangeArrangementFrame contains the ChangeArrangementView and allows
 * changing and adding of domains to an arrangement.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementFrame extends ToolFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for a new ChangeArrangementFrame
	 */
    public ChangeArrangementFrame() {
		super();	
	}
    
    /**
     * @see ToolFrame
     */
    @Override
	public void addView(View view) {
    	setTitle(view.getViewInfo().getName());
    }
    
    /**
     * Sets the initialized panel representing the ChangeArrangementView.
     * 
     * @param content
     * 		panel representing the ChangeArrangementView
     */
    public void setContent(JPanel content) {
    	this.setContentPane(content);
    	//this.setSize(780, 400);
    	this.setResizable(false);
    	this.pack();
    }

}
