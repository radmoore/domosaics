package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.workspace.actions.SweepWorkspaceAction;



public class SweepWorkspaceDir extends AbstractMenuAction {

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		new SweepWorkspaceAction();
		
	}
	
}
