package angstd.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.webservices.interproscan.ui.AnnotatorFrame;



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
		if (annotatorFrame == null || !annotatorFrame.isVisible())
			annotatorFrame = new AnnotatorFrame();
		else 
			annotatorFrame.setState(Frame.NORMAL);
	}
}
