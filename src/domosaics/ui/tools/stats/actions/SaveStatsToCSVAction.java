package domosaics.ui.tools.stats.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;


import com.csvreader.CsvWriter;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.tools.stats.Stats;
import domosaics.ui.util.FileDialogs;

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
		File file = FileDialogs.showSaveDialog(DoMosaicsUI.getInstance(), "CSV");
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
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e1);
			else			
				Configuration.getLogger().debug(e1.toString());
		}
	}
		
}
		
		