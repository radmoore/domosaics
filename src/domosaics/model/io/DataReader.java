package domosaics.model.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import domosaics.model.DoMosaicsData;
import domosaics.model.arrangement.io.XdomReader;




/**
 * Interface DataReader defines the methods to retrieve data from
 * a stream, file or a string. <br>
 * {@link AbstractDataReader} is the basic implementation for this 
 * interface and special parser classes like {@link XdomReader} should
 * extend this AbstractDataReader.
 * <p>
 * Because the return type of the methods are generic the requested type
 * must implement the empty {@link DoMosaicsData} interface.
 * 
 * @author Andreas Held
 *
 */
public interface DataReader <T extends DoMosaicsData>{
	
	/**
	 * Retrieves data from a stream. This comes in handy when parsing 
	 * data from within a jar file.
	 * 
	 * @param is
	 * 		stream containing data
	 * @return
	 * 		parsing result from the stream
	 */
	public T[] getDataFromStream(InputStream is);
	
	/**
	 * Retrieves data from a file.
	 * 
	 * @param file
	 * 		file containing data
	 * @return
	 * 		parsing result from the file
	 */
	public T[] getDataFromFile(File file);

	/**
	 * Retrieves data from a string.
	 * 
	 * @param dataStr
	 * 		string containing data
	 * @return
	 * 		parsing result from the string
	 */
	public T[] getDataFromString(String dataStr);
	
	/**
	 * In this method the actual parsing is done. All other
	 * methods just create the Inputstream which is then passed to
	 * this method.
	 * 
	 * @param r
	 * 		Reader generated from a stream, file or string
	 * @return
	 * 		parsing result
	 * @throws IOException
	 * 		exceptions which may occur during the parsing progress.
	 */
	public T[] getData(Reader r) throws IOException;
}
