package angstd.ui.tools.radscan;

import info.radm.radscan.Parser;
import info.radm.radscan.QueryBuilder;
import info.radm.radscan.RADSResults;
import info.radm.radscan.RADSRunner;
import info.radm.radscan.ds.Protein;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainFamily;
import angstd.model.arrangement.DomainType;
import angstd.model.arrangement.io.GatheringThresholdsReader;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;
import angstd.webservices.RADS.RadsParms;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JTextField matchScore, mismatchPen, intOpenGapPen, intExtenGapPen, 
	terOpenGapPen, terExtenGapPen;
	private JCheckBox domLenScoring, resolveOverlaps, mergeHits;
	private JButton runScan, reset, close, apply;
	private JProgressBar progressBar;
	private RadScanView view;
	private ArrangementComponent arrComp;
	private RADSRunner radsRunner;
	private TreeSet<Protein> proteins;
	private boolean radsRunning = false;
	private ArrangementManager arrSet;
	private DomainArrangement queryProtein;
	
	
	public RadScanPanel(RadScanView view) {
		super(new MigLayout());
		this.view = view;
		initComponents();
		
		add(new JXTitledSeparator("RADS match scores"), "growx, span, wrap, gaptop 10");
		add(new JLabel("Match: "), "gap 10, gaptop 10");
		add(matchScore, "span 2, h 25!, wrap");
		add(new JLabel("Mismatch: "), "gap 10, gaptop 10");
		add(mismatchPen, "span 2, h 25!, wrap");

		add(new JXTitledSeparator("RADS gap penalties"), "growx, span, wrap, gaptop 10");
		
		// internal gap pen
		add(new JLabel("Internal"), "gap 10, gaptop 10");
		add(new JLabel("open: "), "gaptop 5");
		add(intOpenGapPen, "h 25!");
		add(new JLabel("extend:"), "gaptop 5");
		add(intExtenGapPen, "h 25!, wrap");
		
		//terminal gap pen
		add(new JLabel("Terminal"), "gap 10, gaptop 10");
		add(new JLabel("open: "), "gaptop 5");
		add(terOpenGapPen, "h 25!");
		add(new JLabel("extend: "), "gaptop 5");
		add(terExtenGapPen, "h 25!, wrap");
		
		add(domLenScoring, "gap 10, gaptop 10");
		add(new JLabel("No length dependant scoring"), "span2, gap 1, gaptop 10, wrap");
		
	
		add(new JXTitledSeparator("Post-processing"), "growx, span, wrap, gaptop 10");
		add(resolveOverlaps, "gap 10, gaptop 10");
		add(new JLabel("Resolve overlaps"), "span2, gap 1, gaptop 10, wrap");
		add(mergeHits, "gap 10, gaptop 5");
		add(new JLabel("Merge split hits"), "span2, gap 1, gaptop 10, wrap");
		
		//add(new JLabel(" "), "gap 10, gaptop 10");
		add(runScan, "growx");
		add(reset, "growx, wrap");
		add(new JXTitledSeparator("Progress"), "growx, span, wrap, gaptop 10");
		add(progressBar, "h 25!, gap 10, gapright 10, span, growX, wrap");
		
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "growx, gap 1");
		add(close, "growx, wrap");
		//setSize(600,350);
	}
	
	
	private void initComponents() {
		
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
				
		domLenScoring = new JCheckBox();
		domLenScoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		resolveOverlaps = new JCheckBox();
		resolveOverlaps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		mergeHits = new JCheckBox();
		mergeHits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO		
			}
		});
		
		runScan = new JButton("Submit Job");
		runScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runScan();
			}
		});
		
		apply = new JButton("Apply");
		apply.setEnabled(false);
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createResultView();
			}
		});
		
		reset = new JButton("Defaults");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();	
			}
		});
		
		close = new JButton("Cancel");
		close.setActionCommand("close");
		close.addActionListener(this);
		
		progressBar = new JProgressBar(0, 105);
		progressBar.setValue(0);
	}
	
	private void runScan(){
		//validateParams();
		runScan.setEnabled(false);
		reset.setEnabled(false);
		queryProtein = view.getArrangementComponent().getDomainArrangement();
		QueryBuilder qBuilder = new QueryBuilder();
		qBuilder.setQuietMode(true);
		qBuilder.setQueryXdomString(queryProtein.toXdom());

		// TODO set params
		this.radsRunner = new RADSRunner(qBuilder.build());
		progressBar.setIndeterminate(true);
		radsRunning = true;
		SwingWorker<TreeSet<Protein>, Void> worker = new SwingWorker<TreeSet<Protein>, Void>() {
			protected TreeSet<Protein> doInBackground() throws Exception {
				RADSResults results = radsRunner.submit();
				Parser resultParser = new Parser(results);
				return resultParser.parse();
			}
			
			public void done() {
				try {
					proteins = get();
				}
				catch (Exception e) {};
			}
		};
		worker.execute();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("state".equals(evt.getPropertyName())) {
					if ( "DONE".equals(evt.getNewValue().toString()) ) {
						System.out.println("Scan Complete.");
						runScan.setEnabled(false);
						radsRunning = false;
						processResults();
					}
				}
			}
		});
	}
	
	private void validateParams() {
		//TODO
	}
	
	private void reset() {
		matchScore.setText(""+RadsParms.DEFAULT_MATCHSCORE.getDeafultValue());
		mismatchPen.setText(""+RadsParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		intOpenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		intExtenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		terOpenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		terExtenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
		domLenScoring.setSelected(false);
		resolveOverlaps.setSelected(false);
		mergeHits.setSelected(false);
	}
	
	private void createResultView() {
		DomainArrangement[] hits = arrSet.get();
		String defaultViewName = queryProtein.getName()+"-radscan-results";
		
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;
		
		project = WorkspaceManager.getInstance().getSelectionManager().getSelectedProject();
		System.out.println("Current project: "+project);
		
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
		if (e.getActionCommand().equals("close"))
			checkScanState(e);
	}
	
	private void checkScanState(ActionEvent e) {
		if (radsRunning) {
			boolean choice = MessageUtil.showDialog(this, "You are running RadScan. Your results will be lost. Are you sure?");
			if (choice)
				view.closeWindow();
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
			da.setDesc("RADS score: "+p.getRADSScore());
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
	}
	
}
