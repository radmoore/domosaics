package angstd.webservices.RADS.ui;

import info.radm.radscan.RADSResults;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.DomainArrangement;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.util.BrowserLauncher;
import angstd.util.URLReader;
import angstd.webservices.RADS.RADSResultsTableModel;

/**
 * This class describes the JPanel which shows the RADS/RAMPAGE scan log.
 * It is implemented as a signelton, such that only one instance
 * can exist. 
 * 
 * See {@link RADSScanPanel} for more details
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsTablePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private RADSResults results;
	private JFrame frame;
	private JButton save, close, seeOnline;
	private StringBuffer crampageLog = null;
	private DomainArrangement queryProtein;
	private RADSResultsTableModel resultTableModel;
	
	private static RADSResultsTablePanel instance = null;
	
	/**
	 * This method is used to get access to the JPanel displaying the results log.
	 * If a result panel is currently being displayed (or e.g. has been backgrounded), the
	 * instance will be destroyed, and a new instance will be created. 
	 * 
	 * @param queryProtein - the RADS/RAMPAGE query arrangement
	 * @param results - the results of the RADSScan (provides access to method used, all parameters, 
	 * scanned database etc)
	 * @param proteins - the list of hit proteins
	 * @return - an instance of the RADSResultsDetailsPanel
	 */
	public static RADSResultsTablePanel createResultsTableFrame(DomainArrangement queryProtein, 
			RADSResults results, RADSResultsTableModel resultTableModel) {
		if (instance != null)
			instance.destroy();
		instance = new RADSResultsTablePanel(queryProtein, results, resultTableModel);
		return instance;
	}
	
	/**
	 * Defines actions to perform on events
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("openResultsInBrowser"))
			BrowserLauncher.openURL(results.getJobUrl());
		if (e.getActionCommand().equals("writeLogToFile"))
			writeLogToFile();
		if (e.getActionCommand().equals("closeReportWindow"))
			hideFrame();
	}
	
	/*
	 * Private constructor - see static access method {@link RADSResultDetailsPanel#showResultsFrame}
	 */
	private RADSResultsTablePanel(DomainArrangement queryProtein, 
			RADSResults results, RADSResultsTableModel resultTableModel) {
		super(new MigLayout());
		this.queryProtein = queryProtein;
		this.results = results;
		this.resultTableModel = resultTableModel;
		initPanel();
	 }
	
	/*
	 * Calls the log reading method and constructs the panel
	 */
	private void initPanel() {
		
		readLogFile();
		
		frame = new JFrame("RADS Results");
		save = new JButton("Save");
		save.setToolTipText("Save scan log to file");
		save.setActionCommand("writeLogToFile");
		save.addActionListener(this);

		close = new JButton("Close");
		close.setToolTipText("Close results window");
		close.setActionCommand("closeReportWindow");
		close.addActionListener(this);
		
		seeOnline = new JButton("Browse online");
		seeOnline.setToolTipText("Opens browser with scan results");
		seeOnline.setActionCommand("openResultsInBrowser");
		seeOnline.addActionListener(this);

		
		JTable resultTable = new JTable(resultTableModel);
		//resultTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
	    resultTable.setFillsViewportHeight(true);;
		
//	    JTextArea textArea = new JTextArea(crampageLog.toString());
		JScrollPane jScrollPane = new JScrollPane(resultTable);
		
	    
		add(new JXTitledSeparator("RadScan Summary"), "growx, span, wrap,");
		add(new JLabel("Query ID:"), "gapleft 10");
		add(new JLabel(queryProtein.getName()), "wrap");
		add(new JLabel("Job ID"), "gapleft 10");
		add(new JLabel(results.getJobID()), "wrap");
		add(new JLabel("Database"), "gapleft 10");
		add(new JLabel(results.getQuery().getDatabase()), "wrap");
		add(new JLabel("Total hits:"), "gapleft 10");
		add(new JLabel(""+results.getHitsNumber()), "wrap");
		//add(new JLabel("Total arrangements:"), "gapleft 10");
		//add(new JLabel(""+RADSProtein.getUniqueArchitectures(proteins).size()), "wrap");
		
		add(new JXTitledSeparator("RADS Results"), "growx, span, wrap, gaptop 10");
		add(jScrollPane, "h 100::400, w 600!, growx, span");
		add(save, "split 3");
		add(close, "");
		add(seeOnline, "align right");
		
		frame.add(this);
		frame.pack();
//		frame.setVisible(true);
		
	}
	
	/* 
	 * go figure
	 */
	public void destroy() {
		frame.dispose();
	}
	
	public void hideFrame() {
		frame.setVisible(false);
	}
	
	public void showFrame() {
		frame.setVisible(true);
	}
	
	/*
	 * Reads the RADS/RAMPAGE scan log. Uses an instance of RADSResults to access
	 * the log file remotely
	 */
	private void readLogFile() {
		try {
			BufferedReader reader = URLReader.read(results.getCrampageOut());
			String line = null;
			crampageLog = new StringBuffer();
			
			while ( (line = reader.readLine()) != null)
				crampageLog.append(line+"\n");
				
			reader.close();
			
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * invoked when user chooses to save the log file
	 */
	private void writeLogToFile() {
		System.out.println("Writing to file");
		File outFile = FileDialogs.showOpenDialog(this);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			bw.write(crampageLog.toString());
			bw.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		MessageUtil.showInformation(null, "Scan log written to "+outFile.getAbsolutePath());
	}
	
}
