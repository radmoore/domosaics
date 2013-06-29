package domosaics.webservices.RADS.ui;

import info.radm.radscan.RADSResults;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainFamily;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.actions.FitDomainsToScreenAction;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;
import domosaics.util.BrowserLauncher;
import domosaics.webservices.RADS.util.RADSResultsTable;
import domosaics.webservices.RADS.util.RADSResultsTableModel;




/**
 * This class describes the JPanel which shows the RADS/RAMPAGE scan results table.
 * It is implemented as a signelton.
 * 
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSResultsTablePanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private RADSResults results;
	private JFrame frame;
	
	private JButton seeOnline, applySelection, selectAll, deselectAll;
	private RADSResultsTable resultTable;
	private DomainArrangement queryProtein;
	private RADSResultsTableModel resultTableModel;
	
	private JLabel selectedHitsLabel;
	private int selectedHits = 0;
	
	private RADSScanPanel scanPanel;
	private DomainViewI queryDomainView = null;
	private static RADSResultsTablePanel instance = null;
	
	/**
	 * This method is used to get access to the JPanel displaying the results table.
	 * If a result panel is currently being displayed (or e.g. has been backgrounded), the
	 * instance will be destroyed, and a new instance will be created. 
	 * 
	 * @param queryProtein - the RADS/RAMPAGE query arrangement
	 * @param results - the results of the RADSScan (provides access to method used, all parameters, 
	 * scanned database etc)
	 * @param proteins - the list of hit proteins
	 * @return - an instance of the RADSResultsTablePanel
	 */
	public static RADSResultsTablePanel createResultsTableFrame(DomainArrangement queryProtein, 
			RADSResults results, RADSResultsTableModel resultTableModel) {
		if (instance != null)
			instance.destroy();
		instance = new RADSResultsTablePanel(queryProtein, results, resultTableModel);
		return instance;
	}
	
	/**
	 * Sets the RADSScanPanel which initiated this frame
	 * @param scanPanel
	 */
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
		if (e.getActionCommand().equals("selectAllHits"))
			selectAllHits();
		if (e.getActionCommand().equals("deselectAllHits"))
			deselectAllHits();
	}
	
	/**
	 * Closes this frame
	 */
	public void destroy() {
		frame.dispose();
	}
	
	/**
	 * Hides this frame. hideFrame and {@link RADSResultsTablePanel#showFrame()} are used
	 * to hide and unhide this frame, as opposed to re-creating an instance of
	 * the same scan results (singleton)
	 */
	public void hideFrame() {
		frame.setVisible(false);
	}
	
	/**
	 * Shows this frame, see also {@link RADSResultsTablePanel#hideFrame()}
	 */
	public void showFrame() {
		frame.setVisible(true);
	}
	
	/**
	 * Private constructor - see static access method {@link RADSResultTablePanel#showResultsFrame()} 
	 * @param queryProtein - the query protein used for the scan
	 * @param results - an instance of RADSResults which holds a reference to the query, the results, etc
	 * @param resultTableModel - an instance of RADSResultsTableModel which holds the result table 
	 */
	private RADSResultsTablePanel(DomainArrangement queryProtein, 
			RADSResults results, RADSResultsTableModel resultTableModel) {
		super(new MigLayout("", "[]25[]", ""));
		this.queryProtein = queryProtein;
		this.selectedHitsLabel = new JLabel(selectedHits+"");
		this.results = results;
		this.resultTableModel = resultTableModel;

		// construct frame
		frame = new JFrame("RADS/RAMPAGE Results");
		frame.setPreferredSize(new Dimension(617, 712));
		frame.setResizable(false);
//		frame.addComponentListener(new ComponentListener() {
//			public void componentResized(ComponentEvent e) {
//				System.out.println("Height: "+e.getComponent().getHeight());
//				System.out.println("Width: "+e.getComponent().getWidth());
//			}
//		});
		
		// construct panel, results table and query
		if ( results.getQuery().getQuerySequence() == null )
			initQueryPanel();
		initTable();
		initPanel();
	 }
	
	/*
	 * Initiates the query panel, which displays the query arrangement. Is only shown
	 * if the query was an arrangement (not, for example, for a sequence)
	 */
	private void initQueryPanel() {
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
	
	/*
	 * Inititate the JTable using the resultTableModel 
	 */
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
	}
	
	/*
	 * Constructs the panel
	 */
	private void initPanel() {
		
		applySelection = new JButton("Import selection");
		applySelection.setToolTipText("Create view from selection");
		applySelection.setActionCommand("createView");
		applySelection.addActionListener(this);
		applySelection.setEnabled(false);

		selectAll = new JButton("Select all");
		selectAll.setToolTipText("Select all hits");
		selectAll.setActionCommand("selectAllHits");
		selectAll.addActionListener(this);
		
		deselectAll = new JButton("Deselect all");
		deselectAll.setToolTipText("Deselect all hits");
		deselectAll.setActionCommand("deselectAllHits");
		deselectAll.addActionListener(this);
		
		seeOnline = new JButton("Browse online");
		seeOnline.setToolTipText("Opens browser with scan results");
		seeOnline.setActionCommand("openResultsInBrowser");
		seeOnline.addActionListener(this);
		
	    resultTable.setFillsViewportHeight(true);;
		JScrollPane jScrollPane = new JScrollPane(resultTable);
		
		add(new JXTitledSeparator("Scan summary"), "growx, span, wrap");
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
		add(new JXTitledSeparator("Query"), "growx, span, wrap, gaptop 10");
		if (queryDomainView != null)
			add(queryDomainView.getParentPane(), "wrap, span");
		else {
			add(new JLabel("Sequence sequence"), "gapleft 10, wrap");
			add(new JLabel("Checksum: "), "gapleft 10");
			add(new JLabel(results.getQuery().getSequenceChecksum()), "span, wrap");
		}
			
		add(new JXTitledSeparator("RADS/RAMPAGE hitlist"), "growx, span, wrap, gaptop 10");
		add(jScrollPane, "h 100::400, w 600!, growx, span, wrap");
		add(applySelection, "");
		add(selectAll, "split 2");
		add(deselectAll, "");
		add(seeOnline, "align right");
		
		frame.add(this);
		frame.pack();
	}
	
	/*
	 * Called when the selectAll button is pressed 
	 */
	private void selectAllHits() {
		selectedHits = 0;
		for (int i=0; i < resultTable.getRowCount(); i++) {
			resultTable.setValueAt(true, i, 1);
			selectedHits ++;
		}
		updateHitCount();
	}
	
	/*
	 * Called when the deselectAll button is pressed
	 */
	private void deselectAllHits() {
		for (int i=0; i < resultTable.getRowCount(); i++) {
			resultTable.setValueAt(false, i, 1);
		}
		selectedHits = 0;
		updateHitCount();
	}
	
	/*
	 * Called when ever a hit is selected or deselect (checkbox) 
	 */
	private void updateHitCount() {
		if (selectedHits > 0)
			applySelection.setEnabled(true);
		else
			applySelection.setEnabled(false);
		selectedHitsLabel.setText(selectedHits+"");
	}

	/*
	 * Called when the used presses the applySelection button
	 */
	private void createView() {
		
		List<DomainArrangement> selectedHits = new ArrayList<DomainArrangement>();
		HashMap<String, DomainArrangement> completeHitList = resultTableModel.getArrangementData();
		
		for (int i=0; i < resultTable.getRowCount(); i++) {
			if ((Boolean)resultTable.getValueAt(i, 1)) {
				selectedHits.add(completeHitList.get(resultTable.getValueAt(i, 2)));
			}
		}
		
		String defaultViewName = queryProtein.getName()+"-radscan";
		
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;

		//@SuppressWarnings("rawtypes")
		while(viewName==null) {
			Map m = WizardManager.getInstance().selectNameWizard(defaultViewName, "RadScan", project, true);
			if(m!=null) {
				viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
				projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
				project = WorkspaceManager.getInstance().getProject(projectName);
			} else {
				return;
			}
		}
		
		DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		domResultView.setDaSet(selectedHits.toArray(new DomainArrangement[selectedHits.size()]));
		ViewHandler.getInstance().addView(domResultView, project);
		scanPanel.close(false);
		destroy();
	}
	
}
