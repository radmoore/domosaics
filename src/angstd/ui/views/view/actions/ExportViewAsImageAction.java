package angstd.ui.views.view.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import angstd.ui.AngstdUI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.tools.Tool;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.manager.DomainLayoutManager.DomainAction;
import angstd.ui.views.view.View;
import angstd.ui.views.view.components.ZoomCompatible;
import angstd.ui.views.view.export.DefaultImageExporter;
import angstd.ui.views.view.export.GraphicFormats;
import angstd.ui.views.view.export.PDFImageExporter;
import angstd.ui.views.view.export.SVGImageExporter;

/**
 * ExportViewAsImageAction exports a view (or the actual focused tool) as
 * image within the format specified by the triggering MenuItems name.
 * <p>
 * IMPORTANT if the formats name is not within the action command (e.g.
 * "BMP" within "...As BMP") this action won't work.
 * <p> 
 * The {@link DefaultImageExporter} is used to export images in PNG, JPG 
 * and BMP. The {@link PDFImageExporter} to export in PDF and 
 * {@link SVGImageExporter} to export in SVG format.
 * <p> 
 * All necessary information about a format is stored within
 * {@link GraphicFormats}.
 * 
 * 
 * @author Andreas Held
 *
 */
public class ExportViewAsImageAction extends AbstractMenuAction implements ZoomCompatible {
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		
		// determine if a tool or a view has to be exported
		View view = ViewHandler.getInstance().getFocussedTool();
		if (view == null) 
			view = ViewHandler.getInstance().getActiveView();
		
		// TODO MOVE this piece of code where it belongs
		boolean oldEnabledState = true;
		if (view instanceof DomainViewI || view instanceof DomainTreeViewI) {
			oldEnabledState = ((DomainViewI) view).getDomainLayoutManager().isEnabled(DomainAction.SHOW_NOTES);
			((DomainViewI) view).getDomainLayoutManager().disable(DomainAction.SHOW_NOTES);
		}

		// resolve the action command into the format and start the export
		if (e.getActionCommand().contains("BMP"))
			export(GraphicFormats.BMP, view);
		else if (e.getActionCommand().contains("JPG"))
			export(GraphicFormats.JPG, view);
		else if (e.getActionCommand().contains("PNG"))
			export(GraphicFormats.PNG, view);
		else if (e.getActionCommand().contains("PDF"))
			export(GraphicFormats.PDF, view);
		else if (e.getActionCommand().contains("SVG"))
			export(GraphicFormats.SVG, view);
		else {
			MessageUtil.showWarning("Angstd couldn't find the right image exporter");
			return;
		}
		
		// TODO cont MOVE this piece of code where it belongs
		if (view instanceof DomainViewI || view instanceof DomainTreeViewI) {
			if (oldEnabledState)
				((DomainViewI) view).getDomainLayoutManager().enable(DomainAction.SHOW_NOTES);
			else
				((DomainViewI) view).getDomainLayoutManager().disable(DomainAction.SHOW_NOTES);
		}
		
		// if its a tool request focus after saving
		if (view instanceof Tool)
			((Tool) view).getToolFrame().requestFocus();
	}
	
	/**
	 * Helper method which starts the file dialog to let the user
	 * choose the destination file and export the image.
	 * 
	 * @param format
	 * 		the determined image format
	 */
	private void export (GraphicFormats format, View view) {
		// open file dialog to choose destination
		File file = FileDialogs.showSaveDialog(AngstdUI.getInstance(), format.getName());
		if (file == null)
			return;
		
		// save the image
		format.getImageExporter().export(view, file, format);
	}
}
