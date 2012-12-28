package angstd.ui.tools.stats;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import angstd.model.arrangement.ArrangementManager;


import net.miginfocom.swing.MigLayout;

/**
 * Basic Overview panel for stats on a dataset.
 * 
 * @author Andreas Held
 *
 */
public class OverviewPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public OverviewPanel (ArrangementManager manager) {
		super(new MigLayout());
		setName("Overview");
		
		this.setBackground(Color.WHITE);
		
		int row = 0;
		for (Stats stat : Stats.values()) {
			JLabel field = new JLabel(stat.getLabel());
			JTextArea val = new JTextArea(0, 11);
			val.setText(stat.getVal(manager));
			val.setEditable(false);
		  	val.setLineWrap(true);
		  	val.setWrapStyleWord(true);
		  	
		  	if (row % 2 != 0)
			    val.setBackground(new Color(255, 255, 255));
			else
				val.setBackground(new Color(240, 240, 255));
			val.setOpaque(true);
			
			add(field, "gap 10");
			add(val, "gap 10, span, wrap");
			row++;
		}
	}
}
