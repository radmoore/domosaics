package angstd.model.arrangement.io;

import java.io.File;

import angstd.model.arrangement.DomainArrangement;

public class ArrangementImporterUtil {

	public static DomainArrangement[] importData(File file) {
		
		DomainArrangement[] arrangementSet = null;
		
		if (XdomReader.checkFormat(file))
			arrangementSet  = new XdomReader().getDataFromFile(file); 
		else {
			System.out.println("Not an xdom file, checking format");
			if (HmmOutReader.checkFileFormat(file));
				System.out.println("Readig hmmout file... ");
				arrangementSet = new HmmOutReader().getDataFromFile(file);	
		}
		
		return arrangementSet;
		
	}
	
}
