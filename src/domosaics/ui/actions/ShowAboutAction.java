package domosaics.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import domosaics.ui.help.AboutFrame;
import domosaics.ui.io.menureader.AbstractMenuAction;




public class ShowAboutAction extends AbstractMenuAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		if(AboutFrame.instance==null)
		 new AboutFrame();
		else
			if(AboutFrame.instance.getState()==Frame.ICONIFIED){
				AboutFrame.instance.dispose();
				new AboutFrame();		
			} else
			{
				AboutFrame.instance.setVisible(true);
			}
	}
}
