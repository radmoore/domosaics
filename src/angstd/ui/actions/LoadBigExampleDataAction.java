package angstd.ui.actions;

import java.awt.event.ActionEvent;

import angstd.ui.io.ExampleDatasetLoader;
import angstd.ui.io.menureader.AbstractMenuAction;

/**
 * Loads the big example dataset
 * 
 * @author Andreas Held
 *
 */
public class LoadBigExampleDataAction extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	    
	public void actionPerformed(ActionEvent e) {
		new ExampleDatasetLoader().loadBigSet();
	}
}
