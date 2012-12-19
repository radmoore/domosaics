package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.workspace.actions.SweepWorkspaceAction;


public class SweepWorkspaceDir extends AbstractMenuAction {

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		new SweepWorkspaceAction();
		
	}
	
}
