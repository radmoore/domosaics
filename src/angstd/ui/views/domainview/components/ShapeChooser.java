package angstd.ui.views.domainview.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.shapes.AngstdShapeIcon;
import angstd.ui.views.domainview.components.shapes.DomainShapes;

/**
 * ShapeChooser is displayed for DomainComponents to change their shapes.
 * Whenever a new shape is chosen the domain view updates with
 * the new shape. If the apply button is triggered the shape chooser
 * just closes. If the cancel button is triggered the old shape is set before the
 * shape chooser disappears.
 * 
 * @author Andreas Held
 *
 */
public class ShapeChooser extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** view which displays the shape chooser, gives access to needed managers */
	protected DomainViewI view;
	
	/** shape ComboBox used to display the usable shapes */
	protected JComboBox shapeList;
	
	/** shapes displayed in the shapeList */
	protected Shape[] shapes;
	
	/** button to trigger the apply for a chosen shape */
	protected JButton jbtOK;
	
	/** button to trigger the cancel action */
	protected JButton jbtCancel;
	
	/** the DomainComponent which shape is going to be changed */
	protected DomainComponent selected;
	
	/** the old shape of the specified domain component, used for cancel */
	protected Shape oldShape;
	
	
	/**
	 * Constructor for a new shape chooser. Initializes the dialog 
	 * and class variables.
	 * 
	 * @param view
	 * 		the view containing the domain component which shape should be changed
	 */
	public ShapeChooser(DomainViewI view) {
		this.view = view;
		
		// the domain component going to be changed
		selected = view.getDomainSelectionManager().getClickedComp();
		oldShape = view.getDomainShapeManager().getDomainShape(selected);
		
		Container container = getContentPane();
		
		// init possible shapes to choose from
		shapes = new Shape[DomainShapes.values().length];
		for (DomainShapes shape : DomainShapes.values()) 
			shapes[shape.getIndex()] = shape.getShape();
		
		// create components
		shapeList = new JComboBox(shapes);
		shapeList.addActionListener(this);
		shapeList.setRenderer(new ShapeCellRenderer(selected));
		shapeList.setSelectedItem(oldShape);
		
		jbtOK = new JButton("OK");
		jbtOK.addActionListener(this);
		
		jbtCancel = new JButton("Cancel");
		jbtCancel.addActionListener(this);
		
		// create shape chooser list box
		Box shapeBox = new Box(BoxLayout.Y_AXIS);
		shapeBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true),
				"Shape:" 							
		)); 	
		shapeBox.add(shapeList);
		shapeBox.setPreferredSize(new Dimension(AngstdShapeIcon.DEFAULT_WIDTH+40,75));  //(5 for the border + 20 space) * 2
		shapeBox.setMaximumSize(new Dimension(AngstdShapeIcon.DEFAULT_WIDTH+40,75));
		
		
		// create the button box
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(jbtOK);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(jbtCancel);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// create mainBox
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		mainBox.add(Box.createVerticalGlue());
		mainBox.add(shapeBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalGlue());
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// set up the dialog
		container.add(mainBox);
		setSize(AngstdShapeIcon.DEFAULT_WIDTH+150, 200);
		setResizable(false);
		setModal(true);
	}
	
	/**
	 * Shows the shape chooser dialog
	 * 
	 * @param parent
	 * 		parent component showing this dialog
	 * @param title
	 * 		title for the dialog
	 * @return
	 * 		always 0
	 */
	public int showDialog(Component parent, String title) {
		this.setTitle(title);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		return 0;
	}

	/**
	 * Set the selected shape as new shape for the 
	 * selected DomainComponent (DomainFamily)
	 * 
	 * @param shape
	 * 		the new shape for the selected domain family
	 */
	public void setNewShape(Shape shape) {
		if(selected == null)
			return;
		
		// map the shape to its index
		for(int i=0; i < DomainShapes.values().length; i++)
			if (DomainShapes.values()[i].getShape().equals(shape)) {
				view.getDomainShapeManager().setDomainShape(selected, i);
				return;
			}
	}
	
	/**
	 * updated the shapes within the shape chooser and updates
	 * the view with it.
	 */
	protected void updateShapes() {
		Shape shape = (Shape) shapeList.getSelectedItem();
		if(shape == null){
			shape = view.getDomainShapeManager().getDomainShape(selected);
			return;
		}
		
		// set the new shape
		setNewShape(shape);
	}

	/**
	 * @see ChangeListener
	 */
	public void stateChanged(ChangeEvent e) {
		updateShapes();
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == shapeList)
			updateShapes();
		if(e.getSource() == jbtOK) 
			this.setVisible(false);
		if(e.getSource() == jbtCancel) {
			setNewShape(oldShape);
			this.setVisible(false);
		}
	}
	
	/**
	 * Inner class ShapeCellRenderer used to render the shape list.
	 * 
	 * @author Andreas Held
	 *
	 */
	class ShapeCellRenderer extends DefaultListCellRenderer{
		private static final long serialVersionUID = 1L;

		/** domain component which shape should be changed, needed to get the color */
		protected DomainComponent dc;
		
		/**
		 * Constructor for a new ShapeCellRenderer
		 * 
		 * @param dc
		 * 		domain component which shape should be changed
		 */
		public ShapeCellRenderer (DomainComponent dc) {
			this.dc = dc;
		}
		
		/**
		 * configures the list cell renderer by creating the shape icons.
		 * 
		 * @param renderer
		 * 		the JLabels rendered as list entries
		 * @param value
		 * 		the shape for the actual list entry
		 */
		private void configureRenderer(JLabel renderer, Object value) {
			Shape shape = (Shape) value;
			renderer.setIcon(new AngstdShapeIcon(shape, view.getDomainColorManager().getDomainColor(dc), true));
			renderer.setText("");
		}
		
		/**
		 * @see DefaultListCellRenderer
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			DefaultListCellRenderer res = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			configureRenderer(res, value);
			return res;
		}
	}

}