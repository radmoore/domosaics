package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * Changes the number of characters after which a tree node label
 * is truncated. Fires a structural change after the adjustment.
 * 
 * @author Andrew Moore <radmoore@uni-muenster.de>
 *
 */
public class ChangeDefaultLabelTruncationLength extends AbstractMenuAction {
	
	private static final long serialVersionUID = 1L;
    
	@Override
	public void actionPerformed(ActionEvent e) {
		
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		int labelLength = Configuration.getInstance().getLabelTruncationLength();
				
		// open change label dialog
		Object ret  = 	JOptionPane.showInputDialog(
						DoMosaicsUI.getInstance(), 
						"Label Truncation Length", 
						"Truncation Length", 
						JOptionPane.DEFAULT_OPTION, 
						null, 
						null, 
						labelLength);
		
		if (ret != null) {
			try {
				labelLength = Integer.parseInt(ret.toString());
				Configuration.getInstance().setLabelTruncationLength(labelLength);
				view.getTreeLayoutManager().structuralChange();
			}
			catch (NumberFormatException nfe) {
				MessageUtil.showWarning("The length must be a number!");
			}
		}
	}	

	
	
}
