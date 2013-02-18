package domosaics.ui.tools.distmatrix.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



import com.csvreader.CsvWriter;

import domosaics.model.configuration.Configuration;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.distmatrix.DistMatrixView;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.views.ViewType;


public class SaveMatrixToCSVAction  extends AbstractMenuAction {
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		DistMatrixView view = (DistMatrixView) ViewHandler.getInstance().getTool(ViewType.DISTANCEMATRIX);
		
		File file = FileDialogs.showSaveDialog(view, "CSV");
		if (file == null)
			return;
		
		double[][] data = view.getData();
		String[] colNames = view.getColNames();
		final int N = colNames.length+1;
		String[] vals = new String[N];
		
		
		try {
			CsvWriter csvWriter = new CsvWriter(new BufferedWriter (new FileWriter(file)), ';');
			csvWriter.setRecordDelimiter('\n');

			// first line
			vals[0] = "";
			for (int i = 0; i < colNames.length; i++)
				vals[i+1] = colNames[i];
			csvWriter.writeRecord(vals, true);
			
			// all other lines
			for (int i = 0; i < data.length; i++) {
				vals = new String[N];
				vals[0] = colNames[i];
				
				for (int j = 0; j < data[i].length; j++) {
					if (data[i][j] == Double.NEGATIVE_INFINITY)
						vals[j+1] = "";
					else
						vals[j+1] = ""+data[i][j];
				}

				csvWriter.writeRecord(vals, true);
			}
			
			csvWriter.flush();
			csvWriter.close();
		} 
		catch (IOException e1) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e1);
			else			
				Configuration.getLogger().debug(e1.toString());
		}
	}
		
		
		
//		try {
//			CsvWriter csvWriter = new CsvWriter(new FileWriter(file), ',');
//			
//			// first column
//			StringBuffer col = new StringBuffer(" ,");
//			for (String s : colNames)
//				col.append(s+",");
//			
//			csvWriter.write(col.toString());
//			
//			// all other columns
//			for (int i = 0; i < data.length; i++) {
//				col = new StringBuffer(colNames[i]+",");
//				for (int j = 0; j < data[i].length; j++) {
//					if (data[i][j] == Double.NEGATIVE_INFINITY)
//						col.append(" ,");
//					else
//						col.append(data[i][j]+",");
//				}
//				csvWriter.write(col.toString());
//			}
//			
//			csvWriter.flush();
//			csvWriter.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//	}

}
