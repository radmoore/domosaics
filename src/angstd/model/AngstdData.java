package angstd.model;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.io.AbstractDataReader;
import angstd.model.io.DataReader;

/**
 * An empty interface indicating objects which can be parsed out of a file.
 * If a class implements this interface (for instance {@link DomainArrangement})
 * it becomes a valid return type for parsers which implement the 
 * {@link DataReader} interface. For details see {@link AbstractDataReader}.
 *  
 * @author Andreas Held
 *
 */
public interface AngstdData {

}
