package angstd.ui.tools.radscan;

import javax.swing.JPanel;

import angstd.ui.tools.ToolFrame;
import angstd.ui.views.view.View;

public class RadScanFrame extends ToolFrame{

	/**
	 * Constructor for a new ChangeArrangementFrame
	 */
    public RadScanFrame() {
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
    	this.setContentPane(content);
    	//this.setSize(780, 400);
    	this.setResizable(false);
    	this.pack();
    }

}
