package domosaics.ui.views.domainview.layout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.manager.DomainShiftManager;
import domosaics.ui.views.view.View;




/**
 * AbstractDomainLayout provides the actual layout methods and just uses
 * methods from implementing subclasses to get arrangement bounds and so
 * on. For instance in the proportional view the domains have a different size
 * than in the unproportional view. The implementing subclasses
 * provide this layouter class with the needed information.
 * <p>
 * The space needed for the layout is calculated in layoutContainer.
 * The layout bounds are only the actual space for layouting the
 * arrangements not their labels. The layout process is started by
 * triggering layoutArrangements().
 * <p>
 * This class has a inner class ArrangementParam which calculates needed
 * attributes to do the layout properly.
 * <p>
 * Its also possible to layout single arrangements by using
 * layoutArrangement directly. This is used for instance in
 * {@link DomainShiftManager}.
 * 
 * 
 * @author Andreas Held
 *
 */
public abstract class AbstractDomainLayout implements DomainLayout{
	
	/** the view to be layouted */
	protected DomainViewI view;	

	/** bounds representing the complete space used for layouting arrangements (without labels). */
	protected Rectangle layoutBounds;

	/** parameter used during the layout process */
	protected ArrangementParameter param = new ArrangementParameter();

	
	/* ********************************************************** *
	 * 						GENERAL METHODS						  *
	 * ********************************************************** */
	
	/**
	 * Sets the view on which this layout should be used on.
	 * 
	 * @param view 
	 * 		component on which the layout is done
	 */
	@Override
	public void setView(View view) {
		this.view = (DomainViewI) view;
	}
	
	/**
	 * @see DomainLayout
	 */
	@Override
	public int getMaxLen() {
		return param.maxLen;
	}
	
	/* ********************************************************** *
	 * 				ABSTRACT METHODS NEEDED IN SUBCLASSES		  *
	 * ********************************************************** */
	
	/**
	 * Return the start x coordinate for the specified domain.
	 * 
	 * @param dom
	 * 		the domain from which the x coordinate is requested
	 * @return
	 * 		the x coordinate for the specified domain
	 */
	public abstract int getDomainX(Domain dom);
	
	/**
	 * Return the width for the specified domain.
	 * 
	 * @param dom
	 * 		the domain from which the width is requested
	 * @return
	 * 		the width for the specified domain
	 */
	public abstract int getDomainWidth(Domain dom);
	   

	/**
	 * Return the width for the specified arrangement.
	 *    
	 * @param da
	 * 		the arrangement from which the width is requested
	 * @return
	 * 		the width for the specified arrangement
	 */
	public abstract int getArrangementWidth(DomainArrangement da);
	
	/**
	 * Return the shifted position of the arrangement or zero if
	 * the shift is not supported in the layout.
	 * 
	 * @param da
	 * 		the arrangement from which the x coordinate is requested
	 * @return
	 * 		the x coordinate for the specified arrangement
	 */
	public abstract int getArrangementX(ArrangementComponent dac);
	
	/* ********************************************************** *
	 * 							LAYOUTING METHODS		 		  *
	 * ********************************************************** */
	
	/**
	 * Calculates the layout area within the parent component and takes
	 * its insets into account. The drawing area is the complete
	 * area without the parents insets and without the space which is
	 * needed to draw the labels.
	 * 
	 * @param parent 
	 * 		container to be layouted
	 */
	@Override
	public void layoutContainer(Container parent) {	
		param.init();				// preliminary init
		
		// set the views height
	    int viewHeight = param.visible * (MIN_DA_HEIGHT+DA_SPACE)+20; 
	    view.setNewViewHeight(viewHeight);

		// now calculate the needed space to draw the biggest arrangement */
		int neededWidth = param.maxLen+param.maxLabelWidth+parent.getInsets().left+parent.getInsets().right+40;

		// if necessary resize the view 
		if (!view.getDomainLayoutManager().isFitDomainsToScreen()) 
			view.setNewViewWidth(neededWidth);
		else
			view.setNewViewWidth(0);
		
		// get the views insets and take them into account 
		Dimension viewSize = parent.getSize();
		Insets viewInsets = parent.getInsets();
		
		// get width and height without insets
		int width = viewSize.width - viewInsets.left - viewInsets.right;
		int height = viewSize.height - viewInsets.top - viewInsets.bottom;
		
		// heres the area to layout the arrangements (inclusive labels)
		Rectangle viewArea = new Rectangle (viewInsets.left, viewInsets.top, width, height);

		// calculate extra space e.g. for labels or the ruler renderer
		Insets extraSpace = getInsets();
		
		// finally calculate the layout area
		Rectangle layoutBounds = new Rectangle(viewArea.x, viewArea.y, viewArea.width, viewArea.height);
		layoutBounds.x += extraSpace.left;
		layoutBounds.y += extraSpace.top;
		layoutBounds.width -= (extraSpace.left + extraSpace.right);
		layoutBounds.height -= (extraSpace.top + extraSpace.bottom);
		setDomainBounds(layoutBounds);
		
		// and start the layouting within this area
		layoutArrangements(getDomainBounds().x, getDomainBounds().y, getDomainBounds().width, getDomainBounds().height);
	}

	/**
	 * Calculate the extra insets for the view on which no arrangements
	 * are layouted. Such as label space and the amino acid ruler if 
	 * present.
	 * 
	 * @return
	 * 		extra insets for the view on which no arrangements are layouted
	 */
	protected Insets getInsets() {
		// insets of the parent
        Insets pInsets = view.getViewComponent().getInsets();	

		// calculate the maximal DA height to display all DAs within the domView size
		double da_height = (view.getViewComponent().getHeight() - pInsets.top - pInsets.bottom)/ (double) param.visible;

		Insets insets = new Insets(0,0,0,0);
		insets.left = param.maxLabelWidth;
		insets.top = (int) (da_height/2.0) + 10;
		insets.bottom = (view.getDomainLayoutManager().isShowLineal()) ? 10 : 0;
		insets.right = (view.getDomainLayoutManager().isCollapseSameArrangements()) ? 40 : 0;
		insets.right = (view.getDomainLayoutManager().isCollapseBySimilarity()) ? 40 : insets.right;
		
		return insets;
	}
	
	/**
	 * @see Domainlayout
	 */
    @Override
	public void layoutArrangements(int x, int y, Dimension size){
    	layoutArrangements(x, y, size.width, size.height);
    }
    
	/**
	 * @see Domainlayout
	 */
	@Override
	public void layoutArrangements(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			return;
		
		param.init(width, height);
	
		int dasDone = 0;
		int border = y;

		for (int i = 0; i < view.getDaSet().length; i++) {
			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
			
			if (!dac.isVisible())
				continue;
			
			y = border + (int) (param.offsetY * dasDone * height);
			layoutArrangement(dac, x, y, width, height);
			dasDone++;
		}
	}	

	/**
	 * @see Domainlayout
	 */
	@Override
	public void layoutArrangement(ArrangementComponent dac, int x, int y, int width, int height) {
		if (!dac.isVisible())
			return;
		
		x += getArrangementX(dac);	// modifiy with shift
		
		dac.getRelativeBounds().width = param.offsetX * getArrangementWidth(dac.getDomainArrangement());
		
		// recalculate relative coordinates into screen coordinates
		dac.setBounds(x,
				  	  y, 			 						 
				  	  (int) Math.round(dac.getRelativeBounds().width * width),
				  	  param.da_height);		

		// layout domains	
		for (DomainComponent dc : view.getArrangementComponentManager().getDomains(dac)) 	
			layoutDomain(dc, dac, x, y, width, height);
	}
	
	/**
	 * Helper method to layout a domain component
	 * within an arrangement component
	 * 
	 * @param dc
	 * 		the domain component to be layouted
	 * @param dac
	 * 		the arrangement component owning the specified domain
	 * @param x
	 * 		the x coordinate of the layout bounds
	 * @param y
	 * 		the y coordinate of the layout bounds
	 * @param width
	 * 		the width of the layout bounds
	 * @param height
	 * 		the height of the layout bounds
	 */
	private void layoutDomain(DomainComponent dc, ArrangementComponent dac, int x, int y, int width, int height) {	
		if (!dc.isVisible())
			return;
		
		int domX = getDomainX(dc.getDomain());
		
		dc.getRelativeBounds().width = param.offsetX * getDomainWidth(dc.getDomain());
		dc.getRelativeBounds().x = param.offsetX * domX;

		// recalculate relative coordinates into screen coordinates
		dc.setBounds((int) (x + Math.round((dc.getRelativeBounds().x * width))),
			  		 y,					 						 
			  		 (int) Math.round(dc.getRelativeBounds().width * width),
			  		param.da_height);	
		
	}

    /**
     * Return the arrangement bounds for all layouted arrangements.
     * The bounds are calculated successively during the layout process
     * by adding a layouted arrangement to those bounds.
     */
	@Override
	public Rectangle getDomainBounds() {
		if (layoutBounds == null) // e.g. all arrangements are invisible or no match with the tree
			layoutBounds = new Rectangle();
		return new Rectangle(layoutBounds.x, layoutBounds.y-param.da_height/2, layoutBounds.width, layoutBounds.height);
	}
	
	/**
	 * @see DomainLayout
	 */
	@Override
	public void setDomainBounds(Rectangle bounds) {
		layoutBounds = bounds;
	}

	/**
	 * Calculate the needed space to layout the arrangement properly.
	 * The actual size is determined by the subclasses.
	 * 
	 * @param da
	 * 		the Domain arrangement which preferred size has to be determined
	 * @return
	 * 		the preferred size to layout the specified domain arrangement
	 */
	@Override
	public Dimension getPreferredArrangementSize(DomainArrangement da){
		int width = getArrangementWidth(da);
		int height =  DomainLayout.DOMAINFONT.getSize()+4;
		return new Dimension(width, height);
	}	
	
	/**
	 * Helper method to determine the width in pixels for a given string
	 * 
	 * @param label
	 * 		the string which width should be calculated
	 * @return
	 * 		the strings width in pixels.
	 */
	private int getStringWidth(String label) {
		Insets pinsets = view.getViewComponent().getInsets();	
		double da_height = (view.getViewComponent().getHeight() - pinsets.top - pinsets.bottom)/ (double) param.visible;
		float fontSize = (float) Math.min(da_height, DomainLayout.ARRANGEMENTFONT.getSize()) +2; //(float) da_height; //Math.min(da_height, view.getDomainArrangementFontManager().getMaximumFontSize());
		Font f = DomainLayout.ARRANGEMENTFONT.deriveFont(fontSize);
		return SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(f), label);
	}

	/* ********************************************************** *
	 * 				ARRANGEMENT PARAMETER CALCULATION 			  *
	 * ********************************************************** */
	
	/**
	 * Returns the used parameter to layout the view properly
	 * 
	 * @return
	 * 		used parameter to layout the view
	 */
	@Override
	public ArrangementParameter getDomainParams() {
		return param;
	}
	
	/**
	 * Inner class ArrangementParameter calculates needed parameters
	 * which are necessary during the layout process.
	 * 
	 * @author Andreas Held
	 *
	 */
	public class ArrangementParameter {
		
		/** the maximum space needed to draw the biggest arrangement */
		public int maxLen = 0;
		
		/** the maximum number of domains within an arrangement */
		public int maxDoms = 0;
		
		/** the offset for the x coordinate. */
		public double offsetX = 0;
		
		/** the offset for the y coordinate. */
		public double offsetY = 0;
		
		/** the height for one arrangement */
		public int da_height = 0;
		
		/** the maximal needed label width */
		public int maxLabelWidth = 0;
		
		/** number of visible arrangements */
		public int visible = 0;
		
		
		/**
		 * Preliminary init of needed parameters
		 */
		public void init () {
			compute_maxLabelWidth();
			compute_maxLen();
		}

		/**
		 * initialization of the parameters needed for the layout process.
		 * 
		 * @param width
		 * 		the width used to layout the arrangements
		 * @param height
		 * 		the height used to layout the arrangements
		 */
		public void init(int width, int height) {
			compute_maxLabelWidth();
			compute_maxLen();
			compute_maxDoms();
			compute_offsetX(width);
			compute_offsetY(height);
			compute_da_height(height);
		}
		
		/**
		 * Retrieve the domain arrangement with the largest label being 
		 * visible.
		 */
		private void compute_maxLabelWidth() {
			// first get the largest label
			String maxLabel = "";
			visible = 0;
			
			for (int i=0; i < view.getDaSet().length; i++) {
				ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
				
				if (!(view instanceof DomainTreeViewI && ((DomainTreeViewI) view).getDomainTreeLayoutManager().isShowTree()))
					if (!dac.isVisible())
						continue;

				visible++;
				
				if (dac.getLabel() != null && dac.getLabel().length() > maxLabel.length())
					maxLabel = dac.getLabel();
			}
			
			// finally compute the width of the largest label.
			maxLabelWidth = getStringWidth(maxLabel);
		}

		/**
		 * Compute the maximal needed width for the biggest 
		 * visible arrangement.
		 */
		private void compute_maxLen() {
			maxLen = 0;
			Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
			while(iter.hasNext()) {
				ArrangementComponent dac = iter.next();
				if (!dac.isVisible())
					continue;
				
				int len = getArrangementWidth(dac.getDomainArrangement());
				maxLen = (len > maxLen) ? len : maxLen;
			}
		}
			
		/**
		 * Compute the maximal number of domains within an visible
		 * arrangement.
		 * 
		 */
		private void compute_maxDoms() {
			maxDoms = 0;
			Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
			while(iter.hasNext()) {
				ArrangementComponent dac = iter.next();
				if (!dac.isVisible())
					continue;
				
				int doms = dac.getDomainArrangement().countDoms();
				maxDoms = (doms > maxDoms) ? doms : maxDoms;
			}
		}
		

		/**
		 * Compute the offset for the x coordinate.
		 * This is for instance 1 / width or in the case of
		 * an enabled fitToScreen option: 1 / maxLen.
		 * 
		 * @param width
		 * 		the views width
		 */
		private void compute_offsetX(int width) {
			boolean resize = false;
			
			if (view.getDomainLayoutManager().isFitDomainsToScreen() && param.maxLen > width)
				resize = true;
			
			offsetX = (resize) ? (1.0 / param.maxLen) : (1.0 / width);
		}
		
		/**
		 * Calculates the offset for the y coordinate. This is 1 / number of 
		 * arrangements.
		 * 
		 * @param numberDAs
		 * 		the number of arrangements to be layouted
		 */
		private void compute_offsetY(int height) {
			offsetY = 1.0 / visible;
		}
		
		/**
		 * Compute the height of an domain arrangement
		 * 
		 * @param height
		 * 		the maximal height
		 */
		private void compute_da_height(int height) {
			da_height = 
				(MAX_DA_HEIGHT+DA_SPACE > height / visible) 
				? MIN_DA_HEIGHT : MAX_DA_HEIGHT;
		}
	}
	
	
}
