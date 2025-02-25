package domosaics.ui.views.domainview.renderer.additional;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.renderer.Renderer;

public class NoteMarkRenderer implements Renderer {
	
	private static final String NOTEMARKPATH = "../../../../resources/notemarker.jpg";

	/** the domain view showing the note marks */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new NoteMarkRenderer
	 * 
	 * @param view
	 * 		view showing the note marks
	 */
	public NoteMarkRenderer(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Renders the note markings for all arrangements which have a 
	 * note associated with them.
	 */
	@Override
	public void render(Graphics2D g) {
		// don't draw if no notes are assigned
		if (!view.getNoteManager().isActive())
			return;
		
		if (!view.getDomainLayoutManager().isShowNotes())
			return;
		
		if (view instanceof DomainTreeViewI && ((DomainTreeViewI) view).getCSAInSubtreeManager().isActive())
			return;
		
		// load note marker
		InputStream is = this.getClass().getResourceAsStream(NOTEMARKPATH);
		ImageIcon logo = null;
		try {
			logo = new ImageIcon(ImageIO.read(is));
		} 
		catch (IOException e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return;
		}
		
//		DomainArr[] toDraw = view.getNoteManager().getArrangements();
//		for (ArrangementComponent dac : toDraw) {
		
		for (DomainArrangement da : view.getDaSet()) {
			
			if (view.getNoteManager().getNote(da) == null)
				return;
		
			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(da);
		
			if (!dac.isVisible())
				continue;

			logo.paintIcon(null, g, dac.getX()-10, dac.getTopLeft());
		}
	}

}
