package angstd.ui.views.view;

import java.awt.Graphics;
import java.io.File;

import javax.swing.JComponent;

import angstd.ui.ViewHandler;
import angstd.ui.views.AngstdViewFactory;
import angstd.ui.views.ViewType;
import angstd.ui.views.view.layout.ViewLayout;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The View interface defining all methods which can be applied to each view.
 * <p>
 * A view object for rendering, layouting and manipulating a backend 
 * data set such as domain arrangements needs to store various information
 * about itself. To mention only a couple of them: its unique id (created by
 * {@link AngstdViewFactory} during its creations), various icons for display
 * within the workspace, its type and so on. All those information are stored
 * in form of a {@link ViewInfo} object and can be accessed using getViewInformation().
 * <p>
 * Each possible view can be created using the AngstdViewFactory (but look
 * first into {@link ViewHandler} where methods are defined for this purpose.
 * When creating a new view someone just have to know its {@link ViewType} the
 * rest is done automatically.
 * <p>
 * When a view is automatically created its properties file is used, which MUST
 * be located in a resource folder which is the same level within the package
 * structure as the special view class itself (The path to this view class
 * is stored in the ViewType and used to find the properties file).
 * <p>
 * For more details on the view see its basic implementation {@link AbstractView}.
 * 
 * 
 * 
 * @author Andreas Held
 *
 */
public interface View {

//	public boolean isChanged();
//	
//	public void setChanged (boolean changed);
	/**
	 * Set the ViewInfo which contains all backend data for the view (e.g.
	 * its unique id, icons ...).
	 *  
	 * @param info
	 * 		the view information to store
	 */
	public void setViewInfo(ViewInfo info);
	
	/**
	 * Returns the views information such as unique id various icons and so on
	 * all coupled within the ViewInfo object.
	 * 
	 * @return
	 * 		wieInfo object holding all relevant information about the view.
	 */
	public ViewInfo getViewInfo();

	/**
	 * Returns the component containing the view.
	 * This can be the view itself or a surrounding component, e.g. ScrollPane
	 * 
	 * @return 
	 * 		the component embedding the view (or the view itself)
	 */
	public JComponent getComponent();
	
	/**
	 * Returns the real view component. This is used to split the view component
	 * from a component that is returned by getComponent(). 
	 * <p>
	 * For example, {@link #getComponent()} might return a ScrollPane that 
	 * contains the JComponent returned by this method.
	 * 
	 * @return 
	 * 		the real view
	 */
	public JComponent getViewComponent();
	
	/**
	 * Exports the view to a file
	 * 
	 * @param file
	 * 		the file into which the view gets exported
	 */
	public void export (File file);

	/**
	 * Returns the panel displaying this view
	 * 
	 * @return 
	 * 		the view panel
	 */
	public ViewPanel getParentPane();
	
	/**
	 * Sets this views parent pane.
	 * 
	 * @param parent 
	 * 		the viewPanel containing the view
	 */
	public void setParentPane(ViewPanel parent);
	
	/**
	 * Render the view on the given graphics object. The graphics object
	 * might be a Graphics object created by an exporter, so the view
	 * can be exported in different formats by using this method. 
	 * 
	 * @param g 
	 * 		the graphics object on which is used for rendering the view
	 */
	public void render(Graphics g);	
	
	/**
	 * Adds a new renderer to the view
	 * 
	 * @param r 
	 * 		the renderer to add
	 */
	public void addRenderer(Renderer r);
	
	/**
	 * Returns whether or not the view is in zoom mode.
	 * 
	 * @return
	 * 		whether or not the view is within zoom mode
	 */
	public boolean isZoomMode();
	
	/**
	 * Switches the zoom mode for a view between on and off.
	 */
	public void toggleZoomMode();
	
	/**
	 * Sets the view width to the specified width
	 * 
	 * @param newWidth
	 * 		the new width for the view.
	 */
	public void setNewViewWidth(int newWidth);
	
	/**
	 * Sets the view height to the specified height
	 * 
	 * @param newHeight
	 * 		the new height for the view.
	 */
	public void setNewViewHeight(int newHeight);
	
	/**
	 * Sets a new layout for the view which is then used
	 * for calculate the components positions.
	 * 
	 * @param layout
	 * 		the new layout which should be used
	 */
	public void setViewLayout(ViewLayout layout);
	
	/**
	 * Sets a new renderer for the view which is then used
	 * for render the view components.
	 * 
	 * @param renderer
	 * 		the new renderer which should be used
	 */
	public void setViewRenderer(Renderer renderer);
	
	/**
	 * Removes all registered mouse listeners
	 */
	public void removeMouseListeners();
	
	/**
	 * Register the necessary mouse listeners for the view
	 */
	public void registerMouseListeners();
	
	/**
	 * Triggers the layout method of a view
	 */
	public void doLayout();
	
	/**
	 * Removes a renderer from the view
	 * 
	 * @param r 
	 * 		the renderer to remove
	 */
	public void removeRenderer (Renderer r);
	
	/**
	 * Removes all additional renderer
	 */
	public void removeAllRenderer ();
	
}
