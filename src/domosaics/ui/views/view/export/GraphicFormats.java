package domosaics.ui.views.view.export;

import java.awt.image.BufferedImage;

/**
 * Enumeration GraphicFormats defines all supported formats for 
 * exporting a view.
 * <p>
 * A format is defined by its extension, e.g. BMP, the class which 
 * defines the exporting methods for the specific format, 
 * e.g. {@link DefaultImageExporter} and the resulting image type
 * (e.g. BufferedImage.TYPE_INT_RGB).
 * <p>
 * Therefore a view can be exported as BMP, i.e. just by calling
 * GraphicFormats.BMP.getImageExporter().export(treeView, file);
 * within an action.
 * 
 * @author Andreas Held
 *
 */
public enum GraphicFormats {
	
	// supported formats for export
	PNG("PNG", new DefaultImageExporter(), BufferedImage.TYPE_INT_ARGB),
	JPG("JPG", new DefaultImageExporter(), BufferedImage.TYPE_INT_RGB),
	BMP("BMP", new DefaultImageExporter(), BufferedImage.TYPE_INT_RGB),
	PDF("PDF", new PDFImageExporter(), null),
	SVG("SVG", new SVGImageExporter(), null);
	
	/** the formats name */
	private String name;
	
	/** Class for exporting the specific format */
	private ImageExporter exporter;
	
	/** the images format, e.g. BufferedImage.TYPE_INT_RGB */
	private Integer format;


	/**
	 * Basic constructor for a GraphicFormat.
	 * 
	 * @param name 
	 * 		formats name
	 * @param exporter 
	 * 		format specific exporter
	 * @param format 
	 * 		format specific exporter
	 */
	private GraphicFormats(String name, ImageExporter exporter, Integer format){
		this.name = name;
		this.exporter = exporter;
		this.format = format;
	} 
	
	/**
	 * Return the formats name
	 * 
	 * @return 
	 * 		format name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the format specific image exporter
	 * 
	 * @return 
	 * 		format specific image exporter
	 */
	public ImageExporter getImageExporter() {
		return exporter;
	}
	
	/**
	 * Return the images format type, e.g. BufferedImage.TYPE_INT_RGB
	 * 
	 * @return
	 * 		the images format type, e.g. BufferedImage.TYPE_INT_RGB
	 */
	public Integer getFormat() {
		return format;
	}
	
}
