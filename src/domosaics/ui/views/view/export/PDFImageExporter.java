package domosaics.ui.views.view.export;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;


import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.view.View;

/**
 * Class PDFImageExporter exports an image in PDF format using the
 * open source iText library which is published under the NPL and LGPL
 * license.
 * 
 * @author Andreas Held
 *
 */
public class PDFImageExporter implements ImageExporter{
	
	/**
	 * @see ImageExporter
	 */
	public void export(View view, File file, GraphicFormats format) {
         try {
        	 java.awt.Rectangle rectangle = view.getViewComponent().getBounds();
             Document document = new Document(new Rectangle((float)rectangle.getWidth(), (float)rectangle.getHeight()));
        	 PdfWriter pdfwriter = PdfWriter.getInstance(document, new FileOutputStream(file));
             document.open();
             PdfContentByte pdfcontentbyte = pdfwriter.getDirectContent();
             PdfTemplate pdftemplate = pdfcontentbyte.createTemplate((float)rectangle.getWidth(), (float)rectangle.getHeight());
             Graphics2D graphics2d = pdftemplate.createGraphics((float)rectangle.getWidth(), (float)rectangle.getHeight(), new DefaultFontMapper());
             view.render(graphics2d);
             graphics2d.dispose();
             pdfcontentbyte.addTemplate(pdftemplate, 0.0F, 0.0F);
             document.close();
         } 
         catch(Exception e) {
        	 MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Error while writing PDF document.");
 			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
         }
	}

}