package angstd.ui.views.view.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import angstd.model.configuration.Configuration;
import angstd.ui.util.DigestUtil;
import angstd.ui.views.view.View;

public abstract class ViewExporter<V extends View> {
	
	public abstract void write(BufferedWriter out, V view);

	// TODO Remove all following functions since not used!
	/**
	 * Writes an xml tag with a specified id to the specified output 
	 * stream. (e.g. <DATA>).
	 * 
	 * @param out
	 * 		the output stream used to print
	 * @param tabs
	 * 		the number of tabs used to shift the starting point of the line
	 * @param id
	 * 		the tags id
	 * @param open
	 * 		flag whether or not its a tag opening or a tag closing
	 * @throws IOException
	 * 		the io exception which may occur
	 */
	protected void writeTag(BufferedWriter out, int tabs, String id, boolean open) throws IOException {
		for (int i = 0; i < tabs; i++)
			out.write("\t");
		if (open)
			out.write("<"+id+">\r\n"); 		// write tag opening
		else {
			out.write("</"+id+">\r\n\r\n"); // write closing tag 	
			out.flush(); 
		}
	}
	
	/**
	 * Writes a parameter line to the specified output stream. <br>
	 * (e.g. <parameter id="VIEWNAME" value="SequenceView 1"/> .
	 * 
	 * @param out
	 * 		the output stream used to print
	 * @param tabs
	 * 		the number of tabs used to shift the starting point of the line
	 * @param id
	 * 		the parameters name
	 * @param param
	 * 		the parameters value
	 * @throws IOException
	 * 		the io exception which may occur
	 */
	protected void writeParam(BufferedWriter out, int tabs, String id, String param) throws IOException {
		for (int i = 0; i < tabs; i++)
			out.write("\t");							// tab it in
		out.write("<parameter id=\""+id+"\" ");		// write parameter id
		out.write("value=\""+param.toString()+"\"");// write parameter value
		out.write("/>\r\n");						// write line ending
	}
	
	protected void writeParam2(BufferedWriter out, int tabs, String id, String param, String param2) throws IOException {
		for (int i = 0; i < tabs; i++)
			out.write("\t");							// tab it in
		out.write("<parameter id=\""+id+"\" ");		// write parameter id
		out.write("value=\""+param.toString()+"\" ");// write parameter value
		out.write("value2=\""+param2.toString()+"\"");// write parameter value 2
		out.write("/>\r\n");						// write line ending
	}
	
	// r - g - b - a
	protected static String color2str(Color color){
		return new String(color.getRed()+"-"+color.getGreen()+"-"+color.getBlue()+"-"+color.getAlpha());
	}
	
	// family - name - style - size
	protected static String font2str(Font font){
		return new String(font.getFamily()+"-"+font.getName()+"-"+font.getStyle()+"-"+font.getSize());
	}
	
	// width - cap - join - miterlimit - dash - dash_phase
	protected static String stroke2str(BasicStroke stroke) {
		StringBuffer strokeStr = new StringBuffer();
		
		// add  width - cap - join - miterlimit -
		strokeStr.append(stroke.getLineWidth()
	      				+"-"+stroke.getEndCap()
	      				+"-"+stroke.getLineJoin()
	      				+"-"+stroke.getMiterLimit());
		
		// now add the dash
		float[] dash = stroke.getDashArray();
		strokeStr.append("-[");
		if (dash != null) {
			for(int i = 0; i < dash.length-1; i++)
				strokeStr.append(dash[i]+",");
			strokeStr.append(dash[dash.length-1]);
		}
		strokeStr.append("]");	
			
		//and close with the dashphase
		strokeStr.append("-"+stroke.getDashPhase());
		return strokeStr.toString();
	}
}
