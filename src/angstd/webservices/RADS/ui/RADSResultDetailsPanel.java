package angstd.webservices.RADS.ui;

import info.radm.radscan.RADSResults;
import info.radm.radscan.ds.RADSProtein;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.DomainArrangement;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.util.BrowserLauncher;
import angstd.util.URLReader;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultDetailsPanel extends JPanel implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RADSResults results;
	private JFrame frame;
	private JButton save, close, seeOnline;
	private StringBuffer crampageLog;
	private String queryProteinID = null;
	private DomainArrangement queryProtein;
	private TreeSet<RADSProtein> proteins; 
	
	private static RADSResultDetailsPanel instance = null;

	public static RADSResultDetailsPanel showResultsFrame(DomainArrangement queryProtein, 
			RADSResults results, TreeSet<RADSProtein> proteins ) {
		if (instance != null)
			instance.close();
		instance = new RADSResultDetailsPanel(queryProtein, results, proteins);
		return instance;
	}

	private RADSResultDetailsPanel(DomainArrangement queryProtein, 
			RADSResults results, TreeSet<RADSProtein> proteins) {
		super(new MigLayout());
		this.queryProtein = queryProtein;
		this.results = results;
		this.proteins = proteins;	
		initPanel();
	 }
	
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

		JTextArea textArea = new JTextArea(crampageLog.toString());
		JScrollPane jScrollPane = new JScrollPane(textArea);
		
		add(new JXTitledSeparator("RadScan Summary"), "growx, span, wrap,");
		add(new JLabel("Query ID:"), "gapleft 10");
		add(new JLabel(queryProtein.getName()), "wrap");
		add(new JLabel("Job ID"), "gapleft 10");
		add(new JLabel(results.getJobID()), "wrap");
		add(new JLabel("Database"), "gapleft 10");
		add(new JLabel(results.getQuery().getDatabase()), "wrap");
		add(new JLabel("Total hits:"), "gapleft 10");
		add(new JLabel(""+results.getHitsNumber()), "wrap");
		add(new JLabel("Total arrangements:"), "gapleft 10");
		add(new JLabel(""+RADSProtein.getUniqueArchitectures(proteins).size()), "wrap");
		
		add(new JXTitledSeparator("RAW rads output"), "growx, span, wrap, gaptop 10");
		add(jScrollPane, "h 100::400, w 600!, growx, span");
		add(save, "split 3");
		add(close, "");
		add(seeOnline, "align right");
		
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private void close() {
		frame.dispose();
	}
	
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

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("openResultsInBrowser"))
			BrowserLauncher.openURL(results.getJobUrl());
		if (e.getActionCommand().equals("writeLogToFile"))
			writeLogToFile();
		if (e.getActionCommand().equals("closeReportWindow"))
			frame.dispose();
	}
	
}
