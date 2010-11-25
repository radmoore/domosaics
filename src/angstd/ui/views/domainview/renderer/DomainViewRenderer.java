package angstd.ui.views.domainview.renderer;

import angstd.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import angstd.ui.views.view.renderer.Renderer;

/**
 * Methods a DomainViewRenderer must support. Look into {@link DefaultDomainViewRenderer}
 * for more details.
 * 
 * @author Andreas Held
 *
 */
public interface DomainViewRenderer extends Renderer{

	/**
	 * Set the ArrangementRenderer which should be used to render 
	 * views arrangement components.
	 * 
	 * @param daRenderer
	 * 		ArrangementRenderer to render arrangements
	 */
	public void setArrangementRenderer(ArrangementRenderer daRenderer);
	
	/**
	 * Returns the ArrangementRenderer which is used to render 
	 * views arrangement components.
	 * 
	 * @return
	 * 		ArrangementRenderer to render arrangements
	 */
	public ArrangementRenderer getArrangementRenderer();
}
