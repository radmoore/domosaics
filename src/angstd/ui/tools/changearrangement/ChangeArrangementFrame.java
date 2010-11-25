package angstd.ui.tools.changearrangement;

import javax.swing.JPanel;

import angstd.ui.tools.ToolFrame;
import angstd.ui.views.view.View;

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
	 * Constructor for a new DotplotFrame
	 */
    public ChangeArrangementFrame() {
		super();	
	}
    
    /**
     * @see ToolFrame
     */
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
    	getContentPane().add(content);
    	this.setSize(720, 380);
    }

}
