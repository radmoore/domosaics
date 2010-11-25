package angstd.model.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import angstd.model.AngstdData;

/**
 * Abstract class AbstractDataWriter is the basic implementation
 * for the {@link DataWriter} interface. A BufferedWriter object is created 
 * for a file which is then passed to the abstract method writeData().
 * This method must be implemented by a subclass and should be responsible
 * for the actual writing.
 * <p>
 * Because the type of data which has to be written into a file 
 * is generic the requested type must implement the empty {@link AngstdData} 
 * interface.
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractDataWriter<T extends AngstdData> implements DataWriter<T> {

	/**
	 * See interface {@link DataWriter}.
	 */
	public void write(File file, T[] data) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            write(out, data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * See interface {@link DataWriter}.
	 */
	public abstract void write(BufferedWriter out, T[] data);
}
