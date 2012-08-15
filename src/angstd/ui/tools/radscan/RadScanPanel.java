package angstd.ui.tools.radscan;

//import info.radm.radscan.Parser;
import info.radm.radscan.QueryBuilder;
import info.radm.radscan.RADSResults;
import info.radm.radscan.ds.Protein;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.model.workspace.ProjectElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;
import angstd.webservices.RADS.RadsParms;
import angstd.webservices.RADS.RadsService;
import angstd.webservices.RADS.ui.RADSResultDetailsPanel;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JTextField matchScore, mismatchPen, intOpenGapPen, intExtenGapPen, 
	terOpenGapPen, terExtenGapPen;
	private JCheckBox domLenScoringCB, resolveOverlapsCB, mergeHitsCB, uniqueHitsCB;
	private JButton runScan, reset, close, apply, details;
	private JProgressBar progressBar;
	private RadScanView view;
	private TreeSet<Protein> proteins;
	private boolean domLenScoring, uniqueArrs, resolveOverlaps, mergeHits;
	private ArrangementManager arrSet;
	private DomainArrangement queryProtein;
	private RADSResults results;
	
	private QueryBuilder qBuilder;
	private Icon radsIcon;
	private RadsService radsService;
	
	public RadScanPanel(RadScanView view) {
		super(new MigLayout());
		this.view = view;
		initComponents();
		
		add(new JXTitledSeparator("RADS match scores"), "growx, span, wrap");
		add(new JLabel("Match: "), "gap 10, gaptop 10");
		add(matchScore, "h 25!, wrap");
	//	add(radsIconLabel, "span 2 2, wrap");
		add(new JLabel("Mismatch: "), "gap 10");
		add(mismatchPen, "h 25!, wrap");
	//	add(new JLabel(""), "span");

		add(new JXTitledSeparator("RADS gap penalties"), "growx, span, wrap, gaptop 10");
		
		// internal gap pen
		add(new JLabel("Internal"), "gap 10, gaptop 10, split 2");
		add(new JLabel("open: "), "");
		add(intOpenGapPen, "h 25!, split 3");
		add(new JLabel("extend:"), "");
		add(intExtenGapPen, "h 25!, wrap");
		
		//terminal gap pen
		add(new JLabel("Terminal"), "gap 10, gaptop 10, split 2");
		add(new JLabel("open: "), "");
		add(terOpenGapPen, "h 25!, split 3");
		add(new JLabel("extend:"), "");
		add(terExtenGapPen, "h 25!, wrap");
//		
//		add(domLenScoringCB, "gap 10, gaptop 10, split 2");
//		add(new JLabel("Length scoring"), "span2, gap 1, gaptop 10, wrap");
//		
	
//		add(new JXTitledSeparator("Post-processing"), "growx, span, wrap, gaptop 10");
//		add(resolveOverlapsCB, "gap 10, gaptop 10, split 2");
//		add(new JLabel("Resolve overlaps"), "span2, gap 1, gaptop 10, wrap");
//		add(mergeHitsCB, "gap 10, gaptop 5, split 2");
//		add(new JLabel("Merge split hits"), "span2, gap 1, gaptop 10, wrap");
//		add(uniqueHits, "gap 10, gaptop 5, split 2");
//		add(new JLabel("Merge split hits"), "span2, gap 1, gaptop 10, wrap");
		
		//add(new JLabel(" "), "gap 10, gaptop 10");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(runScan);
		buttonPanel.add(reset);
		add(buttonPanel, "gaptop 10, wrap, span");
	
		add(new JXTitledSeparator("Progress"), "growx, span, wrap, gaptop 10");
		add(progressBar, "h 25!, gap 10, gapright 10, span, growX, wrap");
		
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "growx, split2");
		add(close, "");
		add(details, "gap 1");
	}
	
	
	private void initComponents() {
		
//		URL radsImgPath = RadScanPanel.class.getResource("../../../webservices/RADS/ui/resources/aniblu.jpg");
//		radsIcon = new ImageIcon(radsImgPath);
//		radsIconLabel = new JLabel(radsIcon);
		
		domLenScoring = true;
		uniqueArrs = false;
		resolveOverlaps = false;
		mergeHits = false;
		
		matchScore = new JTextField(5);
		matchScore.setText(""+RadsParms.DEFAULT_MATCHSCORE.getDeafultValue());
				
		mismatchPen = new JTextField(5);
		mismatchPen.setText(""+RadsParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		
		intOpenGapPen = new JTextField(5);
		intOpenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		
		intExtenGapPen = new JTextField(5);
		intExtenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		
		terOpenGapPen = new JTextField(5);
		terOpenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		
		terExtenGapPen = new JTextField(5);
		terExtenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
				
		domLenScoringCB = new JCheckBox();
		domLenScoringCB.setSelected(true);
		domLenScoringCB.setToolTipText("Weight domain-match score by their length");
		domLenScoringCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				domLenScoring = !domLenScoring;
			}
		});
		
		resolveOverlapsCB = new JCheckBox();
		resolveOverlapsCB.setToolTipText("Resolve domain overlaps");
		resolveOverlapsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resolveOverlaps = !resolveOverlaps;
			}
		});
		
		mergeHitsCB = new JCheckBox();
		mergeHitsCB.setToolTipText("Merge overlapping hits to same domain model");
		mergeHitsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mergeHits = !mergeHits;
			}
		});
		
		uniqueHitsCB = new JCheckBox();
		uniqueHitsCB.setToolTipText("Extract unique arrangements from list of hits");
		uniqueHitsCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uniqueArrs = !uniqueArrs;	
			}
		});
		
		runScan = new JButton("Submit Job");
		runScan.setToolTipText("Submit RADScan job");
		runScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runScan();
			}
		});
		
		apply = new JButton("Apply");
		apply.setToolTipText("Create domain view from scan results");
		apply.setEnabled(false);
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createResultView();
			}
		});
		
		reset = new JButton("Defaults");
		reset.setToolTipText("Reset all param to default values");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();	
			}
		});
		
		details = new JButton("Show scan report");
		details.setToolTipText("");
		details.setEnabled(false);
		details.setActionCommand("showReportWindow");
		details.addActionListener(this);
		
		close = new JButton("Cancel");
		close.setActionCommand("closeRadsWindow");
		close.addActionListener(this);
		
		progressBar = new JProgressBar(0, 105);
		progressBar.setValue(0);
	}
	
	private void runScan(){
		
		qBuilder = new QueryBuilder();	
		if (validateParams()) {
			runScan.setText("Running scan");
			runScan.setEnabled(false);
			reset.setEnabled(false);
			
			queryProtein = view.getArrangementComponent().getDomainArrangement();
			qBuilder.setQuietMode(true);
			qBuilder.setQueryXdomString(queryProtein.toXdom());
			radsService = new RadsService(qBuilder.build(), queryProtein);
			progressBar.setIndeterminate(true);
			
			radsService.execute();
			
			radsService.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if ("state".equals(evt.getPropertyName())) {
						if ( "DONE".equals(evt.getNewValue().toString()) ) {
							runScan.setText("Submit Job");
							runScan.setEnabled(true);
							proteins = radsService.getHits();
							results = radsService.getScanResults();
							processResults();
						}
					}
				}
			});
		}
		
	}
	
	private boolean validateParams() {
		try {
						
			int matchScoreValue = Integer.valueOf(matchScore.getText());
			qBuilder.setRads_M(matchScoreValue);
			
			int mismatchPenValue = Integer.valueOf(mismatchPen.getText());
			qBuilder.setRads_m(mismatchPenValue);
			
			int intOpenGapPenValue = Integer.valueOf(intOpenGapPen.getText());
			qBuilder.setRads_G(intOpenGapPenValue);
			
			int intExtenGapPenValue = Integer.valueOf(intExtenGapPen.getText());
			qBuilder.setRads_g(intExtenGapPenValue);
			
			int terOpenGapPenValue = Integer.valueOf(terOpenGapPen.getText());
			qBuilder.setRads_T(terOpenGapPenValue);
			
			int terExtenGapPenValue = Integer.valueOf(terExtenGapPen.getText());
			qBuilder.setRads_t(terExtenGapPenValue);
		}
		catch (NumberFormatException nfe) {
			MessageUtil.showWarning("Values for scores and penalties must be numbers");
			return false;
		}
		return true;
	}
	
	private void reset() {
		matchScore.setText(""+RadsParms.DEFAULT_MATCHSCORE.getDeafultValue());
		mismatchPen.setText(""+RadsParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		intOpenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		intExtenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		terOpenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		terExtenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
		domLenScoringCB.setSelected(false);
		resolveOverlapsCB.setSelected(false);
		mergeHitsCB.setSelected(false);
	}
	
	private void createResultView() {
		DomainArrangement[] hits = arrSet.get();
		String defaultViewName = queryProtein.getName()+"-radscan";
		
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;
		
		project = WorkspaceManager.getInstance().getSelectionManager().getSelectedProject();
		System.out.println("Current project: "+project);
		
		@SuppressWarnings("rawtypes")
		Map m = WizardManager.getInstance().selectNameWizard(defaultViewName, "RadScan results", project, true);
		viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
		projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
		project = WorkspaceManager.getInstance().getProject(projectName);
			
		if (viewName == null) 
			MessageUtil.showWarning("A valid view name is needed to complete this action");
	
		DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		domResultView.setDaSet(hits);
		ViewHandler.getInstance().addView(domResultView, project);
		view.closeWindow();
	}
	

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("closeRadsWindow"))
			closeRadsWindow(e);
		if (e.getActionCommand().equals("showReportWindow"))
			new RADSResultDetailsPanel(queryProtein, results, proteins);
			
	}
	
	private void closeRadsWindow(ActionEvent e) {
		if (radsService.isRunning()) {
			boolean choice = MessageUtil.showDialog(this, "You are running RadScan. Your results will be lost. Are you sure?");
			if (choice) {
				radsService.cancelScan();
				view.closeWindow();
			}
			else
				return;
		}
		view.closeWindow();
	}	
	
	private void processResults() {
		if (proteins == null) {
			view.closeWindow();
			MessageUtil.showInformation("No hits found");
			return;
		}
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(proteins.size());
		progressBar.setValue(0);
		int i = 1;

		arrSet = new ArrangementManager();
		DomainArrangement da; 
		for (Protein p: proteins) {
			progressBar.setValue(i);
			progressBar.setString("Processing hit "+i+ " of "+progressBar.getMaximum());
			da = new DomainArrangement();
			da.setName(p.getID());
			da.setSeqLen(p.getLength());
			
			for (info.radm.radscan.ds.Domain resDom: p.getDomains()) {
			
				String acc = resDom.getID();
				DomainFamily domFamily = GatheringThresholdsReader.getInstance().get(acc);
				if (domFamily == null) {
					domFamily = new DomainFamily(acc, acc, DomainType.getType(acc));
					GatheringThresholdsReader.getInstance().put(acc, domFamily);
				}
				int from = resDom.getFrom();
				int to = resDom.getTo();
				double evalue = resDom.getEvalue();
				Domain dom = new Domain(from, to, domFamily);
				if (evalue != -1)
					dom.setEvalue(evalue);
				da.addDomain(dom);
			}
			// TODO: consider implementing comparable to avoid this
			// (ie. define natural order)
			da.sortDomains();
			arrSet.add(da);
			i++;
		}
		progressBar.setString("Scan complete");
		apply.setEnabled(true);
		details.setEnabled(true);
		runScan.setEnabled(true);
		reset.setEnabled(true);
	}
	

	
}
