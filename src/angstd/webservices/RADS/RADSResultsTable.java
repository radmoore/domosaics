package angstd.webservices.RADS;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import angstd.webservices.RADS.util.RADSResultsTableModel;

public class RADSResultsTable extends JPanel{

	private JFrame parentFrame;
	private JPanel panel;
	
	private JTable table;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RADSResultsTable(RADSResultsTableModel rtm) {
		init();
		table = new JTable(rtm);
	}
	
	
	private void init() {
		parentFrame = new JFrame("RADScan Results");
		panel = new JPanel();
		
	}
}
