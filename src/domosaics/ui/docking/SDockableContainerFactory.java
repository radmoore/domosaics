package domosaics.ui.docking;

import com.vlsolutions.swing.docking.DefaultDockableContainerFactory;
import com.vlsolutions.swing.docking.DetachedDockView;
import com.vlsolutions.swing.docking.DockTabbedPane;
import com.vlsolutions.swing.docking.DockView;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.MaximizedDockView;
import com.vlsolutions.swing.docking.SingleDockableContainer;
import com.vlsolutions.swing.docking.TabbedDockView;
import com.vlsolutions.swing.docking.TabbedDockableContainer;

/**
 * This container is an extension of the DefaultDockableContainerFactory
 * from VLDocking.
 * <p>
 * The reason for its existence (as well as {@link SDockKey} 
 * and {@link SDockView}) is to introduce a visibility flag for title bars. 
 * Therefore it is possible to  display the view desktop virtually without 
 * title bar. 
 * 
 * 
 * @author Andreas Held (based on EPOS by Thasso Griebel)
 *
 */
public class SDockableContainerFactory extends DefaultDockableContainerFactory{

	
	  /** Returns the component used to modify the expand panel size when expanded from the top.
	   * This implementation uses the following components : 
	   *<ul>
	   * <li> DockView for standard dockables
	   * <li> TabbedDockView for dockable contained in a tabContainer
	   * <li> MaximizedDockView for maximized dockables
	   * <li> DetachedDockView for floating dockables.
	   *</ul>
	   *
	   *@see DockView
	   *@see TabbedDockView
	   *@see MaximizedDockView
	   *@see DetachedDockView
	   */
	  public SingleDockableContainer createDockableContainer(Dockable dockable, int parentType) {
	    switch (parentType){
	      case PARENT_TABBED_CONTAINER:
	        return new TabbedDockView(dockable);
	      case PARENT_DESKTOP:
	        new SDockView(dockable, true);
	      case PARENT_SPLIT_CONTAINER:
	        return new SDockView(dockable);  
	      case PARENT_DETACHED_WINDOW:
	        return new  com.vlsolutions.swing.docking.DetachedDockView(dockable);// DetachedDockView(dockable);
	      default :
	        throw new RuntimeException("Wrong dockable container type");        
	    }
	  }

	  /** Returns the container used for tabbed docking.
	   *<p>
	   * Current implementation uses the DockTabbedPane class.
	   *
	   * @see DockTabbedPane
	   */
	  public TabbedDockableContainer createTabbedDockableContainer() {
		  return new DockTabbedPane();
	  }

}
