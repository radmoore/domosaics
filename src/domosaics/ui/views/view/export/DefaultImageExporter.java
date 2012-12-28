package domosaics.ui.views.view.export;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import domosaics.model.configuration.Configuration;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.view.View;





/**
 * DefaultImageExporter is the basic implementation for the ImageExporter 
 * interface allowing the export in PNG, BMP and JPG format.
 * 
 * @author Andreas Held
 *
 */
public class DefaultImageExporter implements ImageExporter{
	
	/**
	 * Creates an image using the specified type within an implementing subclass.
	 * 
	 * @param width 
	 * 		the views width
	 * @param height 
	 * 		the views height
	 * @param format 
	 * 		the resulting images format type, e.g. BufferedImage.TYPE_INT_RGB
	 * @return 
	 * 		a new empty buffered image in a specific format.
	 */
	public BufferedImage createImage (int width, int height, int format) {
		return new BufferedImage(width, height, format);
	}


	/**
	 * @see ImageExporter
	 */
	public void export(View view, File file, GraphicFormats format) {
		// create image using the complete views size
		BufferedImage img = createImage(
				view.getViewComponent().getWidth(), 
				view.getViewComponent().getHeight(),
				format.getFormat()
		);
		
		// get graphics object for this empty image and set clipping to the view size
		Graphics g = img.getGraphics();
		g.setClip(0,0, view.getViewComponent().getWidth(), view.getViewComponent().getHeight());
		
		// render the view
		view.render(g);
		
		// Write image to file
		ImageWriter writer = ImageIO.getImageWritersByFormatName(format.getName()).next();
		try {
			ImageOutputStream stream = ImageIO.createImageOutputStream(file);
			writer.setOutput(stream);
			writer.write(img);
			writer.dispose();
			stream.close();
		} 
		catch (IOException e) {
       	 	Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("Unable to export image to " + file.getName());
			writer.abort();
		}
		
	}

}
