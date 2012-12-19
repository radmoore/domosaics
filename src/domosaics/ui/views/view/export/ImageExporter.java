package domosaics.ui.views.view.export;

import java.io.File;

import domosaics.ui.views.view.View;


/**
 * Interface ImageExporter defines all methods which are needed to
 * export views in various formats.
 * 
 * @author Andreas Held
 *
 */
public interface ImageExporter {

	/**
	 * Exports a view into a given file.
	 * 
	 * @param view 
	 * 		the view to export
	 * @param file 
	 * 		the file in which the view will be exported
	 * @param format 
	 * 		the format in which the view should be exported
	 */
	public void export(View view, File file, GraphicFormats format);

}
