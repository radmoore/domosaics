package domosaics.ui.tools.domaingraph.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import domosaics.ui.tools.domaingraph.DomainGraphView;



import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;

/**
 * The slider which is embedded below the prefuse graph to change the
 * colorization of domains depending on the number of their neighbours.
 * 
 * @author Andreas Held
 *
 */
public class MyThresholdSlider extends JComponent{
	private static final long serialVersionUID = 1L;
	
	/** the prefuse graph in which the colorization is changed */
	protected PrefuseGraph graph;

	/** the slider component */
	protected JSlider slider;
	
	/** the old threshold */
	protected int oldThreshold = 1;
	
	/** the maximal threshold */
	protected int maxThreshold;
	
	/** the actual threshold */
	protected int threshold = 1;
	
	/** the actual threshold displayed as label next to the slider */
	protected JFastLabel valueLabel;
	
	
	/**
	 * Constructor for a new threshold slider used with the 
	 * prefuse co occurrence graph
	 * 
	 * @param view
	 * 		the domain graph view
	 * @param graph
	 * 		the prefuse graph reacting on slider changes
	 * @param maxConnections
	 * 		tzhe maximal number of connection within the graph
	 */
	public MyThresholdSlider (DomainGraphView view, PrefuseGraph graph, int maxConnections) {
		this.graph = graph;
		this.maxThreshold = maxConnections;
		this.slider = new JSlider(1, maxThreshold, 1);
		
		// set labels for min and max value
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel("1"));

		labelTable.put(new Integer(maxThreshold), new JLabel(""+maxThreshold));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true); 
		slider.setBackground(Color.WHITE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		slider.setPreferredSize(new Dimension((int) screenSize.getWidth(),40));
		slider.setMaximumSize(new Dimension(640,40));

        valueLabel = new JFastLabel("  1");
        valueLabel.setPreferredSize(new Dimension(80, 24));
        valueLabel.setVerticalAlignment(SwingConstants.BOTTOM);
      
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
        valueLabel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 24));
        valueLabel.setHighQuality(true);

        // set Color values for components
        slider.setBackground(Color.WHITE);
        valueLabel.setBackground(Color.WHITE);
        super.setForeground(Color.WHITE);
        
    	slider.setForeground(Color.BLACK);
    	valueLabel.setForeground(Color.BLACK);
        super.setForeground(Color.BLACK);
  
        initUI();
	}
	
	protected void initUI() {
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				oldThreshold = threshold;
	            threshold = source.getValue();
	            if (oldThreshold == threshold)
	            	return;
	            setFieldValue();
	            graph.recalculateNodes(threshold);
			}
		});
		
		slider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
//             	graph.fireAutoZoom();
            }
        });
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(slider);
		add(valueLabel);
	}
   
    /**
     * set the actual slider value into the value field label
     *
     */
    private void setFieldValue() {
    	if (threshold < 100)
    		valueLabel.setText("  "+threshold);
    	else if (threshold < 1000)
    		valueLabel.setText(" "+threshold);
    	else
    		valueLabel.setText(""+threshold);
    }

}
