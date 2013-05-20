package domosaics.model.arrangement.io;

import java.io.File;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;




public class ArrangementImporterUtil {

	public static DomainArrangement[] importData(File file) {
		
		DomainArrangement[] arrangementSet = null;
		if (new XdomReader().checkFormat(file))
			arrangementSet  = new XdomReader().getDataFromFile(file); 
		else if (HmmOutReader.checkFileFormat(file)) {
			arrangementSet = new HmmOutReader().getDataFromFile(file);
		}
		else {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Failed to read file - unknown file format");
			return null;
		}
			
		return arrangementSet;
		
	}
	
}
