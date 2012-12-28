package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.help.AboutFrame;
import angstd.ui.io.menureader.AbstractMenuAction;



public class ShowAboutAction extends AbstractMenuAction {

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		new AboutFrame().setVisible(true);
		
	}
}
