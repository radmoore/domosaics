package domosaics.model.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import domosaics.model.DoMosaicsData;
import domosaics.model.configuration.Configuration;




/**
 * Abstract class AbstractDataWriter is the basic implementation
 * for the {@link DataWriter} interface. A BufferedWriter object is created 
 * for a file which is then passed to the abstract method writeData().
 * This method must be implemented by a subclass and should be responsible
 * for the actual writing.
 * <p>
 * Because the type of data which has to be written into a file 
 * is generic the requested type must implement the empty {@link DoMosaicsData} 
 * interface.
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractDataWriter<T extends DoMosaicsData> implements DataWriter<T> {

	/**
	 * See interface {@link DataWriter}.
	 */
	public void write(File file, T[] data) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            write(out, data);
            out.close();
        } 
        catch (IOException e) {
        	Configuration.getLogger().debug(e.toString());
        }
    }
	
	/**
	 * See interface {@link DataWriter}.
	 */
	public void wrappedWrite(File file, T[] data, int wrapAfter) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            wrappedWrite(out, data, wrapAfter);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * See interface {@link DataWriter}.
	 */
	public abstract void write(BufferedWriter out, T[] data);
	
	//public abstract void wrappedWrite(BufferedWriter out, T[] data, int wrapAfter);
}
