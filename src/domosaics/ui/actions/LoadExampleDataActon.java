package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.ExampleDatasetLoader;
import domosaics.ui.io.menureader.AbstractMenuAction;




/**
 * Loads example dataset into the current workspace
 * 
 * @author <a href="http://radm.info">Andrew Moore</a>
 *
 */
public class LoadExampleDataActon extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	@Override
	public void actionPerformed(ActionEvent e) {
		new ExampleDatasetLoader().loadExample();
	}

}

