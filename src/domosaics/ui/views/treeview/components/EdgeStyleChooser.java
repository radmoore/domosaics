package domosaics.ui.views.treeview.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import domosaics.model.tree.TreeEdgeI;
import domosaics.ui.views.treeview.TreeViewI;




/**
 * EdgeStyleChooser is the graphical front end to let the user choose
 * a new edge style for tree edges.
 * If the user cancels the dialog the old edge styles are gonna be restored.
 * 
 * @author Andreas Held (based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class EdgeStyleChooser extends JDialog implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** spinner to adjust the edge thickness */
	protected JSpinner thicknessSpinner;
	
	/** available strokes */
	protected BasicStroke[] strokes;
	
	/** graphical component list to show the available strokes */
	protected JComboBox strokeList;
	
	/** button to apply the settings */
	protected JButton jbtOK;
	
	/** button to cancel the dialog */
	protected JButton jbtCancel;
	
	/** the tree view in where changes are made */
	protected TreeViewI view;
	
	/** collection of all selected edges */
	protected Collection<TreeEdgeI> selected;
	
	/** mapping between edges and their old strokes in case of a cancel */
	protected Map<TreeEdgeI, BasicStroke> oldStrokes;
	
	
	/**
	 * Constructor for a new EdgeStyleChooser
	 * 
	 * @param view
	 * 		the tree view in where changes are made
	 */
	public EdgeStyleChooser(TreeViewI view) {
		this.view = view;
		Container container = getContentPane();
		
		// first of all gather all selected edges and backup their strokes 
		oldStrokes = new HashMap<TreeEdgeI, BasicStroke>();
		
		BasicStroke startStroke = (BasicStroke) view.getTreeStrokeManager().getDefaultEdgeStroke();
		double startThickness = 1;
		
		selected = view.getTreeSelectionManager().getSelectedEdges();
		for (TreeEdgeI edge : selected) {
			oldStrokes.put(edge, (BasicStroke) view.getTreeStrokeManager().getEdgeStroke(edge));
			startStroke = (BasicStroke) view.getTreeStrokeManager().getEdgeStroke(edge);
			startThickness = ((BasicStroke) view.getTreeStrokeManager().getEdgeStroke(edge)).getLineWidth();
		}
		
		// init the list of strokes being available to choose from
		initStrokes();
		
		// create components
		strokeList = new JComboBox(strokes);
		strokeList.addActionListener(this);
		strokeList.setRenderer(new StrokeCellRenderer());
	
		thicknessSpinner = new JSpinner(new SpinnerNumberModel(1.0, 1.0, 50.0, 0.2));
		thicknessSpinner.setValue(startThickness);
		thicknessSpinner.addChangeListener(this);
		
		strokeList.setSelectedIndex(stroke2index(startStroke));
		
		jbtOK = new JButton("OK");
		jbtOK.addActionListener(this);
		
		jbtCancel = new JButton("Cancel");
		jbtCancel.addActionListener(this);
		
		// create stroke style box
		Box strokeStyleBox = new Box(BoxLayout.Y_AXIS);
		strokeStyleBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true),
				"Stroke Style:" 							
		)); 	
		strokeStyleBox.add(strokeList);

		// create thicknessSpinner box
		Box thicknessBox = new Box(BoxLayout.Y_AXIS);
		thicknessBox.setBorder(BorderFactory.createTitledBorder(
				new LineBorder(Color.black, 1, true), 
				"Thickness:" 							
		)); 	
		thicknessBox.add(thicknessSpinner);
		
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
		mainBox.add(strokeStyleBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(thicknessBox);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalGlue());
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		// set up the dialog
		container.add(mainBox);
		setSize(200, 200);
		setResizable(false);
		setModal(true);
	}
	
	/**
	 * Sets the new stroke style for the selected edges.
	 * If no edges are selected, the default edge stroke is set (for all edges)
	 * 
	 * @param stroke
	 * 		the new stroke to set
	 */
	public void setNewStroke(Stroke stroke) {
		// set new edge stroke for all
		if(selected == null || selected.size() == 0){
			view.getTreeStrokeManager().setDefaultEdgeStroke(stroke);	
			return;
		}
		
		// set new edge stroke for selection (fires visual change)
		view.getTreeStrokeManager().setEdgeStroke((BasicStroke) stroke, selected);
	}

	/**
	 * Helper method to convert a stroke into an index within the stroke list
	 * 
	 * @param stroke
	 * 		the stroke to be converted
	 * @return
	 * 		the index within the stroke list
	 */
	private int stroke2index (BasicStroke stroke) {
		for (int i = 0; i < strokes.length; i++) 
			if (Arrays.equals(stroke.getDashArray(), strokes[i].getDashArray()))
				return i;
		return -1;
	}
	
	/**
	 * Helper method to initialize the available edge strokes
	 */
	private void initStrokes() {
		BasicStroke a = new BasicStroke(1f);
		BasicStroke b = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5,2}, 0f);
		BasicStroke c = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5,2,2,5}, 0f);
		BasicStroke d = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{2,2}, 0f);
		BasicStroke e = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{8,5}, 0f);
		BasicStroke f = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{8,5,3,5 }, 0f);
		BasicStroke g = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10,5 }, 0f);
		strokes = new BasicStroke[]{a,b,c,d,e,f,g};
	}
	
	/**
	 * Displays the dialog 
	 * 
	 * @param parent
	 * 		the component showing this dialog
	 * @param title
	 * 		the dialogs title
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
	 * React on list changes
	 * 
	 * @param e
	 * 		action event triggered by the stroke list
	 */
	public void valueChanged(ListSelectionEvent e) {
		updateStrokes();
	}

	/**
	 * React on list changes
	 * 
	 * @param e
	 * 		action event triggered by the stroke list
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		updateStrokes();
	}

	/**
	 * Creates the selected stroke and delegates to setNewStroke().
	 */
	protected void updateStrokes() {
		BasicStroke stroke = (BasicStroke) strokeList.getSelectedItem();
		if(stroke == null)
			stroke = (BasicStroke) view.getTreeStrokeManager().getDefaultEdgeStroke();
		
		double selectedWidth = ((Double)thicknessSpinner.getValue());
		
		if(selectedWidth != 1.0){
			stroke = new BasicStroke((float)selectedWidth,
								       stroke.getEndCap(),
								       stroke.getLineJoin(),
								       stroke.getMiterLimit(),
								       stroke.getDashArray(),
								       stroke.getDashPhase()
									 );
		}
		
		if (stroke == null)
			return;
		
		// set the new stroke
		setNewStroke(stroke);
	}

	/**
	 * React on buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == strokeList)
			updateStrokes();
		
		if(e.getSource() == jbtOK) 
			this.setVisible(false);
		
		if(e.getSource() == jbtCancel) {
			// revert the old strokes 
			for (TreeEdgeI edge : selected) 
				view.getTreeStrokeManager().setEdgeStroke(oldStrokes.get(edge), edge);
			this.setVisible(false);
		}
	}
	
	/**
	 * Renderer for the stroke list.
	 *
	 * @author Andreas Held (based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
	 */
	class StrokeCellRenderer extends DefaultListCellRenderer{
		private static final long serialVersionUID = 1L;
		
		private Stroke stroke;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			this.stroke = (Stroke) value;
			return super.getListCellRendererComponent(list, " ", index, isSelected, cellHasFocus);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			Stroke oldS = g2d.getStroke();
			Color oldC = g2d.getColor();
			
			Rectangle r = g2d.getClipBounds();
			
			g2d.setColor(Color.white);
			g2d.fill(r);
			g2d.setStroke(stroke);
			g2d.setColor(Color.black);
			g2d.drawLine(r.x + 1, (int) (r.height/2.0), r.x+r.width-1, (int) (r.height/2.0));
			
			g2d.setStroke(oldS);
			g2d.setColor(oldC);
		}
	}

}
