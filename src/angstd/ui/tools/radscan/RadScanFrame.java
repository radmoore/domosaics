package angstd.ui.tools.radscan;

import javax.swing.JPanel;

import angstd.ui.tools.ToolFrame;
import angstd.ui.views.view.View;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanFrame extends ToolFrame{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RadScanFrame() {
		super();	
	}
    
    public void addView(View view) {
    	setTitle(view.getViewInfo().getName());
    }
    
    public void setContent(JPanel content) {
    	this.setContentPane(content);
    	//this.setSize(780, 400);
    	this.setResizable(false);
		setAlwaysOnTop(true);
    	this.pack();
    }

}
