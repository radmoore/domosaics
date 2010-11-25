package angstd.ui.docking;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockView;
import com.vlsolutions.swing.docking.Dockable;

/**
 * The extended Dockview handles the title bar management which is added to
 * SDockKey. Therefore it is possible to remove the title bar for the
 * view desktop.
 * 
 * @author Andreas Held
 *
 */
public class SDockView extends DockView{
	private static final long serialVersionUID = 1L;

	public SDockView() {
		super();
	}

	public SDockView(Dockable dockable) {
		this(dockable, true);
	}

	public SDockView(Dockable dockable, boolean b) {
		super(dockable, b);
		DockKey k = getDockable().getDockKey();       		
		if(k instanceof SDockKey){
			if(((SDockKey)k).getTitlebar() != null){
				remove(title);
				title = ((SDockKey)k).getTitlebar();
				add(title, BorderLayout.NORTH);
			}
			if(!((SDockKey)k).isShowTitlebar()){
				title.setVisible(false);
			}
		}
        setBorder(BorderFactory.createEmptyBorder());        
	}
}
