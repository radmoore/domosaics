package domosaics.ui.tools;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import domosaics.ui.views.view.View;


/**
 * Tools are views which are embedded in a Toolframe. This
 * interface defines the methods a ToolFrame has to implement.
 * 
 * @author Andreas Held
 *
 */
public interface ToolFrameI {

	/**
	 * Adds the view to the toolFrame
	 * 
	 * @param view
	 * 		the view to be embedded in the frame
	 */
	public void addView(View view);
	
	/**
	 * @see JFrame
	 */
	public boolean isFocused();
	
	/**
	 * @see JFrame
	 */
	public int getWidth();
	
	/**
	 * @see JFrame
	 */
	public int getHeight();
	
	/**
	 * @see JFrame
	 */
	public void setSize(int width, int height);
	
	/**
	 * @see JFrame
	 */
	public void setJMenuBar(JMenuBar bar);
	
	/**
	 * @see JFrame
	 */
	public void requestFocus();
	
	/**
	 * @see JFrame
	 */
	public void dispose();
}
