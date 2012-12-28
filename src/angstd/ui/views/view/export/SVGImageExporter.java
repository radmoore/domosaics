package angstd.ui.views.view.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import angstd.model.configuration.Configuration;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.View;



/**
 * Class SVGImageExporter exports an image in SVG format using the
 * open source batik library which is published under the Apache 
 * license.
 * 
 * @author Andreas Held
 *
 */
public class SVGImageExporter implements ImageExporter{
	
	/**
	 * @see ImageExporter
	 */
	public void export(View view, File file, GraphicFormats format) {
		// Get a DOMImplementation
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		
		// Create an instance of org.w3c.dom.Document
		Document document = domImpl.createDocument(null, "svg", null);
	
		// Create an instance of the SVG Generator
		SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		svgGenerator.setClip(0,0, view.getViewComponent().getWidth(), view.getViewComponent().getHeight());
		
		// set the precision to avoid a null pointer exception in Batik 1.5
		svgGenerator.getGeneratorContext().setPrecision(6);
		
		// Ask the view to render into the SVG Graphics2D implementation
        view.render(svgGenerator);
	
		// Finally, stream out SVG to a file using UTF-8 character to byte encoding
		boolean useCSS = true;
		try {
			FileOutputStream stream = new FileOutputStream(file);
			Writer out = new OutputStreamWriter(stream, "UTF-8");
			svgGenerator.stream(out, useCSS);
			out.close();
			stream.close();
		}catch (IOException e) {
       	 	Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("Error while writing PDF document.");
		}

	}

}
