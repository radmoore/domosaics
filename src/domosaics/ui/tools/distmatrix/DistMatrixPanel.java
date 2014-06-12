package domosaics.ui.tools.distmatrix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import domosaics.ui.tools.distmatrix.components.MatrixTableCellRenderer;
import domosaics.ui.tools.distmatrix.components.MatrixTableModel;




/**
 * The Panel containing the distance matrix table. This panel is
 * embedded in the ToolFrame so the view is only used for creation and
 * manipulation but the layout and rendering is initiated by this panel.
 * 
 * @author Andreas Held
 *
 */
public class DistMatrixPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** the font size for matrix entries */
	protected static final int FONTSIZE = 12;
	
	/** formatter to format decimal numbers into x.xx */
	protected NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH); 

	/** the matrix entries */
	protected String[][] data;
	
	/** the column names, e.g. protein names */
	protected String[] colNames;
	
	/** the table used to display the matrix */
	protected JTable table;
	
	/** the renderer used to render the matrix */
	protected MatrixTableCellRenderer tableRenderer;
	
	/**
	 * Constructor for a new matrix panel
	 * 
	 * @param data
	 * 		the matrix entries
	 * @param colNames
	 * 		column names
	 */
	public DistMatrixPanel(double[][] data, String[] colNames) {
		super(new BorderLayout());
		formatter.setMaximumFractionDigits(2);
		
		this.colNames = colNames;
		
		fillTable(data);
		tableRenderer = new MatrixTableCellRenderer();
		
		setTableOptions();
		addListSelectionModel();
		
    	table.setSize(table.getPreferredSize());
    	add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	/**
	 * Sets a new matrix, e.g. if the distance measure was changed
	 * 
	 * @param data
	 * 		the new field data
	 */
	public void setMatrix(double[][] data) {
		fillTable(data);
	}
	
	/* ********************************************************* *
	 * 							INIT TABLE						 *
	 * ********************************************************* */
	
	/**
	 * Helper method to initialize the table data. 
	 */
	private void fillTable(double[][] data) {
		generateTableData(data);
		if(table == null)
			table = new JTable(new MatrixTableModel(this.data, this.colNames));
	     else
	         table.setModel(new MatrixTableModel(this.data, this.colNames));
	}
	
	/**
	 * Helper method to initialize the table data. 
	 */
	private void generateTableData(double[][] data) {
		int N = data.length;
		int M = data[0].length;
		
		this.data = new String[N][M];
		
		for (int r = 0; r < N; r++)
			for (int c = 0; c < M; c++)
				if (data[r][c] == Double.NEGATIVE_INFINITY)	// e.g. if only diagonalmatrix to show
					this.data[r][c] = new String("");
				else
					this.data[r][c] = new String(formatter.format(data[r][c]));
	}
	
	/**
	 * Helper method to initialize the table data. 
	 */
	private void setTableOptions() {
		int prefWidth  = (int) table.getTableHeader().getPreferredSize().getWidth();
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(prefWidth,15));
		table.setDefaultRenderer(table.getColumnClass(1), tableRenderer);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowGrid(false);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
	}
	
	/**
	 * Helper method to initialize the table data. 
	 */
	private void addListSelectionModel() {
		ListSelectionListener lsm = new  ListSelectionListener() {
		    int c = -1;
		    int r = -1;
            
		    @Override
			public void valueChanged(ListSelectionEvent e) {
                boolean change = false;
                if(table.getSelectedColumn() != c){
                    c = table.getSelectedColumn();
                    change = true;
                }
                if(table.getSelectedRow() != r){
                    r = table.getSelectedRow();
                    change = true;
                }
                if(change){                                  
                    tableRenderer.setSelection(c,r);
                    table.repaint();
                }
            }
        };
		table.getSelectionModel().addListSelectionListener(lsm);
		table.getColumnModel().getSelectionModel().addListSelectionListener(lsm);
	}
	
}
