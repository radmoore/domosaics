package domosaics.ui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


import com.bric.swing.ColorPicker;

import domosaics.ui.DoMosaicsUI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.manager.TreeSelectionManager;
import domosaics.ui.views.view.View;

/**
 * The AngstdColorPicker interfaces the external library ColorPicker
 * to open a new color picker dialog. Changes made on this dialog are
 * directly transfered into Angstd via event handling.
 * <p>
 * When starting the dialog a type is passed to let the dialog 
 * know what elements will be colorized.
 * 
 * @author Andreas Held
 *
 */
public class AngstdColorPicker implements PropertyChangeListener{
	private static final long serialVersionUID = 1L;
	
	/** constant to indicate that a subtree has to be colorized */
	public static final int SUBTREE = 0;
	
	/** constant to indicate that a path to root has to be colorized */
	public static final int PATH_TO_ROOT = 1;
	
	/** constant to indicate that a path to parent has to be colorized */
	public static final int PATH_TO_PARENT = 2;
	
	/** constant to indicate that a path to children has to be colorized */
	public static final int PATH_TO_CHILDREN = 3;
	
	/** constant to indicate that a selection has to be colorized */
	public static final int SELECTION = 4;
	
	/** constant to indicate that a node has to be colorized */
	public static final int NODE = 5;
	
	/** constant to indicate that a selection of nodes has to be colorized */
	public static final int NODESELECTION = 6;
	
	/** constant to indicate that a domain has to be colorized */
	public static final int DOMAIN = 7;
	
	/** the actual type used for colorization */
	protected int type;

	/** the tree view in which elements are colorized */
	protected TreeViewI treeView;
	
	/** the domain view in which a colorize event happens */
	protected DomainViewI domView;
	
	/** instance to the external ColorPicker */
	protected ColorPicker picker;
	
	/** the oldColor of the component needed also to reset on cancel */
	protected Color startColor;
	
	
	/**
	 * Constructor for a new AngstdColorPicker.
	 * 
	 * @param type
	 * 		the type of the colorization e.g. domains
	 * @param view
	 * 		the view in which the colorization takes place
	 * @param startColor
	 * 		the old color of the component which has to be colorized
	 */
	public AngstdColorPicker(int type, View view, Color startColor) {
		this.type = type;
		this.startColor = startColor;
		
		if (type < DOMAIN)
			this.treeView = (TreeViewI) view;
		else
			this.domView = (DomainViewI) view;
		
		picker = new ColorPicker();

		picker.setHexControlsVisible(true);
		picker.setHSBControlsVisible(false);
		picker.setModeControlsVisible(false);
		picker.setOpacityVisible(false);
		picker.setPreviewSwatchVisible(false);
		picker.setRGBControlsVisible(true);
		picker.addPropertyChangeListener(ColorPicker.SELECTED_COLOR_PROPERTY, this);
//		picker.addPropertyChangeListener(ColorPicker.MODE_PROPERTY, this);
	}

	/**
	 * Shows the color picker dialog.
	 * 
	 * @return
	 * 		the chosen color
	 */
	public Color show() {
		AngstdColorPickerDialog dialog = new AngstdColorPickerDialog(picker, DoMosaicsUI.getInstance(), startColor, false);
		dialog.setTitle("Angstd");
		dialog.pack();
		
		// if the start color is black, convert it to red, because its difficult to start with black
		if (startColor.getRed() == 0 && startColor.getGreen() == 0  && startColor.getBlue() == 0)
			picker.setRGB(255, 0, 0);
		else
			picker.setRGB(startColor.getRed(), startColor.getGreen(), startColor.getBlue());
		
		dialog.setVisible(true);
		return dialog.getColor();
	}

	/**
	 * Triggers the colorize method for each new color selected.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// get new color
		Color newColor = ((Color) e.getNewValue());
		if (newColor == null)
			return;
		
		colorize(newColor);
	}
	
	/**
	 * Helper method to perform the actual colorization based
	 * on the specified type.
	 * 
	 * @param newColor
	 * 		the new color in which the components will be colorized
	 */
	private void colorize(Color newColor) {
		if (type < DOMAIN) {
			// set new color
			TreeSelectionManager selectionManager = treeView.getTreeSelectionManager();
			
			// colorize selection
			if (type == SELECTION) {
				Iterator<NodeComponent> iter = selectionManager.getSelectionIterator();
				while(iter.hasNext()) {
					NodeComponent nc = iter.next();
					if (selectionManager.isCompSelected(nc) && selectionManager.isCompSelected(nc.getParent()))
						treeView.getTreeColorManager().setEdgeColor(nc.getNode().getEdgeToParent(), newColor);
				}
				treeView.getTreeColorManager().setSelectionColor(newColor);
				return;
			}
			
			if (type == NODESELECTION) {
				Iterator<NodeComponent> iter = selectionManager.getSelectionIterator();
				while(iter.hasNext()) 
					treeView.getTreeColorManager().setNodeColor(iter.next(), newColor);
				treeView.getTreeColorManager().setSelectionColor(newColor);
				return;
			}
		
			NodeComponent targetNode = selectionManager.getClickedComp();
			if (targetNode == null) 
				return;
			
			if (type == NODE) {
				treeView.getTreeColorManager().setNodeColor(targetNode, newColor);
			}
			
			// colorize subtree
			if (type == SUBTREE) {
				for (NodeComponent nc : targetNode.depthFirstIterator()) 
					if (!nc.equals(targetNode))
						treeView.getTreeColorManager().setEdgeColor(nc.getNode().getEdgeToParent(), newColor);
			}
			
			// colorize path to root
			if (type == PATH_TO_ROOT) {
				while(targetNode != null && targetNode.getNode().getEdgeToParent() != null){
					treeView.getTreeColorManager().setEdgeColor(targetNode.getNode().getEdgeToParent(), newColor);
					targetNode = targetNode.getParent();
				}					
			}
			
			// colorize path to parent
			if (type == PATH_TO_PARENT) {
				treeView.getTreeColorManager().setEdgeColor(targetNode.getNode().getEdgeToParent(), newColor);
			}
			
			// colorize path to children
			if (type == PATH_TO_CHILDREN) {
				for (NodeComponent nc : targetNode.children()) 
					treeView.getTreeColorManager().setEdgeColor(nc.getNode().getEdgeToParent(), newColor);
			}

			return;
		}
		
		DomainComponent dc = domView.getDomainSelectionManager().getClickedComp();
		if (dc == null)
			return;
			
		// colorize domains
		if (type == DOMAIN)
			domView.getDomainColorManager().setDomainColor(dc, newColor);
	}
	
	
	/**
	 * Dialog which wraps around the color picker
	 * 
	 * @author Andreas Held
	 *
	 */
	class AngstdColorPickerDialog extends JDialog { 
		private static final long serialVersionUID = 1L;
		
		protected int alpha;
		protected JButton ok = new JButton("OK");
		protected JButton cancel = new JButton("Cancel");
		protected Color returnValue = null;
		protected ActionListener buttonListener;
		
		public AngstdColorPickerDialog(ColorPicker cp, Frame owner, Color color, boolean includeOpacity) {
			super(owner);
			initialize(cp, owner,color,includeOpacity);
		}

		public AngstdColorPickerDialog(ColorPicker cp, Dialog owner, Color color, boolean includeOpacity) {
			super(owner);
			initialize(cp, owner,color,includeOpacity);
		}
		
		private void initialize(final ColorPicker cp, Component owner, final Color color,boolean includeOpacity) {
			buttonListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();
					if(src == ok) {
						returnValue = cp.getColor();
						colorize (returnValue);
					} 
					else {
						cp.setColor(color); 
						returnValue = null;
					}
					setVisible(false);
				}
			};
			
			setModal(true);
			setResizable(false);
			getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0;
			c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(10,10,10,10);
			getContentPane().add(cp,c);
			c.gridy++; c.gridwidth = 1;
			getContentPane().add(new JPanel(),c);
			c.gridx++; c.weightx = 0;
			getContentPane().add(cancel,c);
			c.gridx++; c.weightx = 0;
			getContentPane().add(ok,c);
			cp.setRGB(color.getRed(), color.getGreen(), color.getBlue());
			cp.setOpacity( ((float)color.getAlpha())/255f );
			alpha = color.getAlpha();
			pack();
	        setLocationRelativeTo(owner);
			
			ok.addActionListener(buttonListener);
			cancel.addActionListener(buttonListener);
			
			getRootPane().setDefaultButton(ok);
		}
		
		/** 
		 * Returns the color committed when the user clicked 'OK'.  
		 * Note this returns <code>null</code> if the user canceled this
		 * dialog, or exited via the close decoration.
		 * 
		 * @return 
		 * 		the selected color or null.
		 */
		public Color getColor() {
			return returnValue;
		}
	}
}
