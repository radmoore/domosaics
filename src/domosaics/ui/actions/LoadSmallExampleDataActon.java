package domosaics.ui.actions;

import java.awt.event.ActionEvent;

import domosaics.ui.io.ExampleDatasetLoader;
import domosaics.ui.io.menureader.AbstractMenuAction;


/**
 * Loads the big small example dataset
 * 
 * @author Andreas Held
 *
 */
public class LoadSmallExampleDataActon extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		new ExampleDatasetLoader().loadSmallSet();
	}

}

