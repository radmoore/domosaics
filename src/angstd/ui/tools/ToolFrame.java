package angstd.ui.tools;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import angstd.ui.views.view.View;



/**
 * Tools are views which are embedded in a Toolframe. This class is the 
 * basic implementation for a ToolFrame.
 * 
 * @author Andreas Held
 *
 */
public class ToolFrame extends JFrame implements ToolFrameI { 
	private static final long serialVersionUID = 1L;
	
	/** the view to be embedded in the frame */
	protected View view;
	
	public ToolFrame() {
		super();	
	
		this.setSize(new Dimension(840, 480));
		int x = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation(x/2-450,y/2-325);
		this.setResizable(true);
		this.setVisible(true);
	}
	
	/**
	 * @see ToolFrameI
	 */
	public void addView(View view) {
		setTitle(view.getViewInfo().getName());
		getContentPane().add(view.getParentPane());
	}
	
}
