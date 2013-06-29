package domosaics.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.webservices.interproscan.ui.AnnotatorFrame;




/**
 * Opens a new AnnotatorFrame
 * 
 * @author Andreas Held
 *
 */
public class ShowAnnotatorAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	protected AnnotatorFrame annotatorFrame;

	public void actionPerformed(ActionEvent e) {
		annotatorFrame = AnnotatorFrame.getFrame();
	}
}
