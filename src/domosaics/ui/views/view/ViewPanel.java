package domosaics.ui.views.view;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import domosaics.ui.views.domainview.DomainView;


/**
 * Class ViewPanel is the basic class for embedding views, such as 
 * {@link DomainView} into the user interface.
 * <p>
 * To set the views specific menu the menu is created as JToolBar and set into
 * the north frame of this panel (kind of a workaround, because the VLDocking
 * panels don't allow menu bars)
 * <p>
 * Its also possible to change the view position within its embedding scroll 
 * panel by using the methods setViewPosition() and getViewPosition(). Setting
 * the position results in an automatic scroll to the specified position which 
 * comes in handy when the user searches for specific elements within the view.
 * 
 * @author Andreas Held
 *
 */
public class ViewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** the view, i.e. DomainView */
    protected View view = null;
    
    /** the view component, e.g. ScrollPane */
    protected JComponent viewComponent;	
    
    /** this is the view specific menu bar */
    protected JToolBar toolbar;
    
    
    /**
     * Basic constructor for a new ViewPanel.
     * 
     * @param name 
     * 		the views name.
     * @param view 
     * 		the view which is stored in this panel.
     */
    public ViewPanel(String name, View view){
    	super(new BorderLayout());
    	setName(name);
        setView(view); 
    }
    
    /**
     * Sets the views specific menu as JToolbar into the north area of this
     * panel.
     * 
     * @param bar
     * 		the JMenuBar to add as views specific menu
     */
    public void setToolbar(JMenuBar bar) {
    	if (bar == null)
    		return;
    	
    	toolbar = new JToolBar();
    	toolbar.add(bar);
		toolbar.revalidate();
		toolbar.repaint();
		
    	add(toolbar, BorderLayout.NORTH);
    }
    
    public void removeToolbar() {
    	remove(toolbar);
    	toolbar = null;
    }
    
    /**
     * Sets the view for this panel.
     * <p>
     * By invoking {@link View#setParentPane(ViewPanel)} a cross reference
     * between the view and its embedding panel is made.
     * 
     * @param view 
     * 		the view which should be embedded into the panel.
     */
	public void setView(View view) {
        // store view in here and the panel within the view
		this.view = view;
        this.view.setParentPane(this);
        
        // make it possible to focus the scroll pane which embeds the view
        this.view.getComponent().setFocusable(true);
        
        // set up the graphical context for the embedding scroll pane
		this.viewComponent = this.view.getComponent();
		this.viewComponent.setBorder(BorderFactory.createEmptyBorder());
		
		// add the scrollpane to the panel and make the view itself focussable
		this.add(this.viewComponent, BorderLayout.CENTER);
       	this.view.getViewComponent().requestFocusInWindow();
    }
	
	/**
	 * Disposes the view panel.
	 */
	public void dispose(){
		this.view = null;	
		this.viewComponent = null;
		if(getParent() != null)
			getParent().remove(this);
	}
	
	/**
	 * Return the actual view position within the scrollPane.
	 * 
	 * @return
	 * 		 actual view position within the scrollPane
	 */
	public Point getViewPosition() {
		if (((JScrollPane) view.getComponent()) != null) {
			return ((JScrollPane) view.getComponent()).getViewport().getViewPosition();
		}
		return new Point(0, 0);
	}

	/**
	 * Set the view position manually within the scrollPane. This method can
	 * be used for instance to jump to domains the user wants to find using a
	 * find dialog.
	 * 
	 * @param newPos
	 * 		the new view position within its embedding scrollPane
	 */
	public void setViewPosition(Point newPos) {
		if (((JScrollPane) view.getComponent()) != null) {
			((JScrollPane) view.getComponent()).getViewport().setViewPosition(newPos);
		}
	}

}