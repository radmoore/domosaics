package angstd.webservices.RADS.ui;

import info.radm.radscan.RADSResults;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.workspace.ProjectElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.actions.FitDomainsToScreenAction;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;
import angstd.util.BrowserLauncher;
import angstd.util.URLReader;
import angstd.webservices.RADS.util.RADSResultsTable;
import angstd.webservices.RADS.util.RADSResultsTableModel;

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
	
	private JButton save, close, seeOnline, applySelection, selectAll, deselectAll;
	private RADSResultsTable resultTable;
	private StringBuffer crampageLog = null;
	private DomainArrangement queryProtein;
	private RADSResultsTableModel resultTableModel;
	
	private JLabel selectedHitsLabel;
	private int selectedHits = 0;
	
	private RADSScanPanel scanPanel;
	private JPanel queryPanel;
	
	

	private DomainViewI queryDomainView;
	
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
	
	public void setRADSPanel(RADSScanPanel scanPanel) {
		this.scanPanel = scanPanel;
	}
	
	/**
	 * Defines actions to perform on events
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("createView"))
			createView();
		if (e.getActionCommand().equals("openResultsInBrowser"))
			BrowserLauncher.openURL(results.getJobUrl());
		if (e.getActionCommand().equals("writeLogToFile"))
			writeLogToFile();
		if (e.getActionCommand().equals("closeReportWindow"))
			hideFrame();
		if (e.getActionCommand().equals("selectAllHits"))
			selectAllHits();
		if (e.getActionCommand().equals("deselectAllHits"))
			deselectAllHits();
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
	 * Private constructor - see static access method {@link RADSResultDetailsPanel#showResultsFrame}
	 */
	private RADSResultsTablePanel(DomainArrangement queryProtein, 
			RADSResults results, RADSResultsTableModel resultTableModel) {
		super(new MigLayout());
		this.queryProtein = queryProtein;
		this.selectedHitsLabel = new JLabel(selectedHits+"");
		this.results = results;
		this.resultTableModel = resultTableModel;
		initQueryPanel();
		initTable();
		initPanel();
	 }
	
	private void initQueryPanel() {
		queryPanel = new JPanel();
		DomainArrangement[] daSet = new DomainArrangement[1];
		daSet[0] = queryProtein;
		queryDomainView = ViewHandler.getInstance().createView(ViewType.DOMAINS, "");
		queryDomainView.setDaSet(daSet);
		queryDomainView.getParentPane().removeToolbar();
		queryDomainView.removeMouseListeners();
		queryDomainView.getDomainLayoutManager().getActionManager().getAction(FitDomainsToScreenAction.class).setState(true);
		for (int i = 0; i < queryProtein.countDoms(); i++) {
			DomainFamily fam = queryProtein.getDomain(i).getFamily();			
			Color color = queryDomainView.getDomainColorManager().getDomainColor(fam);
			queryDomainView.getDomainColorManager().setDomainColor(fam, color);
		}
	}
	
	private void initTable() {
		resultTable = new RADSResultsTable(resultTableModel);
		resultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				for (int r : resultTable.getSelectedRows()) {
					boolean cValue = (Boolean) resultTable.getValueAt(r, 1);
					// note: this is *before* the checkbox = true
					if (!cValue) {
						System.out.println("Is selected!");
						selectedHits++;
					}
					else {
						selectedHits--;
					}
					resultTable.setValueAt((!cValue), r, 1);
				}
				updateHitCount();
			}
		}); 
		resultTable.setAutoCreateRowSorter(true);
		resultTable.getTableHeader().setToolTipText("Click to sort");
		TableColumn selectCol = resultTable.getColumnModel().getColumn(0);
		
		TableColumn hitCountCol = resultTable.getColumnModel().getColumn(1);
		TableColumn idCol = resultTable.getColumnModel().getColumn(2);
		TableColumn scoreCol = resultTable.getColumnModel().getColumn(3);
		TableColumn arrCol = resultTable.getColumnModel().getColumn(4);
		selectCol.setPreferredWidth(25);
		hitCountCol.setPreferredWidth(50);
		idCol.setPreferredWidth(50);
		scoreCol.setPreferredWidth(50);
		arrCol.setPreferredWidth(400);
	}
	
	
	/*
	 * Calls the log reading method and constructs the panel
	 */
	private void initPanel() {
		
		readLogFile();
		
		frame = new JFrame("RADS Results");
		
		applySelection = new JButton("Import");
		applySelection.setToolTipText("Create view from selection");
		applySelection.setActionCommand("createView");
		applySelection.addActionListener(this);
		
		save = new JButton("Save");
		save.setToolTipText("Save scan log to file");
		save.setActionCommand("writeLogToFile");
		save.addActionListener(this);

		selectAll = new JButton("Select all");
		selectAll.setToolTipText("Select all hits");
		selectAll.setActionCommand("selectAllHits");
		selectAll.addActionListener(this);
		
		deselectAll = new JButton("Deselect all");
		deselectAll.setToolTipText("Deselect all hits");
		deselectAll.setActionCommand("deselectAllHits");
		deselectAll.addActionListener(this);
		
		/**
		close = new JButton("Close");
		close.setToolTipText("Close results window");
		close.setActionCommand("closeReportWindow");
		close.addActionListener(this);
		**/
		
		seeOnline = new JButton("Browse online");
		seeOnline.setToolTipText("Opens browser with scan results");
		seeOnline.setActionCommand("openResultsInBrowser");
		seeOnline.addActionListener(this);
		
	    resultTable.setFillsViewportHeight(true);;
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
		add(new JLabel("Selected hits:"), "gapleft 10");
		add(selectedHitsLabel, "wrap");
		add(new JXTitledSeparator("Query Arrangement"), "growx, span, wrap, gaptop 10");
		add(queryDomainView.getParentPane(), "wrap, span");
		
		add(new JXTitledSeparator("RADS Results"), "growx, span, wrap, gaptop 10");
		add(jScrollPane, "h 100::400, w 600!, growx, span");
		add(applySelection, "split 4");
		add(save, "");
		//add(close, "");
		add(selectAll, "");
		add(deselectAll, "");
		add(seeOnline, "align right");
		
		frame.add(this);
		frame.pack();
	}
	
	
	private void selectAllHits() {
		selectedHits = 0;
		for (int i=0; i < resultTable.getRowCount(); i++) {
			resultTable.setValueAt(true, i, 1);
			selectedHits ++;
		}
		updateHitCount();
	}
	
	private void deselectAllHits() {
		for (int i=0; i < resultTable.getRowCount(); i++) {
			resultTable.setValueAt(false, i, 1);
		}
		selectedHits = 0;
		updateHitCount();
	}
	
	
	
	private void updateHitCount() {
		selectedHitsLabel.setText(selectedHits+"");
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
	
	
	private void createView() {
		
		List<DomainArrangement> selectedHits = new ArrayList<DomainArrangement>();
		HashMap<String, DomainArrangement> completeHitList = resultTableModel.getArrangementData();
		
		for (int i=0; i < resultTable.getRowCount(); i++) {
			if ((Boolean)resultTable.getValueAt(i, 1)) {
				selectedHits.add(completeHitList.get(resultTable.getValueAt(i, 2)));
			}
		}
		
		String defaultViewName = queryProtein.getName()+"-radscan";
		
//		View currentView = getView();
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;

		//project = WorkspaceManager.getInstance().getViewElement(currentView.getViewInfo()).getProject();

		@SuppressWarnings("rawtypes")
		Map m = WizardManager.getInstance().selectNameWizard(defaultViewName, "RadScan", project, true);
		viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
		projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
		project = WorkspaceManager.getInstance().getProject(projectName);
			
		if (viewName == null) 
			MessageUtil.showWarning("A valid view name is needed to complete this action");
	
		DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		// TODO if we want domain colors, something like this
//		DomainColorManager domColorMan = domResultView.getDomainColorManager();
//		DomainViewI domView = ViewHandler.getInstance().getView(currentView.getViewInfo()) ;
//		Color color = domView.getDomainColorManager().getDomainColor(domFamily);
//		System.out.println("Color: "+color.toString());
		domResultView.setDaSet(selectedHits.toArray(new DomainArrangement[selectedHits.size()]));
		ViewHandler.getInstance().addView(domResultView, project);
		scanPanel.close(false);
		destroy();
		
	}
	
	
	// Not used: for rendering arrangement views in cells
	private class ArrangementTableCellRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			if (isSelected) {
				// TODO
			}
			if (hasFocus) {
				// TODO
			}

			DomainViewI domView = (DomainViewI) value;
			
			//domView.a
			domView.getParentPane().removeToolbar();
			domView.removeMouseListeners();
			domView.getDomainLayoutManager().getActionManager().getAction(FitDomainsToScreenAction.class).setState(true);
			//panel.add(domView.getParentPane(), BorderLayout.NORTH);
			//parentFrame.add(panel);
			//frame.setSize(200, 100);
			//panel.setSize(panel.getPreferredSize());
			//System.out.println("Arr panel height: "+panel.getSize());
			return (Component) domView;
		}
		
	}
	
}
