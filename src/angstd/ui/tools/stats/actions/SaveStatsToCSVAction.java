package angstd.ui.tools.stats.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;


import angstd.model.arrangement.ArrangementManager;
import angstd.model.configuration.Configuration;
import angstd.ui.AngstdUI;
import angstd.ui.tools.stats.Stats;
import angstd.ui.util.FileDialogs;

import com.csvreader.CsvWriter;


public class SaveStatsToCSVAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	protected ArrangementManager manager;

	public SaveStatsToCSVAction (ArrangementManager manager) {
		super();
		this.manager = manager;
		putValue(Action.NAME, "Save As CSV File");
		putValue(Action.SHORT_DESCRIPTION, "Saves the stats to a csv formatted file");
	}
	
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.showSaveDialog(AngstdUI.getInstance(), "CSV");
		if (file == null)
			return;
		
		try {
			CsvWriter csvWriter = new CsvWriter(new BufferedWriter (new FileWriter(file)), ';');
			csvWriter.setRecordDelimiter('\n');
			
			String[] vals = new String[2];
			for (Stats stat: Stats.values()) {
				vals[0] = stat.getLabel();
				vals[1] = stat.getVal(manager);
				csvWriter.writeRecord(vals, true);
			}
			
			csvWriter.flush();
			csvWriter.close();
		} 
		catch (IOException e1) {
			Configuration.getLogger().debug(e1.toString());	
		}
	}
		
}
		
		