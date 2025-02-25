package domosaics.model.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import domosaics.model.DoMosaicsData;
import domosaics.model.configuration.Configuration;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;




/**
 * Abstract class AbstractDataReader is the basic implementation
 * for the {@link DataReader} interface. A Reader object is created from 
 * a file, string or stream which is passed to the abstract method getData().
 * This method must be implemented by a subclass and should be responsible
 * for the actual parsing.
 * <p>
 * Because the return type of the methods are generic the requested type
 * must implement the empty {@link DoMosaicsData} interface.
 * 
 * @author Andreas Held
 * 
 */
public abstract class AbstractDataReader <T extends DoMosaicsData> implements DataReader<T>{
	
	/**  get the line separator for the running system */
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 * @see DataReader
	 */
	@Override
	public T[] getDataFromStream(InputStream is) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			T[] data = getData(in);
			in.close();
			return data;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Reading file aborted");
		} 
		return null;
	}
	
	/**
	 * @see DataReader
	 */
	@Override
	public T[] getDataFromFile(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			T[] data = getData(in);
			in.close();
			return data;
		} 
		catch (FileNotFoundException fnfe) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Could not find file:"+ file.getPath());
		} 
		catch (IOException ioe) {
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Could not read file:"+ file.getName());
		}
		return null;
	}
	
	/**
	 * @see DataReader
	 */
	@Override
	public T[] getDataFromString(String dataStr) {
		try {
			BufferedReader in = new BufferedReader(new StringReader(dataStr));
			T[] data = getData(in);
			in.close();
			return data;
		} 
		catch (IOException ioe) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(ioe);
			else			
				Configuration.getLogger().debug(ioe.toString());
			MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Reading file aborted");
		}
		return null;
	}
	
	/**
	 * @see DataReader
	 */
	@Override
	public abstract T[] getData(Reader reader) throws IOException;
}
