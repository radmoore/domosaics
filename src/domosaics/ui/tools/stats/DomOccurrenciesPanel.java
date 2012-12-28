package domosaics.ui.tools.stats;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import domosaics.model.arrangement.ArrangementManager;
import domosaics.model.arrangement.DomainFamily;



import net.miginfocom.swing.MigLayout;

public class DomOccurrenciesPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public DomOccurrenciesPanel (ArrangementManager manager) {
		super(new MigLayout());
		setName("Domain Occurrencies");
		
		this.setBackground(Color.WHITE);
		
		// sort the domain family occurencies
		Map<DomainFamily, Integer> domFamOccurrencies = manager.getDomFamOccurrences();
		Map.Entry[] entries = domFamOccurrencies.entrySet().toArray(new Map.Entry[domFamOccurrencies.size()]);

		Arrays.sort(entries, new Comparator<Object>() {
			public int compare(Object lhs, Object rhs) {
				Map.Entry le = (Map.Entry)lhs;
		        Map.Entry re = (Map.Entry)rhs;
		        return -((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
			}
		});
		Map.Entry<DomainFamily, Integer>[] data = (Map.Entry<DomainFamily, Integer>[]) entries;

		// layouting
		int row = 0;
		for (Map.Entry<DomainFamily, Integer> d : data) {
			JLabel field = new JLabel(d.getKey().getId());
			JTextArea val = new JTextArea(0, 11);
			val.setText(""+d.getValue());
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
