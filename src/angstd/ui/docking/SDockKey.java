package angstd.ui.docking;

import javax.swing.Icon;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockViewTitleBar;
import com.vlsolutions.swing.docking.DockingConstants.Hide;

/**
 * Extends the Dockkey class with options for title bars. 
 * Therefore it is possible to display the view desktop virtually 
 * without title bar.
 * 
 * @author Andreas Held (based on EPOS by Thasso Griebel)
 *
 */
public class SDockKey extends DockKey{

	protected boolean showTitlebar = true;
	private DockViewTitleBar titlebar;

	public SDockKey() {
		super();
	}

	public SDockKey(String dockKey, String name, String tooltip, Icon icon, Hide hideBorder) {
		super(dockKey, name, tooltip, icon, hideBorder);
	}

	public SDockKey(String dockKey, String name, String tooltip, Icon icon) {
		super(dockKey, name, tooltip, icon);
	}

	public SDockKey(String dockKey, String name, String tooltip) {
		super(dockKey, name, tooltip);
	}

	public SDockKey(String dockKey, String name) {
		super(dockKey, name);
	}

	public SDockKey(String dockKey) {
		super(dockKey);
	}

	/**
	 * Return whether or not the tile bar should be displayed
	 * 
	 * @return
	 * 		whether or not the tile bar should be displayed
	 */
	public boolean isShowTitlebar() {
		return showTitlebar;
	}

	/**
	 * Set whether or not the tile bar should be displayed
	 * 
	 * @param showTitlebar
	 * 		flag whether or not the tile bar should be displayed
	 */
	public void setShowTitlebar(boolean showTitlebar) {
		this.showTitlebar = showTitlebar;
	}

	/**
	 * Sets the title bar to be displayed
	 * 
	 * @param bar
	 * 		title bar to be displayed
	 */
	public void setTitlebar(DockViewTitleBar bar) {
		this.titlebar = bar;
	}

	/**
	 * Return the title bar which should be displayed
	 * 
	 * @return
	 * 		title bar which should be displayed
	 */
	public DockViewTitleBar getTitlebar() {
		return titlebar;
	}
}
