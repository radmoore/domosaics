package domosaics.ui.views.domainview.manager;

import java.util.HashMap;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.view.manager.DefaultViewManager;


public class NoteManager extends DefaultViewManager {

	/** mapping between arrangements and their notes */
	protected Map<DomainArrangement, String> da2note;
	
	
	/**
	 * Constructor for a new note manager.
	 * 
	 */
	public NoteManager() {
		da2note = new HashMap<DomainArrangement, String>();
	}
	
	/**
	 * resets the note mapping
	 * 
	 */
	public void reset() {
		da2note.clear();
	}
	
	/**
	 * Returns all arrangements which have a note associated 
	 * to them
	 * 
	 * @return
	 * 		all arrangements with an associated note
	 */
	public DomainArrangement[] getArrangements() {
		return da2note.keySet().toArray(new DomainArrangement[da2note.keySet().size()]);
	}
	
	/**
	 * Sets a new note for a specified arrangement
	 * 
	 * @param dac
	 * 		the arrangement which has to be associated with a note
	 * @param note
	 * 		the note to associate
	 */
	public void setNode(DomainArrangement da, String note) {
		if (note.trim().isEmpty() && da2note.get(da) != null)
			da2note.remove(da);
		else
			da2note.put(da, note);
	}
	
	/**
	 * Removes the note for the specified arrangement
	 * 
	 * @param dac
	 * 		the arrangement which note has to be removed
	 */
	public void removeNote(DomainArrangement da) {
		if (da2note.get(da) != null)
			da2note.remove(da);
	}
	
	
	/**
	 * Returns the associated note for the specified arrangement 
	 * 
	 * @return
	 * 		associated note for the specified arrangement
	 */
	public String getNote(DomainArrangement da) {
		return da2note.get(da);
	}

	/**
	 * Flag indicating whether or not the manager has notes stored
	 * 
	 * @return
	 * 		whether or not the manager has notes stored
	 */
	public boolean isActive() {
		return da2note.size() != 0;
	}



	
}

