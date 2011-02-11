package angstd.model.arrangement.io;

import java.io.File;

import angstd.model.arrangement.DomainArrangement;

public class ArrangementImporterUtil {

	public static DomainArrangement[] importData(File file) {
		
		DomainArrangement[] arrangementSet = null;
		
		if (XdomReader.checkFormat(file))
			arrangementSet  = new XdomReader().getDataFromFile(file); 
		else {
			if (HmmOutReader.checkFileFormat(file));
				arrangementSet = new HmmOutReader().getDataFromFile(file);
		}
		return arrangementSet;
		
	}
	
}
