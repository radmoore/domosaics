package angstd.model.io;

import java.io.BufferedWriter;
import java.io.File;

import angstd.model.AngstdData;
import angstd.model.arrangement.io.XdomWriter;

/**
 * Interface DataWriter defines the methods to write data into a file. <br>
 * {@link AbstractDataWriter} is the basic implementation for this 
 * interface and special parser classes like {@link XdomWriter} should
 * extend this AbstractDataWriter.
 * <p>
 * Because the type of data which has to be written into a file 
 * is generic the requested type must implement the empty {@link AngstdData} 
 * interface.
 * 
 * @author Andreas Held
 *
 */
public interface DataWriter <T extends AngstdData> {

	/**
	 * Writes the data into a file.
	 * 
	 * @param file
	 * 		file to be created
	 * @param data
	 * 		the data to be written
	 */
	public void write(File file, T[] data);
	
	/**
	 * In this method the actual parsing is done. All other
	 * methods just create the BufferedWriter which is then passed to
	 * this method.
	 * 
	 * @param out
	 * 		the created BufferedWriter used to write into the file
	 * @param data
	 * 		the data to be written
	 */
	public abstract void write(BufferedWriter out, T[] data);
	
	
	/**
	 * Same as write(), except that the data is translated into
	 * byte[] and the line is wrapped after wrapAfter chars have been 
	 * written  
	 * 
	 * @param out
	 * 		the created BufferedWriter used to write into the file
	 * @param data
	 * 		the data to be written
	 * @param wrapAfter
	 * 		number of charaters of data that are written before the line is wrapped 
	 */
	public abstract void wrappedWrite(BufferedWriter out, T[] data, int wrapAfter);
	
}
