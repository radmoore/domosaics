package angstd.ui.views.domaintreeview.layout;

import java.util.List;

import angstd.ui.views.domaintreeview.manager.CSAInSubtreeManager;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.treeview.components.NodeComponent;

/**
 * CSAModeDomainTreeLayout is the layout class used to layout 
 * a domain tree view whenever at least one node is collapsed 
 * within csa mode.
 * 
 * @author Andreas Held
 *
 */
public class CSAModeDomainTreeLayout extends DefaultDomainTreeLayout {
	
	/**
	 * Does a normal layout of the domain arrangements followed by a relayout
	 * for all arrangements being collapsed (but remained visible) during
	 * a csa node collapse.
	 */
	public void layoutArrangements(int x, int y, int width, int height) {
		// first do a normal layout
		super.layoutArrangements(x, y, width, height);

		// then relayout the arrangements of interest
		CSAInSubtreeManager csaManager = view.getCSAInSubtreeManager();
		List<NodeComponent> csaNodes =  csaManager.getCSAnodes();
		
		for (int i = 0; i < csaNodes.size(); i++) {
			if (!csaNodes.get(i).isVisible())
				continue;

			x = (int) csaManager.getSubtreeBounds(csaNodes.get(i)).getMaxX();
			y = (int) csaManager.getSubtreeBounds(csaNodes.get(i)).getY();
			
			
			// change the start position so its centered to the subtree height
			int space_BetweenDAs = (int) (getDomainParams().offsetY* height) - getDomainParams().da_height;
			int maxHeight = (int) csaManager.getSubtreeBounds(csaNodes.get(i)).height;
			int visible = csaManager.getArrangements(csaNodes.get(i)).size();
			int neededHeight = (visible-1) * space_BetweenDAs + visible * getDomainParams().da_height;
			int killSPace = getDomainParams().da_height /2 + (maxHeight - neededHeight) / 2;

			int border = y+killSPace;
			int dasDone = 0;
			
			for (ArrangementComponent dac : csaManager.getArrangements(csaNodes.get(i))) {
				y = border + (int) (getDomainParams().offsetY * dasDone * height);
				layoutArrangement(dac, x, y, width, height);
				dasDone++;
			}
		
		}
	}
	
}
